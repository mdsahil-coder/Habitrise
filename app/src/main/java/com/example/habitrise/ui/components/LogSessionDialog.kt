package com.example.habitrise.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.habitrise.data.model.Habit
import com.example.habitrise.util.Localization

@Composable
fun LogSessionDialog(
    habit: Habit,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onLogReading: (bookTitle: String, pages: Int, durationMins: Int, notes: String) -> Unit,
    onLogExercise: (activityType: String, durationMins: Int, notes: String) -> Unit,
    onLogFinance: (topic: String, durationMins: Int, keyLesson: String) -> Unit
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }
    var field4 by remember { mutableStateOf("") }

    val isReading = habit.iconKey == "book" || habit.name.contains("Reading", ignoreCase = true)
    val isExercise = habit.iconKey == "fitness" || habit.name.contains("Exercise", ignoreCase = true)
    val isFinance = habit.iconKey == "finance" || habit.name.contains("Finance", ignoreCase = true)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("log_session_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${Localization.get("log_session", isHindi)} - ${habit.name}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isReading -> {
                        OutlinedTextField(
                            value = field1,
                            onValueChange = { field1 = it },
                            label = { Text(Localization.get("book_title", isHindi)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field2,
                            onValueChange = { field2 = it },
                            label = { Text(Localization.get("pages_read", isHindi)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field3,
                            onValueChange = { field3 = it },
                            label = { Text(Localization.get("minutes_read", isHindi)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field4,
                            onValueChange = { field4 = it },
                            label = { Text(Localization.get("personal_notes", isHindi)) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    isExercise -> {
                        OutlinedTextField(
                            value = field1,
                            onValueChange = { field1 = it },
                            label = { Text(Localization.get("activity_type", isHindi)) },
                            placeholder = { Text("e.g. Gym, Running, Yoga") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field2,
                            onValueChange = { field2 = it },
                            label = { Text(Localization.get("minutes_read", isHindi)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field3,
                            onValueChange = { field3 = it },
                            label = { Text(Localization.get("personal_notes", isHindi)) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> { // Finance or General
                        OutlinedTextField(
                            value = field1,
                            onValueChange = { field1 = it },
                            label = { Text(Localization.get("finance_topic", isHindi)) },
                            placeholder = { Text("e.g. Budgeting, Emergency Fund") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field2,
                            onValueChange = { field2 = it },
                            label = { Text(Localization.get("minutes_read", isHindi)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = field3,
                            onValueChange = { field3 = it },
                            label = { Text(Localization.get("key_lesson", isHindi)) },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(Localization.get("cancel", isHindi))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            when {
                                isReading -> {
                                    val pages = field2.toIntOrNull() ?: 0
                                    val mins = field3.toIntOrNull() ?: 20
                                    onLogReading(field1.ifBlank { "Daily Book" }, pages, mins, field4)
                                }
                                isExercise -> {
                                    val mins = field2.toIntOrNull() ?: 30
                                    onLogExercise(field1.ifBlank { "Workout" }, mins, field3)
                                }
                                else -> {
                                    val mins = field2.toIntOrNull() ?: 15
                                    onLogFinance(field1.ifBlank { "Financial Education" }, mins, field3)
                                }
                            }
                        },
                        modifier = Modifier.testTag("submit_session_button")
                    ) {
                        Text(Localization.get("save", isHindi))
                    }
                }
            }
        }
    }
}
