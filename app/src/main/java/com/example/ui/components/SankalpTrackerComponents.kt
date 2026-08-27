package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.entity.Mantra
import com.example.data.entity.Sankalp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SankalpTrackerSection(
    sankalps: List<Sankalp>,
    allMantras: List<Mantra>,
    defaultDailyGoal: Int,
    language: String = "HINGLISH",
    onCreateSankalp: (String, String, Int, Int, String, String) -> Unit,
    onCancelSankalp: (Sankalp) -> Unit,
    onDeleteSankalp: (Int) -> Unit,
    onNavigateToJaap: () -> Unit
) {
    var showNewSankalpSheet by remember { mutableStateOf(false) }
    var showHistorySection by remember { mutableStateOf(false) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val activeSankalps = remember(sankalps) { sankalps.filter { it.status == "ACTIVE" } }
    val historySankalps = remember(sankalps) { sankalps.filter { it.status != "ACTIVE" } }

    // Clean, un-cluttered localized strings based on language setting
    val headerStrings = remember(language) {
        when (language) {
            "HINDI" -> SankalpHeaderStrings(
                headerTitle = "संकल्प साधना",
                headerSubtitle = "आपकी भक्ति साधना, नियम एवं जाप प्रतिज्ञाएं",
                newBtnText = "+ नया संकल्प",
                emptyTitle = "कोई सक्रिय संकल्प नहीं है",
                emptyDesc = "21-दिन या 40-दिन की जाप साधना का पवित्र नियम लें",
                emptyBtnText = "पवित्र संकल्प लें",
                historyHeaderTitle = "पुराने संकल्प",
                showText = "देखें",
                hideText = "छिपाएं"
            )
            "HINGLISH" -> SankalpHeaderStrings(
                headerTitle = "Sankalp Tracker",
                headerSubtitle = "Apni bhakti sadhana, niyams aur daily jaap vows ka track rakhein",
                newBtnText = "+ Naya Sankalp",
                emptyTitle = "Koi Active Sankalp Nahi Hai",
                emptyDesc = "21-day ya 40-day Jaap Sadhana ka sacred niyam lein aur pragati dekhein",
                emptyBtnText = "Pavitra Sankalp Lein",
                historyHeaderTitle = "Past Sankalp History",
                showText = "Dekhein",
                hideText = "Chhipayein"
            )
            else -> SankalpHeaderStrings(
                headerTitle = "Sacred Sankalp Tracker",
                headerSubtitle = "Track your spiritual vows, daily sadhana, and devotional commitments",
                newBtnText = "+ New Sankalp",
                emptyTitle = "No Active Sankalp Vows",
                emptyDesc = "Set a sacred commitment like a 21-day or 40-day Jaap Sadhana",
                emptyBtnText = "Take Sacred Vow",
                historyHeaderTitle = "Sankalp Vow History",
                showText = "Show",
                hideText = "Hide"
            )
        }
    }

    val primarySaffron = Color(0xFFFF9800)
    val goldAccent = Color(0xFFFFC107)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = primarySaffron.copy(alpha = 0.25f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Sleek Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Sleek Sacred Logo Badge (Compact 32dp)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF9800),
                                        Color(0xFFFF5722)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Sankalp Sacred Logo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = headerStrings.headerTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )

                    if (activeSankalps.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primarySaffron.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${activeSankalps.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primarySaffron
                            )
                        }
                    }
                }

                // Compact Action Button
                Button(
                    onClick = { showNewSankalpSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primarySaffron
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_new_sankalp")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Sankalp",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = headerStrings.newBtnText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Sankalps List or Empty State
            if (activeSankalps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(primarySaffron.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                colors = listOf(
                                    primarySaffron.copy(alpha = 0.3f),
                                    goldAccent.copy(alpha = 0.2f)
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(primarySaffron.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = primarySaffron,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = headerStrings.emptyTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = headerStrings.emptyDesc,
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showNewSankalpSheet = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primarySaffron)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(headerStrings.emptyBtnText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    activeSankalps.forEach { sankalp ->
                        SankalpCardItem(
                            sankalp = sankalp,
                            language = language,
                            onCancel = { onCancelSankalp(sankalp) },
                            onChantNow = onNavigateToJaap
                        )
                    }
                }
            }

            // History Section Toggle
            if (historySankalps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showHistorySection = !showHistorySection }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = textColorSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "${headerStrings.historyHeaderTitle} (${historySankalps.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorSecondary
                        )
                    }
                    Text(
                        if (showHistorySection) headerStrings.hideText else headerStrings.showText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeColor
                    )
                }

                AnimatedVisibility(visible = showHistorySection) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        historySankalps.forEach { hSankalp ->
                            SankalpHistoryItem(
                                sankalp = hSankalp,
                                language = language,
                                onDelete = { onDeleteSankalp(hSankalp.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // New Sankalp Bottom Sheet
    if (showNewSankalpSheet) {
        CreateSankalpBottomSheet(
            allMantras = allMantras,
            defaultDailyGoal = defaultDailyGoal,
            language = language,
            onDismiss = { showNewSankalpSheet = false },
            onCreate = { name, mantra, duration, dailyGoal, startDate, notes ->
                onCreateSankalp(name, mantra, duration, dailyGoal, startDate, notes)
                showNewSankalpSheet = false
            }
        )
    }
}

object SankalpDateUtils {
    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun getDaysPassed(startDateString: String): Int {
        return try {
            val start = synchronized(sdf) { sdf.parse(startDateString) } ?: Date()
            val diffMillis = System.currentTimeMillis() - start.time
            ((diffMillis / (1000 * 60 * 60 * 24)) + 1).toInt().coerceAtLeast(1)
        } catch (e: Exception) {
            1
        }
    }
}

@Composable
fun SankalpCardItem(
    sankalp: Sankalp,
    language: String = "HINGLISH",
    onCancel: () -> Unit,
    onChantNow: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val primarySaffron = remember { Color(0xFFFF9800) }
    val goldAccent = remember { Color(0xFFFFC107) }

    val progressPct = remember(sankalp.completedMalas, sankalp.targetMalasTotal) {
        if (sankalp.targetMalasTotal > 0) {
            ((sankalp.completedMalas.toFloat() / sankalp.targetMalasTotal) * 100f).coerceIn(0f, 100f)
        } else 0f
    }

    // Calculate Days Passed using cached parser
    val daysPassed = remember(sankalp.startDateString) {
        SankalpDateUtils.getDaysPassed(sankalp.startDateString)
    }

    val remainingDays = remember(sankalp.durationDays, daysPassed) {
        (sankalp.durationDays - daysPassed).coerceAtLeast(0)
    }

    val cardStrings = remember(language, daysPassed, remainingDays, sankalp.durationDays, sankalp.endDateString) {
        when (language) {
            "HINDI" -> SankalpCardStrings(
                progressLabel = "कुल प्रगति",
                dayStr = "दिन $daysPassed / ${sankalp.durationDays}",
                dailyGoalLabel = "दैनिक लक्ष्य",
                daysLeftStr = "⏳ $remainingDays दिन शेष (समाप्ति: ${sankalp.endDateString})",
                cancelText = "रद्द करें",
                chantText = "📿 जाप शुरू करें"
            )
            "HINGLISH" -> SankalpCardStrings(
                progressLabel = "Total Progress",
                dayStr = "Day $daysPassed of ${sankalp.durationDays}",
                dailyGoalLabel = "Daily Target",
                daysLeftStr = "⏳ $remainingDays Days Baki (Target: ${sankalp.endDateString})",
                cancelText = "Cancel",
                chantText = "📿 Jaap Shuru Karein"
            )
            else -> SankalpCardStrings(
                progressLabel = "Total Progress",
                dayStr = "Day $daysPassed of ${sankalp.durationDays}",
                dailyGoalLabel = "Daily Target",
                daysLeftStr = "⏳ $remainingDays Days Left (Target: ${sankalp.endDateString})",
                cancelText = "Cancel",
                chantText = "📿 Start Jaap"
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    primarySaffron.copy(alpha = 0.5f),
                    goldAccent.copy(alpha = 0.4f),
                    primarySaffron.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            primarySaffron.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header Row: Vow Name & Percentage Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        primarySaffron.copy(alpha = 0.25f),
                                        goldAccent.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = primarySaffron,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = sankalp.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = sankalp.mantraText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = primarySaffron
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(primarySaffron)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f%%", progressPct),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stat 1: Malas Progress
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(primarySaffron.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(cardStrings.progressLabel, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = textColorSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${sankalp.completedMalas}/${sankalp.targetMalasTotal}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                        Text("Malas", fontSize = 10.sp, color = primarySaffron, fontWeight = FontWeight.Bold)
                    }
                }

                // Stat 2: Days Tracker
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(primarySaffron.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Current Day", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = textColorSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(cardStrings.dayStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                        Text("Timeline", fontSize = 10.sp, color = primarySaffron, fontWeight = FontWeight.Bold)
                    }
                }

                // Stat 3: Daily Target
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(primarySaffron.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(cardStrings.dailyGoalLabel, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = textColorSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${sankalp.dailyGoalMalas}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                        Text("Malas/day", fontSize = 10.sp, color = primarySaffron, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Modern Gradient Progress Bar
            LinearProgressIndicator(
                progress = { progressPct / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = primarySaffron,
                trackColor = primarySaffron.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cardStrings.daysLeftStr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColorSecondary,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onCancel,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(cardStrings.cancelText, fontSize = 11.sp, color = Color.Red.copy(alpha = 0.8f))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onChantNow,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primarySaffron),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Jaap",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(cardStrings.chantText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SankalpHistoryItem(
    sankalp: Sankalp,
    language: String = "HINGLISH",
    onDelete: () -> Unit
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val (statusLabel, statusColor) = when (sankalp.status) {
        "COMPLETED" -> Pair(
            if (language == "HINDI") "पूर्ण" else if (language == "HINGLISH") "Completed" else "Completed",
            Color(0xFF4CAF50)
        )
        "CANCELLED" -> Pair(
            if (language == "HINDI") "रद्द" else if (language == "HINGLISH") "Cancelled" else "Cancelled",
            Color(0xFFE53935)
        )
        else -> Pair(sankalp.status, textColorSecondary)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sankalp.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(statusLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("${sankalp.completedMalas} / ${sankalp.targetMalasTotal} Malas • ${sankalp.startDateString}", fontSize = 11.sp, color = textColorSecondary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = textColorSecondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSankalpBottomSheet(
    allMantras: List<Mantra>,
    defaultDailyGoal: Int,
    language: String = "HINGLISH",
    onDismiss: () -> Unit,
    onCreate: (String, String, Int, Int, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val presetNames = when (language) {
        "HINDI" -> listOf(
            "21-दिवसीय राधे राधे संकल्प",
            "40-दिवसीय हरे कृष्णा महामंत्र संकल्प",
            "108-माला दैनिक साधना संकल्प",
            "पुरुषोत्तम मास विशेष संकल्प",
            "नित्य अखंड नाम जाप संकल्प"
        )
        "HINGLISH" -> listOf(
            "21-Day Radhe Radhe Sankalp",
            "40-Day Hare Krishna Sankalp",
            "108-Mala Daily Sadhana Sankalp",
            "Purushottam Month Special Sankalp",
            "Nitya Akhand Naam Jaap Sankalp"
        )
        else -> listOf(
            "21-Day Radhe Radhe Vow",
            "40-Day Hare Krishna Mahamantra Vow",
            "108-Mala Daily Sadhana Vow",
            "Sacred Month Special Vow",
            "Continuous Daily Naam Jaap Vow"
        )
    }

    var sankalpName by remember { mutableStateOf(presetNames[0]) }
    var selectedMantraText by remember {
        mutableStateOf(allMantras.firstOrNull { it.isSelected }?.textEnglish ?: "Radhe Radhe")
    }
    var durationDays by remember { mutableIntStateOf(21) }
    var customDurationInput by remember { mutableStateOf("21") }
    var dailyGoalInput by remember { mutableStateOf(defaultDailyGoal.toString()) }
    var notesInput by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var startDateString by remember { mutableStateOf(sdf.format(Date())) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val sheetStrings = when (language) {
        "HINDI" -> SankalpSheetStrings(
            sheetHeaderTitle = "पवित्र संकल्प लें",
            presetLabel = "उदा. लोकप्रिय संकल्प:",
            vowNameLabel = "संकल्प नाम",
            mantraSelectLabel = "मंत्र चुनें:",
            durationSelectLabel = "अवधि (दिन):",
            dailyTargetLabel = "दैनिक लक्ष्य (माला)",
            startDateLabel = "आरंभ तिथि",
            notesLabel = "व्यक्तिगत प्रार्थना / टिप्पणी (ऐच्छिक)",
            confirmBtnText = "पवित्र संकल्प की पुष्टि करें"
        )
        "HINGLISH" -> SankalpSheetStrings(
            sheetHeaderTitle = "Naya Sacred Sankalp Lein",
            presetLabel = "Preset Vow Examples:",
            vowNameLabel = "Sankalp Vow Name",
            mantraSelectLabel = "Mantra Chunein:",
            durationSelectLabel = "Duration (Days):",
            dailyTargetLabel = "Daily Target (Malas)",
            startDateLabel = "Start Date",
            notesLabel = "Notes / Personal Prayer (Optional)",
            confirmBtnText = "Sacred Sankalp Confirm Karein"
        )
        else -> SankalpSheetStrings(
            sheetHeaderTitle = "Take Sacred Sankalp Vow",
            presetLabel = "Preset Vow Examples:",
            vowNameLabel = "Sankalp Vow Name",
            mantraSelectLabel = "Select Mantra:",
            durationSelectLabel = "Duration (Days):",
            dailyTargetLabel = "Daily Target (Malas)",
            startDateLabel = "Start Date",
            notesLabel = "Notes / Personal Prayer (Optional)",
            confirmBtnText = "Confirm & Take Sacred Sankalp"
        )
    }

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
                    Icon(Icons.Default.Flag, contentDescription = null, tint = activeColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = sheetStrings.sheetHeaderTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = textColorSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preset Chips
            Text(sheetStrings.presetLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColorSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetNames.forEach { preset ->
                    val isSel = sankalpName == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) activeColor else cardBorder.copy(alpha = 0.3f))
                            .clickable { sankalpName = preset }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(preset, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MaterialTheme.colorScheme.onPrimary else textColorPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = sankalpName,
                onValueChange = { sankalpName = it },
                label = { Text(sheetStrings.vowNameLabel) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_sankalp_name")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Select Mantra Dropdown
            Text(sheetStrings.mantraSelectLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColorSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allMantras.forEach { m ->
                    val isSel = selectedMantraText == m.textEnglish || selectedMantraText == m.textHindi
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) secondaryColor else cardBorder.copy(alpha = 0.3f))
                            .clickable { selectedMantraText = if (language == "HINDI") m.textHindi else m.textEnglish }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(if (language == "HINDI") m.textHindi else m.textEnglish, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MaterialTheme.colorScheme.onSecondary else textColorPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Duration Selector
            Text(sheetStrings.durationSelectLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColorSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(7, 11, 21, 30, 40, 108).forEach { dur ->
                    val isSel = durationDays == dur
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) activeColor else cardBorder.copy(alpha = 0.3f))
                            .clickable {
                                durationDays = dur
                                customDurationInput = dur.toString()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$dur D", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MaterialTheme.colorScheme.onPrimary else textColorPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = dailyGoalInput,
                    onValueChange = { dailyGoalInput = it.filter { char -> char.isDigit() } },
                    label = { Text(sheetStrings.dailyTargetLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = startDateString,
                    onValueChange = { startDateString = it },
                    label = { Text(sheetStrings.startDateLabel) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notesInput,
                onValueChange = { notesInput = it },
                label = { Text(sheetStrings.notesLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val goalInt = dailyGoalInput.toIntOrNull() ?: defaultDailyGoal
                    val durInt = customDurationInput.toIntOrNull() ?: durationDays
                    onCreate(sankalpName, selectedMantraText, durInt, goalInt, startDateString, notesInput)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_save_sankalp"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
            ) {
                Text(sheetStrings.confirmBtnText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private data class SankalpHeaderStrings(
    val headerTitle: String,
    val headerSubtitle: String,
    val newBtnText: String,
    val emptyTitle: String,
    val emptyDesc: String,
    val emptyBtnText: String,
    val historyHeaderTitle: String,
    val showText: String,
    val hideText: String
)

private data class SankalpCardStrings(
    val progressLabel: String,
    val dayStr: String,
    val dailyGoalLabel: String,
    val daysLeftStr: String,
    val cancelText: String,
    val chantText: String
)

private data class SankalpSheetStrings(
    val sheetHeaderTitle: String,
    val presetLabel: String,
    val vowNameLabel: String,
    val mantraSelectLabel: String,
    val durationSelectLabel: String,
    val dailyTargetLabel: String,
    val startDateLabel: String,
    val notesLabel: String,
    val confirmBtnText: String
)
