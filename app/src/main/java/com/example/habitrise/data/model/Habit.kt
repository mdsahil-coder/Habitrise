package com.example.habitrise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nameHindi: String = "",
    val description: String,
    val descriptionHindi: String = "",
    val category: String, // Spiritual Growth, Morning Routine, Health and Fitness, Learning and Knowledge, Financial Education, Personal Development, Productivity, Custom
    val iconKey: String, // "prayer", "sunrise", "fitness", "book", "finance", "check", "star", "water", "meditation"
    val colorHex: String = "#1B4332",
    val scheduleType: String = "DAILY", // "DAILY", "WEEKDAYS", "CUSTOM"
    val targetDaysPerWeek: Int = 7,
    val targetDurationMinutes: Int = 0,
    val targetMetricGoal: String = "", // e.g. "5 prayers", "30 mins", "20 pages"
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val reminderDaysMask: Int = 127, // Bitmask for Sun-Sat (1111111 = 127 = every day)
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
