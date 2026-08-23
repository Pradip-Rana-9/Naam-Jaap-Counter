package com.example.ui.components

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.viewmodel.JaapViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DevotionalNotificationDialog(
    viewModel: JaapViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val upcomingEvents by viewModel.upcomingPanchangEvents.collectAsState()
    val nextEkadashi = upcomingEvents.firstOrNull { it.category == "EKADASHI" }
    val language = userSettings?.language ?: "ENGLISH"
    val isHindi = language == "HINDI"

    val reminderEnabled = userSettings?.reminderEnabled ?: false
    val reminderTime = userSettings?.reminderTime ?: "06:30"

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(
                context,
                if (isHindi) "सूचनाएं सक्षम की गईं! 🔔" else "Notifications enabled! 🔔",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                if (isHindi) "कृपया डिवाइस सेटिंग से सूचना अनुमति दें" else "Please allow notification permission in settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val activeColor = Color(0xFFFF9800)
    val cyanAccent = Color(0xFF00E5FF)
    val cardBg = Color(0xFF0F1522)
    val cardBorder = Color(0xFF222B3D)
    val textColorPrimary = Color(0xFFF1F5F9)
    val textColorSecondary = Color(0xFF94A3B8)

    // Formatted reminder time display
    val formattedTime = remember(reminderTime) {
        try {
            val parts = reminderTime.split(":")
            val h = parts[0].toInt()
            val m = parts[1].toInt()
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
            }
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.format(cal.time)
        } catch (_: Exception) {
            reminderTime
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
            color = Color(0xFF0A0E17)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // 1. Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, activeColor.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (reminderEnabled) activeColor else textColorSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHindi) "भक्ति सूचनाएं व अनुस्मारक" else "Spiritual Notifications",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                            Text(
                                text = if (isHindi) "दैनिक जाप व एकादशी स्मरण" else "Daily Sadhana & Vrat Alerts",
                                fontSize = 12.sp,
                                color = textColorSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textColorSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Permission Warning Banner if Android 13+ and not granted
                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF331B00)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = "Permission Needed",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "सूचना अनुमति आवश्यक है" else "Permission Required",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFE0B2)
                                )
                                Text(
                                    text = if (isHindi) "समय पर जाप याद दिलाने के लिए अनुमति दें" else "Enable permission to receive daily reminders",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFCC80)
                                )
                            }
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                            ) {
                                Text(
                                    text = if (isHindi) "अनुमति दें" else "Allow",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // 2. DAILY SITTING REMINDER TOGGLE CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
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
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Daily Reminder",
                                        tint = activeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "दैनिक जाप स्मरण" else "Daily Jaap Reminder",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColorPrimary
                                    )
                                    Text(
                                        text = if (reminderEnabled) {
                                            if (isHindi) "समय: $formattedTime पर सक्रिय" else "Set for: $formattedTime"
                                        } else {
                                            if (isHindi) "निष्क्रिय (बंद)" else "Disabled"
                                        },
                                        fontSize = 12.sp,
                                        color = if (reminderEnabled) cyanAccent else textColorSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { isChecked ->
                                    if (isChecked && !hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    viewModel.toggleReminder(isChecked, context)
                                    if (isChecked) {
                                        Toast.makeText(
                                            context,
                                            if (isHindi) "स्मरण $formattedTime पर सेट हुआ 🕉️" else "Reminder set for $formattedTime 🕉️",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = activeColor
                                )
                            )
                        }

                        // If enabled, allow picking time and quick presets
                        if (reminderEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom Time Picker Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF161F2E))
                                    .clickable {
                                        val parts = reminderTime.split(":")
                                        val currentHour = parts.getOrNull(0)?.toIntOrNull() ?: 6
                                        val currentMin = parts.getOrNull(1)?.toIntOrNull() ?: 30

                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                viewModel.setReminderTime(hourOfDay, minute, context)
                                                Toast.makeText(
                                                    context,
                                                    if (isHindi) "नया समय सेट हुआ!" else "Reminder time updated!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            currentHour,
                                            currentMin,
                                            false
                                        ).show()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "समय बदलें (कस्टम)" else "Change Exact Time",
                                    fontSize = 13.sp,
                                    color = textColorPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formattedTime,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = activeColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Edit Time",
                                        tint = activeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Presets
                            Text(
                                text = if (isHindi) "शुभ काल चयन:" else "Recommended Sacred Times:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PresetChip(
                                    label = if (isHindi) "ब्रह्म मुहूर्त (04:30 AM)" else "Brahma Muhurta (4:30 AM)",
                                    icon = Icons.Default.WbTwilight,
                                    isSelected = reminderTime == "04:30",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    viewModel.setReminderTime(4, 30, context)
                                }

                                PresetChip(
                                    label = if (isHindi) "संध्या आरती (06:30 PM)" else "Sandhya (6:30 PM)",
                                    icon = Icons.Default.WbSunny,
                                    isSelected = reminderTime == "18:30",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    viewModel.setReminderTime(18, 30, context)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. INSTANT TEST BLESSING / NOTIFICATION TRIGGER
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Test Notification",
                                    tint = cyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "तत्काल सूचना परीक्षण" else "Send Test Blessing Alert",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = if (isHindi) "डिवाइस पर नोटिफिकेशन की जांच करें" else "Trigger an instant divine reminder",
                                    fontSize = 11.5.sp,
                                    color = textColorSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                viewModel.testReminderNotification(
                                    context,
                                    title = if (isHindi) "🌸 दैनिक हरि नाम स्मरण 🕉️" else "🌸 Daily Sacred Jaap Reminder 🕉️",
                                    body = if (isHindi) "राधे राधे! अपने मन को शांत कर आज का माला जाप पूर्ण करें।" else "Radhe Radhe! Take a serene pause to chant holy mantras today."
                                )
                                Toast.makeText(
                                    context,
                                    if (isHindi) "सूचना भेजी गई! चेक करें 🔔" else "Divine notification sent! Check bar 🔔",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, cyanAccent.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = cyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "टेस्ट नोटिफिकेशन भेजें" else "Test Notification Now",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = cyanAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. RECENT SPIRITUAL ALERTS & SATSANG UPDATES
                Text(
                    text = if (isHindi) "सक्रिय आध्यात्मिक अलर्ट" else "Active Devotional Alerts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                NotificationFeedItem(
                    icon = Icons.Default.CalendarMonth,
                    iconTint = Color(0xFF00E5FF),
                    title = if (nextEkadashi != null) {
                        if (isHindi) "${nextEkadashi.nameHindi} स्मरण" else "Upcoming ${nextEkadashi.nameEnglish}"
                    } else {
                        if (isHindi) "एकादशी व्रत स्मरण" else "Ekadashi Vrat Reminder"
                    },
                    description = if (nextEkadashi != null) {
                        if (isHindi) "${nextEkadashi.dateString} को शुभ व्रत है। स्मरण सक्रिय है।" else "Sacred fast on ${nextEkadashi.dateString}. Fasting guidelines & parana alert active."
                    } else {
                        if (isHindi) "आगामी एकादशी के लिए व्रत स्मरण सक्रिय है।" else "Upcoming Ekadashi fast guidelines & devotional alert active."
                    },
                    timeTag = "Active"
                )

                Spacer(modifier = Modifier.height(6.dp))

                NotificationFeedItem(
                    icon = Icons.Default.SelfImprovement,
                    iconTint = Color(0xFFFF9800),
                    title = if (isHindi) "दैनिक साधना संकल्प" else "Daily Sadhana Goal",
                    description = if (isHindi) "प्रतिदिन 10 माला का लक्ष्य निर्धारित है।" else "Daily target of malas tracked with audio & haptic chime.",
                    timeTag = "Daily"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                ) {
                    Text(
                        text = if (isHindi) "पूर्ण (Done)" else "Done",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFFFF9800)
    val cardBorder = if (isSelected) activeColor else Color(0xFF222B3D)
    val bg = if (isSelected) activeColor.copy(alpha = 0.15f) else Color(0xFF161F2E)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) activeColor else Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else Color(0xFFF1F5F9),
            maxLines = 1
        )
    }
}

@Composable
private fun NotificationFeedItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    timeTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1522))
            .border(1.dp, Color(0xFF222B3D), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF1F5F9)
                )
                Text(
                    text = timeTag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconTint
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 14.sp
            )
        }
    }
}
