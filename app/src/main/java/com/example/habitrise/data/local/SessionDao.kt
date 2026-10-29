package com.example.habitrise.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitrise.data.model.ExerciseSession
import com.example.habitrise.data.model.FinanceSession
import com.example.habitrise.data.model.ReadingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    // Reading Sessions
    @Query("SELECT * FROM reading_sessions ORDER BY timestamp DESC")
    fun getAllReadingSessions(): Flow<List<ReadingSession>>

    @Query("SELECT * FROM reading_sessions WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getReadingSessionsForHabit(habitId: Long): Flow<List<ReadingSession>>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM reading_sessions")
    fun getTotalReadingMinutes(): Flow<Int>

    @Query("SELECT COALESCE(SUM(pagesRead), 0) FROM reading_sessions")
    fun getTotalPagesRead(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingSession(session: ReadingSession): Long

    @Delete
    suspend fun deleteReadingSession(session: ReadingSession)

    // Exercise Sessions
    @Query("SELECT * FROM exercise_sessions ORDER BY timestamp DESC")
    fun getAllExerciseSessions(): Flow<List<ExerciseSession>>

    @Query("SELECT * FROM exercise_sessions WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getExerciseSessionsForHabit(habitId: Long): Flow<List<ExerciseSession>>

    @Query("SELECT COUNT(*) FROM exercise_sessions")
    fun getTotalExerciseSessionsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM exercise_sessions")
    fun getTotalExerciseMinutes(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSession(session: ExerciseSession): Long

    @Delete
    suspend fun deleteExerciseSession(session: ExerciseSession)

    // Finance Sessions
    @Query("SELECT * FROM finance_sessions ORDER BY timestamp DESC")
    fun getAllFinanceSessions(): Flow<List<FinanceSession>>

    @Query("SELECT * FROM finance_sessions WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getFinanceSessionsForHabit(habitId: Long): Flow<List<FinanceSession>>

    @Query("SELECT COUNT(*) FROM finance_sessions")
    fun getTotalFinanceSessionsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM finance_sessions")
    fun getTotalFinanceMinutes(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceSession(session: FinanceSession): Long

    @Delete
    suspend fun deleteFinanceSession(session: FinanceSession)

    // Clear
    @Query("DELETE FROM reading_sessions")
    suspend fun clearReadingSessions()

    @Query("DELETE FROM exercise_sessions")
    suspend fun clearExerciseSessions()

    @Query("DELETE FROM finance_sessions")
    suspend fun clearFinanceSessions()
}
