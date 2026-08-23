package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthState
import com.example.data.entity.DailyProgress
import com.example.data.entity.Mantra
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import com.example.data.entity.UserSettings
import com.example.data.repository.JaapRepository
import com.example.reminder.ReminderManager
import com.example.ui.components.DevotionalEvent
import com.example.util.SoundHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JaapViewModel(private val repository: JaapRepository) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.authRepository.authState

    private val _isGuestReminderDismissed = MutableStateFlow(false)
    val isGuestReminderDismissed: StateFlow<Boolean> = _isGuestReminderDismissed.asStateFlow()

    val isGuestUser: StateFlow<Boolean> = combine(authState, repository.userSettings) { state, settings ->
        if (state is AuthState.Authenticated) {
            false
        } else if (repository.authRepository.isUserLoggedIn()) {
            false
        } else {
            settings?.authMethod == "GUEST" || state is AuthState.Guest || state is AuthState.Idle
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = !repository.authRepository.isUserLoggedIn()
    )

    val showGuestReminder: StateFlow<Boolean> = combine(isGuestUser, _isGuestReminderDismissed) { isGuest, dismissed ->
        isGuest && !dismissed
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = !repository.authRepository.isUserLoggedIn()
    )

    fun isGuestMode(): Boolean {
        return isGuestUser.value
    }

    fun dismissGuestReminder() {
        _isGuestReminderDismissed.value = true
    }

    fun resetGuestReminder() {
        _isGuestReminderDismissed.value = false
    }

    private val _currentTab = MutableStateFlow(0) // 0: Home, 1: Jaap, 2: Progress, 3: Profile
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    val allMantras: StateFlow<List<Mantra>> = repository.allMantras.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedMantra: StateFlow<Mantra?> = repository.selectedMantra.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val userSettings: StateFlow<UserSettings?> = repository.userSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val todayProgress: StateFlow<DailyProgress?> = repository.getTodayProgress().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val todaySessions: StateFlow<List<Session>> = repository.getTodaySessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weeklyCompletionDays: StateFlow<Set<Int>> = repository.getWeeklyCompletionDays().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val allDailyProgress: StateFlow<List<DailyProgress>> = repository.allDailyProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allSankalps: StateFlow<List<Sankalp>> = repository.allSankalps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeSankalps: StateFlow<List<Sankalp>> = repository.activeSankalps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentSessions: StateFlow<List<Session>> = repository.recentSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalMalas: StateFlow<Int> = repository.totalMalas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalBeads: StateFlow<Int> = repository.totalBeads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private val _upcomingPanchangEvents = MutableStateFlow<List<DevotionalEvent>>(emptyList())
    val upcomingPanchangEvents: StateFlow<List<DevotionalEvent>> = _upcomingPanchangEvents.asStateFlow()

    private val _streaks = MutableStateFlow(Pair(0, 0)) // Current Streak, Longest Streak
    val streaks: StateFlow<Pair<Int, Int>> = _streaks.asStateFlow()

    private val _showMalaCompletionGlow = MutableStateFlow(false)
    val showMalaCompletionGlow: StateFlow<Boolean> = _showMalaCompletionGlow.asStateFlow()

    private val _hapticTrigger = MutableSharedFlow<Unit>()
    val hapticTrigger: SharedFlow<Unit> = _hapticTrigger.asSharedFlow()

    private val _chimeTrigger = MutableSharedFlow<Unit>()
    val chimeTrigger: SharedFlow<Unit> = _chimeTrigger.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialData()
            refreshStreaks()
            refreshPanchangEvents()
        }
    }

    fun refreshPanchangEvents() {
        _upcomingPanchangEvents.value = repository.panchangRepository.getUpcomingEvents()
    }

    fun setTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun refreshStreaks() {
        viewModelScope.launch {
            _streaks.value = repository.calculateStreaks()
        }
    }

    fun selectMantra(mantraId: Int) {
        viewModelScope.launch {
            repository.selectMantra(mantraId)
        }
    }

    fun addCustomMantra(textEnglish: String, textHindi: String = "") {
        viewModelScope.launch {
            repository.addCustomMantra(textEnglish, textHindi)
        }
    }

    fun editCustomMantra(id: Int, textEnglish: String, textHindi: String = "") {
        viewModelScope.launch {
            repository.editCustomMantra(id, textEnglish, textHindi)
        }
    }

    fun deleteCustomMantra(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomMantra(id)
        }
    }

    fun toggleFavoriteMantra(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteMantra(id, isFavorite)
        }
    }

    fun updateDailyGoal(goalMalas: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(goalMalas)
        }
    }

    fun updateUserSettings(
        userName: String? = null,
        themeMode: String? = null,
        language: String? = null,
        hapticEnabled: Boolean? = null,
        audioChimeEnabled: Boolean? = null,
        reminderEnabled: Boolean? = null,
        reminderTime: String? = null
    ) {
        viewModelScope.launch {
            repository.updateUserSettings(
                userName = userName,
                themeMode = themeMode,
                language = language,
                hapticEnabled = hapticEnabled,
                audioChimeEnabled = audioChimeEnabled,
                reminderEnabled = reminderEnabled,
                reminderTime = reminderTime
            )
        }
    }

    private var activeMalaStartTime: Long = 0L

    fun onBeadTapped(context: Context) {
        viewModelScope.launch {
            val settings = userSettings.value ?: repository.getUserSettingsDirect() ?: UserSettings()
            if (settings.hapticFeedbackEnabled) {
                performHapticFeedback(context)
            }

            val currentBead = settings.currentBeadInIncompleteMala
            val now = System.currentTimeMillis()
            if (currentBead == 0 || activeMalaStartTime <= 0L) {
                activeMalaStartTime = now
            }

            val elapsedMillis = if (activeMalaStartTime > 0 && now >= activeMalaStartTime) {
                (now - activeMalaStartTime).coerceIn(5_000L, 7_200_000L)
            } else {
                60_000L
            }

            val malaCompleted = repository.incrementBead(elapsedMillis)
            if (malaCompleted) {
                activeMalaStartTime = System.currentTimeMillis()
                _showMalaCompletionGlow.value = true
                if (settings.audioChimeEnabled) {
                    _chimeTrigger.emit(Unit)
                    SoundHelper.playMalaCompleteTone()
                }
                if (settings.hapticFeedbackEnabled) {
                    performMalaCompletionVibration(context)
                }
                refreshStreaks()
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _showMalaCompletionGlow.value = false
                }
            }
        }
    }

    fun toggleAudioChime(context: Context? = null) {
        viewModelScope.launch {
            val settings = userSettings.value ?: repository.getUserSettingsDirect() ?: UserSettings()
            val newSetting = !settings.audioChimeEnabled
            repository.updateUserSettings(audioChimeEnabled = newSetting)
            if (newSetting) {
                SoundHelper.playConfirmationTone()
            }
        }
    }

    fun toggleHapticFeedback(context: Context? = null) {
        viewModelScope.launch {
            val settings = userSettings.value ?: repository.getUserSettingsDirect() ?: UserSettings()
            val newSetting = !settings.hapticFeedbackEnabled
            repository.updateUserSettings(hapticEnabled = newSetting)
            if (newSetting && context != null) {
                performHapticFeedback(context)
            }
        }
    }

    fun resetCurrentMala() {
        viewModelScope.launch {
            repository.resetCurrentMalaBeads()
        }
    }

    private fun performHapticFeedback(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun performMalaCompletionVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (vibrator.hasVibrator()) {
                val pattern = longArrayOf(0, 180, 120, 220, 120, 400)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleReminder(enabled: Boolean, context: Context) {
        updateUserSettings(reminderEnabled = enabled)
        if (enabled) {
            val time = userSettings.value?.reminderTime ?: "20:00"
            val parts = time.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
            val min = parts.getOrNull(1)?.toIntOrNull() ?: 0
            ReminderManager.scheduleDailyReminder(context, hour, min)
        } else {
            ReminderManager.cancelReminder(context)
        }
    }

    fun setReminderTime(hour: Int, minute: Int, context: Context) {
        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        updateUserSettings(reminderTime = timeStr, reminderEnabled = true)
        ReminderManager.scheduleDailyReminder(context, hour, minute)
    }

    fun testReminderNotification(context: Context, title: String? = null, body: String? = null) {
        ReminderManager.triggerTestNotification(context, title, body)
    }

    fun exportDataAsCSV(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sessions = repository.allSessions.first()
                val csvBuilder = StringBuilder()
                csvBuilder.append("Session ID,Mantra,Date,Start Time,End Time,Total Beads,Total Malas\n")

                val sdfDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())

                sessions.forEach { s ->
                    val dateStr = sdfDate.format(Date(s.endTimestamp))
                    val startTimeStr = sdfTime.format(Date(s.startTimestamp))
                    val endTimeStr = sdfTime.format(Date(s.endTimestamp))
                    csvBuilder.append("${s.id},\"${s.mantraNameHindi}\",$dateStr,$startTimeStr,$endTimeStr,${s.totalBeadsInSession},${s.totalMalasInSession}\n")
                }

                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                val file = File(exportDir, "Jaap_History_${System.currentTimeMillis()}.csv")
                file.writeText(csvBuilder.toString())

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                withContext(Dispatchers.Main) {
                    try {
                        val chooser = Intent.createChooser(shareIntent, "Export Jaap History").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(chooser)
                        Toast.makeText(context, "CSV exported successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "No app available to handle CSV export", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to export CSV: ${e.localizedMessage ?: "Error"}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun createSankalp(
        name: String,
        mantraText: String,
        durationDays: Int,
        dailyGoalMalas: Int,
        startDateString: String,
        notes: String
    ) {
        viewModelScope.launch {
            val targetTotal = durationDays * dailyGoalMalas
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startCal = Calendar.getInstance()
            try {
                val parsed = sdf.parse(startDateString)
                if (parsed != null) startCal.time = parsed
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val endCal = startCal.clone() as Calendar
            endCal.add(Calendar.DAY_OF_YEAR, durationDays)
            val endDateString = sdf.format(endCal.time)

            val newSankalp = Sankalp(
                name = name,
                mantraText = mantraText,
                targetMalasTotal = targetTotal,
                dailyGoalMalas = dailyGoalMalas,
                durationDays = durationDays,
                startDateString = startDateString,
                endDateString = endDateString,
                completedMalas = 0,
                completedBeads = 0,
                status = "ACTIVE",
                notes = notes
            )
            repository.createSankalp(newSankalp)
        }
    }

    fun cancelSankalp(sankalp: Sankalp) {
        viewModelScope.launch {
            repository.updateSankalp(sankalp.copy(status = "CANCELLED"))
        }
    }

    fun deleteSankalp(id: Int) {
        viewModelScope.launch {
            repository.deleteSankalp(id)
        }
    }

    fun saveUserProfileAndCompleteOnboarding(
        name: String,
        dailyGoalMalas: Int,
        selectedMantraId: Int,
        morningReminderEnabled: Boolean,
        morningReminderTime: String,
        eveningReminderEnabled: Boolean,
        eveningReminderTime: String,
        authMethod: String = "GUEST",
        email: String = "",
        avatarId: Int = 1,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val current = userSettings.value ?: repository.getUserSettingsDirect() ?: UserSettings()
                val trimmedName = name.trim()
                val finalName = if (trimmedName.isNotBlank()) trimmedName else "Devotee"
                val initial = if (finalName.isNotBlank()) finalName.take(1).uppercase(Locale.getDefault()) else "D"

                val updated = current.copy(
                    userName = finalName,
                    userInitial = initial,
                    avatarId = avatarId,
                    dailyGoalMalas = dailyGoalMalas,
                    selectedMantraId = selectedMantraId,
                    morningReminderEnabled = morningReminderEnabled,
                    morningReminderTime = morningReminderTime,
                    eveningReminderEnabled = eveningReminderEnabled,
                    eveningReminderTime = eveningReminderTime,
                    authMethod = authMethod,
                    userEmail = email,
                    isOnboardingCompleted = true
                )
                repository.saveFullUserSettings(updated)
                repository.selectMantra(selectedMantraId)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.localizedMessage ?: "Failed to save profile settings")
                }
            }
        }
    }

    fun updateUserAvatar(avatarId: Int) {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettings()
            repository.saveFullUserSettings(current.copy(avatarId = avatarId))
        }
    }

    fun updateUserNameAndAvatar(name: String, avatarId: Int) {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettings()
            val trimmedName = name.trim()
            val finalName = if (trimmedName.isNotBlank()) trimmedName else "Devotee"
            val initial = if (finalName.isNotBlank()) finalName.take(1).uppercase() else "D"
            repository.saveFullUserSettings(
                current.copy(
                    userName = finalName,
                    userInitial = initial,
                    avatarId = avatarId
                )
            )
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return repository.authRepository.getCurrentFirebaseUser()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.authRepository.isUserLoggedIn()
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        onLoggedIn: () -> Unit = {},
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.authRepository.signUpWithEmail(email, password)
            result.onSuccess {
                refreshStreaks()
                onLoggedIn()
            }.onFailure { e ->
                onError(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun signInWithEmail(
        email: String,
        password: String,
        onLoggedIn: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.authRepository.signInWithEmail(email, password)
            result.onSuccess {
                refreshStreaks()
                onLoggedIn()
            }.onFailure { e ->
                onError(e.localizedMessage ?: "Sign in failed")
            }
        }
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.authRepository.sendPasswordReset(email)
            result.onSuccess { onSuccess() }.onFailure { onError(it.localizedMessage ?: "Failed to send reset link") }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val current = userSettings.value ?: repository.getUserSettingsDirect() ?: UserSettings()
            val updated = current.copy(
                authMethod = "GUEST",
                userEmail = ""
            )
            repository.saveFullUserSettings(updated)
            repository.authRepository.setGuestState()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun logoutUser(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.logoutUser()
            result.onSuccess {
                refreshStreaks()
                onSuccess()
            }.onFailure { error ->
                onError(error.localizedMessage ?: "Failed to log out safely.")
            }
        }
    }

    fun resetGuestData(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.resetGuestData()
            refreshStreaks()
            onSuccess()
        }
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.authRepository.deleteAccount()
            result.onSuccess { onSuccess() }.onFailure { onError(it.localizedMessage ?: "Failed to delete account") }
        }
    }

    fun resetAuthState() {
        repository.authRepository.resetAuthState()
    }

    fun completeOnboardingQuickly(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val current = userSettings.value ?: repository.getUserSettingsDirect() ?: UserSettings()
            repository.saveFullUserSettings(current.copy(isOnboardingCompleted = true))
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettings()
            repository.saveFullUserSettings(current.copy(isOnboardingCompleted = false))
        }
    }

    override fun onCleared() {
        super.onCleared()
        SoundHelper.release()
    }
}
