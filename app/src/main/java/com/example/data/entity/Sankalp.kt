package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sankalps")
data class Sankalp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val mantraText: String,
    val targetMalasTotal: Int,
    val dailyGoalMalas: Int,
    val durationDays: Int,
    val startDateString: String,
    val endDateString: String,
    val completedMalas: Int = 0,
    val completedBeads: Int = 0,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, CANCELLED, EXPIRED
    val notes: String = "",
    val createdTimestamp: Long = System.currentTimeMillis()
)
