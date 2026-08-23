package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mantraId: Int,
    val mantraNameHindi: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val totalBeadsInSession: Int,
    val totalMalasInSession: Int,
    val dateString: String // YYYY-MM-DD
)
