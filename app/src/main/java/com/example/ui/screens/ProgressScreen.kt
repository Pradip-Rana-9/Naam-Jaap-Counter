package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AchievementDetailBottomSheet
import com.example.ui.components.CreateSankalpBottomSheet
import com.example.ui.components.CurrentStreakCard
import com.example.ui.components.DailyActivityBarChart
import com.example.ui.components.DetailedInsightsBottomSheet
import com.example.ui.components.MilestoneBadgesSection
import com.example.ui.components.MilestonesBottomSheet
import com.example.ui.components.OverviewStatsGrid
import com.example.ui.components.RecentAchievementsSection
import com.example.ui.components.SankalpArcGaugeCard
import com.example.ui.components.SankalpTrackerSection
import com.example.ui.components.SpiritualAchievement
import com.example.ui.components.TimeFilterSegmentedControl
import com.example.ui.components.computeFilteredStats
import com.example.ui.viewmodel.JaapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: JaapViewModel) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val totalMalas by viewModel.totalMalas.collectAsState()
    val totalBeads by viewModel.totalBeads.collectAsState()
    val streaks by viewModel.streaks.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()
    val selectedMantra by viewModel.selectedMantra.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val allDailyProgress by viewModel.allDailyProgress.collectAsState()
    val activeSankalps by viewModel.activeSankalps.collectAsState()
    val allSankalps by viewModel.allSankalps.collectAsState()
    val allMantras by viewModel.allMantras.collectAsState()

    val currentStreak = streaks.first
    val longestStreak = streaks.second

    val practiceDaysCount = remember(allDailyProgress) {
        allDailyProgress.count { it.totalBeadsCompleted > 0 || it.totalMalasCompleted > 0 }
    }
    val dailyGoal = userSettings?.dailyGoalMalas ?: 10
    val userName = userSettings?.userName?.takeIf { it.isNotBlank() } ?: "Devotee"
    val mantraName = selectedMantra?.textEnglish ?: "Radhe Radhe"
    val joinDate = userSettings?.joinDateString ?: "02 Aug 2026"

    val firstActiveSankalp = activeSankalps.firstOrNull()
    val activeSankalpName = firstActiveSankalp?.name ?: "21-Day Radhe Radhe Sankalp"
    val sankalpTotalDays = firstActiveSankalp?.durationDays ?: 21
    val sankalpCurrentDay = 8
    val sankalpProgressPercent = remember(firstActiveSankalp) {
        if (firstActiveSankalp != null && firstActiveSankalp.targetMalasTotal > 0) {
            ((firstActiveSankalp.completedMalas.toFloat() / firstActiveSankalp.targetMalasTotal) * 100).toInt().coerceIn(0, 100)
        } else 38
    }

    var selectedFilter by remember { mutableStateOf("Week") }

    // Sub-screens & Sheet States
    var showDetailedInsightsSheet by remember { mutableStateOf(false) }
    var showMilestonesSheet by remember { mutableStateOf(false) }
    var showCreateSankalpSheet by remember { mutableStateOf(false) }
    var showSankalpManagerSheet by remember { mutableStateOf(false) }
    var selectedAchievementForDetail by remember { mutableStateOf<SpiritualAchievement?>(null) }

    // Dynamic Filtered Calculations memoized with remember
    val filteredStats = remember(allDailyProgress, selectedFilter, currentStreak, longestStreak) {
        computeFilteredStats(
            allProgress = allDailyProgress,
            filter = selectedFilter,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }

    val darkBg = Color(0xFF0F1218)
    val darkCardBg = Color(0xFF161B22)
    val orangeAccent = Color(0xFFFF8C00)
    val labelColor = Color(0xFF8B949E)

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Header Row
        item(key = "progress_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progress",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Track your spiritual growth",
                        fontSize = 13.sp,
                        color = labelColor
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(darkCardBg)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Time Filter Segmented Control
        item(key = "time_filter") {
            TimeFilterSegmentedControl(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // 3. Overview 2x2 Grid (Screen 1 layout)
        item(key = "overview_grid") {
            OverviewStatsGrid(stats = filteredStats)
        }

        // 4. Current Streak Banner (Screen 1 layout with flame icon and Mala beads illustration)
        item(key = "streak_card") {
            CurrentStreakCard(currentStreak = currentStreak)
        }

        // 5. Daily Activity Bar Chart (Screen 1 layout with Y-axis labels and glowing orange vertical bars)
        item(key = "activity_chart") {
            DailyActivityBarChart(
                chartData = filteredStats.chartData,
                selectedFilterName = filteredStats.filterName
            )
        }

        // 6. Sankalp Progress Ring Card (Screen 2 layout with glowing arc ring gauge)
        item(key = "sankalp_gauge") {
            SankalpArcGaugeCard(
                activeSankalp = firstActiveSankalp,
                onStartSankalp = { showCreateSankalpSheet = true },
                onManageSankalps = { showSankalpManagerSheet = true }
            )
        }

        // 7. Milestone Badges Section (Screen 2 layout with hexagonal/shield badges)
        item(key = "milestone_badges") {
            MilestoneBadgesSection(
                currentStreak = currentStreak,
                totalBeads = totalBeads,
                totalMalas = totalMalas,
                longestStreak = longestStreak,
                onViewAll = { showMilestonesSheet = true },
                onSelectMilestone = { selectedAchievementForDetail = it }
            )
        }

        // 8. Recent Achievements Section (Screen 2 layout)
        item(key = "recent_achievements") {
            RecentAchievementsSection(
                totalBeads = totalBeads,
                currentStreak = currentStreak,
                totalMalas = totalMalas,
                longestStreak = longestStreak,
                recentSessions = recentSessions,
                onSelectAchievement = { selectedAchievementForDetail = it }
            )
        }

        // 9. Detailed Insights Action Button (Screen 3 trigger)
        item(key = "btn_insights") {
            Button(
                onClick = { showDetailedInsightsSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_view_detailed_insights"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, orangeAccent)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = orangeAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "View Detailed Insights",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = orangeAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item(key = "progress_bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // BOTTOM SHEETS FOR DETAILED INSIGHTS, MILESTONES, & SANKALP MANAGEMENT
    if (showDetailedInsightsSheet) {
        DetailedInsightsBottomSheet(
            totalBeads = totalBeads,
            totalMalas = totalMalas,
            practiceDaysCount = practiceDaysCount,
            dailyGoal = dailyGoal,
            userName = userName,
            mantraName = mantraName,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            joinDate = joinDate,
            activeSankalpName = activeSankalpName,
            sankalpCurrentDay = sankalpCurrentDay,
            sankalpTotalDays = sankalpTotalDays,
            sankalpProgressPercent = sankalpProgressPercent,
            recentSessions = recentSessions,
            allDailyProgress = allDailyProgress,
            onExportCSV = { viewModel.exportDataAsCSV(context) },
            onDismiss = { showDetailedInsightsSheet = false }
        )
    }

    if (showMilestonesSheet) {
        MilestonesBottomSheet(
            totalBeads = totalBeads,
            currentStreak = currentStreak,
            totalMalas = totalMalas,
            longestStreak = longestStreak,
            onDismiss = { showMilestonesSheet = false },
            onSelectAchievement = { achievement ->
                showMilestonesSheet = false
                selectedAchievementForDetail = achievement
            }
        )
    }

    selectedAchievementForDetail?.let { achievement ->
        AchievementDetailBottomSheet(
            achievement = achievement,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalMalas = totalMalas,
            totalBeads = totalBeads,
            onDismiss = { selectedAchievementForDetail = null }
        )
    }

    if (showCreateSankalpSheet) {
        CreateSankalpBottomSheet(
            allMantras = allMantras,
            defaultDailyGoal = dailyGoal,
            language = userSettings?.language ?: "HINGLISH",
            onDismiss = { showCreateSankalpSheet = false },
            onCreate = { name, mantra, duration, goal, startDate, notes ->
                viewModel.createSankalp(name, mantra, duration, goal, startDate, notes)
                showCreateSankalpSheet = false
            }
        )
    }

    if (showSankalpManagerSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSankalpManagerSheet = false },
            sheetState = sheetState,
            containerColor = darkCardBg
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                SankalpTrackerSection(
                    sankalps = allSankalps,
                    allMantras = allMantras,
                    defaultDailyGoal = dailyGoal,
                    language = userSettings?.language ?: "HINGLISH",
                    onCreateSankalp = { name, mantra, duration, goal, startDate, notes ->
                        viewModel.createSankalp(name, mantra, duration, goal, startDate, notes)
                    },
                    onCancelSankalp = { sankalp ->
                        viewModel.cancelSankalp(sankalp)
                    },
                    onDeleteSankalp = { id ->
                        viewModel.deleteSankalp(id)
                    },
                    onNavigateToJaap = {
                        showSankalpManagerSheet = false
                        viewModel.setTab(1)
                    }
                )
            }
        }
    }
}
