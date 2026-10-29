package com.example.habitrise.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_sessions",
    indices = [Index(value = ["date"]), Index(value = ["habitId"])]
)
data class ReadingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val bookTitle: String,
    val pagesRead: Int = 0,
    val durationMinutes: Int = 0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exercise_sessions",
    indices = [Index(value = ["date"]), Index(value = ["habitId"])]
)
data class ExerciseSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val activityType: String, // "Gym", "Running", "Cardio", "Yoga", "Bodyweight", "Stretching"
    val durationMinutes: Int = 0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "finance_sessions",
    indices = [Index(value = ["date"]), Index(value = ["habitId"])]
)
data class FinanceSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val topic: String, // e.g. "Personal Budgeting", "Index Funds", "Emergency Fund"
    val durationMinutes: Int = 0,
    val keyLesson: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
