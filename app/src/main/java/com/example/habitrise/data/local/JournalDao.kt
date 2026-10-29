package com.example.habitrise.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitrise.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    fun getJournalEntryForDate(date: String): Flow<JournalEntry?>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getJournalEntryForDateDirect(date: String): JournalEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateJournal(entry: JournalEntry): Long

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE date = :date")
    suspend fun deleteJournalEntryByDate(date: String)

    @Query("DELETE FROM journal_entries")
    suspend fun clearAllJournalEntries()
}
