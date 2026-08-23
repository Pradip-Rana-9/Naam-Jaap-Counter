package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DailyProgress
import com.example.data.entity.Mantra
import com.example.data.entity.Sankalp
import com.example.data.entity.Session
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

data class SpiritualAchievement(
    val id: String,
    val title: String,
    val shortTitle: String,
    val icon: String,
    val description: String,
    val requirementText: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val progressUnit: String,
    val isUnlocked: Boolean,
    val unlockDate: String? = null
)

fun getSpiritualMilestones(
    currentStreak: Int,
    longestStreak: Int,
    totalMalas: Int,
    totalBeads: Int
): List<SpiritualAchievement> {
    val bestStreak = currentStreak.coerceAtLeast(longestStreak)
    return listOf(
        SpiritualAchievement(
            id = "7_days_streak",
            title = "7 Days Streak",
            shortTitle = "7 Days",
            icon = "🔥",
            description = "Maintain continuous daily Naam Jaap for 7 consecutive days.",
            requirementText = "7 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 7,
            progressUnit = "days",
            isUnlocked = bestStreak >= 7
        ),
        SpiritualAchievement(
            id = "21_days_streak",
            title = "21 Days Streak",
            shortTitle = "21 Days",
            icon = "✨",
            description = "Cultivate a profound spiritual habit with 21 consecutive days of Jaap.",
            requirementText = "21 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 21,
            progressUnit = "days",
            isUnlocked = bestStreak >= 21
        ),
        SpiritualAchievement(
            id = "108_malas",
            title = "108 Malas",
            shortTitle = "108 Malas",
            icon = "📿",
            description = "Complete 108 full sacred Japa Malas in your devotional journey.",
            requirementText = "108 completed malas",
            currentProgress = totalMalas,
            targetProgress = 108,
            progressUnit = "malas",
            isUnlocked = totalMalas >= 108
        ),
        SpiritualAchievement(
            id = "1_lakh_beads",
            title = "1 Lakh Beads",
            shortTitle = "1 Lakh Beads",
            icon = "🏆",
            description = "Chant 100,000 holy names and attain the Mantra Upaasaka milestone.",
            requirementText = "100,000 total holy beads",
            currentProgress = totalBeads,
            targetProgress = 100000,
            progressUnit = "beads",
            isUnlocked = totalBeads >= 100000
        )
    )
}

