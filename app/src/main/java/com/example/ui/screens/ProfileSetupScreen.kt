package com.example.ui.screens

import android.app.TimePickerDialog
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.AvatarSelectionDialog
import com.example.ui.components.SpiritualAvatarGraphic
import com.example.ui.viewmodel.JaapViewModel
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileSetupScreen(
    viewModel: JaapViewModel,
    authMethod: String = "GUEST",
    userEmail: String = "",
    onNavigateBack: () -> Unit,
    onCompleteProfile: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val userSettings by viewModel.userSettings.collectAsState()
    val allMantras by viewModel.allMantras.collectAsState()

    var userName by remember(userSettings) {
        mutableStateOf(userSettings?.userName.takeIf { !it.isNullOrBlank() } ?: "Devotee")
    }
    var showNameEditDialog by remember { mutableStateOf(false) }
    var tempNameInput by remember { mutableStateOf(userName) }

    var selectedAvatarId by remember(userSettings) { mutableIntStateOf(userSettings?.avatarId ?: 1) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    var selectedGoalMalas by remember(userSettings) { mutableIntStateOf(userSettings?.dailyGoalMalas ?: 10) }
    var showCustomGoalDialog by remember { mutableStateOf(false) }
    var customGoalInput by remember { mutableStateOf("") }

    var selectedMantraId by remember(userSettings) { mutableIntStateOf(userSettings?.selectedMantraId ?: 1) }
    var mantraDropdownExpanded by remember { mutableStateOf(false) }

    var morningReminderEnabled by remember(userSettings) {
        mutableStateOf(userSettings?.morningReminderEnabled ?: true)
    }
    var morningReminderTime by remember(userSettings) {
        mutableStateOf(userSettings?.morningReminderTime ?: "06:00 AM")
    }

    var eveningReminderEnabled by remember(userSettings) {
        mutableStateOf(userSettings?.eveningReminderEnabled ?: true)
    }
    var eveningReminderTime by remember(userSettings) {
        mutableStateOf(userSettings?.eveningReminderTime ?: "08:00 PM")
    }

    var isSaving by remember { mutableStateOf(false) }

    val selectedMantraObj = allMantras.find { it.id == selectedMantraId }
        ?: allMantras.firstOrNull()

    // Time picker dialog helper
    fun openTimePicker(initialTimeStr: String, onTimeSet: (String) -> Unit) {
        val cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR_OF_DAY)
        var minute = cal.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, h, m ->
                val amPm = if (h >= 12) "PM" else "AM"
                val h12 = if (h % 12 == 0) 12 else h % 12
                val formatted = String.format("%02d:%02d %s", h12, m, amPm)
                onTimeSet(formatted)
            },
            hour,
            minute,
            false
        ).show()
    }

    // Name Edit Dialog
    if (showNameEditDialog) {
        Dialog(onDismissRequest = { showNameEditDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF130E2E))
                    .border(1.dp, Color(0xFF2E2452), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enter Your Name",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showNameEditDialog = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF9D8EC4)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = tempNameInput,
                        onValueChange = { tempNameInput = it },
                        placeholder = { Text("Devotee", color = Color(0xFF6E648F)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (tempNameInput.isNotBlank()) {
                                    userName = tempNameInput.trim()
                                }
                                showNameEditDialog = false
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color(0xFF2E2452),
                            focusedContainerColor = Color(0xFF0C0720),
                            unfocusedContainerColor = Color(0xFF0C0720)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = { showNameEditDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = Color(0xFF9D8EC4))
                        }

                        Button(
                            onClick = {
                                if (tempNameInput.isNotBlank()) {
                                    userName = tempNameInput.trim()
                                }
                                showNameEditDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Custom Goal Dialog
    if (showCustomGoalDialog) {
        AlertDialog(
            onDismissRequest = { showCustomGoalDialog = false },
            containerColor = Color(0xFF130E2E),
            title = {
                Text(
                    text = "Custom Daily Mala Goal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your target malas per day (e.g. 10, 11, 16, 21, 51, 108):",
                        color = Color(0xFFB8B0D3),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customGoalInput,
                        onValueChange = { customGoalInput = it.filter { char -> char.isDigit() }.take(4) },
                        placeholder = { Text("10", color = Color(0xFF6E648F)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val parsed = customGoalInput.toIntOrNull()
                                if (parsed != null && parsed > 0) {
                                    selectedGoalMalas = parsed
                                }
                                showCustomGoalDialog = false
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color(0xFF2E2452),
                            focusedContainerColor = Color(0xFF0C0720),
                            unfocusedContainerColor = Color(0xFF0C0720)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = customGoalInput.toIntOrNull()
                        if (parsed != null && parsed > 0) {
                            selectedGoalMalas = parsed
                        }
                        showCustomGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Set Goal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomGoalDialog = false }) {
                    Text("Cancel", color = Color(0xFF9D8EC4))
                }
            }
        )
    }

    if (showAvatarDialog) {
        AvatarSelectionDialog(
            selectedAvatarId = selectedAvatarId,
            onAvatarSelected = { newAvatarId ->
                selectedAvatarId = newAvatarId
                showAvatarDialog = false
            },
            onDismissRequest = { showAvatarDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070417),
                        Color(0xFF0B0620),
                        Color(0xFF0E0728),
                        Color(0xFF070417)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF140D2E))
                        .border(1.dp, Color(0xFF261D4E), RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigateBack() }
                        .testTag("button_back"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Avatar & Mandala Backdrop Section
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showAvatarDialog = true }
                    .testTag("avatar_container"),
                contentAlignment = Alignment.Center
            ) {
                // Sacred Mandala and Starfield Canvas
                SacredMandalaBackdrop()

                // Devotional Avatar Graphic
                SpiritualAvatarGraphic(
                    avatarId = selectedAvatarId,
                    sizeDp = 108.dp,
                    showBorder = true,
                    onClick = { showAvatarDialog = true }
                )
            }

            // Tap to choose devotional avatar link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showAvatarDialog = true }
                    .padding(vertical = 4.dp)
                    .testTag("tap_avatar_prompt")
            ) {
                Text(
                    text = "Tap to choose devotional avatar ",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "✦",
                    fontSize = 13.sp,
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title with Devotional Flourishes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🍃 ",
                    fontSize = 18.sp,
                    color = Color(0xFFA78BFA)
                )
                Text(
                    text = "Let’s Set Up Your Profile",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = " 🍃",
                    fontSize = 18.sp,
                    color = Color(0xFFA78BFA)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Customize your spiritual journey",
                fontSize = 13.5.sp,
                color = Color(0xFF9E93BE),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ================= 1. YOUR NAME CARD =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0B24))
                    .border(1.dp, Color(0xFF231948), RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        tempNameInput = userName
                        showNameEditDialog = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("card_your_name")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar/Person Icon Badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF221648)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Devotee Profile",
                            tint = Color(0xFFB5A4E3),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Name Column
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Your Name",
                            fontSize = 11.5.sp,
                            color = Color(0xFF9588B8),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userName,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Edit Pencil Icon
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = Color(0xFFB5A4E3),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 2. DAILY JAAP GOAL CARD =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0B24))
                    .border(1.dp, Color(0xFF231948), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("card_daily_goal")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "🎯 ", fontSize = 15.sp)
                        Text(
                            text = "Daily Jaap Goal",
                            fontSize = 13.5.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Preset Goal Options
                    val defaultOptions = listOf(1, 2, 4, 8)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        defaultOptions.forEach { goal ->
                            val isSelected = (selectedGoalMalas == goal)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF221648) else Color(0xFF130E2E)
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF2B2050),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedGoalMalas = goal }
                                    .testTag("goal_chip_$goal"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$goal Mala",
                                    color = if (isSelected) Color.White else Color(0xFFB3A8D4),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Goal Pill with Centered Star Badge on Top Border
                    val isCustomSelected = !defaultOptions.contains(selectedGoalMalas)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Main Pill Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isCustomSelected) Color(0xFF221648) else Color(0xFF130E2E)
                                )
                                .border(
                                    width = if (isCustomSelected) 1.5.dp else 1.2.dp,
                                    color = Color(0xFF7C3AED),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    customGoalInput = if (isCustomSelected) selectedGoalMalas.toString() else "10"
                                    showCustomGoalDialog = true
                                }
                                .testTag("button_custom_goal"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "✨ ",
                                    fontSize = 14.sp,
                                    color = Color(0xFFC084FC)
                                )
                                Text(
                                    text = if (isCustomSelected) "Custom Goal: $selectedGoalMalas Malas" else "Custom Goal: 10 Malas",
                                    color = Color(0xFFC084FC),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Centered Golden Star Badge overlapping top border
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEAB308))
                                .border(1.dp, Color(0xFF130E2E), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "★",
                                fontSize = 11.sp,
                                color = Color(0xFF130E2E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 3. DEFAULT MANTRA CARD =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0B24))
                    .border(1.dp, Color(0xFF231948), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("card_default_mantra")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Om Symbol Badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF221648)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ॐ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC084FC)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Mantra Dropdown Container
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Default Mantra",
                            fontSize = 11.5.sp,
                            color = Color(0xFF9588B8),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF130E2E))
                                .border(1.dp, Color(0xFF2B2050), RoundedCornerShape(12.dp))
                                .clickable { mantraDropdownExpanded = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "📿 ", fontSize = 15.sp)
                                    Text(
                                        text = selectedMantraObj?.textEnglish ?: "Radhe Radhe",
                                        color = Color.White,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (selectedMantraObj?.textHindi?.isNotBlank() == true) {
                                        Text(
                                            text = " (${selectedMantraObj.textHindi})",
                                            color = Color(0xFFB5A4E3),
                                            fontSize = 12.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Mantra",
                                    tint = Color(0xFF9588B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = mantraDropdownExpanded,
                                onDismissRequest = { mantraDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(Color(0xFF18103A))
                                    .border(1.dp, Color(0xFF332660), RoundedCornerShape(12.dp))
                            ) {
                                allMantras.forEach { mantra ->
                                    val isSelected = mantra.id == selectedMantraId
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${mantra.textEnglish} (${mantra.textHindi})",
                                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color(0xFF00E5FF),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedMantraId = mantra.id
                                            mantraDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 4. REMINDER (OPTIONAL) CARD =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0B24))
                    .border(1.dp, Color(0xFF231948), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("card_reminders")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "🔔 ", fontSize = 15.sp)
                        Text(
                            text = "Reminder (Optional)",
                            fontSize = 13.5.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Inner Grouped Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF130E2E))
                            .border(1.dp, Color(0xFF261C48), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            // Morning Reminder Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "☀️ ", fontSize = 16.sp)
                                    Text(
                                        text = "Morning Reminder",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = morningReminderTime,
                                        color = Color(0xFFB5A4E3),
                                        fontSize = 12.5.sp,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                openTimePicker(morningReminderTime) { newTime ->
                                                    morningReminderTime = newTime
                                                }
                                            }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                            .testTag("time_picker_morning")
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Switch(
                                        checked = morningReminderEnabled,
                                        onCheckedChange = { morningReminderEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF7C3AED),
                                            uncheckedThumbColor = Color(0xFF7E729E),
                                            uncheckedTrackColor = Color(0xFF261C48)
                                        ),
                                        modifier = Modifier.testTag("switch_morning")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFF20163E))
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Evening Reminder Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🌙 ", fontSize = 16.sp)
                                    Text(
                                        text = "Evening Reminder",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = eveningReminderTime,
                                        color = Color(0xFFB5A4E3),
                                        fontSize = 12.5.sp,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                openTimePicker(eveningReminderTime) { newTime ->
                                                    eveningReminderTime = newTime
                                                }
                                            }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                            .testTag("time_picker_evening")
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Switch(
                                        checked = eveningReminderEnabled,
                                        onCheckedChange = { eveningReminderEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF7C3AED),
                                            uncheckedThumbColor = Color(0xFF7E729E),
                                            uncheckedTrackColor = Color(0xFF261C48)
                                        ),
                                        modifier = Modifier.testTag("switch_evening")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ================= 5. START MY JOURNEY BUTTON =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4F46E5),
                                Color(0xFF6366F1),
                                Color(0xFF3B82F6)
                            )
                        )
                    )
                    .clickable(enabled = !isSaving) {
                        isSaving = true
                        viewModel.saveUserProfileAndCompleteOnboarding(
                            name = userName,
                            dailyGoalMalas = selectedGoalMalas,
                            selectedMantraId = selectedMantraId,
                            morningReminderEnabled = morningReminderEnabled,
                            morningReminderTime = morningReminderTime,
                            eveningReminderEnabled = eveningReminderEnabled,
                            eveningReminderTime = eveningReminderTime,
                            authMethod = authMethod,
                            email = userEmail,
                            avatarId = selectedAvatarId,
                            onSuccess = {
                                isSaving = false
                                onCompleteProfile()
                            },
                            onError = { errorMsg ->
                                isSaving = false
                                android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    .testTag("button_start_journey"),
                contentAlignment = Alignment.Center
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✨ ",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Start My Journey",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "  →",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Skip for now Link
            Text(
                text = "Skip for now",
                color = Color(0xFF8E84B0),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable(enabled = !isSaving) {
                        isSaving = true
                        viewModel.completeOnboardingQuickly(
                            onSuccess = {
                                isSaving = false
                                onCompleteProfile()
                            }
                        )
                    }
                    .padding(8.dp)
                    .testTag("link_skip_for_now")
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Renders the sacred mandala circular geometry with golden halo & starfield
 * optimized to eliminate object allocation during rendering.
 */
@Composable
fun SacredMandalaBackdrop() {
    val infiniteTransition = rememberInfiniteTransition(label = "mandala_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    // Precomputed unit geometry for 16 petals to prevent runtime Math/Trig allocations
    val petalCount = 16
    val unitPetals = remember {
        (0 until petalCount).map { i ->
            val angle = Math.toRadians(i * 360.0 / petalCount)
            val nextAngle = Math.toRadians((i + 1) * 360.0 / petalCount)
            val midAngle = (angle + nextAngle) / 2.0
            val ctrlAngle1 = angle + (nextAngle - angle) * 0.25
            val ctrlAngle2 = angle + (nextAngle - angle) * 0.75

            floatArrayOf(
                cos(angle).toFloat(), sin(angle).toFloat(),
                cos(ctrlAngle1).toFloat(), sin(ctrlAngle1).toFloat(),
                cos(midAngle).toFloat(), sin(midAngle).toFloat(),
                cos(ctrlAngle2).toFloat(), sin(ctrlAngle2).toFloat(),
                cos(nextAngle).toFloat(), sin(nextAngle).toFloat()
            )
        }
    }

    val reusablePetalPath = remember { Path() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f

        // 1. Soft cosmic amber glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD700).copy(alpha = 0.20f * glowPulse),
                    Color(0xFF8B5CF6).copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = center,
                radius = maxRadius * 0.95f
            ),
            radius = maxRadius * 0.95f,
            center = center
        )

        // 2. Outer concentric geometric rings
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.18f),
            radius = maxRadius * 0.88f,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFA78BFA).copy(alpha = 0.15f),
            radius = maxRadius * 0.72f,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.25f),
            radius = maxRadius * 0.58f,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // 3. 16-Petal Sacred Mandala Lotus Geometry using reused precomputed coords
        val rInner = maxRadius * 0.58f
        val rOuter = maxRadius * 0.86f
        val rCtrl = rOuter * 0.75f
        val strokeStyle = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
        val petalColor = Color(0xFFFFD700).copy(alpha = 0.12f)
        val dotColor = Color(0xFFFFD700).copy(alpha = 0.40f)
        val dotRadius = 1.5.dp.toPx()

        for (coords in unitPetals) {
            val x1 = center.x + rInner * coords[0]
            val y1 = center.y + rInner * coords[1]
            val c1x = center.x + rCtrl * coords[2]
            val c1y = center.y + rCtrl * coords[3]
            val xTip = center.x + rOuter * coords[4]
            val yTip = center.y + rOuter * coords[5]
            val c2x = center.x + rCtrl * coords[6]
            val c2y = center.y + rCtrl * coords[7]
            val x2 = center.x + rInner * coords[8]
            val y2 = center.y + rInner * coords[9]

            reusablePetalPath.apply {
                reset()
                moveTo(x1, y1)
                quadraticTo(c1x, c1y, xTip, yTip)
                quadraticTo(c2x, c2y, x2, y2)
            }

            drawPath(
                path = reusablePetalPath,
                color = petalColor,
                style = strokeStyle
            )

            // Small decorative dots at petal tips
            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(xTip, yTip)
            )
        }

        // 4. Fixed star dust points around the mandala
        val starPositions = listOf(
            Offset(0.15f, 0.25f),
            Offset(0.85f, 0.22f),
            Offset(0.12f, 0.75f),
            Offset(0.88f, 0.78f),
            Offset(0.28f, 0.12f),
            Offset(0.72f, 0.14f),
            Offset(0.22f, 0.88f),
            Offset(0.78f, 0.86f),
            Offset(0.50f, 0.06f),
            Offset(0.94f, 0.48f),
            Offset(0.06f, 0.52f)
        )

        starPositions.forEachIndexed { index, normOffset ->
            val starX = size.width * normOffset.x
            val starY = size.height * normOffset.y
            val alpha = if (index % 2 == 0) 0.55f else 0.35f
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = alpha),
                radius = (if (index % 3 == 0) 2.dp else 1.2.dp).toPx(),
                center = Offset(starX, starY)
            )
        }
    }
}
