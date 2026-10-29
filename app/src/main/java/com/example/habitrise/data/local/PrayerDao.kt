package com.example.habitrise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habitrise.data.model.PrayerRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_records WHERE date = :date LIMIT 1")
    fun getPrayerRecordForDate(date: String): Flow<PrayerRecord?>

    @Query("SELECT * FROM prayer_records WHERE date = :date LIMIT 1")
    suspend fun getPrayerRecordForDateDirect(date: String): PrayerRecord?

    @Query("SELECT * FROM prayer_records ORDER BY date DESC")
    fun getAllPrayerRecords(): Flow<List<PrayerRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: PrayerRecord): Long

    @Query("DELETE FROM prayer_records")
    suspend fun clearAllPrayerRecords()
}
