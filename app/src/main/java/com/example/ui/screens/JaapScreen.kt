package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Mantra
import com.example.ui.components.DevoteeAuthDialog
import com.example.ui.components.GuestModeReminderCard
import com.example.ui.viewmodel.JaapViewModel
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Precomputed angles for 108 beads (cos, sin) to eliminate per-frame trigonometry allocations
private val BEAD_ANGLES_108: List<Pair<Float, Float>> = (0 until 108).map { i ->
    val angle = (i * (2 * Math.PI / 108) - Math.PI / 2).toFloat()
    Pair(kotlin.math.cos(angle), kotlin.math.sin(angle))
}

// Precomputed angles for 12 mandala petals
private val MANDALA_ANGLES_12: List<Pair<Float, Float>> = (0 until 12).map { petal ->
    val pAngle = (petal * (2 * Math.PI / 12)).toFloat()
    Pair(kotlin.math.cos(pAngle), kotlin.math.sin(pAngle))
}

@Composable
fun JaapScreen(viewModel: JaapViewModel) {
    val context = LocalContext.current
    val allMantras by viewModel.allMantras.collectAsState()
    val selectedMantra by viewModel.selectedMantra.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val todayProgress by viewModel.todayProgress.collectAsState()
    val showGlow by viewModel.showMalaCompletionGlow.collectAsState()
    val showGuestReminder by viewModel.showGuestReminder.collectAsState()
    val totalBeads by viewModel.totalBeads.collectAsState()
    val totalMalas by viewModel.totalMalas.collectAsState()

    val currentBead = userSettings?.currentBeadInIncompleteMala ?: 0
    val malaNumber = userSettings?.currentMalaNumber ?: 1
    val dailyGoal = userSettings?.dailyGoalMalas ?: 10
    val completedToday = todayProgress?.totalMalasCompleted ?: 0

    val soundEnabled = userSettings?.audioChimeEnabled ?: false
    val vibrationEnabled = userSettings?.hapticFeedbackEnabled ?: false

    var showMantraSelectorDialog by remember { mutableStateOf(false) }
    var showAddMantraDialog by remember { mutableStateOf(false) }
    var mantraToEdit by remember { mutableStateOf<Mantra?>(null) }
    var mantraToDelete by remember { mutableStateOf<Mantra?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showGuestAuthDialog by remember { mutableStateOf(false) }

    val activeColor = Color(0xFFF5820A) // Vibrant Spiritual Orange
    val cyanAccent = Color(0xFF00E5FF)  // Vibrant Cyan Accent
    val darkBg = Color(0xFF070D18)      // Deep Navy Black
    val cardBg = Color(0xFF131D2E)      // Dark Card Surface
    val cardBorder = Color(0xFF202D42)  // Card Border
    val textColorPrimary = Color(0xFFFFFFFF)
    val textColorSecondary = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ----------------------------------------------------
            // 1. TOP HEADER: Mantra Management + Daily Malas Badge
            // ----------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Mantra Management Title & Add Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showMantraSelectorDialog = true }
                ) {
                    Text(
                        text = "Mantra Management",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(cardBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(8.dp))
                            .clickable { showAddMantraDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Mantra",
                            tint = activeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Right: Daily Mala Progress Badge (e.g. 34 / 10 Malas Today)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(activeColor.copy(alpha = 0.12f))
                        .border(1.2.dp, activeColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Small Mala bead icon
                        MalaIconSmall(tint = activeColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$completedToday / $dailyGoal Malas Today",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ----------------------------------------------------
            // GUEST MODE AWARENESS & DATA PROTECTION REMINDER
            // ----------------------------------------------------
            AnimatedVisibility(
                visible = showGuestReminder,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    GuestModeReminderCard(
                        totalBeads = totalBeads,
                        totalMalas = totalMalas,
                        onOpenAuth = { showGuestAuthDialog = true },
                        onDismiss = { viewModel.dismissGuestReminder() }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // ----------------------------------------------------
            // 2. SUB-ROW: Mantra Dropdown Pill + Settings Button
            // ----------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Mantra Selector Pill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(1.2.dp, activeColor, RoundedCornerShape(20.dp))
                        .clickable { showMantraSelectorDialog = true }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedMantra?.textEnglish ?: "Radhe Radhe",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Mantra",
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Right: Settings Icon Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                        .border(1.dp, cardBorder, CircleShape)
                        .clickable { showSettingsDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = textColorSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ----------------------------------------------------
            // 3. MANTRA HERO TITLE & SPIRITUAL FLOURISH
            // ----------------------------------------------------
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = selectedMantra?.textEnglish ?: "Radhe Radhe",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = activeColor,
                    textAlign = TextAlign.Center
                )
                if (!selectedMantra?.textHindi.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = selectedMantra?.textHindi ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColorSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Elegant Spiritual Flourish Divider
                SpiritualFlourish(
                    tint = activeColor,
                    modifier = Modifier
                        .width(160.dp)
                        .height(14.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // ----------------------------------------------------
            // 4. MAIN CIRCULAR 108-BEAD JAAP COUNTER (INTERACTIVE)
            // ----------------------------------------------------
            Circular108JaapCounter(
                currentBead = currentBead,
                malaNumber = malaNumber,
                showGlow = showGlow,
                activeColor = activeColor,
                cyanAccent = cyanAccent,
                textColorPrimary = textColorPrimary,
                textColorSecondary = textColorSecondary,
                onTap = { viewModel.onBeadTapped(context) }
            )

            Spacer(modifier = Modifier.weight(0.6f))

            // ----------------------------------------------------
            // 5. BOTTOM ACTION CONTROLS (Sound, Reset Mala, Vibration)
            // ----------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Sound Toggle Pill
                Surface(
                    onClick = { viewModel.toggleAudioChime(context) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (soundEnabled) activeColor.copy(alpha = 0.18f) else cardBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (soundEnabled) activeColor else cardBorder
                    ),
                    modifier = Modifier
                        .testTag("sound_toggle")
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Sound Toggle",
                            tint = if (soundEnabled) activeColor else activeColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (soundEnabled) "Sound ON" else "Sound OFF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (soundEnabled) activeColor else textColorPrimary
                        )
                    }
                }

                // Center: 108 Reset Mala Circular Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("reset_mala_button")
                        .clip(CircleShape)
                        .background(cardBg)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(cyanAccent, activeColor, cyanAccent)
                            ),
                            shape = CircleShape
                        )
                        .clickable { showResetConfirmDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "108",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = cyanAccent
                        )
                        Text(
                            text = "Reset Mala",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = cyanAccent
                        )
                    }
                }

                // Right: Vibration Toggle Pill
                Surface(
                    onClick = { viewModel.toggleHapticFeedback(context) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (vibrationEnabled) activeColor.copy(alpha = 0.18f) else cardBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (vibrationEnabled) activeColor else cardBorder
                    ),
                    modifier = Modifier
                        .testTag("vibration_toggle")
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibration Toggle",
                            tint = textColorPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (vibrationEnabled) "Vibration ON" else "Vibration OFF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vibrationEnabled) activeColor else textColorPrimary
                        )
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // DIALOGS & BOTTOM SHEETS
    // ----------------------------------------------------

    // 1. Reset Current Mala Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = "Reset current mala?",
                    color = textColorPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Your current bead progress ($currentBead / 108) will be lost. Total completed malas will not be affected.",
                    color = textColorSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetCurrentMala()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel", color = textColorPrimary)
                }
            },
            containerColor = cardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 2. Mantra Selector Dialog
    if (showMantraSelectorDialog) {
        MantraSelectorDialog(
            mantras = allMantras,
            selectedMantraId = selectedMantra?.id ?: 1,
            onSelect = { id ->
                viewModel.selectMantra(id)
                showMantraSelectorDialog = false
            },
            onAddCustom = {
                showMantraSelectorDialog = false
                showAddMantraDialog = true
            },
            onEditCustom = { mantra ->
                showMantraSelectorDialog = false
                mantraToEdit = mantra
            },
            onDeleteCustom = { mantra ->
                showMantraSelectorDialog = false
                mantraToDelete = mantra
            },
            onDismiss = { showMantraSelectorDialog = false }
        )
    }

    // 3. Add Custom Mantra Dialog
    if (showAddMantraDialog) {
        AddEditMantraDialog(
            title = "Add Custom Mantra",
            onDismiss = { showAddMantraDialog = false },
            onSave = { eng, hin ->
                viewModel.addCustomMantra(eng, hin)
                showAddMantraDialog = false
            }
        )
    }

    // 4. Edit Custom Mantra Dialog
    mantraToEdit?.let { mantra ->
        AddEditMantraDialog(
            title = "Edit Custom Mantra",
            initialEnglish = mantra.textEnglish,
            initialHindi = mantra.textHindi ?: "",
            onDismiss = { mantraToEdit = null },
            onSave = { eng, hin ->
                viewModel.editCustomMantra(mantra.id, eng, hin)
                mantraToEdit = null
            }
        )
    }

    // 5. Delete Custom Mantra Dialog
    mantraToDelete?.let { mantra ->
        AlertDialog(
            onDismissRequest = { mantraToDelete = null },
            title = { Text("Delete Mantra", color = textColorPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${mantra.textEnglish}'? If selected, it will fall back to Radhe Radhe.", color = textColorSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCustomMantra(mantra.id)
                        mantraToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mantraToDelete = null }) {
                    Text("Cancel", color = textColorPrimary)
                }
            },
            containerColor = cardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 6. Jaap Settings Dialog
    if (showSettingsDialog) {
        JaapSettingsDialog(
            viewModel = viewModel,
            dailyGoal = dailyGoal,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            onDismiss = { showSettingsDialog = false }
        )
    }

    // 7. Devotee Auth Dialog (from Guest Reminder)
    if (showGuestAuthDialog) {
        DevoteeAuthDialog(
            viewModel = viewModel,
            initialTab = 1,
            onDismissRequest = { showGuestAuthDialog = false },
            onAuthSuccess = {
                showGuestAuthDialog = false
            }
        )
    }
}

// ----------------------------------------------------
// CIRCULAR 108-BEAD JAAP COUNTER COMPOSABLE
// ----------------------------------------------------

@Composable
fun Circular108JaapCounter(
    currentBead: Int,
    malaNumber: Int,
    showGlow: Boolean,
    activeColor: Color,
    cyanAccent: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    onTap: () -> Unit
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (showGlow) 0.85f else 0.0f,
        animationSpec = tween(500),
        label = "glow"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val counterSize = min(maxWidth.value, 330f).dp

        Box(
            modifier = Modifier
                .size(counterSize)
                .testTag("jaap_circular_counter")
                .clip(CircleShape)
                .clickable { onTap() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = (size.width / 2f) - 18.dp.toPx()
                val innerTrackRadius = outerRadius - 20.dp.toPx()

                // 1. Mala completion celebration glow
                if (glowAlpha > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                activeColor.copy(alpha = glowAlpha),
                                cyanAccent.copy(alpha = glowAlpha * 0.5f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = outerRadius + 28.dp.toPx()
                        ),
                        radius = outerRadius + 24.dp.toPx()
                    )
                }

                // 2. Outer 108 Beads Circle
                val totalBeads = 108
                val activeBeadIndex = if (currentBead > 0) (currentBead - 1) % totalBeads else -1

                for (i in 0 until totalBeads) {
                    val (cosA, sinA) = BEAD_ANGLES_108[i]
                    val x = center.x + outerRadius * cosA
                    val y = center.y + outerRadius * sinA

                    if (i == activeBeadIndex || (activeBeadIndex == -1 && i == 0)) {
                        // Current Active Bead (Larger glowing bead at top)
                        drawCircle(
                            color = activeColor.copy(alpha = 0.35f),
                            radius = 10.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = activeColor,
                            radius = 6.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )
                    } else {
                        val isPassed = activeBeadIndex >= 0 && i < activeBeadIndex
                        if (isPassed) {
                            // Chanted bead (Orange)
                            drawCircle(
                                color = activeColor,
                                radius = 3.5.dp.toPx(),
                                center = Offset(x, y)
                            )
                        } else {
                            // Inactive bead (Subtle navy dot)
                            drawCircle(
                                color = Color(0xFF1E2A3A),
                                radius = 2.5.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                }

                // 3. Inner Glowing Gradient Track (Cyan on Left, Orange on Right)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            cyanAccent,
                            activeColor,
                            cyanAccent
                        ),
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - innerTrackRadius, center.y - innerTrackRadius),
                    size = Size(innerTrackRadius * 2, innerTrackRadius * 2),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // 4. Subtle background Mandala geometry pattern
                val mandalaRadius = innerTrackRadius * 0.85f
                for (petal in 0 until 12) {
                    val (cosP, sinP) = MANDALA_ANGLES_12[petal]
                    val px = center.x + (mandalaRadius * 0.6f) * cosP
                    val py = center.y + (mandalaRadius * 0.6f) * sinP
                    drawCircle(
                        color = Color.White.copy(alpha = 0.035f),
                        radius = mandalaRadius * 0.4f,
                        center = Offset(px, py),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            // Inner Central Information Layout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large High-Contrast Bead Number
                Text(
                    text = "$currentBead",
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary,
                    lineHeight = 64.sp
                )

                // "of 108 beads"
                Text(
                    text = "of 108 beads",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColorSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pill Badge: "Mala #2"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(cyanAccent.copy(alpha = 0.12f))
                        .border(1.2.dp, cyanAccent.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Mala #$malaNumber",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = cyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Instructional Hint: Tap to Jaap
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Tap",
                        tint = activeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tap to Jaap",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColorSecondary
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// DECORATIVE SPIRITUAL FLOURISH
// ----------------------------------------------------

@Composable
fun SpiritualFlourish(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerY = h / 2f
        val centerX = w / 2f

        val lineLength = w * 0.36f

        // Left Line & Dot
        drawLine(
            color = tint.copy(alpha = 0.7f),
            start = Offset(0f, centerY),
            end = Offset(lineLength, centerY),
            strokeWidth = 1.2.dp.toPx()
        )
        drawCircle(
            color = tint,
            radius = 2.dp.toPx(),
            center = Offset(lineLength + 5.dp.toPx(), centerY)
        )

        // Center Lotus Petals
        val lotusCenter = Offset(centerX, centerY)
        // Center petal
        drawOval(
            color = tint,
            topLeft = Offset(lotusCenter.x - 3.dp.toPx(), centerY - 6.dp.toPx()),
            size = Size(6.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )
        // Left petal
        drawArc(
            color = tint,
            startAngle = 140f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(lotusCenter.x - 9.dp.toPx(), centerY - 5.dp.toPx()),
            size = Size(10.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )
        // Right petal
        drawArc(
            color = tint,
            startAngle = 300f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(lotusCenter.x - 1.dp.toPx(), centerY - 5.dp.toPx()),
            size = Size(10.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Right Line & Dot
        val rightStart = w - lineLength
        drawCircle(
            color = tint,
            radius = 2.dp.toPx(),
            center = Offset(rightStart - 5.dp.toPx(), centerY)
        )
        drawLine(
            color = tint.copy(alpha = 0.7f),
            start = Offset(rightStart, centerY),
            end = Offset(w, centerY),
            strokeWidth = 1.2.dp.toPx()
        )
    }
}

// ----------------------------------------------------
// SMALL MALA BEADS ICON
// ----------------------------------------------------

@Composable
fun MalaIconSmall(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.width / 2f) - 2.dp.toPx()
        val beads = 8
        for (i in 0 until beads) {
            val angle = (i * (2 * Math.PI / beads)).toFloat()
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            drawCircle(
                color = tint,
                radius = 1.6.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

// ----------------------------------------------------
// MANTRA SELECTOR DIALOG
// ----------------------------------------------------

@Composable
fun MantraSelectorDialog(
    mantras: List<Mantra>,
    selectedMantraId: Int,
    onSelect: (Int) -> Unit,
    onAddCustom: () -> Unit,
    onEditCustom: (Mantra) -> Unit,
    onDeleteCustom: (Mantra) -> Unit,
    onDismiss: () -> Unit
) {
    val cardBg = Color(0xFF131D2E)
    val cardBorder = Color(0xFF202D42)
    val activeColor = Color(0xFFF5820A)
    val textColorPrimary = Color.White
    val textColorSecondary = Color(0xFF94A3B8)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Mantra",
                    color = textColorPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onAddCustom) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = activeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Custom", color = activeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mantras) { mantra ->
                    val isSelected = mantra.id == selectedMantraId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mantra.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) activeColor.copy(alpha = 0.15f) else Color(0xFF0D1522)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) activeColor else cardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mantra.textEnglish,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) activeColor else textColorPrimary
                                )
                                if (!mantra.textHindi.isNullOrBlank()) {
                                    Text(
                                        text = mantra.textHindi ?: "",
                                        fontSize = 12.sp,
                                        color = textColorSecondary
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = activeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (mantra.isCustom) {
                                Row {
                                    IconButton(
                                        onClick = { onEditCustom(mantra) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = textColorSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteCustom(mantra) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = textColorPrimary)
            }
        },
        containerColor = cardBg,
        shape = RoundedCornerShape(20.dp)
    )
}

// ----------------------------------------------------
// ADD / EDIT CUSTOM MANTRA DIALOG
// ----------------------------------------------------

@Composable
fun AddEditMantraDialog(
    title: String,
    initialEnglish: String = "",
    initialHindi: String = "",
    onDismiss: () -> Unit,
    onSave: (textEnglish: String, textHindi: String) -> Unit
) {
    var engText by remember { mutableStateOf(initialEnglish) }
    var hinText by remember { mutableStateOf(initialHindi) }
    var errorMsg by remember { mutableStateOf("") }
    val textColorPrimary = Color.White
    val cardBg = Color(0xFF131D2E)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = textColorPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = engText,
                    onValueChange = {
                        engText = it
                        errorMsg = ""
                    },
                    label = { Text("Mantra Name / Text (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hinText,
                    onValueChange = { hinText = it },
                    label = { Text("Hindi / Devanagari (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (engText.trim().isEmpty()) {
                        errorMsg = "Mantra name cannot be empty"
                    } else {
                        onSave(engText.trim(), hinText.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5820A))
            ) {
                Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColorPrimary)
            }
        },
        containerColor = cardBg,
        shape = RoundedCornerShape(20.dp)
    )
}

// ----------------------------------------------------
// JAAP SETTINGS DIALOG
// ----------------------------------------------------

@Composable
fun JaapSettingsDialog(
    viewModel: JaapViewModel,
    dailyGoal: Int,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cardBg = Color(0xFF131D2E)
    val cardBorder = Color(0xFF202D42)
    val activeColor = Color(0xFFF5820A)
    val textColorPrimary = Color.White
    val textColorSecondary = Color(0xFF94A3B8)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Jaap Preferences",
                color = textColorPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Daily Target Selector
                Text(
                    text = "Daily Mala Goal: $dailyGoal Malas",
                    color = textColorSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 16, 21, 51, 108).forEach { goal ->
                        val isSel = goal == dailyGoal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) activeColor else Color(0xFF0D1522))
                                .border(1.dp, if (isSel) activeColor else cardBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateDailyGoal(goal) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$goal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else textColorPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sound Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Audio Chime on Mala Finish", color = textColorPrimary, fontSize = 14.sp)
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.toggleAudioChime(context) },
                        colors = SwitchDefaults.colors(checkedThumbColor = activeColor)
                    )
                }

                // Vibration Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Haptic Vibration on Tap", color = textColorPrimary, fontSize = 14.sp)
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { viewModel.toggleHapticFeedback(context) },
                        colors = SwitchDefaults.colors(checkedThumbColor = activeColor)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = cardBg,
        shape = RoundedCornerShape(20.dp)
    )
}
