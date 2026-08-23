package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MilestoneCardPreset(
    val title: String,
    val devTitle: String,
    val thresholdBeads: Int
)

val milestonePresetsList = listOf(
    MilestoneCardPreset("1K Beads", "Pratham Bhakta", 1000),
    MilestoneCardPreset("10K Beads", "Japn Explorer", 10000),
    MilestoneCardPreset("50K Beads", "Sadhak", 50000),
    MilestoneCardPreset("1 Lakh Beads", "Mantra Upaasaka", 100000),
    MilestoneCardPreset("10 Lakh Beads", "Maha Bhakta", 1000000),
    MilestoneCardPreset("1 Crore Beads", "Param Siddha", 10000000)
)

data class CardTemplate(
    val id: String,
    val name: String,
    val bgGradient: List<Color>,
    val borderColor: Color,
    val accentColor: Color,
    val textColor: Color,
    val subTextColor: Color,
    val glassBg: Color
)

val cardTemplatesList = listOf(
    CardTemplate(
        id = "ROYAL_GOLD",
        name = "Royal Gold 👑",
        bgGradient = listOf(Color(0xFF0C0414), Color(0xFF1E0A2A), Color(0xFF2E004F), Color(0xFF0F0C20)),
        borderColor = Color(0xFFFFD700),
        accentColor = Color(0xFFFFD700),
        textColor = Color.White,
        subTextColor = Color(0xFFFFE082),
        glassBg = Color(0x33FFFFFF)
    ),
    CardTemplate(
        id = "VRINDAVAN",
        name = "Vrindavan 🦚",
        bgGradient = listOf(Color(0xFF00221C), Color(0xFF00382E), Color(0xFF004D40), Color(0xFF001510)),
        borderColor = Color(0xFF80CBC4),
        accentColor = Color(0xFF4DB6AC),
        textColor = Color.White,
        subTextColor = Color(0xFFE0F2F1),
        glassBg = Color(0x38FFFFFF)
    ),
    CardTemplate(
        id = "RADHA_LOTUS",
        name = "Radha Lotus 🪷",
        bgGradient = listOf(Color(0xFF2A001A), Color(0xFF4A002E), Color(0xFF6A0042), Color(0xFF1A0010)),
        borderColor = Color(0xFFFF80AB),
        accentColor = Color(0xFFFF4081),
        textColor = Color.White,
        subTextColor = Color(0xFFFFD1DC),
        glassBg = Color(0x38FFFFFF)
    ),
    CardTemplate(
        id = "MIDNIGHT",
        name = "Midnight 🌌",
        bgGradient = listOf(Color(0xFF050A1A), Color(0xFF0A1535), Color(0xFF101F4D), Color(0xFF02040B)),
        borderColor = Color(0xFF82B1FF),
        accentColor = Color(0xFF448AFF),
        textColor = Color.White,
        subTextColor = Color(0xFFE3F2FD),
        glassBg = Color(0x38FFFFFF)
    ),
    CardTemplate(
        id = "TEMPLE_HERITAGE",
        name = "Temple Heritage 🛕",
        bgGradient = listOf(Color(0xFF1F0F03), Color(0xFF381C08), Color(0xFF522A0C), Color(0xFF120801)),
        borderColor = Color(0xFFFFB74D),
        accentColor = Color(0xFFFF9800),
        textColor = Color.White,
        subTextColor = Color(0xFFFFE0B2),
        glassBg = Color(0x38FFFFFF)
    ),
    CardTemplate(
        id = "MINIMAL_WHITE",
        name = "Minimal White ☀️",
        bgGradient = listOf(Color(0xFFFFFDF5), Color(0xFFFFF9E6), Color(0xFFFFF3CC), Color(0xFFFFFBF0)),
        borderColor = Color(0xFFE65100),
        accentColor = Color(0xFFF57C00),
        textColor = Color(0xFF212121),
        subTextColor = Color(0xFFE65100),
        glassBg = Color(0x1A000000)
    )
)

val dailyQuotesList = listOf(
    "Every Holy Name brings you closer to Krishna.",
    "Chanting cleanses the dust from the mirror of the mind.",
    "Fix your mind on Supreme Divine Consciousness.",
    "In constant devotion, true inner peace is attained.",
    "Surrender all actions to the Lord and rejoice in grace."
)

