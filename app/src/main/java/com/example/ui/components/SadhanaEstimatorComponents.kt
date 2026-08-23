package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

data class EstimatorResult(
    val targetBeads: Long,
    val remainingBeads: Long,
    val totalDays: Long,
    val years: Int,
    val months: Int,
    val days: Int,
    val completionDateString: String,
    val avgBeadsPerDay: Double,
    val avgMalasPerDay: Double,
    val progressPercent: Float
)

fun calculateEstimation(
    targetBeads: Long,
    currentBeads: Long,
    practiceDaysCount: Int,
    dailyGoalMalas: Int
): EstimatorResult {
    val totalDaysHistory = if (practiceDaysCount > 0) practiceDaysCount else 1
    val actualAvgBeadsPerDay = if (currentBeads > 0) currentBeads.toDouble() / totalDaysHistory else (dailyGoalMalas * 108).toDouble()
    val effectiveAvgBeads = if (actualAvgBeadsPerDay > 0) actualAvgBeadsPerDay else (dailyGoalMalas * 108).toDouble()
    val effectiveAvgMalas = effectiveAvgBeads / 108.0

    val remainingBeads = (targetBeads - currentBeads).coerceAtLeast(0)
    val remainingDaysNeeded = if (effectiveAvgBeads > 0) ceil(remainingBeads.toDouble() / effectiveAvgBeads).toLong() else 0L

    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, remainingDaysNeeded.toInt())
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val completionDateString = sdf.format(cal.time)

    val years = (remainingDaysNeeded / 365).toInt()
    val remAfterYears = remainingDaysNeeded % 365
    val months = (remAfterYears / 30).toInt()
    val days = (remAfterYears % 30).toInt()

    val progressPct = if (targetBeads > 0) ((currentBeads.toFloat() / targetBeads) * 100f).coerceIn(0f, 100f) else 0f

    return EstimatorResult(
        targetBeads = targetBeads,
        remainingBeads = remainingBeads,
        totalDays = remainingDaysNeeded,
        years = years,
        months = months,
        days = days,
        completionDateString = completionDateString,
        avgBeadsPerDay = effectiveAvgBeads,
        avgMalasPerDay = effectiveAvgMalas,
        progressPercent = progressPct
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SadhanaCompletionEstimatorSection(
    totalBeads: Int,
    totalMalas: Int,
    practiceDaysCount: Int,
    dailyGoalMalas: Int
) {
    var showTargetCalculatorSheet by remember { mutableStateOf(false) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val lakhEst = calculateEstimation(100000L, totalBeads.toLong(), practiceDaysCount, dailyGoalMalas)
    val tenLakhEst = calculateEstimation(1000000L, totalBeads.toLong(), practiceDaysCount, dailyGoalMalas)
    val croreEst = calculateEstimation(10000000L, totalBeads.toLong(), practiceDaysCount, dailyGoalMalas)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title & Subtitle + Target Calculator Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sadhana Completion Estimator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Text(
                    text = "Estimate your spiritual milestones based on your actual chanting consistency.",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showTargetCalculatorSheet = true },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                modifier = Modifier.testTag("btn_target_calculator")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Target Calculator",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Target Calculator",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Estimator Cards List
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // 1 Lakh Card
            MilestoneEstimateCard(
                title = "1 Lakh Target (100,000 Beads)",
                estimation = lakhEst,
                accentColor = activeColor
            )

            // 10 Lakh Card
            MilestoneEstimateCard(
                title = "10 Lakh Target (1,000,000 Beads)",
                estimation = tenLakhEst,
                accentColor = secondaryColor
            )

            // 1 Crore Card
            MilestoneEstimateCard(
                title = "1 Crore Target (10,000,000 Beads)",
                estimation = croreEst,
                accentColor = activeColor
            )
        }
    }

    // Target Calculator Bottom Sheet
    if (showTargetCalculatorSheet) {
        TargetCalculatorBottomSheet(
            currentBeads = totalBeads.toLong(),
            practiceDaysCount = practiceDaysCount,
            dailyGoalMalas = dailyGoalMalas,
            onDismiss = { showTargetCalculatorSheet = false }
        )
    }
}

@Composable
fun MilestoneEstimateCard(
    title: String,
    estimation: EstimatorResult,
    accentColor: Color
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val durationText = when {
        estimation.years > 0 -> "${estimation.years} Years, ${estimation.months} Months, ${estimation.days} Days"
        estimation.months > 0 -> "${estimation.months} Months, ${estimation.days} Days"
        else -> "${estimation.totalDays} Days Remaining"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f%%", estimation.progressPercent),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Time",
                            fontSize = 11.sp,
                            color = textColorSecondary
                        )
                        Text(
                            text = durationText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Estimated Completion",
                            fontSize = 11.sp,
                            color = textColorSecondary
                        )
                        Text(
                            text = estimation.completionDateString,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Remaining: ${estimation.remainingBeads} Beads",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
                Text(
                    text = "Based on: ${String.format(Locale.getDefault(), "%.1f", estimation.avgMalasPerDay)} Malas / Day",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { estimation.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = cardBorder
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetCalculatorBottomSheet(
    currentBeads: Long,
    practiceDaysCount: Int,
    dailyGoalMalas: Int,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var customInput by remember { mutableStateOf("100000") }
    val targetVal = customInput.toLongOrNull() ?: 100000L

    val estimation = calculateEstimation(targetVal, currentBeads, practiceDaysCount, dailyGoalMalas)

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = cardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Target Sadhana Calculator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = textColorSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select or Enter Target Beads",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColorSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Preset Chips
            val presets = listOf(
                Pair("50K", 50000L),
                Pair("1 Lakh", 100000L),
                Pair("5 Lakh", 500000L),
                Pair("1 Million", 1000000L),
                Pair("10 Million", 10000000L)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = targetVal == preset.second
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) activeColor else cardBorder)
                            .clickable { customInput = preset.second.toString() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset.first,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else textColorPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = customInput,
                onValueChange = { customInput = it.filter { char -> char.isDigit() } },
                label = { Text("Custom Target Beads") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_custom_target_beads")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Calculated Results Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = activeColor.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, activeColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instant Calculation Results",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Remaining Beads:", fontSize = 13.sp, color = textColorSecondary)
                        Text("${estimation.remainingBeads}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Remaining Days:", fontSize = 13.sp, color = textColorSecondary)
                        Text("${estimation.totalDays} Days", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = activeColor)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Time Breakdown:", fontSize = 13.sp, color = textColorSecondary)
                        Text("${estimation.years}Y ${estimation.months}M ${estimation.days}D", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Completion Date:", fontSize = 13.sp, color = textColorSecondary)
                        Text(estimation.completionDateString, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = activeColor)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Required Daily Average:", fontSize = 13.sp, color = textColorSecondary)
                        Text("${String.format(Locale.getDefault(), "%.1f", estimation.avgMalasPerDay)} Malas/Day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { estimation.progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = activeColor,
                        trackColor = cardBorder
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Completed ${String.format(Locale.getDefault(), "%.1f%%", estimation.progressPercent)} of Target",
                        fontSize = 11.sp,
                        color = textColorSecondary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
