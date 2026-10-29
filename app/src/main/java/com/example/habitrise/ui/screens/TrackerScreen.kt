package com.example.habitrise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitrise.data.model.HabitCompletion
import com.example.habitrise.ui.HabitUiState
import com.example.habitrise.ui.HabitViewModel
import com.example.habitrise.ui.components.EmptyStateView
import com.example.habitrise.ui.components.HabitCard
import com.example.habitrise.ui.components.MonthlyCalendarView
import com.example.habitrise.ui.components.WeeklyCalendarStrip
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SuccessGreen
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.Localization
import java.util.Calendar

@Composable
fun TrackerScreen(
    uiState: HabitUiState,
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val isHindi = uiState.userSettings.isHindi
    val selectedDate = uiState.selectedDate
    val habits = uiState.habits
    val totalActiveHabits = habits.size

    val cal = remember(selectedDate) {
        Calendar.getInstance().apply { time = DateUtils.parseDate(selectedDate) }
    }
    val currentYear = cal.get(Calendar.YEAR)
    val currentMonth = cal.get(Calendar.MONTH)

    // Calculate completions for the month
    val allCompletions: List<HabitCompletion> by viewModel.getCompletionsBetween(
        startDate = String.format("%04d-%02d-01", currentYear, currentMonth + 1),
        endDate = String.format("%04d-%02d-31", currentYear, currentMonth + 1)
    ).collectAsState(initial = emptyList())

    val completionsPerDate = remember(allCompletions) {
        val map = mutableMapOf<String, Int>()
        allCompletions.forEach { c ->
            map[c.date] = (map[c.date] ?: 0) + 1
        }
        map
    }

    val selectedDateCompletedIds = remember(allCompletions, selectedDate) {
        allCompletions.filter { it.date == selectedDate }.map { it.habitId }.toSet()
    }

    val selectedDateCompletedCount = selectedDateCompletedIds.size
    val dailyRatePct = if (totalActiveHabits > 0) ((selectedDateCompletedCount.toFloat() / totalActiveHabits) * 100).toInt() else 0

    // Weekly completions (last 7 days)
    val past7Days = DateUtils.getPastDays(7)
    val weeklyTotalCompletions = past7Days.sumOf { dateStr -> completionsPerDate[dateStr] ?: 0 }
    val weeklyPossible = totalActiveHabits * 7
    val weeklyRatePct = if (weeklyPossible > 0) ((weeklyTotalCompletions.toFloat() / weeklyPossible) * 100).toInt() else 0

    // Monthly completions
    val monthlyTotalCompletions = completionsPerDate.values.sum()
    val monthlyPossible = totalActiveHabits * DateUtils.getDaysInMonth(currentYear, currentMonth).size
    val monthlyRatePct = if (monthlyPossible > 0) ((monthlyTotalCompletions.toFloat() / monthlyPossible) * 100).toInt() else 0

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("tracker_screen")
    ) {
        // Title
        item {
            Column {
                Text(
                    text = Localization.get("nav_tracker", isHindi),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = DateUtils.formatDateForDisplay(selectedDate, isHindi),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 3 Key Stats: Daily Rate, Weekly Rate, Monthly Rate
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Daily Rate
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = Localization.get("daily_rate", isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$dailyRatePct%",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (dailyRatePct >= 80) SuccessGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Weekly Rate
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = Localization.get("weekly_rate", isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$weeklyRatePct%",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Monthly Rate
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = Localization.get("monthly_rate", isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$monthlyRatePct%",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = SageSecondary
                        )
                    }
                }
            }
        }

        // Weekly Quick Strip
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = Localization.get("weekly_calendar", isHindi),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    WeeklyCalendarStrip(
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.setSelectedDate(it) },
                        completionDates = completionsPerDate.filter { it.value > 0 }.keys,
                        isHindi = isHindi
                    )
                }
            }
        }

        // Monthly Interactive Calendar Grid
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = DateUtils.getMonthYearDisplay(currentYear, currentMonth, isHindi),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SuccessGreen,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .height(8.dp)
                                .width(8.dp)
                        ) {}
                        Text(text = "Completed", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                MonthlyCalendarView(
                    year = currentYear,
                    month = currentMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.setSelectedDate(it) },
                    completionsPerDate = completionsPerDate,
                    totalActiveHabits = totalActiveHabits,
                    isHindi = isHindi
                )
            }
        }

        // Habits for Selected Date Header
        item {
            Text(
                text = "${Localization.get("completion_history", isHindi)} (${DateUtils.getShortDateForDisplay(selectedDate)})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (habits.isEmpty()) {
            item {
                EmptyStateView(message = Localization.get("no_habits", isHindi))
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                val isCompleted = selectedDateCompletedIds.contains(habit.id)
                val streakInfo = uiState.streaksMap[habit.id]

                HabitCard(
                    habit = habit,
                    isCompleted = isCompleted,
                    streakInfo = streakInfo,
                    isHindi = isHindi,
                    onToggleCompletion = {
                        viewModel.toggleHabitCompletion(habit.id, selectedDate)
                    },
                    onClick = {
                        viewModel.openHabitDetail(habit.id)
                    }
                )
            }
        }
    }
}
