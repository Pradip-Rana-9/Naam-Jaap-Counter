package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mantras")
data class Mantra(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val textHindi: String = "राधे राधे",
    val textEnglish: String,
    val description: String = "Divine love and supreme devotion",
    val isDefault: Boolean = false,
    val isSelected: Boolean = false,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)
