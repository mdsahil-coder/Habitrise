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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitrise.ui.HabitUiState
import com.example.habitrise.ui.HabitViewModel
import com.example.habitrise.ui.components.CircularProgressCard
import com.example.habitrise.ui.components.EmptyStateView
import com.example.habitrise.ui.components.HabitCard
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.Localization

@Composable
fun HomeScreen(
    uiState: HabitUiState,
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val isHindi = uiState.userSettings.isHindi
    val todayDate = uiState.todayDate
    val habits = uiState.habits
    val completedCount = uiState.todayCompletions.size
    val totalCount = habits.size

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddHabitDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Localization.get("add_habit", isHindi)
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: Greeting & Date
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = DateUtils.getGreeting(isHindi).uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = DateUtils.getShortDateForDisplay(todayDate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = Localization.get("tagline", isHindi),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Circular Progress Indicator Card
            item {
                CircularProgressCard(
                    completedCount = completedCount,
                    totalCount = totalCount,
                    isHindi = isHindi
                )
            }

            // Motivational note banner
            item {
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (completedCount == 0) {
                            Localization.get("streak_encouragement_3", isHindi)
                        } else if (completedCount < totalCount) {
                            Localization.get("streak_encouragement_1", isHindi)
                        } else {
                            if (isHindi) "शानदार! आपने आज की सभी आदतें पूरी कर ली हैं।" else "Outstanding! You've accomplished all your habits today."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            // Habits Section Header
            item {
                Text(
                    text = if (isHindi) "आज की आदतें" else "Today's Habits",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Habit Cards List
            if (habits.isEmpty()) {
                item {
                    EmptyStateView(message = Localization.get("no_habits", isHindi))
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    val isCompleted = uiState.todayCompletions.contains(habit.id)
                    val streakInfo = uiState.streaksMap[habit.id]

                    HabitCard(
                        habit = habit,
                        isCompleted = isCompleted,
                        streakInfo = streakInfo,
                        isHindi = isHindi,
                        onToggleCompletion = {
                            viewModel.toggleHabitCompletion(habit.id, todayDate)
                        },
                        onClick = {
                            viewModel.openHabitDetail(habit.id)
                        }
                    )
                }
            }
        }
    }
}
