package com.example.habitrise.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitrise.data.model.HabitCompletion
import com.example.habitrise.ui.HabitUiState
import com.example.habitrise.ui.HabitViewModel
import com.example.habitrise.ui.components.DayBarData
import com.example.habitrise.ui.components.WeeklyCompletionBarChart
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SuccessGreen
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.Localization

@Composable
fun InsightsScreen(
    uiState: HabitUiState,
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val isHindi = uiState.userSettings.isHindi
    val stats = uiState.overallStats
    val activeHabitsCount = uiState.habits.size

    val past7Days = remember { DateUtils.getPastDays(7) }
    val completionsPastWeek: List<HabitCompletion> by viewModel.getCompletionsBetween(
        startDate = past7Days.first(),
        endDate = past7Days.last()
    ).collectAsState(initial = emptyList())

    val chartData = remember(completionsPastWeek, activeHabitsCount) {
        past7Days.map { dateStr ->
            val count = completionsPastWeek.count { it.date == dateStr }
            DayBarData(
                dateStr = dateStr,
                dayLabel = DateUtils.getDayOfWeekLabel(dateStr, isHindi),
                completedCount = count,
                totalCount = activeHabitsCount
            )
        }
    }

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
            .testTag("insights_screen")
    ) {
        // Screen Title
        item {
            Column {
                Text(
                    text = Localization.get("stats_title", isHindi),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHindi) "आपकी आदतों की निरंतरता और प्रगति का विश्लेषण" else "Detailed consistency analytics from your actual habit records.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Top Highlights: Best Streak & Total Sessions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatHighlightCard(
                    title = Localization.get("best_streak", isHindi),
                    value = "${stats.bestOverallStreak} Days",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = GoldAccent,
                    modifier = Modifier.weight(1f)
                )

                StatHighlightCard(
                    title = Localization.get("total_sessions", isHindi),
                    value = stats.totalSessionsCount.toString(),
                    icon = Icons.Default.TrendingUp,
                    iconTint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Completion Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isHindi) "साप्ताहिक पूर्णता रुझान" else "Weekly Completion Trend",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Last 7 Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    WeeklyCompletionBarChart(data = chartData)
                }
            }
        }

        // Notebook Habits Drilldown Metrics (Reading, Exercise, Finance)
        item {
            Text(
                text = if (isHindi) "विशेष आदतों का विवरण" else "Notebook Habits Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NotebookMetricCard(
                    title = Localization.get("reading_minutes", isHindi),
                    value = "${uiState.totalReadingMinutes}m",
                    subtitle = "${uiState.totalPagesRead} pages read",
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )

                NotebookMetricCard(
                    title = Localization.get("exercise_sessions", isHindi),
                    value = "${uiState.totalExerciseCount}",
                    subtitle = "${uiState.totalExerciseMinutes}m workout",
                    icon = Icons.Default.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )

                NotebookMetricCard(
                    title = Localization.get("finance_sessions", isHindi),
                    value = "${uiState.totalFinanceCount}",
                    subtitle = "${uiState.totalFinanceMinutes}m studied",
                    icon = Icons.Default.Savings,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Habit Consistency & Needs Attention
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Insight",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "स्मार्ट अंतर्दृष्टि" else "Data-Driven Insights",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (stats.mostConsistentHabitName.isNotEmpty() && stats.mostConsistentHabitName != "None") {
                        InsightItem(
                            emoji = "🏆",
                            title = Localization.get("most_consistent", isHindi),
                            description = stats.mostConsistentHabitName
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (stats.needsAttentionHabitName.isNotEmpty() && stats.needsAttentionHabitName != "None") {
                        InsightItem(
                            emoji = "🎯",
                            title = Localization.get("needs_attention", isHindi),
                            description = "${stats.needsAttentionHabitName} — Try making your routine smaller or linking it to morning coffee."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    InsightItem(
                        emoji = "💡",
                        title = "Encouragement",
                        description = if (stats.todayCompletionPercentage >= 60) {
                            "You are on pace! Consistency compound over weeks and months."
                        } else {
                            "Never miss twice. Even a 2-minute effort keeps your habit momentum alive."
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatHighlightCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NotebookMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun InsightItem(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 20.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