fun getAllSpiritualAchievements(
    currentStreak: Int,
    longestStreak: Int,
    totalMalas: Int,
    totalBeads: Int
): List<SpiritualAchievement> {
    val bestStreak = currentStreak.coerceAtLeast(longestStreak)
    return listOf(
        SpiritualAchievement(
            id = "pratham_bhakta",
            title = "Pratham Bhakta",
            shortTitle = "1st Bead",
            icon = "👑",
            description = "Take the first auspicious step on the sacred path of Naam Jaap.",
            requirementText = "Complete at least 1 bead of Jaap",
            currentProgress = totalBeads.coerceAtMost(1),
            targetProgress = 1,
            progressUnit = "beads",
            isUnlocked = totalBeads >= 1
        ),
        SpiritualAchievement(
            id = "streak_master",
            title = "Streak Master",
            shortTitle = "1 Day",
            icon = "🔥",
            description = "Maintain your sacred daily rhythm of divine remembrance.",
            requirementText = "1 active day streak",
            currentProgress = bestStreak.coerceAtMost(1),
            targetProgress = 1,
            progressUnit = "days",
            isUnlocked = bestStreak >= 1
        ),
        SpiritualAchievement(
            id = "3_days_streak",
            title = "3 Days Streak",
            shortTitle = "3 Days",
            icon = "🌱",
            description = "Sustain consistent daily Jaap for 3 consecutive days.",
            requirementText = "3 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 3,
            progressUnit = "days",
            isUnlocked = bestStreak >= 3
        ),
        SpiritualAchievement(
            id = "7_days_streak",
            title = "7 Days Streak",
            shortTitle = "7 Days",
            icon = "🔥",
            description = "Maintain continuous daily Naam Jaap for 7 consecutive days.",
            requirementText = "7 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 7,
            progressUnit = "days",
            isUnlocked = bestStreak >= 7
        ),
        SpiritualAchievement(
            id = "21_days_streak",
            title = "21 Days Streak",
            shortTitle = "21 Days",
            icon = "✨",
            description = "Cultivate a profound spiritual habit with 21 consecutive days of Jaap.",
            requirementText = "21 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 21,
            progressUnit = "days",
            isUnlocked = bestStreak >= 21
        ),
        SpiritualAchievement(
            id = "40_days_streak",
            title = "40 Days Sadhana",
            shortTitle = "40 Days",
            icon = "🕉️",
            description = "Complete an entire 40-day sacred spiritual anushthan.",
            requirementText = "40 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 40,
            progressUnit = "days",
            isUnlocked = bestStreak >= 40
        ),
        SpiritualAchievement(
            id = "108_days_streak",
            title = "108 Days Tapasya",
            shortTitle = "108 Days",
            icon = "🌟",
            description = "Achieve the auspicious 108 days of uninterrupted chanting.",
            requirementText = "108 consecutive active days",
            currentProgress = bestStreak,
            targetProgress = 108,
            progressUnit = "days",
            isUnlocked = bestStreak >= 108
        ),
        SpiritualAchievement(
            id = "mala_sadhak",
            title = "Mala Sadhak",
            shortTitle = "1 Mala",
            icon = "📿",
            description = "Complete your first complete round of 108 holy beads.",
            requirementText = "1 completed mala",
            currentProgress = totalMalas.coerceAtMost(1),
            targetProgress = 1,
            progressUnit = "malas",
            isUnlocked = totalMalas >= 1
        ),
        SpiritualAchievement(
            id = "108_malas",
            title = "108 Malas",
            shortTitle = "108 Malas",
            icon = "📿",
            description = "Complete 108 full sacred Japa Malas in your devotional journey.",
            requirementText = "108 completed malas",
            currentProgress = totalMalas,
            targetProgress = 108,
            progressUnit = "malas",
            isUnlocked = totalMalas >= 108
        ),
        SpiritualAchievement(
            id = "1k_beads",
            title = "1,000 Beads",
            shortTitle = "1,000 Beads",
            icon = "🪷",
            description = "Chant 1,000 sacred holy names.",
            requirementText = "1,000 total holy beads",
            currentProgress = totalBeads,
            targetProgress = 1000,
            progressUnit = "beads",
            isUnlocked = totalBeads >= 1000
        ),
        SpiritualAchievement(
            id = "10k_beads",
            title = "10,000 Beads (Japn Explorer)",
            shortTitle = "10k Beads",
            icon = "🔱",
            description = "Chant 10,000 holy names on your sacred path.",
            requirementText = "10,000 total holy beads",
            currentProgress = totalBeads,
            targetProgress = 10000,
            progressUnit = "beads",
            isUnlocked = totalBeads >= 10000
        ),
        SpiritualAchievement(
            id = "50k_beads",
            title = "50,000 Beads (Sadhak)",
            shortTitle = "50k Beads",
            icon = "🏵️",
            description = "Attain deep devotional immersion with 50,000 chants.",
            requirementText = "50,000 total holy beads",
            currentProgress = totalBeads,
            targetProgress = 50000,
            progressUnit = "beads",
            isUnlocked = totalBeads >= 50000
        ),
        SpiritualAchievement(
            id = "1_lakh_beads",
            title = "1 Lakh Beads",
            shortTitle = "1 Lakh Beads",
            icon = "🏆",
            description = "Chant 100,000 holy names and attain the Mantra Upaasaka milestone.",
            requirementText = "100,000 total holy beads",
            currentProgress = totalBeads,
            targetProgress = 100000,
            progressUnit = "beads",
            isUnlocked = totalBeads >= 100000
        )
    )
}

data class DailyBreakdownItem(
    val dayShort: String,
    val dateDisplay: String,
    val beads: Int,
    val malas: Int
)

data class FilteredStats(
    val filterName: String,
    val dateRangeText: String,
    val totalMalas: Int,
    val totalBeads: Int,
    val activeDays: Int,
    val bestStreak: Int,
    val chartData: List<Pair<String, Int>>
)

