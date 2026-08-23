package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("privacy_policy_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            PrivacyPolicyScreenContent(onNavigateBack = onNavigateBack)
        }
    }
}

@Composable
fun PrivacyPolicyScreenContent(
    onNavigateBack: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("privacy_policy_content")
    ) {
        // Header with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("btn_privacy_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textColorPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Privacy Policy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Text(
                    text = "Naam Jaap",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = activeColor
                )
            }
        }

        // Sacred Privacy Commitment Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(activeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Devotional Privacy Commitment",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last Updated: August 17, 2026",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColorSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your spiritual practice is sacred. Naam Jaap protects your chanting journey with complete privacy and zero data monetization.",
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. About Naam Jaap
        PolicySectionCard(
            sectionNumber = "1",
            title = "About Naam Jaap",
            icon = Icons.Default.Info,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Naam Jaap is a devotional chanting application that helps users track Jaap, Mala progress, daily goals, chanting sessions, Sankalps, reminders, and related devotional preferences.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Information We Store
        PolicySectionCard(
            sectionNumber = "2",
            title = "Information We Store",
            icon = Icons.Default.Security,
            iconTint = secondaryColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            PolicySubHeading("Personal/Profile Information:", textColorPrimary)
            PolicyBulletItem("Name", textColorSecondary)
            PolicyBulletItem("Email address for registered accounts", textColorSecondary)
            PolicyBulletItem("Selected devotional avatar", textColorSecondary)

            Spacer(modifier = Modifier.height(10.dp))

            PolicySubHeading("Jaap & Activity Data:", textColorPrimary)
            PolicyBulletItem("Mala count", textColorSecondary)
            PolicyBulletItem("Bead progress", textColorSecondary)
            PolicyBulletItem("Daily Jaap progress", textColorSecondary)
            PolicyBulletItem("Chanting sessions", textColorSecondary)
            PolicyBulletItem("Session duration", textColorSecondary)
            PolicyBulletItem("Sankalp/progress information", textColorSecondary)
            PolicyBulletItem("Selected/default mantra", textColorSecondary)

            Spacer(modifier = Modifier.height(10.dp))

            PolicySubHeading("App Preferences:", textColorPrimary)
            PolicyBulletItem("Daily Jaap goal", textColorSecondary)
            PolicyBulletItem("Theme preference", textColorSecondary)
            PolicyBulletItem("Language preference", textColorSecondary)
            PolicyBulletItem("Reminder settings", textColorSecondary)
            PolicyBulletItem("Audio/haptic preferences", textColorSecondary)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(cardBorder.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Column {
                    PolicySubHeading("Guest users:", textColorPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The app can be used as a guest without creating an account. Guest activity is stored locally on the device and may be lost if guest data is reset, the app data is cleared, or the user does not register/login.",
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. How We Use Information
        PolicySectionCard(
            sectionNumber = "3",
            title = "How We Use Information",
            icon = Icons.Default.Shield,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "The information is used only to:",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            PolicyBulletItem("Provide Jaap counting and Mala tracking", textColorSecondary)
            PolicyBulletItem("Save chanting progress", textColorSecondary)
            PolicyBulletItem("Show daily progress and history", textColorSecondary)
            PolicyBulletItem("Provide Sankalp and devotional features", textColorSecondary)
            PolicyBulletItem("Maintain reminders and app preferences", textColorSecondary)
            PolicyBulletItem("Synchronize registered-user data across supported sessions/devices", textColorSecondary)
            PolicyBulletItem("Provide account authentication and account management", textColorSecondary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Firebase Services
        PolicySectionCard(
            sectionNumber = "4",
            title = "Firebase Services",
            icon = Icons.Default.CloudQueue,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Registered accounts use Google Firebase services:",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            PolicyBulletItem("Firebase Authentication for account authentication", textColorSecondary)
            PolicyBulletItem("Cloud Firestore for synchronized user/profile and devotional progress data", textColorSecondary)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Firebase services transmit data securely over encrypted connections.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColorPrimary,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Data Sharing
        PolicySectionCard(
            sectionNumber = "5",
            title = "Data Sharing",
            icon = Icons.Default.Lock,
            iconTint = secondaryColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Naam Jaap does not sell personal information and does not share user information with advertisers or data brokers.\n\nFirebase is used as the application's infrastructure/service provider for authentication and cloud data storage.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Data Security
        PolicySectionCard(
            sectionNumber = "6",
            title = "Data Security",
            icon = Icons.Default.Security,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Data transmitted to Firebase services uses secure encrypted connections such as HTTPS/TLS.\n\nPasswords are handled by Firebase Authentication and are not stored as plain text inside the Naam Jaap application.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7. Data Retention
        PolicySectionCard(
            sectionNumber = "7",
            title = "Data Retention",
            icon = Icons.Default.Info,
            iconTint = secondaryColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Registered-user cloud data is retained while the account is active.\n\nLocal guest data remains on the device until it is reset, deleted, or the application data is cleared.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 8. Account Deletion
        PolicySectionCard(
            sectionNumber = "8",
            title = "Account Deletion",
            icon = Icons.Default.DeleteSweep,
            iconTint = Color(0xFFEF4444),
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Registered users can delete their account from the Account/Profile settings.\n\nWhen an account is deleted, the application attempts to remove:",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            PolicyBulletItem("Firebase Authentication account", textColorSecondary)
            PolicyBulletItem("Cloud Firestore profile data", textColorSecondary)
            PolicyBulletItem("Jaap progress", textColorSecondary)
            PolicyBulletItem("Chanting sessions", textColorSecondary)
            PolicyBulletItem("Sankalps", textColorSecondary)
            PolicyBulletItem("Relevant local application data", textColorSecondary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 9. Logout
        PolicySectionCard(
            sectionNumber = "9",
            title = "Logout",
            icon = Icons.Default.Lock,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Logging out clears the local personal session state from the device and returns the application to guest mode. Registered cloud data remains associated with the user's account unless the account itself is deleted.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 10. Notifications, Audio & Haptics
        PolicySectionCard(
            sectionNumber = "10",
            title = "Notifications, Audio & Haptics",
            icon = Icons.Default.Notifications,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "The application may use Android notifications for user-enabled reminders and Jaap-related notifications.\n\nAudio and vibration are processed locally on the device and are not transmitted as personal data.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 11. Data Export / Sharing
        PolicySectionCard(
            sectionNumber = "11",
            title = "Data Export / Sharing",
            icon = Icons.Default.Security,
            iconTint = secondaryColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "When the user explicitly chooses an export/share feature, the application may create temporary files required for that user-initiated action. No data is automatically shared without the user's action.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 12. Third-Party Services
        PolicySectionCard(
            sectionNumber = "12",
            title = "Third-Party Services",
            icon = Icons.Default.CloudQueue,
            iconTint = activeColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "The application uses Google Firebase services for authentication and cloud synchronization for registered users.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 13. Children's Privacy
        PolicySectionCard(
            sectionNumber = "13",
            title = "Children's Privacy",
            icon = Icons.Default.Shield,
            iconTint = secondaryColor,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary
        ) {
            Text(
                text = "Naam Jaap is not designed to knowingly collect personal information from children without appropriate authorization. If a parent or guardian becomes aware that their child has provided personal information without consent, they may request account deletion through the app or contact support.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer note
        Text(
            text = "Naam Jaap • Spiritual Chanting Companion",
            fontSize = 11.sp,
            color = textColorSecondary.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PolicySectionCard(
    sectionNumber: String,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    cardBg: Color,
    cardBorder: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$sectionNumber. $title",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = cardBorder.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun PolicySubHeading(
    text: String,
    textColorPrimary: Color
) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColorPrimary
    )
}

@Composable
private fun PolicyBulletItem(
    text: String,
    textColorSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = textColorSecondary,
            lineHeight = 18.sp
        )
    }
}
