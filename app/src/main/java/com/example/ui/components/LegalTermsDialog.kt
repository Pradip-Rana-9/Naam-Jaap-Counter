package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R

@Composable
fun LegalTermsDialog(
    initialTab: Int = 0, // 0: Privacy Policy, 1: Terms & Conditions
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.5f),
                            Color(0xFF7C3AED).copy(alpha = 0.6f),
                            Color(0xFF3B82F6).copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = Color(0xFF0C0721)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ----------------------------------------------------
                // Header with Logo & Close Button
                // ----------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF190F3B),
                                    Color(0xFF0C0721)
                                )
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Naam Jaap",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Privacy Policy & Terms",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9D8DBE)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF26194F), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFFC7BEDD),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // ----------------------------------------------------
                // Sticky Tab Bar
                // ----------------------------------------------------
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF140D2E),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF00E5FF),
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Privacy Policy",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Color(0xFF00E5FF) else Color(0xFF8B7CB2),
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Terms & Conditions",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Color(0xFF00E5FF) else Color(0xFF8B7CB2),
                                fontSize = 14.sp
                            )
                        }
                    )
                }

                // ----------------------------------------------------
                // Scrollable Content
                // ----------------------------------------------------
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    if (selectedTab == 0) {
                        PrivacyPolicySection()
                    } else {
                        TermsAndConditionsSection()
                    }
                }

                // ----------------------------------------------------
                // Bottom Done Action
                // ----------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF110A29))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "I Understand & Accept",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------