fun computeFilteredStats(
    allProgress: List<DailyProgress>,
    filter: String,
    currentStreak: Int,
    longestStreak: Int
): FilteredStats {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()

    return when (filter) {
        "Week" -> {
            val weekCal = Calendar.getInstance()
            while (weekCal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                weekCal.add(Calendar.DAY_OF_YEAR, -1)
            }

            val displaySdf = SimpleDateFormat("d MMM", Locale.getDefault())
            val startDateStr = displaySdf.format(weekCal.time)

            val daysMap = mutableMapOf<String, Int>()
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val dateToLabelMap = mutableMapOf<String, String>()

            for (i in 0..6) {
                val dStr = sdf.format(weekCal.time)
                dateToLabelMap[dStr] = dayLabels[i]
                daysMap[dayLabels[i]] = 0
                if (i < 6) weekCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            val endDateStr = displaySdf.format(weekCal.time)
            val dateRange = "$startDateStr - $endDateStr"

            val weekProgress = allProgress.filter { dateToLabelMap.containsKey(it.dateString) }
            var malasSum = 0
            var beadsSum = 0
            var activeDaysCount = 0

            weekProgress.forEach { prog ->
                val label = dateToLabelMap[prog.dateString] ?: return@forEach
                daysMap[label] = prog.totalBeadsCompleted
                malasSum += prog.totalMalasCompleted
                beadsSum += prog.totalBeadsCompleted
                if (prog.totalBeadsCompleted > 0 || prog.totalMalasCompleted > 0) {
                    activeDaysCount++
                }
            }

            val chartData = dayLabels.map { Pair(it, daysMap[it] ?: 0) }

            FilteredStats(
                filterName = "This Week Overview",
                dateRangeText = dateRange,
                totalMalas = malasSum,
                totalBeads = beadsSum,
                activeDays = activeDaysCount,
                bestStreak = currentStreak.coerceAtLeast(longestStreak),
                chartData = chartData
            )
        }
        "Month" -> {
            val monthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
            val monthDisplay = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
            val monthProgress = allProgress.filter { it.dateString.startsWith(monthStr) }

            val malasSum = monthProgress.sumOf { it.totalMalasCompleted }
            val beadsSum = monthProgress.sumOf { it.totalBeadsCompleted }
            val activeDaysCount = monthProgress.count { it.totalBeadsCompleted > 0 || it.totalMalasCompleted > 0 }

            val maxDayInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val daysPerBucket = (maxDayInMonth / 4).coerceAtLeast(7)
            val chartList = mutableListOf<Pair<String, Int>>()

            for (w in 1..4) {
                val startDay = (w - 1) * daysPerBucket + 1
                val endDay = if (w == 4) maxDayInMonth else w * daysPerBucket
                val sumForBucket = monthProgress.filter {
                    val dayNum = it.dateString.takeLast(2).toIntOrNull() ?: 0
                    dayNum in startDay..endDay
                }.sumOf { it.totalBeadsCompleted }
                chartList.add(Pair("W$w", sumForBucket))
            }

            FilteredStats(
                filterName = "This Month Overview",
                dateRangeText = monthDisplay,
                totalMalas = malasSum,
                totalBeads = beadsSum,
                activeDays = activeDaysCount,
                bestStreak = currentStreak.coerceAtLeast(longestStreak),
                chartData = chartList
            )
        }
        "Year" -> {
            val yearStr = SimpleDateFormat("yyyy", Locale.getDefault()).format(cal.time)
            val yearProgress = allProgress.filter { it.dateString.startsWith(yearStr) }

            val malasSum = yearProgress.sumOf { it.totalMalasCompleted }
            val beadsSum = yearProgress.sumOf { it.totalBeadsCompleted }
            val activeDaysCount = yearProgress.count { it.totalBeadsCompleted > 0 || it.totalMalasCompleted > 0 }

            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthSums = MutableList(12) { 0 }

            yearProgress.forEach { prog ->
                try {
                    val monthIdx = prog.dateString.substring(5, 7).toInt() - 1
                    if (monthIdx in 0..11) {
                        monthSums[monthIdx] += prog.totalBeadsCompleted
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val chartList = monthNames.mapIndexed { idx, m -> Pair(m, monthSums[idx]) }

            FilteredStats(
                filterName = "This Year Overview",
                dateRangeText = "Jan - Dec $yearStr",
                totalMalas = malasSum,
                totalBeads = beadsSum,
                activeDays = activeDaysCount,
                bestStreak = currentStreak.coerceAtLeast(longestStreak),
                chartData = chartList
            )
        }
        else -> {
            // Lifetime
            val malasSum = allProgress.sumOf { it.totalMalasCompleted }
            val beadsSum = allProgress.sumOf { it.totalBeadsCompleted }
            val activeDaysCount = allProgress.count { it.totalBeadsCompleted > 0 || it.totalMalasCompleted > 0 }

            val chartList = mutableListOf<Pair<String, Int>>()
            val tempCal = Calendar.getInstance()
            val monthSdf = SimpleDateFormat("MMM", Locale.getDefault())
            val yearMonthSdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())

            val buckets = mutableListOf<Pair<String, String>>()
            for (i in 5 downTo 0) {
                val c = tempCal.clone() as Calendar
                c.add(Calendar.MONTH, -i)
                buckets.add(Pair(monthSdf.format(c.time), yearMonthSdf.format(c.time)))
            }

            buckets.forEach { (lbl, ym) ->
                val sumForMonth = allProgress.filter { it.dateString.startsWith(ym) }.sumOf { it.totalBeadsCompleted }
                chartList.add(Pair(lbl, sumForMonth))
            }

            FilteredStats(
                filterName = "Lifetime Overview",
                dateRangeText = "All Time",
                totalMalas = malasSum,
                totalBeads = beadsSum,
                activeDays = activeDaysCount,
                bestStreak = currentStreak.coerceAtLeast(longestStreak),
                chartData = chartList
            )
        }
    }
}

/**
 * Orange Vibrant Segmented Control: Week | Month | Year | Lifetime
 */
@Composable
fun TimeFilterSegmentedControl(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Week", "Month", "Year", "Lifetime")
    val orangeAccent = Color(0xFFFF8C00)
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val mutedTextColor = Color(0xFF8B949E)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(darkCardBg)
            .border(1.dp, darkBorder, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        filters.forEach { filter ->
            val isSel = selectedFilter == filter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSel) orangeAccent else Color.Transparent)
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    fontSize = 13.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSel) Color.White else mutedTextColor
                )
            }
        }
    }
}

/**
 * 2x2 Overview Stat Cards Grid matching Screen 1 exact layout
 */
