package com.example.habitrise.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.habitrise.util.DateUtils
import java.util.Calendar

data class DayBarData(
    val dateStr: String,
    val dayLabel: String,
    val completedCount: Int,
    val totalCount: Int
) {
    val percentage: Float
        get() = if (totalCount > 0) (completedCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f
}

@Composable
fun WeeklyCompletionBarChart(
    data: List<DayBarData>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(durationMillis = 650))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barCount = data.size.coerceAtLeast(1)
                val spacing = 16.dp.toPx()
                val totalSpacing = spacing * (barCount + 1)
                val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(12.dp.toPx())
                val chartHeight = size.height - 24.dp.toPx()

                // Draw horizontal guide lines
                val linePaintColor = Color.LightGray.copy(alpha = 0.3f)
                val steps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                for (s in steps) {
                    val y = chartHeight * (1f - s)
                    drawLine(
                        color = linePaintColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw bars
                data.forEachIndexed { index, dayData ->
                    val x = spacing + index * (barWidth + spacing)
                    val fullBarHeight = chartHeight
                    val filledHeight = chartHeight * dayData.percentage * animProgress.value

                    // Background bar
                    drawRoundRect(
                        color = trackColor,
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, fullBarHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Active bar
                    if (filledHeight > 0f) {
                        val barColor = if (dayData.percentage >= 0.8f) SuccessGreen else primaryColor
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, fullBarHeight - filledHeight),
                            size = Size(barWidth, filledHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }
            }
        }

        // Labels underneath bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { dayData ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = dayData.dayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(dayData.percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyCalendarView(
    year: Int,
    month: Int, // 0-indexed (0 = Jan)
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    completionsPerDate: Map<String, Int>, // date -> completed count
    totalActiveHabits: Int,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday...

    val dayHeaders = if (isHindi) {
        listOf("रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि")
    } else {
        listOf("S", "M", "T", "W", "T", "F", "S")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayHeaders.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Days Grid
        val totalCells = ((firstDayOfWeek - 1) + maxDays)
        val rows = (totalCells + 6) / 7

        for (r in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (c in 0 until 7) {
                    val cellIndex = r * 7 + c
                    val dayNum = cellIndex - (firstDayOfWeek - 1) + 1

                    if (dayNum in 1..maxDays) {
                        val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayNum)
                        val isSelected = dateString == selectedDate
                        val completedCount = completionsPerDate[dateString] ?: 0
                        val isToday = dateString == DateUtils.getTodayDate()
                        val isPast = dateString < DateUtils.getTodayDate()

                        val indicatorColor = when {
                            completedCount >= totalActiveHabits && totalActiveHabits > 0 -> SuccessGreen
                            completedCount > 0 -> SageSecondary
                            isPast -> WarningAmber.copy(alpha = 0.35f)
                            else -> Color.Transparent
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onDateSelected(dateString) }
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // Indicator dot
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected && indicatorColor != Color.Transparent) Color.White else indicatorColor)
                                )
                            }
                        }
                    } else {
                        // Empty cell for alignment
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
