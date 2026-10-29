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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitrise.ui.HabitUiState
import com.example.habitrise.ui.HabitViewModel
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.Localization

data class HabitLesson(
    val titleEn: String,
    val titleHi: String,
    val summaryEn: String,
    val summaryHi: String,
    val contentEn: String,
    val contentHi: String,
    val emoji: String
)

@Composable
fun LearnScreen(
    uiState: HabitUiState,
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val isHindi = uiState.userSettings.isHindi
    val selectedDate = uiState.selectedDate
    val journal = uiState.currentJournalEntry

    var q1 by remember { mutableStateOf("") }
    var q2 by remember { mutableStateOf("") }
    var q3 by remember { mutableStateOf("") }

    // Sync state when selected date's journal changes
    LaunchedEffect(journal, selectedDate) {
        q1 = journal?.wentWell ?: ""
        q2 = journal?.madeDifficult ?: ""
        q3 = journal?.improveTomorrow ?: ""
    }

    val lessons = remember {
        listOf(
            HabitLesson(
                titleEn = "Start Small (The 2-Minute Rule)",
                titleHi = "छोटे से शुरुआत करें (2 मिनट का नियम)",
                summaryEn = "Make the barrier to start ridiculously small.",
                summaryHi = "शुरुआत करने की बाधा को बेहद छोटा बनाएं।",
                contentEn = "Instead of trying to read for one hour immediately, begin with five minutes. Increase your target gradually when it feels manageable. The goal at first is showing up.",
                contentHi = "शुरुआत में तुरंत एक घंटा पढ़ने के बजाय, केवल 5 मिनट से शुरू करें। जब यह सहज लगने लगे तो धीरे-धीरे समय बढ़ाएं।",
                emoji = "🌱"
            ),
            HabitLesson(
                titleEn = "Habit Stacking",
                titleHi = "आदतों को जोड़ना (Habit Stacking)",
                summaryEn = "Anchor new habits onto existing solid routines.",
                summaryHi = "नई आदत को पहले से स्थापित दिनचर्या से जोड़ें।",
                contentEn = "Connect a new habit to something you already do without thinking. Example: 'After I complete Fajr prayer, I will immediately open my book and read 5 pages.'",
                contentHi = "नई आदत को किसी मौजूदा आदत से जोड़ें। जैसे: 'फ़ज्र की नमाज़ के तुरंत बाद, मैं 5 पेज किताब पढूँगा।'",
                emoji = "🔗"
            ),
            HabitLesson(
                titleEn = "Never Miss Twice",
                titleHi = "कभी भी लगातार दो दिन न छोड़ें",
                summaryEn = "One missed day is an accident; two is a new habit.",
                summaryHi = "एक छूटा दिन दुर्घटना है; दो छूटे दिन नई आदत बन जाते हैं।",
                contentEn = "Life happens. Missing one day does not ruin your progress. The cardinal rule of discipline is simply not missing two days in a row. Restart immediately with 1 small action.",
                contentHi = "एक दिन चूक जाना असफलता नहीं है। मुख्य नियम यह है कि लगातार दो दिन न चूकें। अगले दिन तुरंत एक छोटे कदम से पुनः शुरू करें।",
                emoji = "⚡"
            ),
            HabitLesson(
                titleEn = "Design Your Environment",
                titleHi = "अपना वातावरण अनुकूल बनाएं",
                summaryEn = "Make good habits obvious and bad habits invisible.",
                summaryHi = "अच्छी आदतों को आसान और स्पष्ट बनाएं।",
                contentEn = "Leave your workout clothes next to your bed. Keep your book on your pillow. Place water bottles where you sit. When cues are obvious, habits follow naturally.",
                contentHi = "जिम के कपड़े रात में ही तैयार रखें। किताब तकिए पर रखें। जब संकेत स्पष्ट होते हैं, तो आदतें बिना मानसिक तनाव के पूरी होती हैं।",
                emoji = "🎯"
            ),
            HabitLesson(
                titleEn = "Weekly Review Without Guilt",
                titleHi = "समीक्षा और सुधार",
                summaryEn = "Iterate on your routine like a scientist, not a judge.",
                summaryHi = "दोष देने के बजाय एक वैज्ञानिक की तरह अपनी दिनचर्या में सुधार करें।",
                contentEn = "If a habit failed multiple times this week, do not judge yourself. Ask: 'Was the target too large? Was the time unrealistic?' Adjust the system.",
                contentHi = "यदि कोई आदत कई बार छूट रही है, तो खुद को दोष न दें। लक्ष्य को छोटा करें या समय बदलें।",
                emoji = "🔍"
            )
        )
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
            .testTag("learn_screen")
    ) {
        // Title
        item {
            Column {
                Text(
                    text = Localization.get("learn_title", isHindi),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHindi) "स्थायी और सकारात्मक आदतें बनाने के व्यावहारिक नियम" else "Practical behavioral science principles for consistent routines.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Daily Reflection Card (Section 10 & 11 from requirements)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_reflection_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Journal",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("daily_journal_title", isHindi),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = DateUtils.getShortDateForDisplay(selectedDate),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Question 1
                    Text(
                        text = "1. " + Localization.get("journal_q1", isHindi),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = q1,
                        onValueChange = { q1 = it },
                        placeholder = { Text(if (isHindi) "आज क्या अच्छा रहा..." else "What worked well today...") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_q1_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Question 2
                    Text(
                        text = "2. " + Localization.get("journal_q2", isHindi),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = q2,
                        onValueChange = { q2 = it },
                        placeholder = { Text(if (isHindi) "किस वजह से कठिनाई आई..." else "What made habits difficult today...") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_q2_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Question 3
                    Text(
                        text = "3. " + Localization.get("journal_q3", isHindi),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = q3,
                        onValueChange = { q3 = it },
                        placeholder = { Text(if (isHindi) "कल किस चीज़ में सुधार करेंगे..." else "One small improvement for tomorrow...") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_q3_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.saveJournal(q1.trim(), q2.trim(), q3.trim())
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_journal_button")
                    ) {
                        Text(Localization.get("save_reflection", isHindi))
                    }
                }
            }
        }

        // Lessons List Section Header
        item {
            Text(
                text = if (isHindi) "आदत निर्माण पाठ" else "Core Habit Lessons",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Lessons Cards
        items(lessons) { lesson ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = lesson.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) lesson.titleHi else lesson.titleEn,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHindi) lesson.summaryHi else lesson.summaryEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expand"
                            )
                        }
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isHindi) lesson.contentHi else lesson.contentEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