@Composable
fun OverviewStatsGrid(
    stats: FilteredStats
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val greenBadgeBg = Color(0xFF132A1C)
    val greenBadgeText = Color(0xFF22C55E)
    val orangeBadgeBg = Color(0xFF2D1E10)
    val orangeBadgeText = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stats.filterName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stats.dateRangeText,
                    fontSize = 12.sp,
                    color = labelColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = labelColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 1: Total Malas & Total Beads
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewStatCard(
                label = "Total Malas",
                value = "${stats.totalMalas}",
                badgeText = "▲ 12%",
                badgeBg = greenBadgeBg,
                badgeTextColor = greenBadgeText,
                modifier = Modifier.weight(1f)
            )
            OverviewStatCard(
                label = "Total Beads",
                value = String.format(Locale.getDefault(), "%,d", stats.totalBeads),
                badgeText = "▲ 15%",
                badgeBg = greenBadgeBg,
                badgeTextColor = greenBadgeText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Days Active & Best Streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewStatCard(
                label = "Days Active",
                value = "${stats.activeDays}",
                badgeText = "▲ 20%",
                badgeBg = greenBadgeBg,
                badgeTextColor = greenBadgeText,
                modifier = Modifier.weight(1f)
            )
            OverviewStatCard(
                label = "Best Streak",
                value = "${stats.bestStreak} Days",
                badgeText = "🔥 New Record",
                badgeBg = orangeBadgeBg,
                badgeTextColor = orangeBadgeText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OverviewStatCard(
    label: String,
    value: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    modifier: Modifier = Modifier
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val labelColor = Color(0xFF8B949E)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = labelColor,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }
    }
}

/**
 * Current Streak Banner with Mala Beads illustration on right
 */
@Composable
fun CurrentStreakCard(
    currentStreak: Int
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(orangeAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak Fire",
                        tint = orangeAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Current Streak",
                        fontSize = 12.sp,
                        color = labelColor
                    )
                    Text(
                        text = if (currentStreak == 1) "1 Day" else "$currentStreak Days",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (currentStreak > 0) "Keep it up! You're doing great." else "Start your jaap today!",
                        fontSize = 11.sp,
                        color = labelColor
                    )
                }
            }

            // Custom Canvas Illustration of Japa Mala / Sacred Beads
            Box(
                modifier = Modifier.size(68.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(64.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2.8f
                    val beadCount = 14
                    val beadRadius = 4.5.dp.toPx()

                    for (i in 0 until beadCount) {
                        val angleRad = (i * 2 * Math.PI / beadCount) - (Math.PI / 2)
                        val bx = center.x + radius * cos(angleRad).toFloat()
                        val by = center.y + radius * sin(angleRad).toFloat()

                        drawCircle(
                            color = Color(0xFFD97706),
                            radius = beadRadius,
                            center = Offset(bx, by)
                        )
                        drawCircle(
                            color = Color(0xFFFBBF24),
                            radius = beadRadius * 0.4f,
                            center = Offset(bx - 1f, by - 1f)
                        )
                    }

                    // Bottom Guru Bead / Tassel
                    val tasselTop = Offset(center.x, center.y + radius + beadRadius)
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = beadRadius * 1.2f,
                        center = tasselTop
                    )
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = tasselTop,
                        end = Offset(center.x - 4f, tasselTop.y + 12f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = tasselTop,
                        end = Offset(center.x + 4f, tasselTop.y + 12f),
                        strokeWidth = 3f
                    )
                }
            }
        }
    }
}

/**
 * Daily Activity Bar Chart Card with glowing orange vertical bars & Y-axis labels
 */
@Composable
fun DailyActivityBarChart(
    chartData: List<Pair<String, Int>>,
    selectedFilterName: String
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val orangeGlow = Color(0xFFFFAB40)
    val labelColor = Color(0xFF8B949E)

    val maxVal = chartData.maxOfOrNull { it.second } ?: 108
    val effectiveMax = if (maxVal == 0) 108 else maxVal

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Daily Activity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Chart Bars Area
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartData.forEach { (label, value) ->
                        val ratio = if (effectiveMax > 0 && value > 0) {
                            (value.toFloat() / effectiveMax).coerceIn(0.08f, 1f)
                        } else 0f

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            if (ratio > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.45f)
                                        .fillMaxHeight(ratio)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(orangeGlow, orangeAccent)
                                            )
                                        )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.45f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFF21262D))
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = labelColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Y-Axis Labels on the Right
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    val label5 = if (effectiveMax >= 1000) "${effectiveMax / 1000}k" else "$effectiveMax"
                    val label4 = if (effectiveMax >= 1000) "${(effectiveMax * 3) / 4000}k" else "${(effectiveMax * 3) / 4}"
                    val label3 = if (effectiveMax >= 1000) "${effectiveMax / 2000}k" else "${effectiveMax / 2}"
                    val label2 = if (effectiveMax >= 1000) "${effectiveMax / 4000}k" else "${effectiveMax / 4}"

                    Text(label5, fontSize = 10.sp, color = labelColor)
                    Text(label4, fontSize = 10.sp, color = labelColor)
                    Text(label3, fontSize = 10.sp, color = labelColor)
                    Text(label2, fontSize = 10.sp, color = labelColor)
                    Text("0", fontSize = 10.sp, color = labelColor)
                }
            }
        }
    }
}

