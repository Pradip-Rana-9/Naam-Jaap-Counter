package com.example.ui.screens

import com.example.ui.components.AvatarSelectionDialog
import com.example.ui.components.SpiritualAvatarGraphic

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JaapLogoGraphic
import com.example.ui.viewmodel.JaapViewModel
import java.util.Calendar

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
    var selectedAvatarId by remember(userSettings) { mutableIntStateOf(userSettings?.avatarId ?: 1) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var selectedGoalMalas by remember(userSettings) { mutableIntStateOf(userSettings?.dailyGoalMalas ?: 4) }
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

    if (showCustomGoalDialog) {
        AlertDialog(
            onDismissRequest = { showCustomGoalDialog = false },
            containerColor = Color(0xFF140D2E),
            title = {
                Text(
                    text = "Custom Daily Mala Goal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your custom target malas per day (e.g. 11, 16, 21, 108):",
                        color = Color(0xFFB8B0D3),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customGoalInput,
                        onValueChange = { customGoalInput = it.filter { char -> char.isDigit() } },
                        placeholder = { Text("16", color = Color.Gray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF332A54),
                            focusedContainerColor = Color(0xFF0C0720),
                            unfocusedContainerColor = Color(0xFF0C0720)
                        ),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text("Set Goal", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomGoalDialog = false }) {
                    Text("Cancel", color = Color(0xFFB8B0D3))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070417),
                        Color(0xFF0F0829),
                        Color(0xFF170C3D),
                        Color(0xFF070417)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF18103A))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Let's Set Up Your Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Customize your spiritual journey",
                fontSize = 13.sp,
                color = Color(0xFFB0A8D1)
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            // Avatar badge
            SpiritualAvatarGraphic(
                avatarId = selectedAvatarId,
                sizeDp = 100.dp,
                onClick = { showAvatarDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap to choose devotional avatar ✦",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFD700),
                modifier = Modifier.clickable { showAvatarDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Your Name Input
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Your Name",
                    fontSize = 13.sp,
                    color = Color(0xFFCEC7E8),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = { Text("Enter your name", color = Color(0xFF6E648F)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = Color(0xFF8B7CB2)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF2E2452),
                        focusedContainerColor = Color(0xFF100A29),
                        unfocusedContainerColor = Color(0xFF100A29)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Daily Jaap Goal Chips
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Daily Jaap Goal",
                    fontSize = 13.sp,
                    color = Color(0xFFCEC7E8),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

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
                                .background(if (isSelected) Color(0xFF211545) else Color(0xFF100A29))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF2E2452),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedGoalMalas = goal },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$goal Mala",
                                color = if (isSelected) Color.White else Color(0xFFADA2D1),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom chip button
                val isCustomSelected = !defaultOptions.contains(selectedGoalMalas)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCustomSelected) Color(0xFF211545) else Color(0xFF100A29))
                        .border(
                            width = if (isCustomSelected) 1.5.dp else 1.dp,
                            color = if (isCustomSelected) Color(0xFF8B5CF6) else Color(0xFF2E2452),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { showCustomGoalDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCustomSelected) "✎ Custom Goal: $selectedGoalMalas Malas" else "✎ Custom",
                        color = if (isCustomSelected) Color(0xFF00E5FF) else Color(0xFFADA2D1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Default Mantra Dropdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Default Mantra",
                    fontSize = 13.sp,
                    color = Color(0xFFCEC7E8),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF100A29))
                        .border(1.dp, Color(0xFF2E2452), RoundedCornerShape(12.dp))
                        .clickable { mantraDropdownExpanded = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📿 ", fontSize = 16.sp)
                            Text(
                                text = selectedMantraObj?.textEnglish ?: "Radhe Radhe",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (selectedMantraObj?.textHindi?.isNotBlank() == true) {
                                Text(
                                    text = " (${selectedMantraObj.textHindi})",
                                    color = Color(0xFF9D8EC4),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Mantra",
                            tint = Color(0xFF8B7CB2)
                        )
                    }

                    DropdownMenu(
                        expanded = mantraDropdownExpanded,
                        onDismissRequest = { mantraDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color(0xFF18103A))
                    ) {
                        allMantras.forEach { mantra ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${mantra.textEnglish} (${mantra.textHindi})",
                                        color = if (mantra.id == selectedMantraId) Color(0xFF00E5FF) else Color.White
                                    )
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

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Reminder (Optional)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Reminder (Optional)",
                    fontSize = 13.sp,
                    color = Color(0xFFCEC7E8),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF100A29))
                        .border(1.dp, Color(0xFF2E2452), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            openTimePicker(morningReminderTime) { newTime ->
                                                morningReminderTime = newTime
                                            }
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = morningReminderTime,
                                        color = Color(0xFFB0A8D1),
                                        fontSize = 12.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Pick time",
                                        tint = Color(0xFF8B7CB2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Switch(
                                    checked = morningReminderEnabled,
                                    onCheckedChange = { morningReminderEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF7C3AED),
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color(0xFF251A46)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF1F163D))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

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
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            openTimePicker(eveningReminderTime) { newTime ->
                                                eveningReminderTime = newTime
                                            }
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = eveningReminderTime,
                                        color = Color(0xFFB0A8D1),
                                        fontSize = 12.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Pick time",
                                        tint = Color(0xFF8B7CB2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Switch(
                                    checked = eveningReminderEnabled,
                                    onCheckedChange = { eveningReminderEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF7C3AED),
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color(0xFF251A46)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            var isSaving by remember { mutableStateOf(false) }

            // Start My Journey Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4F46E5),
                                Color(0xFF7C3AED),
                                Color(0xFF2563EB)
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
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isSaving) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "★ Start My Journey →",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Skip for now Link
            Text(
                text = "Skip for now",
                color = Color(0xFF9D8EC4),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(enabled = !isSaving) {
                    isSaving = true
                    viewModel.completeOnboardingQuickly(
                        onSuccess = {
                            isSaving = false
                            onCompleteProfile()
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