// PRIVACY POLICY CONTENT
// ---------------------------------------------------------------------------------
@Composable
private fun PrivacyPolicySection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Privacy Policy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Naam Jaap • Last updated: August 17, 2026",
            fontSize = 12.sp,
            color = Color(0xFF8B7CB2)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sacred Privacy Promise Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1346))
                .border(1.dp, Color(0xFF7C3AED), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "The Naam Jaap Sacred Privacy Promise",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your devotional practice is sacred, private, and personal. We do not sell, rent, or monetize your chanting records, mantra logs, or personal information under any circumstances.",
                        fontSize = 12.sp,
                        color = Color(0xFFDCD6EF),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        PolicyHeading("1. About Naam Jaap")
        PolicyParagraph("Naam Jaap is a devotional chanting application that helps users track Jaap, Mala progress, daily goals, chanting sessions, Sankalps, reminders, and related devotional preferences.")

        PolicyHeading("2. Information We Store")
        PolicyParagraph("Personal/Profile Information:")
        PolicyBullet("Name")
        PolicyBullet("Email address for registered accounts")
        PolicyBullet("Selected devotional avatar")
        Spacer(modifier = Modifier.height(6.dp))
        PolicyParagraph("Jaap & Activity Data:")
        PolicyBullet("Mala count")
        PolicyBullet("Bead progress")
        PolicyBullet("Daily Jaap progress")
        PolicyBullet("Chanting sessions")
        PolicyBullet("Session duration")
        PolicyBullet("Sankalp/progress information")
        PolicyBullet("Selected/default mantra")
        Spacer(modifier = Modifier.height(6.dp))
        PolicyParagraph("App Preferences:")
        PolicyBullet("Daily Jaap goal")
        PolicyBullet("Theme preference")
        PolicyBullet("Language preference")
        PolicyBullet("Reminder settings")
        PolicyBullet("Audio/haptic preferences")
        Spacer(modifier = Modifier.height(6.dp))
        PolicyParagraph("Guest users:\nThe app can be used as a guest without creating an account. Guest activity is stored locally on the device and may be lost if guest data is reset, the app data is cleared, or the user does not register/login.")

        PolicyHeading("3. How We Use Information")
        PolicyParagraph("The information is used only to:")
        PolicyBullet("Provide Jaap counting and Mala tracking")
        PolicyBullet("Save chanting progress")
        PolicyBullet("Show daily progress and history")
        PolicyBullet("Provide Sankalp and devotional features")
        PolicyBullet("Maintain reminders and app preferences")
        PolicyBullet("Synchronize registered-user data across supported sessions/devices")
        PolicyBullet("Provide account authentication and account management")

        PolicyHeading("4. Firebase Services")
        PolicyParagraph("Registered accounts use Google Firebase services:")
        PolicyBullet("Firebase Authentication for account authentication")
        PolicyBullet("Cloud Firestore for synchronized user/profile and devotional progress data")
        Spacer(modifier = Modifier.height(4.dp))
        PolicyParagraph("Firebase services transmit data securely over encrypted connections.")

        PolicyHeading("5. Data Sharing")
        PolicyParagraph("Naam Jaap does not sell personal information and does not share user information with advertisers or data brokers.\n\nFirebase is used as the application's infrastructure/service provider for authentication and cloud data storage.")

        PolicyHeading("6. Data Security")
        PolicyParagraph("Data transmitted to Firebase services uses secure encrypted connections such as HTTPS/TLS.\n\nPasswords are handled by Firebase Authentication and are not stored as plain text inside the Naam Jaap application.")

        PolicyHeading("7. Data Retention")
        PolicyParagraph("Registered-user cloud data is retained while the account is active.\n\nLocal guest data remains on the device until it is reset, deleted, or the application data is cleared.")

        PolicyHeading("8. Account Deletion")
        PolicyParagraph("Registered users can delete their account from the Account/Profile settings.\n\nWhen an account is deleted, the application attempts to remove:")
        PolicyBullet("Firebase Authentication account")
        PolicyBullet("Cloud Firestore profile data")
        PolicyBullet("Jaap progress")
        PolicyBullet("Chanting sessions")
        PolicyBullet("Sankalps")
        PolicyBullet("Relevant local application data")

        PolicyHeading("9. Logout")
        PolicyParagraph("Logging out clears the local personal session state from the device and returns the application to guest mode. Registered cloud data remains associated with the user's account unless the account itself is deleted.")

        PolicyHeading("10. Notifications, Audio & Haptics")
        PolicyParagraph("The application may use Android notifications for user-enabled reminders and Jaap-related notifications.\n\nAudio and vibration are processed locally on the device and are not transmitted as personal data.")

        PolicyHeading("11. Data Export / Sharing")
        PolicyParagraph("When the user explicitly chooses an export/share feature, the application may create temporary files required for that user-initiated action. No data is automatically shared without the user's action.")

        PolicyHeading("12. Third-Party Services")
        PolicyParagraph("The application uses Google Firebase services for authentication and cloud synchronization for registered users.")

        PolicyHeading("13. Children's Privacy")
        PolicyParagraph("Naam Jaap is not designed to knowingly collect personal information from children without appropriate authorization. If a parent or guardian becomes aware that their child has provided personal information without consent, they may request account deletion through the app or contact support.")

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ---------------------------------------------------------------------------------
// TERMS & CONDITIONS CONTENT
// ---------------------------------------------------------------------------------
@Composable
private fun TermsAndConditionsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Terms & Conditions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Last updated: August 2026",
            fontSize = 12.sp,
            color = Color(0xFF8B7CB2)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Terms Banner Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF151D3B))
                .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Devotional Terms of Service",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "By downloading, accessing, or using Naam Jaap, you agree to abide by these Terms and Conditions designed to foster a sacred and respectful environment.",
                        fontSize = 12.sp,
                        color = Color(0xFFD2E3FC),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        PolicyHeading("1. Devotional & Personal Purpose")
        PolicyParagraph("Naam Jaap is provided free for personal meditation, mantra repetition, and devotional practice. You agree to use the application in accordance with all applicable laws and respect the spiritual nature of the platform.")

        PolicyHeading("2. Account Responsibility")
        PolicyParagraph("If you register an email account, you are responsible for maintaining the confidentiality of your account credentials. You agree to notify us immediately of any unauthorized use of your account.")

        PolicyHeading("3. Mantras & Custom Chants")
        PolicyParagraph("Users can record custom mantras and notes. You agree not to enter abusive, hateful, or unlawful text into custom mantra fields.")

        PolicyHeading("4. Offline Access & Availability")
        PolicyParagraph("Naam Jaap is designed with an offline-first architecture. While we strive for 100% uptime and seamless cloud synchronization, we are not liable for temporary network disruptions or unsynced offline sessions prior to device connection.")

        PolicyHeading("5. Intellectual Property")
        PolicyParagraph("The Naam Jaap logo, visual themes, custom chime sound designs, and original artwork are protected by intellectual property laws. You may not extract, reverse engineer, or redistribute app assets without express permission.")

        PolicyHeading("6. Termination & Deletion")
        PolicyParagraph("We reserve the right to suspend or terminate accounts that violate these terms or engage in malicious activity. Users may terminate their agreement at any time by deleting their account and uninstalling the application.")

        PolicyHeading("7. Updates to Terms")
        PolicyParagraph("We may occasionally update these Terms & Conditions to reflect new features or regulatory requirements. Continued use of the application after changes constitutes acceptance of the modified terms.")

        PolicyHeading("8. Contact & Devotional Support")
        PolicyParagraph("If you have questions regarding these terms, please contact:")
        Text(
            text = "terms@naamjaap.app",
            color = Color(0xFF00E5FF),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ---------------------------------------------------------------------------------
// Helper Composables for Clean Policy Typography
// ---------------------------------------------------------------------------------

@Composable
private fun PolicyHeading(title: String) {
    Spacer(modifier = Modifier.height(14.dp))
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF00E5FF),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun PolicyParagraph(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color(0xFFC7BEDD),
        lineHeight = 19.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun PolicyBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = Color(0xFF7C3AED),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 12.5.sp,
            color = Color(0xFFD5CEEA),
            lineHeight = 18.sp
        )
    }
}
