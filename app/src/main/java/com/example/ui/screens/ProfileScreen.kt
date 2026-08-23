package com.example.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.LegalTermsDialog
import com.example.ui.components.ProfileEditDialog
import com.example.ui.components.SpiritualAvatarGraphic
import com.example.ui.viewmodel.JaapViewModel
import java.util.Calendar

enum class ProfileSubScreen {
    MAIN,
    REMINDER,
    DAILY_TARGET,
    HAPTIC_SOUND,
    THEME_APPEARANCE,
    LANGUAGE,
    DATA_PRIVACY,
    ACCOUNT,
    PRIVACY_POLICY
}

@Composable
fun ProfileScreen(
    viewModel: JaapViewModel,
    onNavigateToProgress: () -> Unit,
    onOpenAccountSetup: () -> Unit = {},
    onLoggedOut: () -> Unit = {}
) {
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission required for daily reminders", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkNotificationPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                onGranted()
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onGranted()
        }
    }

    val userSettings by viewModel.userSettings.collectAsState()
    val totalBeads by viewModel.totalBeads.collectAsState()

    val rawUserName = userSettings?.userName
    val userName = if (!rawUserName.isNullOrBlank()) rawUserName else "Devotee"
    val avatarId = userSettings?.avatarId ?: 1
    val joinDate = userSettings?.joinDateString ?: "02 Aug, 2026"
    val reminderEnabled = userSettings?.reminderEnabled ?: false
    val reminderTime = userSettings?.reminderTime ?: "20:00"
    val hapticEnabled = userSettings?.hapticFeedbackEnabled ?: false
    val chimeEnabled = userSettings?.audioChimeEnabled ?: false
    val dailyGoal = userSettings?.dailyGoalMalas ?: 10
    val themeMode = userSettings?.themeMode ?: "DARK"
    val language = userSettings?.language ?: "ENGLISH"

    val authMethod = userSettings?.authMethod ?: "GUEST"
    val userEmail = userSettings?.userEmail ?: ""
    val isLoggedIn = authMethod != "GUEST" && userEmail.isNotBlank()

    var activeSubScreen by remember { mutableStateOf(ProfileSubScreen.MAIN) }

    // Dialog states
    var showLoginDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var isLogoutLoading by remember { mutableStateOf(false) }
    var logoutErrorMsg by remember { mutableStateOf<String?>(null) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showResetDataDialog by remember { mutableStateOf(false) }
    var inputLoginEmail by remember { mutableStateOf("") }
    var inputLoginName by remember { mutableStateOf(userName) }

    var showAvatarDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }
    var showCustomGoalDialog by remember { mutableStateOf(false) }
    var customGoalInput by remember { mutableStateOf("$dailyGoal") }

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutAppDialog by remember { mutableStateOf(false) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val spiritualRank = when {
        totalBeads >= 1000000 -> "Param Siddha"
        totalBeads >= 100000 -> "Bhakti Acharya"
        totalBeads >= 10000 -> "Sadhak"
        totalBeads >= 1000 -> "Pratham Bhakta"
        else -> "Beginner Devotee"
    }

    // Intercept back button when inside a sub-screen
    if (activeSubScreen != ProfileSubScreen.MAIN) {
        BackHandler {
            activeSubScreen = ProfileSubScreen.MAIN
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (activeSubScreen) {
                ProfileSubScreen.MAIN -> {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Profile",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                            Text(
                                text = "Manage your spiritual journey",
                                fontSize = 13.sp,
                                color = textColorSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(cardBg)
                                .border(1.dp, cardBorder, CircleShape)
                                .clickable { activeSubScreen = ProfileSubScreen.REMINDER },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (reminderEnabled) activeColor else textColorSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Personal Devotional Identity Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAvatarDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SpiritualAvatarGraphic(
                                avatarId = avatarId,
                                sizeDp = 64.dp,
                                onClick = { showAvatarDialog = true }
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Practicing since $joinDate",
                                    fontSize = 12.sp,
                                    color = textColorSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Elegant Rank Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(activeColor.copy(alpha = 0.15f))
                                        .border(1.dp, activeColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Rank: $spiritualRank",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = activeColor
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    editedName = userName
                                    showEditNameDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = activeColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grouped Settings List Sections
                    SectionHeader(title = "PRACTICE")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingRowItem(
                            icon = Icons.Default.Schedule,
                            iconTint = activeColor,
                            title = "Daily Jaap Reminder",
                            subtitle = if (reminderEnabled) "Scheduled for $reminderTime" else "Off",
                            onClick = { activeSubScreen = ProfileSubScreen.REMINDER }
                        )

                        SettingRowItem(
                            icon = Icons.Default.TrackChanges,
                            iconTint = activeColor,
                            title = "Daily Target",
                            subtitle = "$dailyGoal Malas daily",
                            onClick = { activeSubScreen = ProfileSubScreen.DAILY_TARGET }
                        )

                        SettingRowItem(
                            icon = Icons.Default.Vibration,
                            iconTint = activeColor,
                            title = "Haptic & Sound",
                            subtitle = "Vibration and completion sound preferences",
                            onClick = { activeSubScreen = ProfileSubScreen.HAPTIC_SOUND }
                        )
                    }

                    SectionHeader(title = "PERSONALIZATION")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingRowItem(
                            icon = Icons.Default.Palette,
                            iconTint = secondaryColor,
                            title = "Theme & Appearance",
                            subtitle = getThemeDisplayName(themeMode),
                            onClick = { activeSubScreen = ProfileSubScreen.THEME_APPEARANCE }
                        )

                        SettingRowItem(
                            icon = Icons.Default.Language,
                            iconTint = secondaryColor,
                            title = "Language",
                            subtitle = getLanguageDisplayName(language),
                            onClick = { activeSubScreen = ProfileSubScreen.LANGUAGE }
                        )
                    }

                    SectionHeader(title = "DATA")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingRowItem(
                            icon = Icons.Default.Security,
                            iconTint = activeColor,
                            title = "Data & Privacy",
                            subtitle = "Export or manage your data privately",
                            onClick = { activeSubScreen = ProfileSubScreen.DATA_PRIVACY },
                            testTag = "card_export_data"
                        )
                    }

                    SectionHeader(title = "ACCOUNT")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingRowItem(
                            icon = Icons.Default.Person,
                            iconTint = if (isLoggedIn) activeColor else Color(0xFFF59E0B),
                            title = "Account",
                            subtitle = if (isLoggedIn) "Edit profile, security & logout" else "Guest Mode • Tap to sign in",
                            onClick = { activeSubScreen = ProfileSubScreen.ACCOUNT },
                            testTag = "card_account_security"
                        )
                    }

                    SectionHeader(title = "ABOUT")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingRowItem(
                            icon = Icons.AutoMirrored.Filled.Help,
                            iconTint = textColorSecondary,
                            title = "Help & Support",
                            onClick = { showHelpDialog = true }
                        )

                        SettingRowItem(
                            icon = Icons.Default.Description,
                            iconTint = textColorSecondary,
                            title = "Privacy Policy",
                            onClick = { activeSubScreen = ProfileSubScreen.PRIVACY_POLICY },
                            testTag = "card_privacy_policy"
                        )

                        SettingRowItem(
                            icon = Icons.Default.Gavel,
                            iconTint = textColorSecondary,
                            title = "Terms & Conditions",
                            onClick = { showTermsDialog = true }
                        )

                        SettingRowItem(
                            icon = Icons.Default.Info,
                            iconTint = textColorSecondary,
                            title = "About App",
                            subtitle = "Version 1.0.0",
                            onClick = { showAboutAppDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Jaap - Daily Spiritual Counter • v1.0.0",
                        fontSize = 11.sp,
                        color = textColorSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ==================== SUB SCREENS ====================

                ProfileSubScreen.REMINDER -> {
                    SubScreenHeader(
                        title = "Daily Jaap Reminder",
                        subtitle = "Manage reminder & notifications",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

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
                                Column {
                                    Text(
                                        text = "Daily Reminder",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColorPrimary
                                    )
                                    Text(
                                        text = "Receive notifications at set time",
                                        fontSize = 12.sp,
                                        color = textColorSecondary
                                    )
                                }

                                Switch(
                                    checked = reminderEnabled,
                                    onCheckedChange = { enabled ->
                                        if (enabled) {
                                            checkNotificationPermission {
                                                viewModel.toggleReminder(true, context)
                                            }
                                        } else {
                                            viewModel.toggleReminder(false, context)
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = activeColor
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardBorder.copy(alpha = 0.5f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                val statusText = if (reminderEnabled) "• Scheduled for: Daily at $reminderTime" else "• Reminder is currently disabled"
                                Text(
                                    text = statusText,
                                    fontSize = 12.sp,
                                    color = if (reminderEnabled) activeColor else textColorSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Quick Preset Times",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val presets = listOf(
                                Pair("6:00 AM", Pair(6, 0)),
                                Pair("8:00 AM", Pair(8, 0)),
                                Pair("9:00 AM", Pair(9, 0)),
                                Pair("6:00 PM", Pair(18, 0)),
                                Pair("8:00 PM", Pair(20, 0)),
                                Pair("9:00 PM", Pair(21, 0))
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                presets.chunked(3).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        row.forEach { preset ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(cardBorder)
                                                    .clickable {
                                                        checkNotificationPermission {
                                                            viewModel.setReminderTime(preset.second.first, preset.second.second, context)
                                                        }
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = preset.first,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = textColorPrimary
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Custom Time Button
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, activeColor, RoundedCornerShape(10.dp))
                                        .clickable {
                                            checkNotificationPermission {
                                                val cal = Calendar.getInstance()
                                                TimePickerDialog(
                                                    context,
                                                    { _, h, m -> viewModel.setReminderTime(h, m, context) },
                                                    cal.get(Calendar.HOUR_OF_DAY),
                                                    cal.get(Calendar.MINUTE),
                                                    false
                                                ).show()
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = activeColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Set Custom Time",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = activeColor
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    checkNotificationPermission {
                                        viewModel.testReminderNotification(context)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_test_reminder"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Test Reminder Notification",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                ProfileSubScreen.DAILY_TARGET -> {
                    SubScreenHeader(
                        title = "Daily Target",
                        subtitle = "Set your daily Mala goal",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

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
                                Column {
                                    Text(
                                        text = "Daily Target Malas",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColorPrimary
                                    )
                                    Text(
                                        text = "Current Goal: $dailyGoal Malas (${dailyGoal * 108} beads)",
                                        fontSize = 12.sp,
                                        color = textColorSecondary
                                    )
                                }

                                Text(
                                    text = "Custom",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeColor,
                                    modifier = Modifier.clickable {
                                        customGoalInput = "$dailyGoal"
                                        showCustomGoalDialog = true
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val goalPresets = listOf(1, 3, 5, 7, 10, 16)
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                goalPresets.chunked(3).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        row.forEach { g ->
                                            val isSel = dailyGoal == g
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSel) activeColor else cardBorder)
                                                    .clickable { viewModel.updateDailyGoal(g) }
                                                    .padding(vertical = 14.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "$g",
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSel) MaterialTheme.colorScheme.onPrimary else textColorPrimary
                                                    )
                                                    Text(
                                                        text = if (g == 1) "Mala" else "Malas",
                                                        fontSize = 11.sp,
                                                        color = if (isSel) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else textColorSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileSubScreen.HAPTIC_SOUND -> {
                    SubScreenHeader(
                        title = "Haptic & Sound",
                        subtitle = "Chanting feedback preferences",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

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
                                    Icon(
                                        imageVector = Icons.Default.Vibration,
                                        contentDescription = null,
                                        tint = activeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Haptic Feedback",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColorPrimary
                                        )
                                        Text(
                                            text = "Feel vibration on every bead count",
                                            fontSize = 12.sp,
                                            color = textColorSecondary
                                        )
                                    }
                                }
                                Switch(
                                    checked = hapticEnabled,
                                    onCheckedChange = { viewModel.updateUserSettings(hapticEnabled = it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = activeColor
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = activeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Completion Chime",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColorPrimary
                                        )
                                        Text(
                                            text = "Play sound when a Mala is completed",
                                            fontSize = 12.sp,
                                            color = textColorSecondary
                                        )
                                    }
                                }
                                Switch(
                                    checked = chimeEnabled,
                                    onCheckedChange = { viewModel.updateUserSettings(audioChimeEnabled = it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = activeColor
                                    )
                                )
                            }
                        }
                    }
                }

                ProfileSubScreen.THEME_APPEARANCE -> {
                    SubScreenHeader(
                        title = "Theme & Appearance",
                        subtitle = "Choose your devotional theme",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

                    val themes = listOf(
                        Triple("Midnight Gold", "MIDNIGHT_GOLD", Color(0xFFF5820A)),
                        Triple("Vrindavan Sunrise", "VRINDAVAN_SUNRISE", Color(0xFFFF6D00)),
                        Triple("Peacock Blue", "PEACOCK_BLUE", Color(0xFF00B0FF)),
                        Triple("Radha Pink", "RADHA_PINK", Color(0xFFFF4081)),
                        Triple("Temple Sandalwood", "TEMPLE_SANDALWOOD", Color(0xFF8D6E63)),
                        Triple("Devotional Light", "LIGHT", Color(0xFFE65100))
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        themes.forEach { t ->
                            val isSel = themeMode == t.second
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateUserSettings(themeMode = t.second) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSel) 2.dp else 1.dp,
                                    if (isSel) t.third else cardBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(t.third)
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Text(
                                            text = t.first,
                                            fontSize = 15.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = textColorPrimary
                                        )
                                    }

                                    if (isSel) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = t.third
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileSubScreen.LANGUAGE -> {
                    SubScreenHeader(
                        title = "Language",
                        subtitle = "Choose app language",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

                    val langs = listOf(
                        Pair("English", "ENGLISH"),
                        Pair("हिन्दी", "HINDI"),
                        Pair("Hinglish", "HINGLISH"),
                        Pair("বাংলা", "BENGALI"),
                        Pair("ગુજરાતી", "GUJARATI"),
                        Pair("मराठी", "MARATHI")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        langs.forEach { l ->
                            val isSel = language == l.second
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateUserSettings(language = l.second) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSel) 1.5.dp else 1.dp,
                                    if (isSel) activeColor else cardBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = l.first,
                                        fontSize = 16.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) activeColor else textColorPrimary
                                    )

                                    if (isSel) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = activeColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileSubScreen.DATA_PRIVACY -> {
                    SubScreenHeader(
                        title = "Data & Privacy",
                        subtitle = "Manage and export your data",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

                    // Export Data Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.exportDataAsCSV(context) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(secondaryColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = secondaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Export Data (CSV)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = "Download your japa and session history",
                                    fontSize = 12.sp,
                                    color = textColorSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = textColorSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDataDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reset App Data",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                                Text(
                                    text = "Reset local counters, streaks, and history to 0",
                                    fontSize = 12.sp,
                                    color = textColorSecondary
                                )
                            }
                        }
                    }

                    if (isLoggedIn) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeleteAccountDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDC2626).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Delete Account",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                    Text(
                                        text = "Permanently remove your account & cloud data",
                                        fontSize = 12.sp,
                                        color = textColorSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeSubScreen = ProfileSubScreen.PRIVACY_POLICY },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(activeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = activeColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Read Privacy Policy",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = "Devotional privacy, data safety & security terms",
                                    fontSize = 12.sp,
                                    color = textColorSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = textColorSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your chanting records are stored securely on your device. When signed in, data is safely synced to your personal account.",
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                ProfileSubScreen.ACCOUNT -> {
                    SubScreenHeader(
                        title = "Account",
                        subtitle = "Manage your profile and sign-in",
                        onBack = { activeSubScreen = ProfileSubScreen.MAIN },
                        textColorPrimary = textColorPrimary,
                        textColorSecondary = textColorSecondary
                    )

                    // Profile Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SpiritualAvatarGraphic(
                                avatarId = avatarId,
                                sizeDp = 52.dp,
                                onClick = { showAvatarDialog = true }
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = "Rank: $spiritualRank",
                                    fontSize = 12.sp,
                                    color = activeColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isLoggedIn) {
                        // Account details for logged in user
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Account Details",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Account Email", fontSize = 13.sp, color = textColorSecondary)
                                    Text(userEmail, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColorPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Sign-In Method", fontSize = 13.sp, color = textColorSecondary)
                                    Text(authMethod, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = activeColor)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Edit Profile Button
                        Button(
                            onClick = {
                                editedName = userName
                                showEditNameDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cardBorder.copy(alpha = 0.5f), contentColor = textColorPrimary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Logout Button inside Account
                        Button(
                            onClick = { showLogoutConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_logout"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626).copy(alpha = 0.15f),
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                    } else {
                        // Guest mode view inside Account
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Guest Mode (Local Only)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Your Jaap counts, streaks, and malas are currently stored locally on this device only and may not be available after app data is cleared, uninstall/reinstall, or device reset. Sign in or create a free account to permanently preserve your sadhana in the cloud.",
                                    fontSize = 12.sp,
                                    color = textColorSecondary,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    inputLoginName = userName
                                    showLoginDialog = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_login_register"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                            ) {
                                Text("Login / Register", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            }

                            Button(
                                onClick = { onOpenAccountSetup() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = cardBorder.copy(alpha = 0.5f), contentColor = textColorPrimary)
                            ) {
                                Text("Account Setup", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColorPrimary)
                            }
                        }
                    }
                }

                ProfileSubScreen.PRIVACY_POLICY -> {
                    PrivacyPolicyScreenContent(
                        onNavigateBack = { activeSubScreen = ProfileSubScreen.MAIN }
                    )
                }
            }
        }
    }

    // ==================== DIALOGS ====================

    if (showAvatarDialog) {
        ProfileEditDialog(
            currentName = userName,
            currentAvatarId = avatarId,
            onSave = { newName, newAvatarId ->
                viewModel.updateUserNameAndAvatar(newName, newAvatarId)
            },
            onDismissRequest = { showAvatarDialog = false }
        )
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Profile Name", color = textColorPrimary) },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Devotee Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalName = if (editedName.isBlank()) "Devotee" else editedName
                        viewModel.updateUserSettings(userName = finalName)
                        showEditNameDialog = false
                    }
                ) {
                    Text("Save", color = activeColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    if (showCustomGoalDialog) {
        AlertDialog(
            onDismissRequest = { showCustomGoalDialog = false },
            title = { Text("Set Custom Daily Target", color = textColorPrimary) },
            text = {
                OutlinedTextField(
                    value = customGoalInput,
                    onValueChange = { customGoalInput = it },
                    label = { Text("Daily Target Malas (e.g. 16)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val num = customGoalInput.toIntOrNull() ?: 10
                        if (num > 0) {
                            viewModel.updateDailyGoal(num)
                        }
                        showCustomGoalDialog = false
                    }
                ) {
                    Text("Save Goal", color = activeColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomGoalDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    if (showLoginDialog) {
        var authDialogTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Register
        var emailAuthInput by remember { mutableStateOf("") }
        var passAuthInput by remember { mutableStateOf("") }
        var passVisible by remember { mutableStateOf(false) }
        var confirmPassAuthInput by remember { mutableStateOf("") }
        var confirmPassVisible by remember { mutableStateOf(false) }
        var authErrorMsg by remember { mutableStateOf<String?>(null) }
        var isAuthLoading by remember { mutableStateOf(false) }
        var showForgotPassInProfile by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                if (!isAuthLoading) {
                    showLoginDialog = false
                    authErrorMsg = null
                }
            },
            title = {
                Column {
                    Text(
                        text = if (authDialogTab == 0) "Sign In to Account" else "Create Devotee Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (authDialogTab == 0) 
                            "Sign in to restore your Naam Jaap records & streaks" 
                        else 
                            "Create an account to backup your chanting progress to cloud",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Tab Selector for Sign In vs Register
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(cardBorder.copy(alpha = 0.4f))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authDialogTab == 0) activeColor else Color.Transparent)
                                .clickable {
                                    authDialogTab = 0
                                    authErrorMsg = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 13.sp,
                                fontWeight = if (authDialogTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (authDialogTab == 0) MaterialTheme.colorScheme.onPrimary else textColorSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authDialogTab == 1) activeColor else Color.Transparent)
                                .clickable {
                                    authDialogTab = 1
                                    authErrorMsg = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Register (Naya)",
                                fontSize = 13.sp,
                                fontWeight = if (authDialogTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (authDialogTab == 1) MaterialTheme.colorScheme.onPrimary else textColorSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (authErrorMsg != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = authErrorMsg ?: "",
                                color = Color(0xFFFF8B8B),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = emailAuthInput,
                        onValueChange = {
                            emailAuthInput = it
                            authErrorMsg = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("devotee@example.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = passAuthInput,
                        onValueChange = {
                            passAuthInput = it
                            authErrorMsg = null
                        },
                        label = { Text("Password (min. 6 chars)") },
                        placeholder = { Text("••••••••") },
                        visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Icon(
                                    imageVector = if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = textColorSecondary
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (authDialogTab == 1) {
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassAuthInput,
                            onValueChange = {
                                confirmPassAuthInput = it
                                authErrorMsg = null
                            },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("••••••••") },
                            visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                    Icon(
                                        imageVector = if (confirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = textColorSecondary
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (authDialogTab == 0) {
                            TextButton(
                                onClick = {
                                    val email = emailAuthInput.trim()
                                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        authErrorMsg = "Please enter your registered email address first."
                                        return@TextButton
                                    }
                                    isAuthLoading = true
                                    viewModel.sendPasswordReset(
                                        email = email,
                                        onSuccess = {
                                            isAuthLoading = false
                                            Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
                                        },
                                        onError = { err ->
                                            isAuthLoading = false
                                            authErrorMsg = err
                                        }
                                    )
                                }
                            ) {
                                Text("Forgot Password?", fontSize = 11.sp, color = textColorSecondary)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        TextButton(
                            onClick = {
                                authDialogTab = if (authDialogTab == 0) 1 else 0
                                authErrorMsg = null
                            }
                        ) {
                            Text(
                                text = if (authDialogTab == 0) "Need account? Register →" else "Have account? Sign In →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = activeColor
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val email = emailAuthInput.trim()
                        val pass = passAuthInput.trim()
                        val confirmPass = confirmPassAuthInput.trim()

                        if (email.isBlank()) {
                            authErrorMsg = "Please enter your email address."
                            return@Button
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            authErrorMsg = "Please enter a valid email address."
                            return@Button
                        }
                        if (pass.isBlank()) {
                            authErrorMsg = "Please enter a password."
                            return@Button
                        }
                        if (pass.length < 6) {
                            authErrorMsg = "Password must be at least 6 characters."
                            return@Button
                        }
                        if (authDialogTab == 1) {
                            if (confirmPass.isBlank()) {
                                authErrorMsg = "Please confirm your password."
                                return@Button
                            }
                            if (pass != confirmPass) {
                                authErrorMsg = "Passwords do not match."
                                return@Button
                            }
                        }

                        isAuthLoading = true
                        authErrorMsg = null

                        if (authDialogTab == 1) {
                            viewModel.signUpWithEmail(
                                email = email,
                                password = pass,
                                onLoggedIn = {
                                    isAuthLoading = false
                                    showLoginDialog = false
                                    Toast.makeText(context, "Account registered successfully! All your chanting data has been saved.", Toast.LENGTH_SHORT).show()
                                },
                                onError = { err ->
                                    isAuthLoading = false
                                    authErrorMsg = err
                                }
                            )
                        } else {
                            viewModel.signInWithEmail(
                                email = email,
                                password = pass,
                                onLoggedIn = {
                                    isAuthLoading = false
                                    showLoginDialog = false
                                    Toast.makeText(context, "Signed in successfully. Records synced!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { err ->
                                    isAuthLoading = false
                                    authErrorMsg = err
                                }
                            )
                        }
                    },
                    enabled = !isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                ) {
                    if (isAuthLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(if (authDialogTab == 0) "Sign In" else "Register (Naya)", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isLogoutLoading) {
                    showLogoutConfirmDialog = false
                    logoutErrorMsg = null
                }
            },
            title = {
                Text(
                    text = "Logout from Account?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to log out?\n\nYour chanting records and profile will be backed up to the cloud before logging out so your progress, malas, and streak history can be restored anytime you log back in.",
                        fontSize = 13.sp,
                        color = textColorSecondary,
                        lineHeight = 18.sp
                    )

                    if (logoutErrorMsg != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = logoutErrorMsg ?: "",
                                    color = Color(0xFFEF4444),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLogoutLoading = true
                        logoutErrorMsg = null
                        viewModel.logoutUser(
                            onSuccess = {
                                isLogoutLoading = false
                                showLogoutConfirmDialog = false
                                logoutErrorMsg = null
                                activeSubScreen = ProfileSubScreen.MAIN
                                Toast.makeText(context, "Logged out. App reset to Guest mode.", Toast.LENGTH_SHORT).show()
                                onLoggedOut()
                            },
                            onError = { error ->
                                isLogoutLoading = false
                                logoutErrorMsg = error
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !isLogoutLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    if (isLogoutLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (logoutErrorMsg != null) "Retry Backup & Logout" else "Logout", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog = false
                        logoutErrorMsg = null
                        isLogoutLoading = false
                    },
                    enabled = !isLogoutLoading
                ) {
                    Text("Cancel", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    if (showResetDataDialog) {
        AlertDialog(
            onDismissRequest = { showResetDataDialog = false },
            title = {
                Text(
                    text = "Reset App Data?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            },
            text = {
                Text(
                    text = "This will erase all your local chanting sessions, malas count, streaks, and sankalps on this device, resetting the counter back to 0.\n\nAre you sure you want to proceed?",
                    fontSize = 13.sp,
                    color = textColorSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetGuestData {
                            showResetDataDialog = false
                            activeSubScreen = ProfileSubScreen.MAIN
                            Toast.makeText(context, "App data reset successfully.", Toast.LENGTH_SHORT).show()
                            onLoggedOut()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Reset All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDataDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Delete Account?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            },
            text = {
                Text(
                    text = "This permanently removes your account and associated cloud data. This action cannot be undone.",
                    fontSize = 13.sp,
                    color = textColorSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount(
                            onSuccess = {
                                showDeleteAccountDialog = false
                                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                onLoggedOut()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete Account", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    if (showPrivacyPolicyDialog) {
        LegalTermsDialog(
            initialTab = 0,
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }

    if (showTermsDialog) {
        LegalTermsDialog(
            initialTab = 1,
            onDismiss = { showTermsDialog = false }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support", color = textColorPrimary) },
            text = {
                Text(
                    text = "Need help with Jaap counter or reminders?\n\n• For support or feedback, contact us at: support@jaapapp.com",
                    fontSize = 13.sp,
                    color = textColorSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close", color = activeColor)
                }
            },
            containerColor = cardBg
        )
    }

    if (showAboutAppDialog) {
        AlertDialog(
            onDismissRequest = { showAboutAppDialog = false },
            title = { Text("About Jaap App", color = textColorPrimary) },
            text = {
                Column {
                    Text(text = "Jaap - Daily Spiritual Counter", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColorPrimary)
                    Text(text = "Version 1.0.0", fontSize = 12.sp, color = activeColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crafted with devotion to help spiritual seekers maintain a disciplined daily chanting practice.",
                        fontSize = 13.sp,
                        color = textColorSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutAppDialog = false }) {
                    Text("Close", color = activeColor)
                }
            },
            containerColor = cardBg
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingRowItem(
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SubScreenHeader(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    textColorPrimary: Color,
    textColorSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = textColorPrimary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColorPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }
        }
    }
}

private fun getThemeDisplayName(mode: String): String {
    return when (mode) {
        "MIDNIGHT_GOLD" -> "Midnight Gold"
        "VRINDAVAN_SUNRISE" -> "Vrindavan Sunrise"
        "PEACOCK_BLUE" -> "Peacock Blue"
        "RADHA_PINK" -> "Radha Pink"
        "TEMPLE_SANDALWOOD" -> "Temple Sandalwood"
        "LIGHT" -> "Devotional Light"
        else -> "Midnight Gold"
    }
}

private fun getLanguageDisplayName(lang: String): String {
    return when (lang) {
        "ENGLISH" -> "English"
        "HINDI" -> "हिन्दी"
        "HINGLISH" -> "Hinglish"
        "BENGALI" -> "বাংলা"
        "GUJARATI" -> "ગુજરાતી"
        "MARATHI" -> "मराठी"
        else -> "English"
    }
}