/**
 * Screen 2: Sankalp Arc Progress Card with Glowing Ring Gauge
 */
@Composable
fun SankalpArcGaugeCard(
    activeSankalp: Sankalp?,
    onStartSankalp: () -> Unit,
    onManageSankalps: () -> Unit
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    val progressPct = if (activeSankalp != null && activeSankalp.targetMalasTotal > 0) {
        ((activeSankalp.completedMalas.toFloat() / activeSankalp.targetMalasTotal) * 100).toInt().coerceIn(0, 100)
    } else 0

    val titleText = activeSankalp?.name ?: "No Active Sankalp"
    val subtitleText = activeSankalp?.mantraText ?: "Tap below to set a new spiritual goal"

    val daysCompleted = if (activeSankalp != null) {
        val diffMs = System.currentTimeMillis() - activeSankalp.createdTimestamp
        (diffMs / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0).coerceAtMost(activeSankalp.durationDays)
    } else 0

    val daysRemaining = if (activeSankalp != null) {
        (activeSankalp.durationDays - daysCompleted).coerceAtLeast(0)
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (activeSankalp == null) onStartSankalp() else onManageSankalps()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Arc Ring Gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                val arcSweep = (progressPct / 100f) * 220f
                val gaugeGradient = remember {
                    Brush.sweepGradient(
                        colors = listOf(Color(0xFFFFB74D), orangeAccent, Color(0xFFFF6D00))
                    )
                }
                val trackColor = remember { Color(0xFF2D3748) }

                Canvas(modifier = Modifier.size(170.dp)) {
                    val arcStroke = 18.dp.toPx()

                    // Background Arc Track
                    drawArc(
                        color = trackColor,
                        startAngle = 160f,
                        sweepAngle = 220f,
                        useCenter = false,
                        style = Stroke(width = arcStroke, cap = StrokeCap.Round)
                    )

                    // Active Glowing Orange Arc
                    if (arcSweep > 0) {
                        drawArc(
                            brush = gaugeGradient,
                            startAngle = 160f,
                            sweepAngle = arcSweep,
                            useCenter = false,
                            style = Stroke(width = arcStroke, cap = StrokeCap.Round)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = if (activeSankalp != null) "Sankalp Goal Progress" else "Start Sankalp",
                        fontSize = 11.sp,
                        color = labelColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$progressPct%",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "Completed",
                        fontSize = 12.sp,
                        color = labelColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = subtitleText,
                fontSize = 13.sp,
                color = labelColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (activeSankalp != null) {
                // Linear Progress Bar
                LinearProgressIndicator(
                    progress = { progressPct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = orangeAccent,
                    trackColor = Color(0xFF2D3748)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$daysCompleted Days Completed",
                        fontSize = 11.sp,
                        color = labelColor
                    )
                    Text(
                        text = "$daysRemaining Days Remaining",
                        fontSize = 11.sp,
                        color = labelColor
                    )
                }
            } else {
                Button(
                    onClick = onStartSankalp,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeAccent)
                ) {
                    Text("+ Start New Sankalp", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Screen 2: Milestone Badges Section (Row of 4 hexagonal/shield badges with real progress)
 */
@Composable
fun MilestoneBadgesSection(
    currentStreak: Int,
    totalBeads: Int,
    totalMalas: Int = 0,
    longestStreak: Int = 0,
    onViewAll: () -> Unit,
    onSelectMilestone: ((SpiritualAchievement) -> Unit)? = null
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    val milestones = remember(currentStreak, longestStreak, totalMalas, totalBeads) {
        getSpiritualMilestones(currentStreak, longestStreak, totalMalas, totalBeads)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Milestone Badges",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "View all",
                    fontSize = 12.sp,
                    color = orangeAccent,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                milestones.forEach { milestone ->
                    val isAchieved = milestone.isUnlocked
                    val progressRatio = (milestone.currentProgress.toFloat() / milestone.targetProgress).coerceIn(0f, 1f)

                    val progressLabel = if (isAchieved) {
                        "Unlocked ✓"
                    } else {
                        when (milestone.id) {
                            "7_days_streak" -> "${milestone.currentProgress}/7 d"
                            "21_days_streak" -> "${milestone.currentProgress}/21 d"
                            "108_malas" -> "${milestone.currentProgress}/108 m"
                            "1_lakh_beads" -> {
                                if (milestone.currentProgress < 1000) "${milestone.currentProgress}/100k"
                                else "${String.format(Locale.getDefault(), "%.1fk", milestone.currentProgress / 1000f)}/100k"
                            }
                            else -> "${milestone.currentProgress}/${milestone.targetProgress}"
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectMilestone?.invoke(milestone) }
                            .padding(vertical = 4.dp)
                    ) {
                        val badgePath = remember { Path() }

                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                badgePath.apply {
                                    reset()
                                    moveTo(w * 0.5f, 0f)
                                    lineTo(w, h * 0.25f)
                                    lineTo(w, h * 0.75f)
                                    lineTo(w * 0.5f, h)
                                    lineTo(0f, h * 0.75f)
                                    lineTo(0f, h * 0.25f)
                                    close()
                                }

                                if (isAchieved) {
                                    drawPath(
                                        path = badgePath,
                                        color = Color(0xFF2D1E10)
                                    )
                                    drawPath(
                                        path = badgePath,
                                        color = orangeAccent,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                } else {
                                    drawPath(
                                        path = badgePath,
                                        color = Color(0xFF21262D)
                                    )
                                    drawPath(
                                        path = badgePath,
                                        color = Color(0xFF30363D),
                                        style = Stroke(width = 1.dp.toPx())
                                    )
                                }
                            }

                            if (isAchieved) {
                                Text(
                                    text = milestone.icon,
                                    fontSize = 20.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = labelColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = milestone.shortTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = progressLabel,
                            fontSize = 9.sp,
                            fontWeight = if (isAchieved) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAchieved) orangeAccent else labelColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )

                        if (!isAchieved) {
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progressRatio },
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = orangeAccent,
                                trackColor = Color(0xFF21262D)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 2: Recent Achievements List Section (Tappable items opening detail)
 */
@Composable
fun RecentAchievementsSection(
    totalBeads: Int,
    currentStreak: Int,
    totalMalas: Int = 0,
    longestStreak: Int = 0,
    recentSessions: List<Session> = emptyList(),
    onSelectAchievement: ((SpiritualAchievement) -> Unit)? = null
) {
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    val allAchievements = remember(totalBeads, currentStreak, totalMalas, longestStreak) {
        getAllSpiritualAchievements(currentStreak, longestStreak, totalMalas, totalBeads)
    }

    val unlockedAchievements = remember(allAchievements) {
        allAchievements.filter { it.isUnlocked }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recent Achievements",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (unlockedAchievements.isEmpty()) {
                Text(
                    text = "No achievements unlocked yet. Start your first Jaap to earn spiritual milestones!",
                    fontSize = 12.sp,
                    color = labelColor
                )
            } else {
                unlockedAchievements.take(3).forEachIndexed { idx, achievement ->
                    if (idx > 0) Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectAchievement?.invoke(achievement) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(orangeAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(achievement.icon, fontSize = 18.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = achievement.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = achievement.description,
                                    fontSize = 11.sp,
                                    color = labelColor,
                                    maxLines = 1
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF132A1C))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Unlocked",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 3: Detailed Insights Full Modal matching Screen 3 exact design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedInsightsBottomSheet(
    totalBeads: Int,
    totalMalas: Int,
    practiceDaysCount: Int,
    dailyGoal: Int,
    userName: String,
    mantraName: String,
    currentStreak: Int,
    longestStreak: Int,
    joinDate: String,
    activeSankalpName: String,
    sankalpCurrentDay: Int,
    sankalpTotalDays: Int,
    sankalpProgressPercent: Int,
    recentSessions: List<Session>,
    allDailyProgress: List<DailyProgress>,
    onExportCSV: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val darkBg = Color(0xFF0F1218)
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val greenBadgeText = Color(0xFF22C55E)
    val labelColor = Color(0xFF8B949E)

    var selectedTab by remember { mutableStateOf("Overview") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedWeekFilter by remember { mutableStateOf("This Week") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = darkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Back Arrow + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Detailed Insights",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Date Picker Button
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(darkCardBg)
                        .border(1.dp, darkBorder, RoundedCornerShape(12.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedWeekFilter,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = labelColor
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(darkCardBg)
                ) {
                    DropdownMenuItem(
                        text = { Text("This Week", color = Color.White) },
                        onClick = {
                            selectedWeekFilter = "This Week"
                            dropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Last Week", color = Color.White) },
                        onClick = {
                            selectedWeekFilter = "Last Week"
                            dropdownExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub Segmented Control: Overview | Malas | Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(darkCardBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Overview", "Malas", "Time").forEach { tab ->
                    val isSel = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) orangeAccent else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else labelColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Overview 2x2 Grid Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = darkCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Overview",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val formattedTotalBeads = String.format(Locale.getDefault(), "%,d", totalBeads)
                    val avgBeadsPerDay = if (practiceDaysCount > 0) totalBeads / practiceDaysCount else 0
                    val formattedAvgBeads = String.format(Locale.getDefault(), "%,d", avgBeadsPerDay)
                    val totalSecs = recentSessions.sumOf { ((it.endTimestamp - it.startTimestamp) / 1000).toInt() }
                    val hours = totalSecs / 3600
                    val mins = (totalSecs % 3600) / 60
                    val secs = totalSecs % 60
                    val formattedTime = when {
                        hours > 0 -> if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
                        mins > 0 -> if (secs > 0) "${mins}m ${secs}s" else "${mins}m"
                        else -> "${secs}s"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Malas", fontSize = 11.sp, color = labelColor)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$totalMalas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Beads", fontSize = 11.sp, color = labelColor)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formattedTotalBeads, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Average Beads / Day", fontSize = 11.sp, color = labelColor)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formattedAvgBeads, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Time", fontSize = 11.sp, color = labelColor)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formattedTime, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Daily Breakdown Card with Compact 7-Day Beads & Malas Visualization
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = darkCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val blueAccent = Color(0xFF38BDF8)

                    // Header Row with Connected Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Breakdown (Past 7 Days)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Connected Legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(orangeAccent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Beads", fontSize = 11.sp, color = labelColor)

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(blueAccent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Malas", fontSize = 11.sp, color = labelColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7-Day Real Data Preparation (both for chart and list)
                    val past7DaysData = remember(allDailyProgress, selectedWeekFilter) {
                        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val sdfDisplay = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
                        val sdfDayShort = SimpleDateFormat("EEE", Locale.getDefault())
                        val cal = Calendar.getInstance()
                        if (selectedWeekFilter == "Last Week") {
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                        }

                        val dateMap = allDailyProgress.associateBy { it.dateString }

                        // 7 days in chronological order (oldest to newest for chart display)
                        val days = mutableListOf<DailyBreakdownItem>()
                        for (i in 6 downTo 0) {
                            val curCal = cal.clone() as Calendar
                            curCal.add(Calendar.DAY_OF_YEAR, -i)
                            val dateStr = sdfInput.format(curCal.time)
                            val displayStr = sdfDisplay.format(curCal.time)
                            val dayShort = sdfDayShort.format(curCal.time)
                            val prog = dateMap[dateStr]

                            val bCount = prog?.totalBeadsCompleted ?: 0
                            val mCount = prog?.totalMalasCompleted ?: 0
                            days.add(
                                DailyBreakdownItem(
                                    dayShort = dayShort,
                                    dateDisplay = displayStr,
                                    beads = bCount,
                                    malas = mCount
                                )
                            )
                        }
                        days
                    }

                    val maxBeadsInWeek = past7DaysData.maxOfOrNull { it.beads } ?: 0
                    val maxMalasInWeek = past7DaysData.maxOfOrNull { it.malas } ?: 0

                    // Compact Dual-Bar Chart Area (Orange = Beads, Blue = Malas)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F1218))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            past7DaysData.forEach { dayItem ->
                                val beadRatio = if (maxBeadsInWeek > 0 && dayItem.beads > 0) {
                                    (dayItem.beads.toFloat() / maxBeadsInWeek).coerceIn(0.12f, 1f)
                                } else 0f

                                val malaRatio = if (maxMalasInWeek > 0 && dayItem.malas > 0) {
                                    (dayItem.malas.toFloat() / maxMalasInWeek).coerceIn(0.12f, 1f)
                                } else 0f

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    // Dual bars container
                                    Row(
                                        modifier = Modifier
                                            .height(58.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        // Orange Bar = Beads
                                        if (beadRatio > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .width(6.dp)
                                                    .fillMaxHeight(beadRatio)
                                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                                    .background(orangeAccent)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .width(6.dp)
                                                    .height(3.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(Color(0xFF21262D))
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(3.dp))

                                        // Blue Bar = Malas
                                        if (malaRatio > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .width(6.dp)
                                                    .fillMaxHeight(malaRatio)
                                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                                    .background(blueAccent)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .width(6.dp)
                                                    .height(3.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(Color(0xFF21262D))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = dayItem.dayShort,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (dayItem.beads > 0 || dayItem.malas > 0) Color.White else labelColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Existing Detailed List (Latest to Oldest)
                    val reversedList = remember(past7DaysData) { past7DaysData.reversed() }
                    reversedList.forEach { item ->
                        val bFormatted = String.format(Locale.getDefault(), "%,d", item.beads)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.dateDisplay,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1.2f)
                            )

                            Text(
                                text = bFormatted,
                                fontSize = 13.sp,
                                color = if (item.beads > 0) orangeAccent else Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )

                            Text(
                                text = "${item.malas} malas",
                                fontSize = 13.sp,
                                color = if (item.malas > 0) blueAccent else labelColor,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Bottom Sheet for All Milestones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestonesBottomSheet(
    totalBeads: Int,
    currentStreak: Int,
    totalMalas: Int = 0,
    longestStreak: Int = 0,
    onDismiss: () -> Unit,
    onSelectAchievement: ((SpiritualAchievement) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val darkBg = Color(0xFF0F1218)
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    val allAchievements = remember(totalBeads, currentStreak, totalMalas, longestStreak) {
        getAllSpiritualAchievements(currentStreak, longestStreak, totalMalas, totalBeads)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = darkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = orangeAccent, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("All Devotional Milestones", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = labelColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            allAchievements.forEach { achievement ->
                val isUnlocked = achievement.isUnlocked
                val progressPct = ((achievement.currentProgress.toFloat() / achievement.targetProgress) * 100f).coerceIn(0f, 100f)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectAchievement?.invoke(achievement) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = darkCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) orangeAccent else darkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(achievement.icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = achievement.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = achievement.requirementText,
                                        fontSize = 11.sp,
                                        color = labelColor
                                    )
                                }
                            }

                            Text(
                                text = if (isUnlocked) "Unlocked 🏆" else "${achievement.currentProgress}/${achievement.targetProgress}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) orangeAccent else labelColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progressPct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (isUnlocked) Color(0xFF4ADE80) else orangeAccent,
                            trackColor = darkBorder
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Bottom Sheet for Single Achievement Details (with Share and Save)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementDetailBottomSheet(
    achievement: SpiritualAchievement,
    currentStreak: Int = 0,
    longestStreak: Int = 0,
    totalMalas: Int = 0,
    totalBeads: Int = 0,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = androidx.compose.ui.platform.LocalContext.current

    val darkBg = Color(0xFF0F1218)
    val darkCardBg = Color(0xFF161B22)
    val darkBorder = Color(0xFF21262D)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    val progressPct = ((achievement.currentProgress.toFloat() / achievement.targetProgress) * 100f).coerceIn(0f, 100f)
    val remaining = (achievement.targetProgress - achievement.currentProgress).coerceAtLeast(0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = darkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spiritual Milestone",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = labelColor
                )

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = labelColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Big Hexagonal Badge
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(w * 0.5f, 0f)
                        lineTo(w, h * 0.25f)
                        lineTo(w, h * 0.75f)
                        lineTo(w * 0.5f, h)
                        lineTo(0f, h * 0.75f)
                        lineTo(0f, h * 0.25f)
                        close()
                    }

                    if (achievement.isUnlocked) {
                        drawPath(path = path, color = Color(0xFF2D1E10))
                        drawPath(path = path, color = orangeAccent, style = Stroke(width = 3.dp.toPx()))
                    } else {
                        drawPath(path = path, color = Color(0xFF161B22))
                        drawPath(path = path, color = darkBorder, style = Stroke(width = 1.5.dp.toPx()))
                    }
                }

                Text(
                    text = achievement.icon,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = achievement.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (achievement.isUnlocked) Color(0xFF132A1C) else Color(0xFF2D1E10))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (achievement.isUnlocked) "✓ Unlocked & Achieved" else "🔒 In Progress (${progressPct.toInt()}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) Color(0xFF4ADE80) else orangeAccent
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = darkCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = labelColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = achievement.description,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Unlock Requirement",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = labelColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = achievement.requirementText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = orangeAccent
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Current Progress",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = labelColor
                        )
                        Text(
                            text = "${achievement.currentProgress} / ${achievement.targetProgress} ${achievement.progressUnit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressPct / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (achievement.isUnlocked) Color(0xFF4ADE80) else orangeAccent,
                        trackColor = Color(0xFF21262D)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (achievement.isUnlocked) {
                            "Milestone completed! May your spiritual practice continue to blossom 🙏"
                        } else {
                            "$remaining ${achievement.progressUnit} remaining to unlock this milestone."
                        },
                        fontSize = 11.sp,
                        color = if (achievement.isUnlocked) Color(0xFF4ADE80) else labelColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions for Unlocked Milestones
            if (achievement.isUnlocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Share Button
                    Button(
                        onClick = {
                            val bitmap = DevotionalCardExporter.generateCardBitmap(
                                context = context,
                                cardType = "MILESTONE",
                                aspectRatio = "1:1",
                                templateId = "ROYAL_GOLD",
                                userName = "Devotee",
                                mantraName = "Naam Jaap",
                                currentStreak = currentStreak,
                                longestStreak = longestStreak,
                                totalBeads = totalBeads,
                                totalMalas = totalMalas,
                                milestoneTitle = achievement.title,
                                milestoneDevTitle = achievement.description
                            )
                            DevotionalCardExporter.shareCard(context, bitmap, achievement.title)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Card", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Save Image Button
                    Button(
                        onClick = {
                            val bitmap = DevotionalCardExporter.generateCardBitmap(
                                context = context,
                                cardType = "MILESTONE",
                                aspectRatio = "1:1",
                                templateId = "ROYAL_GOLD",
                                userName = "Devotee",
                                mantraName = "Naam Jaap",
                                currentStreak = currentStreak,
                                longestStreak = longestStreak,
                                totalBeads = totalBeads,
                                totalMalas = totalMalas,
                                milestoneTitle = achievement.title,
                                milestoneDevTitle = achievement.description
                            )
                            DevotionalCardExporter.saveAsPng(context, bitmap, "Milestone_${achievement.id}")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, darkBorder)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = orangeAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Image", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D))
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
