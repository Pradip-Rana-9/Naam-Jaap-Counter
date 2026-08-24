package com.example.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.JaapDao
import com.example.data.entity.DailyProgress
import com.example.data.entity.Mantra
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import com.example.data.entity.UserSettings

private fun isColumnExists(db: SupportSQLiteDatabase, table: String, column: String): Boolean {
    return try {
        db.query("PRAGMA table_info($table)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex != -1) {
                while (cursor.moveToNext()) {
                    val existingCol = cursor.getString(nameIndex)
                    if (existingCol.equals(column, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        false
    } catch (e: Exception) {
        false
    }
}

private fun safeAddColumn(db: SupportSQLiteDatabase, table: String, column: String, typeWithDefault: String) {
    try {
        if (!isColumnExists(db, table, column)) {
            db.execSQL("ALTER TABLE $table ADD COLUMN $column $typeWithDefault")
        }
    } catch (e: Exception) {
        Log.d("AppDatabase", "Column $column already exists or could not be added: ${e.message}")
    }
}

private fun ensureAllTablesAndColumns(db: SupportSQLiteDatabase) {
    try {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS sankalps (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                mantraText TEXT NOT NULL,
                targetMalasTotal INTEGER NOT NULL,
                dailyGoalMalas INTEGER NOT NULL,
                durationDays INTEGER NOT NULL,
                startDateString TEXT NOT NULL,
                endDateString TEXT NOT NULL,
                completedMalas INTEGER NOT NULL DEFAULT 0,
                completedBeads INTEGER NOT NULL DEFAULT 0,
                status TEXT NOT NULL DEFAULT 'ACTIVE',
                notes TEXT NOT NULL DEFAULT '',
                createdTimestamp INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    } catch (e: Exception) {
        Log.e("AppDatabase", "Error ensuring sankalps table: ${e.message}")
    }

    safeAddColumn(db, "user_settings", "isOnboardingCompleted", "INTEGER NOT NULL DEFAULT 0")
    safeAddColumn(db, "user_settings", "authMethod", "TEXT NOT NULL DEFAULT 'GUEST'")
    safeAddColumn(db, "user_settings", "userEmail", "TEXT NOT NULL DEFAULT ''")
    safeAddColumn(db, "user_settings", "morningReminderEnabled", "INTEGER NOT NULL DEFAULT 1")
    safeAddColumn(db, "user_settings", "morningReminderTime", "TEXT NOT NULL DEFAULT '06:00 AM'")
    safeAddColumn(db, "user_settings", "eveningReminderEnabled", "INTEGER NOT NULL DEFAULT 1")
    safeAddColumn(db, "user_settings", "eveningReminderTime", "TEXT NOT NULL DEFAULT '08:00 PM'")
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        safeAddColumn(db, "user_settings", "isOnboardingCompleted", "INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        safeAddColumn(db, "user_settings", "authMethod", "TEXT NOT NULL DEFAULT 'GUEST'")
        safeAddColumn(db, "user_settings", "userEmail", "TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        safeAddColumn(db, "user_settings", "morningReminderEnabled", "INTEGER NOT NULL DEFAULT 1")
        safeAddColumn(db, "user_settings", "morningReminderTime", "TEXT NOT NULL DEFAULT '06:00 AM'")
        safeAddColumn(db, "user_settings", "eveningReminderEnabled", "INTEGER NOT NULL DEFAULT 1")
        safeAddColumn(db, "user_settings", "eveningReminderTime", "TEXT NOT NULL DEFAULT '08:00 PM'")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_1_7 = object : Migration(1, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_2_7 = object : Migration(2, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_3_7 = object : Migration(3, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_4_7 = object : Migration(4, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

val MIGRATION_5_7 = object : Migration(5, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureAllTablesAndColumns(db)
    }
}

@Database(
    entities = [Mantra::class, Session::class, DailyProgress::class, UserSettings::class, Sankalp::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun jaapDao(): JaapDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "jaap_counter_db"
                    )
                        .addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3,
                            MIGRATION_3_4,
                            MIGRATION_4_5,
                            MIGRATION_5_6,
                            MIGRATION_6_7,
                            MIGRATION_1_7,
                            MIGRATION_2_7,
                            MIGRATION_3_7,
                            MIGRATION_4_7,
                            MIGRATION_5_7
                        )
                        .fallbackToDestructiveMigrationOnDowngrade()
                        .addCallback(AppDatabaseCallback())
                        .build()
                } catch (e: Throwable) {
                    Log.e("AppDatabase", "Error initializing database with explicit migrations: ${e.message}")
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "jaap_counter_db"
                    )
                        .addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3,
                            MIGRATION_3_4,
                            MIGRATION_4_5,
                            MIGRATION_5_6,
                            MIGRATION_6_7,
                            MIGRATION_1_7,
                            MIGRATION_2_7,
                            MIGRATION_3_7,
                            MIGRATION_4_7,
                            MIGRATION_5_7
                        )
                        .fallbackToDestructiveMigrationOnDowngrade()
                        .addCallback(AppDatabaseCallback())
                        .build()
                }
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                try {
                    db.execSQL("INSERT OR IGNORE INTO mantras (id, textHindi, textEnglish, description, isDefault, isSelected, isFavorite, isCustom, sortOrder) VALUES (1, 'राधे राधे', 'Radhe Radhe', 'Divine love and supreme devotion to Sri Radha Rani', 1, 1, 1, 0, 1)")
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Error inserting default mantra on create: ${e.message}")
                }
            }
        }

        suspend fun populateInitialData(dao: JaapDao) {
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
                        currentSessionMalasCount = 0,
                        isOnboardingCompleted = false,
                        authMethod = "GUEST",
                        userEmail = ""
                    )
                    dao.insertOrUpdateUserSettings(initialUserSettings)
                }
            } catch (e: Exception) {
                Log.e("AppDatabase", "Error populating initial data: ${e.message}")
            }
        }
    }
}
