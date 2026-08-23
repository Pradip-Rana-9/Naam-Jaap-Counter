package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AvatarSelectionDialog
import com.example.ui.components.DailyGitaWisdomCard
import com.example.ui.components.DevoteeAuthDialog
import com.example.ui.components.DevotionalNotificationDialog
import com.example.ui.components.EkadashiCalendarSection
import com.example.ui.components.FullEkadashiCalendarBottomSheet
import com.example.ui.components.GuestModeReminderCard
import com.example.ui.components.HomeUpcomingEventsSection
import com.example.ui.components.ProfileEditDialog
import com.example.ui.components.SankalpTrackerSection
import com.example.ui.components.SpiritualAvatarGraphic
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.JaapViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

data class MilestoneBadgeData(
    val id: Int,
    val title: String,
    val devTitle: String,
    val threshold: Int
)

val milestoneBadgesList = listOf(
    MilestoneBadgeData(1, "7 Days Achieved", "Saptahik Sadhak", 7),
    MilestoneBadgeData(2, "21 Days Locked", "Tapashvi", 21),
    MilestoneBadgeData(3, "108 Malas Locked", "Mantra Siddhi", 108),
    MilestoneBadgeData(4, "1L Beads Locked", "Laksha Japin", 100000),
    MilestoneBadgeData(5, "10 Lakh Beads", "Maha Bhakta", 1000000)
)

// Precomputed angles for 21 beads (cos, sin) to eliminate per-frame trigonometry calculations
private val BEAD_ANGLES_21: List<Pair<Float, Float>> = (0 until 21).map { i ->
    val angleRad = Math.toRadians((i * (360.0 / 21) - 90)).toFloat()
    Pair(cos(angleRad), sin(angleRad))
}