@Composable
fun DevotionalAchievementCardSection(
    userName: String = "Devotee",
    mantraName: String = "Radhe Radhe",
    dailyGoal: Int = 10,
    currentStreak: Int = 8,
    longestStreak: Int = 14,
    joinDate: String = "02 Aug 2026",
    totalBeads: Int = 4520,
    totalMalas: Int = 42,
    activeSankalpName: String = "21-Day Radhe Radhe Sankalp",
    sankalpCurrentDay: Int = 8,
    sankalpTotalDays: Int = 21,
    sankalpProgressPercent: Int = 38,
    latestBadgeName: String = "Pratham Bhakta",
    earnedBadgesCount: Int = 5
) {
    val context = LocalContext.current

    var cardType by remember { mutableStateOf("SANKALP") } // "SANKALP" or "MILESTONE"
    var selectedAspectRatio by remember { mutableStateOf("9:16") } // "9:16", "1:1", "16:9"
    var selectedTemplate by remember { mutableStateOf(cardTemplatesList[0]) }
    var selectedMilestone by remember { mutableStateOf(milestonePresetsList[0]) }
    var showCelebrationDialog by remember { mutableStateOf(false) }

    // Download Success Dialog state
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    var savedFilePathStr by remember { mutableStateOf("") }
    var savedFileUri by remember { mutableStateOf<Uri?>(null) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val todayDateStr = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    }
    val todayTimeStr = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title & Subtitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🏆 Devotional Achievement Card",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Text(
                    text = "Generates live spiritual cards with automatic user stats.",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(activeColor.copy(alpha = 0.15f))
                    .border(1.dp, activeColor, RoundedCornerShape(20.dp))
                    .clickable { showCelebrationDialog = true }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Celebration 🎉",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card Type Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (cardType == "SANKALP") activeColor else cardBg)
                    .border(1.dp, if (cardType == "SANKALP") activeColor else cardBorder, RoundedCornerShape(16.dp))
                    .clickable { cardType = "SANKALP" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🚩 My Daily Sankalp",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (cardType == "SANKALP") MaterialTheme.colorScheme.onPrimary else textColorPrimary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (cardType == "MILESTONE") activeColor else cardBg)
                    .border(1.dp, if (cardType == "MILESTONE") activeColor else cardBorder, RoundedCornerShape(16.dp))
                    .clickable { cardType = "MILESTONE" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆 Milestone Badge",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (cardType == "MILESTONE") MaterialTheme.colorScheme.onPrimary else textColorPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Milestone Selector if Milestone Card selected
        if (cardType == "MILESTONE") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                milestonePresetsList.forEach { m ->
                    val isSel = selectedMilestone.title == m.title
                    val isUnlocked = totalBeads >= m.thresholdBeads
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) secondaryColor else cardBg)
                            .border(
                                1.dp,
                                if (isSel) secondaryColor else if (isUnlocked) activeColor else cardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedMilestone = m }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isSel) MaterialTheme.colorScheme.onSecondary else activeColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = m.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) MaterialTheme.colorScheme.onSecondary else textColorPrimary
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Template Themes Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Card Theme Style:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColorSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cardTemplatesList.forEach { t ->
                    val isSel = selectedTemplate.id == t.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) t.borderColor.copy(alpha = 0.25f) else cardBg)
                            .border(
                                1.5.dp,
                                if (isSel) t.borderColor else cardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedTemplate = t }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = t.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) t.borderColor else textColorPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Aspect Ratio Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aspect Ratio:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColorSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Pair("Story (9:16)", "9:16"),
                    Pair("Square (1:1)", "1:1"),
                    Pair("Cover (16:9)", "16:9")
                ).forEach { ratio ->
                    val isSel = selectedAspectRatio == ratio.second
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) activeColor.copy(alpha = 0.2f) else cardBorder)
                            .border(1.dp, if (isSel) activeColor else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { selectedAspectRatio = ratio.second }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = ratio.first,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) activeColor else textColorPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Card Visual Preview Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("preview_devotional_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, selectedTemplate.borderColor)
        ) {
            val aspectFloat = when (selectedAspectRatio) {
                "9:16" -> 9f / 16f
                "16:9" -> 16f / 9f
                else -> 1f
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = Brush.verticalGradient(colors = selectedTemplate.bgGradient))
                    .border(2.dp, selectedTemplate.borderColor, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // HERO SECTION: Sacred Golden Om & Mandala Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(selectedTemplate.borderColor, selectedTemplate.accentColor)
                                    )
                                )
                                .border(1.5.dp, Color(0xFFFFF8E1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ॐ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2A0D08)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "❖ DEVOTIONAL ACHIEVEMENT ❖",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = selectedTemplate.borderColor,
                            letterSpacing = 1.2.sp
                        )

                        Text(
                            text = "Every Holy Name brings you closer to Krishna",
                            fontSize = 9.sp,
                            color = selectedTemplate.subTextColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // USER PROFILE GLASS CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(selectedTemplate.glassBg)
                            .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(selectedTemplate.borderColor, selectedTemplate.accentColor)
                                        )
                                    )
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🪶",
                                    fontSize = 26.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = userName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = selectedTemplate.textColor
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(selectedTemplate.borderColor.copy(alpha = 0.25f))
                                            .border(1.dp, selectedTemplate.borderColor, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "⭐ $latestBadgeName",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = selectedTemplate.subTextColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "📅 Since $joinDate",
                                        fontSize = 9.sp,
                                        color = selectedTemplate.subTextColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // CURRENT MANTRA BANNER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectedTemplate.borderColor.copy(alpha = 0.15f))
                            .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(vertical = 6.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📿 ", fontSize = 12.sp)
                            Text(
                                text = "CURRENT MANTRA: ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedTemplate.borderColor
                            )
                            Text(
                                text = mantraName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = selectedTemplate.textColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2x2 STATISTICS GRID
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Daily Goal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedTemplate.glassBg)
                                .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎯 DAILY GOAL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.subTextColor)
                                Text("$dailyGoal", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = selectedTemplate.textColor)
                                Text("Malas", fontSize = 8.sp, color = selectedTemplate.subTextColor)
                            }
                        }

                        // Streak
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedTemplate.glassBg)
                                .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥 STREAK", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.subTextColor)
                                Text("$currentStreak", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = selectedTemplate.textColor)
                                Text("Days", fontSize = 8.sp, color = selectedTemplate.subTextColor)
                            }
                        }

                        // Lifetime Beads
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedTemplate.glassBg)
                                .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔵 LIFETIME", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.subTextColor)
                                Text("$totalBeads", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = selectedTemplate.textColor)
                                Text("Beads", fontSize = 8.sp, color = selectedTemplate.subTextColor)
                            }
                        }

                        // Badges
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedTemplate.glassBg)
                                .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⭐ BADGES", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.subTextColor)
                                Text("$earnedBadgesCount", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = selectedTemplate.textColor)
                                Text("Earned", fontSize = 8.sp, color = selectedTemplate.subTextColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ACTIVE SANKALP SECTION
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectedTemplate.glassBg)
                            .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🚩 ", fontSize = 11.sp)
                                    Text("ACTIVE SANKALP: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.borderColor)
                                    Text(activeSankalpName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.textColor)
                                }
                                Text("$sankalpProgressPercent%", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = selectedTemplate.borderColor)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { sankalpProgressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = selectedTemplate.borderColor,
                                trackColor = Color(0x33FFFFFF)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Day $sankalpCurrentDay of $sankalpTotalDays", fontSize = 8.sp, color = selectedTemplate.subTextColor)
                                Text("${sankalpTotalDays - sankalpCurrentDay} Days Remaining 🙏", fontSize = 8.sp, color = selectedTemplate.subTextColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // LATEST ACHIEVEMENT SECTION
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        selectedTemplate.borderColor.copy(alpha = 0.2f),
                                        selectedTemplate.accentColor.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .border(1.dp, selectedTemplate.borderColor, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑 ", fontSize = 18.sp)
                                Column {
                                    Text("LATEST ACHIEVEMENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.borderColor)
                                    Text(latestBadgeName, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = selectedTemplate.textColor)
                                }
                            }
                            Text("✨ Unlocked Today", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.subTextColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // BHAGAVAD GITA QUOTE BOX
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectedTemplate.glassBg)
                            .border(1.dp, selectedTemplate.borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🪔 ", fontSize = 16.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "\"One who controls the mind finds supreme peace.\"",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = selectedTemplate.textColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "– Bhagavad Gita 6.26 –",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedTemplate.borderColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Text(" 🪷", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // CARD FOOTER & QR CODE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Generated by", fontSize = 7.sp, color = selectedTemplate.subTextColor)
                            Text("🪷 Jaap Counter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = selectedTemplate.borderColor)
                        }

                        Text("📅 $todayDateStr | ⏰ $todayTimeStr", fontSize = 8.sp, color = selectedTemplate.subTextColor)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp))
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⬛", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan to view\nmy journey", fontSize = 6.sp, color = selectedTemplate.subTextColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Export Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val primaryArgb = activeColor.toArgb()
            val secondaryArgb = secondaryColor.toArgb()

            val onGenerateBitmap = {
                DevotionalCardExporter.generateCardBitmap(
                    context = context,
                    cardType = cardType,
                    aspectRatio = selectedAspectRatio,
                    templateId = selectedTemplate.id,
                    userName = userName,
                    mantraName = mantraName,
                    dailyGoal = dailyGoal,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    joinDate = joinDate,
                    totalBeads = totalBeads,
                    totalMalas = totalMalas,
                    activeSankalpName = activeSankalpName,
                    sankalpCurrentDay = sankalpCurrentDay,
                    sankalpTotalDays = sankalpTotalDays,
                    sankalpProgressPercent = sankalpProgressPercent,
                    latestBadgeName = latestBadgeName,
                    earnedBadgesCount = earnedBadgesCount,
                    primaryColorInt = primaryArgb,
                    secondaryColorInt = secondaryArgb
                )
            }

            // Save PNG
            OutlinedButton(
                onClick = {
                    val bmp = onGenerateBitmap()
                    val (uri, path) = DevotionalCardExporter.saveAsPng(context, bmp, "Devotional_Card")
                    savedFileUri = uri
                    savedFilePathStr = path
                    showSaveSuccessDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_export_png"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PNG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Save JPG
            OutlinedButton(
                onClick = {
                    val bmp = onGenerateBitmap()
                    val (uri, path) = DevotionalCardExporter.saveAsJpg(context, bmp, "Devotional_Card")
                    savedFileUri = uri
                    savedFilePathStr = path
                    showSaveSuccessDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_export_jpg"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("JPG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Save PDF
            OutlinedButton(
                onClick = {
                    val bmp = onGenerateBitmap()
                    val (uri, path) = DevotionalCardExporter.saveAsPdf(context, bmp, "Devotional_Card")
                    savedFileUri = uri
                    savedFilePathStr = path
                    showSaveSuccessDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_export_pdf"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Share
            Button(
                onClick = {
                    val bmp = onGenerateBitmap()
                    DevotionalCardExporter.shareCard(context, bmp, "My Devotional Achievement")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_export_share"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

    // Save Success Popup Dialog
    if (showSaveSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSaveSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Saved to File Manager! 🎉",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Text(
                            text = "फ़ाइल मैनेजर में सेव हो गया",
                            fontSize = 11.sp,
                            color = textColorSecondary
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your devotional card has been successfully exported directly into your device's File Manager storage.",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(activeColor.copy(alpha = 0.1f))
                            .border(1.dp, activeColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "📁 Storage Folder:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = savedFilePathStr,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        savedFileUri?.let { uri ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "image/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Ignore if no gallery viewer
                            }
                        }
                        showSaveSuccessDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                ) {
                    Text("View in Gallery 🖼️", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveSuccessDialog = false }) {
                    Text("Done / OK", color = textColorSecondary, fontSize = 12.sp)
                }
            },
            containerColor = cardBg
        )
    }

    // Celebration Dialog with Festive Confetti Graphic & Actions
    if (showCelebrationDialog) {
        val primaryArgb = activeColor.toArgb()
        val secondaryArgb = secondaryColor.toArgb()

        AlertDialog(
            onDismissRequest = { showCelebrationDialog = false },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🎉 🪶 ✨ 👑 ✨ 🪶 🎉", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Devotional Milestone Unlocked!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Congratulations $userName! You have achieved divine progress in your Japa Sadhana with $totalBeads beads offered to Divinity. 🙏",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = textColorSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bmp = DevotionalCardExporter.generateCardBitmap(
                            context = context,
                            cardType = cardType,
                            aspectRatio = selectedAspectRatio,
                            templateId = selectedTemplate.id,
                            userName = userName,
                            mantraName = mantraName,
                            dailyGoal = dailyGoal,
                            currentStreak = currentStreak,
                            longestStreak = longestStreak,
                            joinDate = joinDate,
                            totalBeads = totalBeads,
                            totalMalas = totalMalas,
                            activeSankalpName = activeSankalpName,
                            sankalpCurrentDay = sankalpCurrentDay,
                            sankalpTotalDays = sankalpTotalDays,
                            sankalpProgressPercent = sankalpProgressPercent,
                            latestBadgeName = latestBadgeName,
                            earnedBadgesCount = earnedBadgesCount,
                            primaryColorInt = primaryArgb,
                            secondaryColorInt = secondaryArgb
                        )
                        DevotionalCardExporter.shareCard(context, bmp, "Devotional Achievement")
                        showCelebrationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                ) {
                    Text("Share Achievement", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCelebrationDialog = false }) {
                    Text("Dismiss", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }
}
