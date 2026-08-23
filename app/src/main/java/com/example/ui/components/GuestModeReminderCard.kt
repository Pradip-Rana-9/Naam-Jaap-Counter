package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact, periodic, non-intrusive reminder for Guest Mode users.
 * Displays backup awareness and quick sync/sign-in action without pushing down main dashboard content.
 */
@Composable
fun GuestModeReminderCard(
    modifier: Modifier = Modifier,
    totalBeads: Int = 0,
    totalMalas: Int = 0,
    onOpenAuth: () -> Unit,
    onDismiss: () -> Unit
) {
    val amberWarning = Color(0xFFF59E0B)
    val darkCardBg = Color(0xFF130E26)
    val borderGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFFF59E0B).copy(alpha = 0.7f),
            Color(0xFFD97706).copy(alpha = 0.5f),
            Color(0xFF7C3AED).copy(alpha = 0.4f)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_guest_mode_reminder")
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderGradient, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = darkCardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Warning Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(amberWarning.copy(alpha = 0.15f))
                    .border(1.dp, amberWarning.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Cloud Offline Warning",
                    tint = amberWarning,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Middle: Title + Short Descriptive Subtitle
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenAuth() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Guest Mode",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = amberWarning
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(amberWarning.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                    ) {
                        Text(
                            text = "Not Synced",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(1.5.dp))

                Text(
                    text = if (totalBeads > 0 || totalMalas > 0) {
                        "$totalBeads beads on device • Tap to sync"
                    } else {
                        "Sign in to backup & preserve your sadhana"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Actions: Compact "Sync" CTA + Dismiss Icon Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onOpenAuth,
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("btn_guest_reminder_auth"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sync",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_guest_reminder_dismiss")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Reminder (Remind Later)",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
