package com.example.data.auth

import android.content.Context
import android.util.Log
import com.example.data.dao.JaapDao
import com.example.data.entity.DailyProgress
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import com.example.data.entity.UserSettings
import com.example.data.firebase.FirebaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(
        val user: FirebaseUser,
        val provider: String = "EMAIL"
    ) : AuthState()
    object Guest : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository(
    private val dao: JaapDao,
    context: Context? = null
) {

    private val appContext = context?.applicationContext

    private fun getFirebaseAuth(ctx: Context? = null): FirebaseAuth? {
        val targetContext = ctx?.applicationContext ?: appContext
        return FirebaseConfig.getAuth(targetContext)
    }

    private fun getFirestore(ctx: Context? = null): FirebaseFirestore? {
        val targetContext = ctx?.applicationContext ?: appContext
        return FirebaseConfig.getFirestore(targetContext)
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        try {
            if (appContext != null) {
                FirebaseConfig.initialize(appContext)
            }
            checkCurrentAuthStatus()
        } catch (e: Throwable) {
            Log.e("AuthRepository", "Init checkCurrentAuthStatus failed: ${e.message}")
        }
    }

    fun checkCurrentAuthStatus() {
        try {
            val auth = getFirebaseAuth()
            if (auth == null) {
                _authState.value = AuthState.Idle
                return
            }
            val user = auth.currentUser
            if (user != null) {
                _authState.value = AuthState.Authenticated(
                    user = user,
                    provider = "EMAIL"
                )
            } else {
                _authState.value = AuthState.Idle
            }
        } catch (e: Throwable) {
            Log.e("AuthRepository", "checkCurrentAuthStatus error: ${e.message}")
            _authState.value = AuthState.Idle
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return try {
            getFirebaseAuth()?.currentUser
        } catch (e: Throwable) {
            null
        }
    }

    fun getCurrentUid(): String? {
        return try {
            getFirebaseAuth()?.currentUser?.uid
        } catch (e: Throwable) {
            null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return try {
            val auth = getFirebaseAuth() ?: return false
            auth.currentUser != null
        } catch (e: Throwable) {
            false
        }
    }

    fun setGuestState() {
        _authState.value = AuthState.Guest
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val auth = getFirebaseAuth() ?: throw Exception("Authentication service could not be initialized. Please check network and try again.")
            _authState.value = AuthState.Loading
            val trimmedEmail = email.trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                val errorMsg = "Please enter a valid email address."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }
            if (password.length < 6) {
                val errorMsg = "Password must be at least 6 characters long."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val user = authResult.user ?: throw Exception("Registration failed: User could not be created.")

            // Initialize user doc in Firestore (no email verification / OTP required)
            syncOrRestoreUserData(user, "EMAIL")

            _authState.value = AuthState.Authenticated(
                user = user,
                provider = "EMAIL"
            )
            Result.success(user)
        } catch (e: Exception) {
            val msg = parseFirebaseAuthException(e)
            _authState.value = AuthState.Error(msg)
            Result.failure(Exception(msg))
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val auth = getFirebaseAuth() ?: throw Exception("Authentication service could not be initialized. Please check network and try again.")
            _authState.value = AuthState.Loading
            val trimmedEmail = email.trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                val errorMsg = "Please enter a valid email address."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }
            if (password.isBlank()) {
                val errorMsg = "Please enter your password."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val authResult = auth.signInWithEmailAndPassword(trimmedEmail, password).await()
            val user = authResult.user ?: throw Exception("Sign in failed: User not found.")

            // Restore/sync user data with Firestore
            syncOrRestoreUserData(user, "EMAIL")

            _authState.value = AuthState.Authenticated(
                user = user,
                provider = "EMAIL"
            )
            Result.success(user)
        } catch (e: Exception) {
            val msg = parseFirebaseAuthException(e)
            _authState.value = AuthState.Error(msg)
            Result.failure(Exception(msg))
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val auth = getFirebaseAuth() ?: throw Exception("Authentication service not available.")
            val trimmed = email.trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                return@withContext Result.failure(Exception("Please enter a valid email address."))
            }
            auth.sendPasswordResetEmail(trimmed).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = parseFirebaseAuthException(e)
            Result.failure(Exception(msg))
        }
    }

    suspend fun signOutUser() = withContext(Dispatchers.IO) {
        try {
            getFirebaseAuth()?.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign out error: ${e.message}")
        }
        // Clear local session/progress/sankalp cache on logout
        try {
            dao.clearAllSessions()
            dao.clearAllDailyProgress()
            dao.clearAllSankalps()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error clearing local tables: ${e.message}")
        }
        val defaultGuestSettings = UserSettings(
            id = 1,
            userName = "Devotee",
            userInitial = "D",
            avatarId = 1,
            dailyGoalMalas = 10,
            currentBeadInIncompleteMala = 0,
            currentMalaNumber = 1,
            currentSessionMalasCount = 0,
            authMethod = "GUEST",
            userEmail = "",
            isOnboardingCompleted = false,
            themeMode = "DARK",
            language = "ENGLISH",
            hapticFeedbackEnabled = false,
            audioChimeEnabled = false,
            selectedMantraId = 1
        )
        dao.insertOrUpdateUserSettings(defaultGuestSettings)
        _authState.value = AuthState.Idle
    }

    suspend fun resetGuestData() = withContext(Dispatchers.IO) {
        try {
            dao.clearAllSessions()
            dao.clearAllDailyProgress()
            dao.clearAllSankalps()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error clearing tables in resetGuestData: ${e.message}")
        }
        val defaultGuestSettings = UserSettings(
            id = 1,
            userName = "Devotee",
            userInitial = "D",
            avatarId = 1,
            dailyGoalMalas = 10,
            currentBeadInIncompleteMala = 0,
            currentMalaNumber = 1,
            currentSessionMalasCount = 0,
            authMethod = "GUEST",
            userEmail = "",
            isOnboardingCompleted = false,
            themeMode = "DARK",
            language = "ENGLISH",
            hapticFeedbackEnabled = false,
            audioChimeEnabled = false,
            selectedMantraId = 1
        )
        dao.insertOrUpdateUserSettings(defaultGuestSettings)
        _authState.value = AuthState.Idle
    }

    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val auth = getFirebaseAuth() ?: throw Exception("Authentication service not available.")
            val user = auth.currentUser ?: throw Exception("No user logged in.")
            val uid = user.uid

            // Remove Firestore user doc if permitted
            try {
                getFirestore()?.collection("users")?.document(uid)?.delete()?.await()
            } catch (e: Exception) {
                Log.w("AuthRepository", "Failed to delete Firestore document: ${e.message}")
            }

            user.delete().await()

            dao.clearAllSessions()
            dao.clearAllDailyProgress()
            dao.clearAllSankalps()

            val defaultGuestSettings = UserSettings(
                id = 1,
                userName = "Devotee",
                userInitial = "D",
                avatarId = 1,
                dailyGoalMalas = 10,
                currentBeadInIncompleteMala = 0,
                currentMalaNumber = 1,
                currentSessionMalasCount = 0,
                authMethod = "GUEST",
                userEmail = "",
                isOnboardingCompleted = false,
                themeMode = "DARK",
                language = "ENGLISH",
                hapticFeedbackEnabled = false,
                audioChimeEnabled = false,
                selectedMantraId = 1
            )
            dao.insertOrUpdateUserSettings(defaultGuestSettings)
            _authState.value = AuthState.Idle
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = parseFirebaseAuthException(e)
            Result.failure(Exception(msg))
        }
    }

    /**
     * Restores user profile and chant data from Firestore or creates initial user doc using Firebase UID.
     */
    suspend fun syncOrRestoreUserData(user: FirebaseUser, authMethod: String = "EMAIL", context: Context? = null) = withContext(Dispatchers.IO) {
        val uid = user.uid
        val email = user.email ?: ""
        val displayName = user.displayName ?: ""

        try {
            val firestore = getFirestore(context) ?: return@withContext
            val docRef = firestore.collection("users").document(uid)
            val snapshot = docRef.get().await()

            val localSettings = dao.getUserSettingsDirect() ?: UserSettings()

            if (snapshot.exists()) {
                // User has existing data in Cloud Firestore -> Clear local cache and restore from Firestore
                try {
                    dao.clearAllSessions()
                    dao.clearAllDailyProgress()
                    dao.clearAllSankalps()
                } catch (e: Exception) {
                    Log.e("AuthRepository", "Error clearing local cache before restore: ${e.message}")
                }

                val remoteName = snapshot.getString("userName")
                val remoteAvatarId = snapshot.getLong("avatarId")?.toInt() ?: localSettings.avatarId
                val remoteDailyGoal = snapshot.getLong("dailyGoalMalas")?.toInt() ?: localSettings.dailyGoalMalas
                val remoteMantraId = snapshot.getLong("selectedMantraId")?.toInt() ?: localSettings.selectedMantraId
                val remoteThemeMode = snapshot.getString("themeMode") ?: localSettings.themeMode
                val remoteLanguage = snapshot.getString("language") ?: localSettings.language
                val remoteMorningReminder = snapshot.getBoolean("morningReminderEnabled") ?: localSettings.morningReminderEnabled
                val remoteMorningTime = snapshot.getString("morningReminderTime") ?: localSettings.morningReminderTime
                val remoteEveningReminder = snapshot.getBoolean("eveningReminderEnabled") ?: localSettings.eveningReminderEnabled
                val remoteEveningTime = snapshot.getString("eveningReminderTime") ?: localSettings.eveningReminderTime
                val remoteIsOnboardingCompleted = snapshot.getBoolean("isOnboardingCompleted") ?: true

                val finalName = if (!remoteName.isNullOrBlank()) {
                    remoteName
                } else if (displayName.isNotBlank()) {
                    displayName
                } else if (email.contains("@")) {
                    email.substringBefore("@").replaceFirstChar { it.uppercase() }
                } else {
                    "Devotee"
                }

                val initial = if (finalName.isNotBlank()) finalName.take(1).uppercase() else "D"

                val updatedSettings = localSettings.copy(
                    userName = finalName,
                    userInitial = initial,
                    avatarId = remoteAvatarId,
                    dailyGoalMalas = remoteDailyGoal,
                    selectedMantraId = remoteMantraId,
                    themeMode = remoteThemeMode,
                    language = remoteLanguage,
                    morningReminderEnabled = remoteMorningReminder,
                    morningReminderTime = remoteMorningTime,
                    eveningReminderEnabled = remoteEveningReminder,
                    eveningReminderTime = remoteEveningTime,
                    authMethod = authMethod,
                    userEmail = email,
                    isOnboardingCompleted = remoteIsOnboardingCompleted,
                    currentBeadInIncompleteMala = 0,
                    currentMalaNumber = 1,
                    currentSessionMalasCount = 0
                )
                dao.insertOrUpdateUserSettings(updatedSettings)

                // Restore Sankalps from subcollection
                try {
                    val sankalpsSnap = docRef.collection("sankalps").get().await()
                    for (doc in sankalpsSnap.documents) {
                        val sankalpId = doc.getLong("id")?.toInt() ?: 0
                        val name = doc.getString("name") ?: ""
                        val mantraText = doc.getString("mantraText") ?: ""
                        val targetMalas = doc.getLong("targetMalasTotal")?.toInt() ?: 0
                        val dailyGoal = doc.getLong("dailyGoalMalas")?.toInt() ?: 0
                        val durationDays = doc.getLong("durationDays")?.toInt() ?: 0
                        val startDate = doc.getString("startDateString") ?: ""
                        val endDate = doc.getString("endDateString") ?: ""
                        val completedMalas = doc.getLong("completedMalas")?.toInt() ?: 0
                        val completedBeads = doc.getLong("completedBeads")?.toInt() ?: 0
                        val status = doc.getString("status") ?: "ACTIVE"
                        val notes = doc.getString("notes") ?: ""

                        if (name.isNotBlank()) {
                            val restoredSankalp = Sankalp(
                                id = sankalpId,
                                name = name,
                                mantraText = mantraText,
                                targetMalasTotal = targetMalas,
                                dailyGoalMalas = dailyGoal,
                                durationDays = durationDays,
                                startDateString = startDate,
                                endDateString = endDate,
                                completedMalas = completedMalas,
                                completedBeads = completedBeads,
                                status = status,
                                notes = notes
                            )
                            dao.insertSankalp(restoredSankalp)
                        }
                    }
                } catch (e: Exception) {
                    Log.w("AuthRepository", "Error restoring subcollection sankalps: ${e.message}")
                }

                // Restore Daily Progress from subcollection
                try {
                    val progressSnap = docRef.collection("dailyProgress").get().await()
                    for (doc in progressSnap.documents) {
                        val dateStr = doc.getString("dateString") ?: doc.id
                        val totalBeads = doc.getLong("totalBeadsCompleted")?.toInt() ?: 0
                        val totalMalas = doc.getLong("totalMalasCompleted")?.toInt() ?: 0
                        val goal = doc.getLong("dailyGoalMalas")?.toInt() ?: remoteDailyGoal
                        val goalMet = doc.getBoolean("goalMet") ?: (totalMalas >= goal)

                        if (dateStr.isNotBlank()) {
                            val prog = DailyProgress(
                                dateString = dateStr,
                                totalBeadsCompleted = totalBeads,
                                totalMalasCompleted = totalMalas,
                                dailyGoalMalas = goal,
                                goalMet = goalMet
                            )
                            dao.upsertDailyProgress(prog)
                        }
                    }
                } catch (e: Exception) {
                    Log.w("AuthRepository", "Error restoring daily progress: ${e.message}")
                }

                // Restore Sessions from subcollection
                try {
                    val sessionsSnap = docRef.collection("sessions").get().await()
                    for (doc in sessionsSnap.documents) {
                        val sId = doc.getLong("id") ?: 0L
                        val mantraId = doc.getLong("mantraId")?.toInt() ?: 1
                        val mantraNameHindi = doc.getString("mantraNameHindi") ?: "राधे राधे"
                        val startTimestamp = doc.getLong("startTimestamp") ?: System.currentTimeMillis()
                        val endTimestamp = doc.getLong("endTimestamp") ?: System.currentTimeMillis()
                        val totalBeadsInSession = doc.getLong("totalBeadsInSession")?.toInt() ?: 108
                        val totalMalasInSession = doc.getLong("totalMalasInSession")?.toInt() ?: 1
                        val dateString = doc.getString("dateString") ?: ""

                        val restoredSession = Session(
                            id = sId,
                            mantraId = mantraId,
                            mantraNameHindi = mantraNameHindi,
                            startTimestamp = startTimestamp,
                            endTimestamp = endTimestamp,
                            totalBeadsInSession = totalBeadsInSession,
                            totalMalasInSession = totalMalasInSession,
                            dateString = dateString
                        )
                        dao.insertSession(restoredSession)
                    }
                } catch (e: Exception) {
                    Log.w("AuthRepository", "Error restoring sessions: ${e.message}")
                }
            } else {
                // Document does not exist in Firestore -> Create new user doc keyed by uid, MIGRATING existing local guest data
                val finalName = if (localSettings.userName.isNotBlank() && localSettings.userName != "Devotee") {
                    localSettings.userName
                } else if (displayName.isNotBlank()) {
                    displayName
                } else if (email.contains("@")) {
                    email.substringBefore("@").replaceFirstChar { it.uppercase() }
                } else {
                    "Devotee"
                }

                val initial = if (finalName.isNotBlank()) finalName.take(1).uppercase() else "D"

                val updatedSettings = localSettings.copy(
                    userName = finalName,
                    userInitial = initial,
                    authMethod = authMethod,
                    userEmail = email,
                    isOnboardingCompleted = localSettings.isOnboardingCompleted
                )
                dao.insertOrUpdateUserSettings(updatedSettings)

                val beads = dao.getTotalBeadsCompletedDirect() ?: 0
                val malas = dao.getTotalMalasCompletedDirect() ?: 0

                val newUserData = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "displayName" to finalName,
                    "userName" to finalName,
                    "userInitial" to initial,
                    "avatarId" to updatedSettings.avatarId,
                    "authProvider" to authMethod,
                    "dailyGoalMalas" to updatedSettings.dailyGoalMalas,
                    "selectedMantraId" to updatedSettings.selectedMantraId,
                    "totalBeads" to beads,
                    "totalMalas" to malas,
                    "morningReminderEnabled" to updatedSettings.morningReminderEnabled,
                    "morningReminderTime" to updatedSettings.morningReminderTime,
                    "eveningReminderEnabled" to updatedSettings.eveningReminderEnabled,
                    "eveningReminderTime" to updatedSettings.eveningReminderTime,
                    "isOnboardingCompleted" to updatedSettings.isOnboardingCompleted,
                    "themeMode" to updatedSettings.themeMode,
                    "language" to updatedSettings.language,
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                docRef.set(newUserData, SetOptions.merge()).await()

                // Migrate existing local sessions, daily progress, and sankalps to Firestore
                try {
                    val localSessions = dao.getAllSessionsDirect()
                    for (s in localSessions) {
                        val docId = if (s.id > 0) s.id.toString() else System.currentTimeMillis().toString()
                        docRef.collection("sessions").document(docId).set(
                            hashMapOf(
                                "id" to s.id,
                                "mantraId" to s.mantraId,
                                "mantraNameHindi" to s.mantraNameHindi,
                                "startTimestamp" to s.startTimestamp,
                                "endTimestamp" to s.endTimestamp,
                                "totalBeadsInSession" to s.totalBeadsInSession,
                                "totalMalasInSession" to s.totalMalasInSession,
                                "dateString" to s.dateString
                            ), SetOptions.merge()
                        ).await()
                    }

                    val localProgress = dao.getAllDailyProgressDirect()
                    for (dp in localProgress) {
                        docRef.collection("dailyProgress").document(dp.dateString).set(
                            hashMapOf(
                                "dateString" to dp.dateString,
                                "totalBeadsCompleted" to dp.totalBeadsCompleted,
                                "totalMalasCompleted" to dp.totalMalasCompleted,
                                "dailyGoalMalas" to dp.dailyGoalMalas,
                                "goalMet" to dp.goalMet,
                                "lastUpdated" to System.currentTimeMillis()
                            ), SetOptions.merge()
                        ).await()
                    }

                    val localSankalps = dao.getAllSankalpsDirect()
                    for (sk in localSankalps) {
                        val docId = if (sk.id > 0) sk.id.toString() else System.currentTimeMillis().toString()
                        docRef.collection("sankalps").document(docId).set(
                            hashMapOf(
                                "id" to sk.id,
                                "name" to sk.name,
                                "mantraText" to sk.mantraText,
                                "targetMalasTotal" to sk.targetMalasTotal,
                                "dailyGoalMalas" to sk.dailyGoalMalas,
                                "durationDays" to sk.durationDays,
                                "startDateString" to sk.startDateString,
                                "endDateString" to sk.endDateString,
                                "completedMalas" to sk.completedMalas,
                                "completedBeads" to sk.completedBeads,
                                "status" to sk.status,
                                "notes" to sk.notes,
                                "lastUpdated" to System.currentTimeMillis()
                            ), SetOptions.merge()
                        ).await()
                    }
                } catch (e: Exception) {
                    Log.w("AuthRepository", "Error migrating local data to new cloud account: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in syncOrRestoreUserData: ${e.message}", e)
        }
    }

    private fun parseFirebaseAuthException(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidUserException -> "No account found with this email. Please switch to the 'Register' tab to create an account."
            is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password. Please check your credentials and try again."
            is FirebaseAuthUserCollisionException -> "An account already exists with this email. Please switch to 'Sign In' to enter your password."
            is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use at least 6 characters."
            is FirebaseAuthException -> e.localizedMessage ?: "Authentication failed. Please try again."
            else -> e.localizedMessage ?: "An unexpected error occurred. Please try again."
        }
    }
}