@Composable
fun HomeScreen(
    viewModel: JaapViewModel,
    onNavigateToJaap: () -> Unit,
    onNavigateToProgress: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val userSettings by viewModel.userSettings.collectAsState()
    val selectedMantra by viewModel.selectedMantra.collectAsState()
    val todayProgress by viewModel.todayProgress.collectAsState()
    val weeklyCompletionDays by viewModel.weeklyCompletionDays.collectAsState()
    val totalMalas by viewModel.totalMalas.collectAsState()
    val totalBeads by viewModel.totalBeads.collectAsState()
    val streaks by viewModel.streaks.collectAsState()
    val allSankalps by viewModel.allSankalps.collectAsState()
    val allMantras by viewModel.allMantras.collectAsState()
    val todaySessions by viewModel.todaySessions.collectAsState()
    val showGuestReminder by viewModel.showGuestReminder.collectAsState()
    val panchangEvents by viewModel.upcomingPanchangEvents.collectAsState()

    var showProfileEditDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showFullCalendarSheet by remember { mutableStateOf(false) }

    val rawUserName = userSettings?.userName
    val userName = if (!rawUserName.isNullOrBlank()) rawUserName else "Devotee"
    val avatarId = userSettings?.avatarId ?: 1
    val dailyGoal = userSettings?.dailyGoalMalas ?: 10
    val reminderEnabled = userSettings?.reminderEnabled ?: false
    val completedToday = todayProgress?.totalMalasCompleted ?: 0
    val todayBeads = todayProgress?.totalBeadsCompleted ?: 0

    val todaySessionsCount = remember(todaySessions.size, todayBeads) {
        todaySessions.size.coerceAtLeast(if (todayBeads > 0) 1 else 0)
    }
    val completedSessionsSeconds = remember(todaySessions) {
        todaySessions.sumOf { ((it.endTimestamp - it.startTimestamp) / 1000L).coerceAtLeast(1L) }
    }
    val incompleteBeads = userSettings?.currentBeadInIncompleteMala ?: 0
    val incompleteSeconds = remember(incompleteBeads) {
        (incompleteBeads * 0.75).toLong()
    }
    val totalTodaySeconds = remember(completedSessionsSeconds, incompleteSeconds, todayBeads) {
        if (completedSessionsSeconds > 0) {
            completedSessionsSeconds + incompleteSeconds
        } else {
            (todayBeads * 0.75).toLong()
        }
    }

    val currentStreak = streaks.first
    val timeGreeting = remember { getTimeBasedGreeting() }
    val progressPct = remember(completedToday, dailyGoal) {
        if (dailyGoal > 0) ((completedToday.toFloat() / dailyGoal) * 100).toInt().coerceIn(0, 100) else 0
    }

    // Theme Colors matching dark spiritual UI
    val cardBg = Color(0xFF0F1522)
    val cardBorder = Color(0xFF222B3D)
    val textColorPrimary = Color(0xFFF1F5F9)
    val textColorSecondary = Color(0xFF94A3B8)
    val goldAccent = Color(0xFFFF9E00)
    val cyanAccent = Color(0xFF00D2FF)

    if (showProfileEditDialog) {
        ProfileEditDialog(
            currentName = userName,
            currentAvatarId = avatarId,
            onSave = { newName, newAvatarId ->
                viewModel.updateUserNameAndAvatar(newName, newAvatarId)
            },
            onDismissRequest = { showProfileEditDialog = false }
        )
    }

    if (showNotificationDialog) {
        DevotionalNotificationDialog(
            viewModel = viewModel,
            onDismiss = { showNotificationDialog = false }
        )
    }

    if (showFullCalendarSheet) {
        FullEkadashiCalendarBottomSheet(
            allEvents = panchangEvents,
            language = userSettings?.language ?: "ENGLISH",
            onDismiss = { showFullCalendarSheet = false },
            onSelectMantraForJaap = { mantraText ->
                val matchMantra = allMantras.firstOrNull { it.textEnglish.equals(mantraText, ignoreCase = true) || it.textHindi.equals(mantraText, ignoreCase = true) }
                if (matchMantra != null) {
                    viewModel.selectMantra(matchMantra.id)
                } else {
                    viewModel.addCustomMantra(mantraText)
                }
                onNavigateToJaap()
            },
            onSetDedicatedGoal = { goalMalas ->
                viewModel.updateDailyGoal(goalMalas)
            }
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. TOP HEADER (Avatar | Good Morning Devotee | Bell)
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SpiritualAvatarGraphic(
                        avatarId = avatarId,
                        sizeDp = 42.dp,
                        showBorder = true,
                        onClick = { showProfileEditDialog = true }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.clickable { showProfileEditDialog = true }
                    ) {
                        Text(
                            text = timeGreeting,
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = goldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                        .border(1.dp, cardBorder, CircleShape)
                        .clickable { showNotificationDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Devotional Notifications & Reminders",
                        tint = if (reminderEnabled) goldAccent else textColorPrimary,
                        modifier = Modifier.size(20.dp)
                    )

                    // Active reminder indicator dot
                    if (reminderEnabled) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 7.dp, end = 7.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(goldAccent)
                                .border(1.5.dp, cardBg, CircleShape)
                        )
                    }
                }
            }
        }

        // GUEST MODE AWARENESS & DATA PROTECTION REMINDER
        if (showGuestReminder) {
            item(key = "guest_mode_reminder") {
                GuestModeReminderCard(
                    totalBeads = totalBeads,
                    totalMalas = totalMalas,
                    onOpenAuth = { showAuthDialog = true },
                    onDismiss = { viewModel.dismissGuestReminder() }
                )
            }
        }

        // 2. MAIN HERO SECTION (Circular Mala Ring + Stats Cards + Start Jaap Button)
        item(key = "hero_section") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_today_goal"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Circular Mala Ring Graphic
                        MalaBeadRingGraphic(
                            completedToday = completedToday,
                            dailyGoal = dailyGoal,
                            modifier = Modifier.size(132.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Right: 2 Stacked Compact Stats Cards
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Day Streak Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF141C2E))
                                    .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(goldAccent.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = "Streak",
                                            tint = goldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "$currentStreak Days",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColorPrimary
                                        )
                                        Text(
                                            text = "Current Streak",
                                            fontSize = 10.5.sp,
                                            color = textColorSecondary
                                        )
                                    }
                                }
                            }

                            // Goal Progress Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF141C2E))
                                    .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(cyanAccent.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PieChart,
                                            contentDescription = "Goal Progress",
                                            tint = cyanAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "$progressPct%",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColorPrimary
                                        )
                                        Text(
                                            text = "Daily Goal",
                                            fontSize = 10.5.sp,
                                            color = textColorSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Start Jaap Button (Cyan to Gold Gradient Pill)
                    Button(
                        onClick = { onNavigateToJaap() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_start_jaap_session"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(cyanAccent, goldAccent)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    tint = Color(0xFF090D16),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (completedToday > 0) "Continue Jaap" else "Start Jaap",
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF090D16)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. TODAY'S PRACTICE & STREAK SUMMARY (Compact Dashboard Section)
        item(key = "today_practice") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = goldAccent,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Today's Practice",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onNavigateToProgress() }
                        ) {
                            Text(
                                text = "Full stats",
                                fontSize = 12.sp,
                                color = goldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = goldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Compact metric pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryMetricCard(
                            value = "$completedToday",
                            label = "Malas",
                            icon = Icons.Default.SelfImprovement,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            value = "$todayBeads",
                            label = "Beads",
                            icon = Icons.Default.BarChart,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            value = formatChantingDuration(totalTodaySeconds),
                            label = "Time",
                            icon = Icons.Default.Timer,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            value = "$todaySessionsCount",
                            label = "Sessions",
                            icon = Icons.Default.AutoAwesome,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Compact 7-Day streak dots row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1522))
                            .border(1.dp, Color(0xFF222B3D), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        days.forEachIndexed { idx, dayLabel ->
                            val isDone = weeklyCompletionDays.contains(idx)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 10.sp,
                                    color = if (isDone) goldAccent else textColorSecondary,
                                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                if (isDone) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(goldAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Completed",
                                            tint = Color(0xFF090D16),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF141C2E))
                                            .border(1.dp, cardBorder.copy(alpha = 0.6f), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. UPCOMING EVENTS & NEXT VRAT (Compact Dashboard: 1 Featured Vrat + 2-3 Event Carousel + View All)
        item(key = "ekadashi_section") {
            HomeUpcomingEventsSection(
                allEvents = panchangEvents,
                language = userSettings?.language ?: "ENGLISH",
                onViewAll = { showFullCalendarSheet = true },
                onSelectMantraForJaap = { mantraText ->
                    val matchMantra = allMantras.firstOrNull { it.textEnglish.equals(mantraText, ignoreCase = true) || it.textHindi.equals(mantraText, ignoreCase = true) }
                    if (matchMantra != null) {
                        viewModel.selectMantra(matchMantra.id)
                    } else {
                        viewModel.addCustomMantra(mantraText)
                    }
                    onNavigateToJaap()
                },
                onSetDedicatedGoal = { goalMalas ->
                    viewModel.updateDailyGoal(goalMalas)
                }
            )
        }

        // 5. QUICK ACTIONS GRID (4 Pill Buttons: Sankalp, Ekadashi, Achievements, History)
        item(key = "quick_actions") {
            Column {
                Text(
                    text = "Quick Actions",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Flag,
                        label = "Sankalp",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(7)
                            }
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.CalendarToday,
                        label = "Ekadashi",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showFullCalendarSheet = true
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.EmojiEvents,
                        label = "Badges",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(8)
                            }
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.History,
                        label = "History",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProgress
                    )
                }
            }
        }

        // 6. TODAY'S MANTRA CARD
        item(key = "today_mantra") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Today's Mantra",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedMantra?.textEnglish ?: "Radhe Radhe",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = goldAccent
                        )
                        if (!selectedMantra?.textHindi.isNullOrBlank() && selectedMantra?.textHindi != selectedMantra?.textEnglish) {
                            Text(
                                text = selectedMantra?.textHindi ?: "राधे राधे",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = textColorPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedMantra?.description ?: "Divine love and supreme devotion to Sri Radha Rani.",
                            fontSize = 11.5.sp,
                            color = textColorSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Krishna Flute Artwork
                    KrishnaFluteGraphic(
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
        }

        // 7. SACRED SANKALP SECTION
        item(key = "sankalp_section") {
            SankalpTrackerSection(
                sankalps = allSankalps,
                allMantras = allMantras,
                defaultDailyGoal = dailyGoal,
                language = userSettings?.language ?: "HINGLISH",
                onCreateSankalp = { name, mantra, duration, target, startDate, notes ->
                    viewModel.createSankalp(name, mantra, duration, target, startDate, notes)
                },
                onCancelSankalp = { sankalp ->
                    viewModel.cancelSankalp(sankalp)
                },
                onDeleteSankalp = { id ->
                    viewModel.deleteSankalp(id)
                },
                onNavigateToJaap = onNavigateToJaap
            )
        }

        // 8. MILESTONE BADGES
        item(key = "milestone_badges") {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Milestone Badges",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Text(
                        text = "View all >",
                        fontSize = 12.sp,
                        color = goldAccent,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToProgress() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(milestoneBadgesList, key = { it.id }) { badge ->
                        val isEarned = totalBeads >= badge.threshold || (badge.id == 1 && currentStreak >= 7)
                        Card(
                            modifier = Modifier
                                .width(110.dp)
                                .clickable { onNavigateToProgress() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isEarned) goldAccent else cardBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isEarned) goldAccent.copy(alpha = 0.2f) else Color(0xFF141C2E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isEarned) {
                                        Text(
                                            text = "7",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = goldAccent
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = textColorSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = badge.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )

                                Text(
                                    text = if (isEarned) "Achieved" else "Locked",
                                    fontSize = 9.5.sp,
                                    color = if (isEarned) goldAccent else textColorSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 9. DAILY WISDOM (BHAGAVAD GITA QUOTE)
        item(key = "gita_wisdom") {
            DailyGitaWisdomCard(
                language = userSettings?.language ?: "HINGLISH"
            )
        }

        // 10. KEEP GOING MOTIVATIONAL BANNER
        item(key = "motivational_banner") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keep Going!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = goldAccent
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Every bead takes you closer to Krishna.",
                            fontSize = 12.sp,
                            color = textColorPrimary,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    KrishnaFluteGraphic(
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showAuthDialog) {
        DevoteeAuthDialog(
            viewModel = viewModel,
            initialTab = 1,
            onDismissRequest = { showAuthDialog = false },
            onAuthSuccess = {
                showAuthDialog = false
            }
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1522))
            .border(1.dp, Color(0xFF222B3D), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFFF9E00),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = if (label.length > 9) 9.5.sp else 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF1F5F9),
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SummaryMetricCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1522))
            .border(1.dp, Color(0xFF222B3D), RoundedCornerShape(16.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF00D2FF),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F5F9)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

private val FLUTE_HOLE_RATIOS = floatArrayOf(0.3f, 0.45f, 0.6f, 0.75f)

@Composable
fun MalaBeadRingGraphic(
    completedToday: Int,
    dailyGoal: Int,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 145.dp
) {
    val progress = if (dailyGoal > 0) (completedToday.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
    val goldColor = remember { Color(0xFFFF9E00) }
    val goldColorAlpha = remember { Color(0xFFFF9E00).copy(alpha = 0.25f) }
    val darkBeadColor = remember { Color(0xFF241C14) }
    val strokeGoldColor = remember { Color(0xFFFFD700) }
    val strokeDarkColor = remember { Color(0xFF443322) }
    val guruColor = remember { Color(0xFFFFB700) }

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.width / 2f) - 16.dp.toPx()
            val totalBeads = 21
            val beadRadius = 5.5.dp.toPx()
            val activeBeads = (progress * totalBeads).toInt()
            val strokeWidth = 1.2.dp.toPx()

            for (i in 0 until totalBeads) {
                val (cosA, sinA) = BEAD_ANGLES_21[i]
                val x = center.x + radius * cosA
                val y = center.y + radius * sinA

                val isCompleted = i < activeBeads
                val beadColor = if (isCompleted) goldColor else darkBeadColor
                val strokeColor = if (isCompleted) strokeGoldColor else strokeDarkColor

                if (isCompleted) {
                    drawCircle(
                        color = goldColorAlpha,
                        radius = beadRadius * 1.4f,
                        center = Offset(x, y)
                    )
                }

                drawCircle(
                    color = beadColor,
                    radius = beadRadius,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = strokeColor,
                    radius = beadRadius,
                    center = Offset(x, y),
                    style = Stroke(width = strokeWidth)
                )
            }

            // Guru bead at bottom
            val guruX = center.x
            val guruY = center.y + radius
            drawCircle(
                color = guruColor,
                radius = 7.dp.toPx(),
                center = Offset(guruX, guruY)
            )

            // Tassel lines
            val tasselTopY = guruY + 6.dp.toPx()
            val tasselEndLeft = Offset(guruX - 5.dp.toPx(), guruY + 16.dp.toPx())
            val tasselEndCenter = Offset(guruX, guruY + 18.dp.toPx())
            val tasselEndRight = Offset(guruX + 5.dp.toPx(), guruY + 16.dp.toPx())
            val tasselTop = Offset(guruX, tasselTopY)
            val tasselStroke = 1.8.dp.toPx()

            drawLine(
                color = goldColor,
                start = tasselTop,
                end = tasselEndLeft,
                strokeWidth = tasselStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = goldColor,
                start = tasselTop,
                end = tasselEndCenter,
                strokeWidth = tasselStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = goldColor,
                start = tasselTop,
                end = tasselEndRight,
                strokeWidth = tasselStroke,
                cap = StrokeCap.Round
            )
        }

        // Center Overlay Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Today's Goal",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "$completedToday / $dailyGoal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F5F9)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "Malas",
                fontSize = 11.sp,
                color = Color(0xFFFF9E00),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun KrishnaFluteGraphic(modifier: Modifier = Modifier) {
    val fluteColor = remember { Color(0xFFFFB700) }
    val holeColor = remember { Color(0xFF090D16) }
    val peacockOuter = remember { Color(0xFF00D2FF).copy(alpha = 0.5f) }
    val peacockMid = remember { Color(0xFF0072FF) }
    val peacockInner = remember { Color(0xFFFF9E00) }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Flute
        drawLine(
            color = fluteColor,
            start = Offset(w * 0.1f, h * 0.7f),
            end = Offset(w * 0.9f, h * 0.35f),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Holes
        val holeRadius = 2.dp.toPx()
        for (ratio in FLUTE_HOLE_RATIOS) {
            val hx = w * (0.1f + ratio * 0.8f)
            val hy = h * (0.7f - ratio * 0.35f)
            drawCircle(
                color = holeColor,
                radius = holeRadius,
                center = Offset(hx, hy)
            )
        }

        // Peacock Feather top
        val fx = w * 0.82f
        val fy = h * 0.28f
        drawCircle(
            color = peacockOuter,
            radius = 10.dp.toPx(),
            center = Offset(fx, fy)
        )
        drawCircle(
            color = peacockMid,
            radius = 6.dp.toPx(),
            center = Offset(fx, fy)
        )
        drawCircle(
            color = peacockInner,
            radius = 3.dp.toPx(),
            center = Offset(fx, fy)
        )
    }
}

fun getTimeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 4..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        in 17..21 -> "Good Evening,"
        else -> "Good Night,"
    }
}

fun formatChantingDuration(totalSeconds: Long): String {
    if (totalSeconds <= 0L) return "0s"
    val hours = totalSeconds / 3600L
    val mins = (totalSeconds % 3600L) / 60L
    val secs = totalSeconds % 60L

    return when {
        hours > 0 -> if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
        mins > 0 -> if (secs > 0) "${mins}m ${secs}s" else "${mins}m"
        else -> "${secs}s"
    }
}
