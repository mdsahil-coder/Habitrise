package com.example.habitrise.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitrise.data.model.ExerciseSession
import com.example.habitrise.data.model.FinanceSession
import com.example.habitrise.data.model.Habit
import com.example.habitrise.data.model.HabitCompletion
import com.example.habitrise.data.model.ReadingSession
import com.example.habitrise.ui.HabitUiState
import com.example.habitrise.ui.HabitViewModel
import com.example.habitrise.ui.components.HabitIconMapper
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SuccessGreen
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.Localization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    uiState: HabitUiState,
    viewModel: HabitViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val isHindi = uiState.userSettings.isHindi
    val habit = uiState.habits.find { it.id == habitId } ?: return
    val streakInfo = uiState.streaksMap[habitId]
    val todayDate = uiState.todayDate
    val isCompletedToday = uiState.todayCompletions.contains(habitId)

    val isNamaz = habit.iconKey == "prayer" || habit.name.contains("Namaz", ignoreCase = true)
    val isReading = habit.iconKey == "book" || habit.name.contains("Reading", ignoreCase = true)
    val isExercise = habit.iconKey == "fitness" || habit.name.contains("Exercise", ignoreCase = true)
    val isFinance = habit.iconKey == "finance" || habit.name.contains("Finance", ignoreCase = true)

    val readingSessions: List<ReadingSession> by viewModel.getReadingSessionsForHabit(habitId).collectAsState(initial = emptyList())
    val exerciseSessions: List<ExerciseSession> by viewModel.getExerciseSessionsForHabit(habitId).collectAsState(initial = emptyList())
    val financeSessions: List<FinanceSession> by viewModel.getFinanceSessionsForHabit(habitId).collectAsState(initial = emptyList())

    val completions: List<HabitCompletion> by viewModel.getCompletionsForHabit(habitId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isHindi && habit.nameHindi.isNotEmpty()) habit.nameHindi else habit.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openEditHabitDialog(habit) }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { viewModel.promptDeleteHabit(habit) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = DangerRed
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("habit_detail_screen")
        ) {
            // Header Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    imageVector = HabitIconMapper.getIcon(habit.iconKey),
                                    contentDescription = habit.name,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = habit.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (habit.targetMetricGoal.isNotEmpty()) habit.targetMetricGoal else "Daily Habit",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (habit.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi && habit.descriptionHindi.isNotEmpty()) habit.descriptionHindi else habit.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (habit.reminderEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = "Reminder",
                                    tint = SageSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Reminder set for " + String.format("%02d:%02d", habit.reminderHour, habit.reminderMinute),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Streak Statistics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailStatCard(
                        title = Localization.get("current_streak", isHindi),
                        value = "${streakInfo?.currentStreak ?: 0} Days",
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatCard(
                        title = Localization.get("best_streak", isHindi),
                        value = "${streakInfo?.bestStreak ?: 0} Days",
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatCard(
                        title = Localization.get("total_days", isHindi),
                        value = "${streakInfo?.totalCompletions ?: 0}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Today's Completion Action Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompletedToday) SuccessGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Today's Status",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isCompletedToday) "Completed for today!" else "Not yet marked complete",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCompletedToday) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleHabitCompletion(habit.id, todayDate) },
                            modifier = Modifier.testTag("toggle_detail_habit")
                        ) {
                            Text(
                                if (isCompletedToday) Localization.get("undo_completion", isHindi)
                                else Localization.get("tap_to_complete", isHindi)
                            )
                        }
                    }
                }
            }

            // Specialized Widget: Namaz 5 Prayers Checklist
            if (isNamaz) {
                item {
                    val prayer = uiState.currentPrayerRecord
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().testTag("namaz_checklist_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = "Prayer",
                                        tint = ForestGreenPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = Localization.get("prayer_checklist_title", isHindi),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Text(
                                    text = "${prayer?.completedCount ?: 0} / 5",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SuccessGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Localization.get("prayer_subtext", isHindi),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            PrayerCheckboxRow("Fajr (फ़ज्र)", prayer?.fajr == true) { viewModel.updatePrayer(fajr = it) }
                            PrayerCheckboxRow("Dhuhr (ज़ुहर)", prayer?.dhuhr == true) { viewModel.updatePrayer(dhuhr = it) }
                            PrayerCheckboxRow("Asr (असर)", prayer?.asr == true) { viewModel.updatePrayer(asr = it) }
                            PrayerCheckboxRow("Maghrib (मग़रिब)", prayer?.maghrib == true) { viewModel.updatePrayer(maghrib = it) }
                            PrayerCheckboxRow("Isha (ईशा)", prayer?.isha == true) { viewModel.updatePrayer(isha = it) }
                        }
                    }
                }
            }

            // Specialized Widget: Reading Sessions
            if (isReading) {
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
                                    text = "Reading Log & Sessions",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                OutlinedButton(onClick = { viewModel.openLogSessionDialog(habit) }) {
                                    Text("+ Log Reading")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (readingSessions.isEmpty()) {
                                Text(
                                    text = "No reading sessions recorded yet. Tap + Log Reading to track pages and minutes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                for (s in readingSessions.take(5)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Column {
                                            Text(text = s.bookTitle, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                            Text(text = "${s.pagesRead} pages • ${s.durationMinutes} mins • ${s.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Specialized Widget: Exercise Sessions
            if (isExercise) {
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
                                    text = "Workout History",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                OutlinedButton(onClick = { viewModel.openLogSessionDialog(habit) }) {
                                    Text("+ Log Workout")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (exerciseSessions.isEmpty()) {
                                Text(
                                    text = "No workout sessions recorded yet. Tap + Log Workout to track gym or exercises.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                for (s in exerciseSessions.take(5)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Column {
                                            Text(text = s.activityType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                            Text(text = "${s.durationMinutes} mins • ${s.date} ${if (s.notes.isNotBlank()) "• ${s.notes}" else ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Specialized Widget: Finance Learning
            if (isFinance) {
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
                                    text = "Financial Study & Concept Notes",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                OutlinedButton(onClick = { viewModel.openLogSessionDialog(habit) }) {
                                    Text("+ Log Study")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (financeSessions.isEmpty()) {
                                Text(
                                    text = "No finance study notes yet. Use this section for budgeting rules, emergency funds, and investment learning notes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                for (s in financeSessions.take(5)) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                        Text(text = s.topic, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        if (s.keyLesson.isNotBlank()) {
                                            Text(text = "“${s.keyLesson}”", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Text(text = "${s.durationMinutes} mins • ${s.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recent Completions History
            item {
                Text(
                    text = "Recent Completion Dates",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (completions.isEmpty()) {
                item {
                    Text(
                        text = "No completions logged yet. Build your first streak today!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(completions.take(15)) { c ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = DateUtils.formatDateForDisplay(c.date, isHindi),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PrayerCheckboxRow(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (checked) SuccessGreen else MaterialTheme.colorScheme.onSurface
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
