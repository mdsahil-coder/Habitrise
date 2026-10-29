package com.example.habitrise.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitrise.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {
    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    suspend fun getCompletionsForDateDirect(date: String): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date DESC")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getCompletionsForHabitDirect(habitId: Long): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions ORDER BY date DESC")
    fun getAllCompletions(): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions ORDER BY date DESC")
    suspend fun getAllCompletionsDirect(): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions WHERE date BETWEEN :startDate AND :endDate")
    fun getCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCompletionsBetweenDirect(startDate: String, endDate: String): List<HabitCompletion>

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId")
    fun getTotalCompletionsForHabit(habitId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM habit_completions")
    fun getTotalCompletionsCount(): Flow<Int>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCompletion(habitId: Long, date: String): HabitCompletion?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: Long, date: String): Int

    @Delete
    suspend fun deleteCompletionRecord(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsByHabitId(habitId: Long)

    @Query("DELETE FROM habit_completions")
    suspend fun clearAllCompletions()
}
