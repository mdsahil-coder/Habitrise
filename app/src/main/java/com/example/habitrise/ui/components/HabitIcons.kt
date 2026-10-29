package com.example.habitrise.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

object HabitIconMapper {
    fun getIcon(iconKey: String): ImageVector {
        return when (iconKey.lowercase()) {
            "prayer", "namaz" -> Icons.Default.SelfImprovement
            "sunrise", "morning", "fajr" -> Icons.Default.WbSunny
            "fitness", "gym", "exercise" -> Icons.Default.FitnessCenter
            "book", "reading" -> Icons.Default.MenuBook
            "finance", "money" -> Icons.Default.AccountBalanceWallet
            "water" -> Icons.Default.WaterDrop
            "timer" -> Icons.Default.Timer
            "lightbulb" -> Icons.Default.Lightbulb
            "star" -> Icons.Default.Star
            else -> Icons.Default.CheckCircle
        }
    }
}
