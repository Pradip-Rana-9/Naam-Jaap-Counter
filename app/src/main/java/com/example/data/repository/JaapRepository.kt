package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.auth.AuthRepository
import com.example.data.dao.JaapDao
import com.example.data.entity.DailyProgress
import com.example.data.entity.Mantra
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import com.example.data.entity.UserSettings
import com.example.data.firebase.FirebaseSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JaapRepository(
    private val dao: JaapDao,
    context: Context? = null
) {
    private val appContext = context?.applicationContext

    val authRepository = AuthRepository(dao, context)
    private val firebaseSyncManager = FirebaseSyncManager()
    private val countMutex = Mutex()
    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    private val prefs by lazy {
        appContext?.getSharedPreferences("jaap_devotional_prefs", Context.MODE_PRIVATE)
    }

    fun getLastGuestReminderDismissedTime(): Long {
        return try {
            prefs?.getLong("key_last_guest_reminder_timestamp", 0L) ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun setLastGuestReminderDismissedTime(timestamp: Long = System.currentTimeMillis()) {
        try {
            prefs?.edit()?.putLong("key_last_guest_reminder_timestamp", timestamp)?.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notifyWidgetUpdate() {
        appContext?.let { ctx ->
            try {
                com.example.widget.NaamJaapWidgetProvider.updateAllWidgets(ctx)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val allMantras: Flow<List<Mantra>> = dao.getAllMantras()
    val selectedMantra: Flow<Mantra?> = dao.getSelectedMantra()
    val userSettings: Flow<UserSettings?> = dao.getUserSettings()
    val recentSessions: Flow<List<Session>> = dao.getRecentSessions(10)
    val allSessions: Flow<List<Session>> = dao.getAllSessions()
    val allDailyProgress: Flow<List<DailyProgress>> = dao.getAllDailyProgress()
    val allSankalps: Flow<List<Sankalp>> = dao.getAllSankalps()
    val activeSankalps: Flow<List<Sankalp>> = dao.getActiveSankalps()

    val totalMalas: Flow<Int> = dao.getTotalMalasCompleted().map { it ?: 0 }
    val totalBeads: Flow<Int> = dao.getTotalBeadsCompleted().map { it ?: 0 }

    suspend fun ensureInitialData() {
        try {
            if (dao.getMantraById(1) == null) {
                val defaultMantras = listOf(
                    Mantra(
                        id = 1,
                        textHindi = "राधे राधे",
                        textEnglish = "Radhe Radhe",
                        description = "Divine love and supreme devotion to Sri Radha Rani",
                        isDefault = true,
                        isSelected = true,
                        isFavorite = true,
                        isCustom = false,
                        sortOrder = 1
                    )
                )
                dao.insertMantras(defaultMantras)
            }

            if (dao.getUserSettingsDirect() == null) {
                val initialUserSettings = UserSettings(
                    id = 1,
                    userName = "Devotee",
                    userInitial = "D",
                    avatarId = 1,
                    joinDateString = "02 Aug, 2026",
                    dailyGoalMalas = 10,
                    hapticFeedbackEnabled = false,
                    audioChimeEnabled = false,
                    themeMode = "DARK",
                    language = "ENGLISH",
                    reminderEnabled = false,
                    reminderTime = "20:00",
                    selectedMantraId = 1,
                    currentBeadInIncompleteMala = 0,
                    currentMalaNumber = 1,
                    currentSessionMalasCount = 0
                )
                dao.insertOrUpdateUserSettings(initialUserSettings)
            }
            notifyWidgetUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTodayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getTodayProgress(): Flow<DailyProgress?> {
        return dao.getDailyProgress(getTodayString())
    }

    fun getTodaySessions(): Flow<List<Session>> {
        return dao.getSessionsForDate(getTodayString())
    }

    fun getWeeklyCompletionDays(): Flow<Set<Int>> {
        return allDailyProgress.map { progressList ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }

            val weekDates = (0..6).map {
                val d = sdf.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, 1)
                d
            }

            val map = progressList.associateBy { it.dateString }
            val completed = mutableSetOf<Int>()
            weekDates.forEachIndexed { idx, dateStr ->
                val prog = map[dateStr]
                if (prog != null && (prog.totalBeadsCompleted > 0 || prog.totalMalasCompleted > 0)) {
                    completed.add(idx)
                }
            }
            completed
        }
    }

    suspend fun selectMantra(mantraId: Int) {
        dao.setSelectedMantraId(mantraId)
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        dao.insertOrUpdateUserSettings(settings.copy(selectedMantraId = mantraId))
        notifyWidgetUpdate()
    }

    suspend fun addCustomMantra(textEnglish: String, textHindi: String = "") {
        val trimmedEnglish = textEnglish.trim()
        if (trimmedEnglish.isBlank()) return
        val trimmedHindi = if (textHindi.isNotBlank()) textHindi.trim() else trimmedEnglish
        val newMantra = Mantra(
            textHindi = trimmedHindi,
            textEnglish = trimmedEnglish,
            description = "Custom devotion mantra",
            isDefault = false,
            isSelected = true,
            isFavorite = false,
            isCustom = true
        )
        val insertedId = dao.insertMantra(newMantra).toInt()
        if (insertedId > 0) {
            selectMantra(insertedId)
        }
    }

    suspend fun editCustomMantra(id: Int, textEnglish: String, textHindi: String = "") {
        val existing = dao.getMantraById(id) ?: return
        val trimmedEnglish = textEnglish.trim()
        if (trimmedEnglish.isBlank()) return
        val trimmedHindi = if (textHindi.isNotBlank()) textHindi.trim() else trimmedEnglish
        val updated = existing.copy(
            textEnglish = trimmedEnglish,
            textHindi = trimmedHindi
        )
        dao.updateMantra(updated)
    }

    suspend fun deleteCustomMantra(id: Int) {
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        if (settings.selectedMantraId == id) {
            // Revert selected mantra to default Radhe Radhe (id = 1)
            selectMantra(1)
        }
        dao.deleteCustomMantra(id)
    }

    suspend fun toggleFavoriteMantra(id: Int, isFavorite: Boolean) {
        dao.toggleFavorite(id, isFavorite)
    }

    suspend fun updateDailyGoal(goalMalas: Int) {
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        val updatedSettings = settings.copy(dailyGoalMalas = goalMalas)
        dao.insertOrUpdateUserSettings(updatedSettings)

        val today = getTodayString()
        val currentProgress = dao.getDailyProgressDirect(today) ?: DailyProgress(dateString = today)
        val isGoalMet = currentProgress.totalMalasCompleted >= goalMalas
        dao.upsertDailyProgress(
            currentProgress.copy(
                dailyGoalMalas = goalMalas,
                goalMet = isGoalMet
            )
        )
        notifyWidgetUpdate()
    }

    suspend fun getUserSettingsDirect(): UserSettings? {
        return dao.getUserSettingsDirect()
    }

    suspend fun saveFullUserSettings(userSettings: UserSettings) {
        dao.insertOrUpdateUserSettings(userSettings)
        try {
            val beads = dao.getTotalBeadsCompletedDirect() ?: 0
            val malas = dao.getTotalMalasCompletedDirect() ?: 0
            val streak = calculateStreaks()
            firebaseSyncManager.syncUserProfile(
                settings = userSettings,
                totalBeads = beads,
                totalMalas = malas,
                currentStreak = streak.first,
                longestStreak = streak.second
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyWidgetUpdate()
    }

    suspend fun updateUserSettings(
        userName: String? = null,
        themeMode: String? = null,
        language: String? = null,
        hapticEnabled: Boolean? = null,
        audioChimeEnabled: Boolean? = null,
        reminderEnabled: Boolean? = null,
        reminderTime: String? = null
    ) {
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        val initial = if (userName != null && userName.isNotBlank()) {
            userName.trim().substring(0, 1).uppercase(Locale.getDefault())
        } else {
            settings.userInitial
        }

        val updated = settings.copy(
            userName = userName ?: settings.userName,
            userInitial = initial,
            themeMode = themeMode ?: settings.themeMode,
            language = language ?: settings.language,
            hapticFeedbackEnabled = hapticEnabled ?: settings.hapticFeedbackEnabled,
            audioChimeEnabled = audioChimeEnabled ?: settings.audioChimeEnabled,
            reminderEnabled = reminderEnabled ?: settings.reminderEnabled,
            reminderTime = reminderTime ?: settings.reminderTime
        )
        dao.insertOrUpdateUserSettings(updated)
        notifyWidgetUpdate()
        try {
            val beads = dao.getTotalBeadsCompletedDirect() ?: 0
            val malas = dao.getTotalMalasCompletedDirect() ?: 0
            val streak = calculateStreaks()
            firebaseSyncManager.syncUserProfile(
                settings = updated,
                totalBeads = beads,
                totalMalas = malas,
                currentStreak = streak.first,
                longestStreak = streak.second
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createSankalp(sankalp: Sankalp): Long {
        val id = dao.insertSankalp(sankalp)
        val created = sankalp.copy(id = id.toInt())
        try {
            firebaseSyncManager.syncSankalp(created)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return id
    }

    suspend fun updateSankalp(sankalp: Sankalp) {
        dao.updateSankalp(sankalp)
        try {
            firebaseSyncManager.syncSankalp(sankalp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteSankalp(id: Int) {
        dao.deleteSankalp(id)
    }

    private suspend fun updateActiveSankalpsOnBeadChanted(isMalaCompleted: Boolean) {
        val activeSankalpsList = activeSankalps.firstOrNull() ?: emptyList()
        if (activeSankalpsList.isEmpty()) return

        activeSankalpsList.forEach { sankalp ->
            val newBeads = sankalp.completedBeads + 1
            val newMalas = if (isMalaCompleted) sankalp.completedMalas + 1 else sankalp.completedMalas
            val isCompleted = newMalas >= sankalp.targetMalasTotal

            val updatedSankalp = sankalp.copy(
                completedBeads = newBeads,
                completedMalas = newMalas,
                status = if (isCompleted) "COMPLETED" else "ACTIVE"
            )
            dao.updateSankalp(updatedSankalp)
            backgroundScope.launch {
                try {
                    firebaseSyncManager.syncSankalp(updatedSankalp)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun resetCurrentMalaBeads() = countMutex.withLock {
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        val updatedSettings = settings.copy(
            currentBeadInIncompleteMala = 0
        )
        dao.insertOrUpdateUserSettings(updatedSettings)
        notifyWidgetUpdate()
    }

    // Handles single bead tap; returns true if a mala (108 beads) was completed!
    suspend fun incrementBead(sessionDurationMillis: Long = 60000L): Boolean = countMutex.withLock {
        val today = getTodayString()
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        val mantra = dao.getMantraById(settings.selectedMantraId)

        val currentBead = settings.currentBeadInIncompleteMala
        val newBead = currentBead + 1

        // Update daily progress bead count
        val currentProgress = dao.getDailyProgressDirect(today) ?: DailyProgress(
            dateString = today,
            dailyGoalMalas = settings.dailyGoalMalas
        )
        val updatedBeads = currentProgress.totalBeadsCompleted + 1

        val isMalaCompleted = (newBead >= 108)

        val finalDailyProgress: DailyProgress

        if (isMalaCompleted) {
            // Mala Complete on 108th bead!
            val newMalaNum = settings.currentMalaNumber + 1
            val newSessionMalas = settings.currentSessionMalasCount + 1
            val updatedMalas = currentProgress.totalMalasCompleted + 1
            val isGoalMet = updatedMalas >= settings.dailyGoalMalas

            finalDailyProgress = currentProgress.copy(
                totalMalasCompleted = updatedMalas,
                totalBeadsCompleted = updatedBeads,
                goalMet = isGoalMet
            )
            dao.upsertDailyProgress(finalDailyProgress)

            // Save completed Mala session with accurate duration
            val now = System.currentTimeMillis()
            val validDuration = sessionDurationMillis.coerceIn(5_000L, 7_200_000L)
            val session = Session(
                mantraId = settings.selectedMantraId,
                mantraNameHindi = mantra?.textHindi ?: "राधे राधे",
                startTimestamp = now - validDuration,
                endTimestamp = now,
                totalBeadsInSession = 108,
                totalMalasInSession = 1,
                dateString = today
            )
            val sessionId = dao.insertSession(session)

            // Update user settings counter: reset current bead to 0 for next mala
            val updatedSettings = settings.copy(
                currentBeadInIncompleteMala = 0,
                currentMalaNumber = newMalaNum,
                currentSessionMalasCount = newSessionMalas
            )
            dao.insertOrUpdateUserSettings(updatedSettings)

            // Auto-update all active Sankalps
            updateActiveSankalpsOnBeadChanted(isMalaCompleted = true)

            notifyWidgetUpdate()

            // Sync to Firebase in background without blocking local DB
            backgroundScope.launch {
                try {
                    firebaseSyncManager.syncSession(session.copy(id = sessionId))
                    val totalBeadsOverall = dao.getTotalBeadsCompletedDirect() ?: 0
                    val totalMalasOverall = dao.getTotalMalasCompletedDirect() ?: 0
                    firebaseSyncManager.syncDailyProgress(
                        dailyProgress = finalDailyProgress,
                        totalBeadsOverall = totalBeadsOverall,
                        totalMalasOverall = totalMalasOverall
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            true
        } else {
            finalDailyProgress = currentProgress.copy(
                totalBeadsCompleted = updatedBeads
            )
            dao.upsertDailyProgress(finalDailyProgress)
            val updatedSettings = settings.copy(
                currentBeadInIncompleteMala = newBead
            )
            dao.insertOrUpdateUserSettings(updatedSettings)

            // Auto-update all active Sankalps
            updateActiveSankalpsOnBeadChanted(isMalaCompleted = false)

            notifyWidgetUpdate()

            // Sync to Firebase in background without blocking local DB
            backgroundScope.launch {
                try {
                    val totalBeadsOverall = dao.getTotalBeadsCompletedDirect() ?: 0
                    val totalMalasOverall = dao.getTotalMalasCompletedDirect() ?: 0
                    firebaseSyncManager.syncDailyProgress(
                        dailyProgress = finalDailyProgress,
                        totalBeadsOverall = totalBeadsOverall,
                        totalMalasOverall = totalMalasOverall
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            false
        }
    }

    // Calculate streaks from daily_progress entries
    suspend fun calculateStreaks(): Pair<Int, Int> {
        val progressList = dao.getAllDailyProgressDirect().filter { it.totalBeadsCompleted > 0 }
        if (progressList.isEmpty()) return Pair(0, 0)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateSet = progressList.map { it.dateString }.toSet()

        val cal = Calendar.getInstance()
        val todayStr = sdf.format(cal.time)

        var currentStreak = 0
        var maxStreak = 0
        var tempStreak = 0

        // Check backwards from today or yesterday
        if (!dateSet.contains(todayStr)) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        var checkStr = sdf.format(cal.time)
        while (dateSet.contains(checkStr)) {
            currentStreak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
            checkStr = sdf.format(cal.time)
        }

        // Longest streak calculation
        val sortedDates = dateSet.mapNotNull {
            try { sdf.parse(it) } catch (e: Exception) { null }
        }.sorted()

        if (sortedDates.isNotEmpty()) {
            tempStreak = 1
            maxStreak = 1
            for (i in 1 until sortedDates.size) {
                val diff = (sortedDates[i].time - sortedDates[i - 1].time) / (1000 * 60 * 60 * 24)
                if (diff == 1L) {
                    tempStreak++
                    if (tempStreak > maxStreak) maxStreak = tempStreak
                } else if (diff > 1L) {
                    tempStreak = 1
                }
            }
        }

        if (currentStreak > maxStreak) maxStreak = currentStreak
        return Pair(currentStreak, maxStreak)
    }

    suspend fun resetSessionCounters() {
        val settings = dao.getUserSettingsDirect() ?: UserSettings()
        dao.insertOrUpdateUserSettings(settings.copy(currentSessionMalasCount = 0))
    }

    suspend fun syncAllDataToCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        val uid = authRepository.getCurrentUid()
        if (uid == null || !authRepository.isUserLoggedIn()) {
            return@withContext Result.success(Unit)
        }
        try {
            val settings = dao.getUserSettingsDirect() ?: UserSettings()
            val beads = dao.getTotalBeadsCompletedDirect() ?: 0
            val malas = dao.getTotalMalasCompletedDirect() ?: 0
            val streak = calculateStreaks()
            firebaseSyncManager.syncUserProfile(
                settings = settings,
                totalBeads = beads,
                totalMalas = malas,
                currentStreak = streak.first,
                longestStreak = streak.second
            )

            val dailyProgressList = dao.getAllDailyProgressDirect()
            for (dp in dailyProgressList) {
                firebaseSyncManager.syncDailyProgress(dp, beads, malas)
            }

            val sessionsList = dao.getAllSessionsDirect()
            for (s in sessionsList) {
                firebaseSyncManager.syncSession(s)
            }

            val sankalpsList = dao.getAllSankalpsDirect()
            for (sk in sankalpsList) {
                firebaseSyncManager.syncSankalp(sk)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JaapRepository", "Error syncing all data to cloud: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun logoutUser(): Result<Unit> = withContext(Dispatchers.IO) {
        if (authRepository.isUserLoggedIn()) {
            val syncResult = syncAllDataToCloud()
            if (syncResult.isFailure) {
                val error = syncResult.exceptionOrNull()
                Log.w("JaapRepository", "Aborting logout: Unsynced local chanting records could not be backed up to cloud: ${error?.message}")
                return@withContext Result.failure(
                    Exception("Your latest chanting progress hasn't been synced yet. Please connect to the internet and try again so your progress can be safely backed up.")
                )
            }
        }
        authRepository.signOutUser()
        Result.success(Unit)
    }

    suspend fun resetGuestData() {
        authRepository.resetGuestData()
    }
}
