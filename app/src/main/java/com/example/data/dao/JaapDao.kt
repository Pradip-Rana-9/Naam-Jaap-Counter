package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.DailyProgress
import com.example.data.entity.Mantra
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import com.example.data.entity.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface JaapDao {

    // Mantras
    @Query("SELECT * FROM mantras ORDER BY isFavorite DESC, sortOrder ASC")
    fun getAllMantras(): Flow<List<Mantra>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMantra(mantra: Mantra): Long

    @Update
    suspend fun updateMantra(mantra: Mantra)

    @Query("DELETE FROM mantras WHERE id = :id AND isDefault = 0")
    suspend fun deleteCustomMantra(id: Int)

    @Query("UPDATE mantras SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMantras(mantras: List<Mantra>)

    @Query("SELECT * FROM mantras WHERE isSelected = 1 LIMIT 1")
    fun getSelectedMantra(): Flow<Mantra?>

    @Query("SELECT * FROM mantras WHERE id = :id LIMIT 1")
    suspend fun getMantraById(id: Int): Mantra?

    @Query("UPDATE mantras SET isSelected = (id = :selectedId)")
    suspend fun setSelectedMantraId(selectedId: Int)

    // Sessions
    @Query("SELECT * FROM sessions ORDER BY endTimestamp DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE dateString = :dateString ORDER BY endTimestamp DESC")
    fun getSessionsForDate(dateString: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions ORDER BY endTimestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<Session>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    // Daily Progress
    @Query("SELECT * FROM daily_progress WHERE dateString = :dateString LIMIT 1")
    fun getDailyProgress(dateString: String): Flow<DailyProgress?>

    @Query("SELECT * FROM daily_progress WHERE dateString = :dateString LIMIT 1")
    suspend fun getDailyProgressDirect(dateString: String): DailyProgress?

    @Query("SELECT * FROM daily_progress ORDER BY dateString ASC")
    fun getAllDailyProgress(): Flow<List<DailyProgress>>

    @Query("SELECT * FROM daily_progress ORDER BY dateString ASC")
    suspend fun getAllDailyProgressDirect(): List<DailyProgress>

    @Query("SELECT * FROM daily_progress WHERE dateString IN (:dateStrings)")
    fun getWeeklyProgress(dateStrings: List<String>): Flow<List<DailyProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyProgress(dailyProgress: DailyProgress)

    // Total Aggregates
    @Query("SELECT SUM(totalMalasCompleted) FROM daily_progress")
    fun getTotalMalasCompleted(): Flow<Int?>

    @Query("SELECT SUM(totalMalasCompleted) FROM daily_progress")
    suspend fun getTotalMalasCompletedDirect(): Int?

    @Query("SELECT SUM(totalBeadsCompleted) FROM daily_progress")
    fun getTotalBeadsCompleted(): Flow<Int?>

    @Query("SELECT SUM(totalBeadsCompleted) FROM daily_progress")
    suspend fun getTotalBeadsCompletedDirect(): Int?

    // User Settings
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getUserSettingsDirect(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserSettings(userSettings: UserSettings)

    // Sankalps
    @Query("SELECT * FROM sankalps ORDER BY createdTimestamp DESC")
    fun getAllSankalps(): Flow<List<Sankalp>>

    @Query("SELECT * FROM sankalps WHERE status = 'ACTIVE' ORDER BY createdTimestamp DESC")
    fun getActiveSankalps(): Flow<List<Sankalp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSankalp(sankalp: Sankalp): Long

    @Update
    suspend fun updateSankalp(sankalp: Sankalp)

    @Query("DELETE FROM sankalps WHERE id = :id")
    suspend fun deleteSankalp(id: Int)

    @Query("SELECT * FROM sankalps")
    suspend fun getAllSankalpsDirect(): List<Sankalp>

    @Query("SELECT * FROM sessions")
    suspend fun getAllSessionsDirect(): List<Session>

    @Query("DELETE FROM sessions")
    suspend fun clearAllSessions()

    @Query("DELETE FROM daily_progress")
    suspend fun clearAllDailyProgress()

    @Query("DELETE FROM sankalps")
    suspend fun clearAllSankalps()
}
