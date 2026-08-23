package com.example.data.firebase

import android.util.Log
import com.example.data.entity.DailyProgress
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import com.example.data.entity.UserSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseSyncManager {

    private val auth: FirebaseAuth?
        get() = FirebaseConfig.getAuth()

    private val firestore: FirebaseFirestore?
        get() = FirebaseConfig.getFirestore()

    fun getAuthenticatedUid(): String? {
        return auth?.currentUser?.uid
    }

    suspend fun syncUserProfile(
        settings: UserSettings,
        totalBeads: Int = 0,
        totalMalas: Int = 0,
        currentStreak: Int = 0,
        longestStreak: Int = 0
    ) {
        val safeFirestore = firestore ?: throw Exception("Firestore is not initialized")
        val uid = getAuthenticatedUid() ?: throw Exception("No authenticated user found for cloud sync")
        val userData = hashMapOf(
            "uid" to uid,
            "userName" to settings.userName,
            "userEmail" to settings.userEmail,
            "userInitial" to settings.userInitial,
            "avatarId" to settings.avatarId,
            "authMethod" to settings.authMethod,
            "dailyGoalMalas" to settings.dailyGoalMalas,
            "selectedMantraId" to settings.selectedMantraId,
            "totalBeads" to totalBeads,
            "totalMalas" to totalMalas,
            "currentStreak" to currentStreak,
            "longestStreak" to longestStreak,
            "morningReminderEnabled" to settings.morningReminderEnabled,
            "morningReminderTime" to settings.morningReminderTime,
            "eveningReminderEnabled" to settings.eveningReminderEnabled,
            "eveningReminderTime" to settings.eveningReminderTime,
            "isOnboardingCompleted" to settings.isOnboardingCompleted,
            "lastSyncedAt" to System.currentTimeMillis()
        )

        try {
            safeFirestore.collection("users").document(uid)
                .set(userData, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncManager", "Synced user profile for $uid to Firebase Firestore!")
        } catch (e: Exception) {
            Log.w("FirebaseSyncManager", "Error syncing user profile: ${e.message}")
            throw e
        }
    }

    suspend fun syncDailyProgress(dailyProgress: DailyProgress, totalBeadsOverall: Int = 0, totalMalasOverall: Int = 0) {
        val safeFirestore = firestore ?: throw Exception("Firestore is not initialized")
        val uid = getAuthenticatedUid() ?: throw Exception("No authenticated user found for cloud sync")
        val progressData = hashMapOf(
            "dateString" to dailyProgress.dateString,
            "totalBeadsCompleted" to dailyProgress.totalBeadsCompleted,
            "totalMalasCompleted" to dailyProgress.totalMalasCompleted,
            "dailyGoalMalas" to dailyProgress.dailyGoalMalas,
            "goalMet" to dailyProgress.goalMet,
            "lastUpdated" to System.currentTimeMillis()
        )

        try {
            safeFirestore.collection("users").document(uid)
                .collection("dailyProgress").document(dailyProgress.dateString)
                .set(progressData, SetOptions.merge())
                .await()

            val summaryData = hashMapOf<String, Any>(
                "totalBeads" to totalBeadsOverall,
                "totalMalas" to totalMalasOverall,
                "lastActiveDate" to dailyProgress.dateString,
                "lastSyncedAt" to System.currentTimeMillis()
            )
            safeFirestore.collection("users").document(uid)
                .set(summaryData, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncManager", "Synced daily progress for date ${dailyProgress.dateString}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncManager", "Error syncing daily progress: ${e.message}")
            throw e
        }
    }

    suspend fun syncSession(session: Session) {
        val safeFirestore = firestore ?: throw Exception("Firestore is not initialized")
        val uid = getAuthenticatedUid() ?: throw Exception("No authenticated user found for cloud sync")
        val sessionData = hashMapOf(
            "id" to session.id,
            "mantraId" to session.mantraId,
            "mantraNameHindi" to session.mantraNameHindi,
            "startTimestamp" to session.startTimestamp,
            "endTimestamp" to session.endTimestamp,
            "totalBeadsInSession" to session.totalBeadsInSession,
            "totalMalasInSession" to session.totalMalasInSession,
            "dateString" to session.dateString
        )

        try {
            val docId = if (session.id > 0) session.id.toString() else System.currentTimeMillis().toString()
            safeFirestore.collection("users").document(uid)
                .collection("sessions").document(docId)
                .set(sessionData, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncManager", "Synced session $docId to Firebase Firestore!")
        } catch (e: Exception) {
            Log.w("FirebaseSyncManager", "Error syncing session: ${e.message}")
            throw e
        }
    }

    suspend fun syncSankalp(sankalp: Sankalp) {
        val safeFirestore = firestore ?: throw Exception("Firestore is not initialized")
        val uid = getAuthenticatedUid() ?: throw Exception("No authenticated user found for cloud sync")
        val sankalpData = hashMapOf(
            "id" to sankalp.id,
            "name" to sankalp.name,
            "mantraText" to sankalp.mantraText,
            "targetMalasTotal" to sankalp.targetMalasTotal,
            "dailyGoalMalas" to sankalp.dailyGoalMalas,
            "durationDays" to sankalp.durationDays,
            "startDateString" to sankalp.startDateString,
            "endDateString" to sankalp.endDateString,
            "completedMalas" to sankalp.completedMalas,
            "completedBeads" to sankalp.completedBeads,
            "status" to sankalp.status,
            "notes" to sankalp.notes,
            "lastUpdated" to System.currentTimeMillis()
        )

        try {
            val docId = if (sankalp.id > 0) sankalp.id.toString() else System.currentTimeMillis().toString()
            safeFirestore.collection("users").document(uid)
                .collection("sankalps").document(docId)
                .set(sankalpData, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncManager", "Synced sankalp $docId to Firebase Firestore!")
        } catch (e: Exception) {
            Log.w("FirebaseSyncManager", "Error syncing sankalp: ${e.message}")
            throw e
        }
    }
}
