package com.example.habitrise.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val wentWell: String = "",
    val madeDifficult: String = "",
    val improveTomorrow: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
