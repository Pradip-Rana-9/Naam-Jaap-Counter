package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_progress")
data class DailyProgress(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val totalMalasCompleted: Int = 0,
    val totalBeadsCompleted: Int = 0,
    val dailyGoalMalas: Int = 10,
    val goalMet: Boolean = false
)
