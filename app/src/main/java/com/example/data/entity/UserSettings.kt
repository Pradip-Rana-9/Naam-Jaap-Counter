package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Devotee",
    val userInitial: String = "D",
    val avatarId: Int = 1,
    val joinDateString: String = "02 Aug, 2026",
    val dailyGoalMalas: Int = 4,
    val hapticFeedbackEnabled: Boolean = false,
    val audioChimeEnabled: Boolean = false,
    val themeMode: String = "DARK", // DARK, LIGHT, SYSTEM
    val language: String = "ENGLISH", // ENGLISH, HINDI, HINGLISH
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "20:00", // "HH:mm" e.g., "20:00" for 8:00 PM
    val selectedMantraId: Int = 1,
    val currentBeadInIncompleteMala: Int = 0,
    val currentMalaNumber: Int = 1,
    val currentSessionMalasCount: Int = 0,
    val isOnboardingCompleted: Boolean = false,
    val authMethod: String = "GUEST", // "GOOGLE", "EMAIL", "GUEST"
    val userEmail: String = "",
    val morningReminderEnabled: Boolean = true,
    val morningReminderTime: String = "06:00 AM",
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "08:00 PM"
)
