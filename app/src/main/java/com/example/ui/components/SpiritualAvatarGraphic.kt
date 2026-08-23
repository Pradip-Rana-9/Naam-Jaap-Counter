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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class SpiritualAvatar(
    val id: Int,
    val name: String,
    val hindiName: String,
    val symbolText: String,
    val bgGradient: List<Color>,
    val borderColor: Color = Color(0xFFFFD700)
)

val SpiritualAvatarList = listOf(
    SpiritualAvatar(
        id = 1,
        name = "Shri Krishna",
        hindiName = "श्री कृष्ण",
        symbolText = "🪈",
        bgGradient = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    ),
    SpiritualAvatar(
        id = 2,
        name = "Golden Om",
        hindiName = "ॐ ओंकार",
        symbolText = "ॐ",
        bgGradient = listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B))
    ),
    SpiritualAvatar(
        id = 3,
        name = "Lord Shiva",
        hindiName = "महादेव",
        symbolText = "🔱",
        bgGradient = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77))
    ),
    SpiritualAvatar(
        id = 4,
        name = "Lord Hanuman",
        hindiName = "हनुमान जी",
        symbolText = "🚩",
        bgGradient = listOf(Color(0xFF800000), Color(0xFFB22222), Color(0xFFFF4500))
    ),
    SpiritualAvatar(
        id = 5,
        name = "Glowing Lotus",
        hindiName = "दिव्य कमल",
        symbolText = "🪷",
        bgGradient = listOf(Color(0xFF2E0854), Color(0xFF6B1173), Color(0xFFB83B5E))
    ),
    SpiritualAvatar(
        id = 6,
        name = "Little Kanha",
        hindiName = "बाल कान्हा",
        symbolText = "🦚",
        bgGradient = listOf(Color(0xFF003049), Color(0xFFD62828), Color(0xFFF77F00))
    ),
    SpiritualAvatar(
        id = 7,
        name = "Sacred Diya",
        hindiName = "पवित्र दीपक",
        symbolText = "🪔",
        bgGradient = listOf(Color(0xFF311000), Color(0xFF8B4513), Color(0xFFFF8C00))
    ),
    SpiritualAvatar(
        id = 8,
        name = "Banyan Tree",
        hindiName = "कल्पवृक्ष",
        symbolText = "🌳",
        bgGradient = listOf(Color(0xFF0B2B11), Color(0xFF1B432A), Color(0xFF2D6A4F))
    ),
    SpiritualAvatar(
        id = 9,
        name = "Namaste",
        hindiName = "नमस्ते मुद्रा",
        symbolText = "🙏",
        bgGradient = listOf(Color(0xFF2B0000), Color(0xFF5A189A), Color(0xFF7B2CBF))
    ),
    SpiritualAvatar(
        id = 10,
        name = "Devotee",
        hindiName = "साधक भक्त",
        symbolText = "🧘",
        bgGradient = listOf(Color(0xFF1D1E2C), Color(0xFF333552), Color(0xFF535680))
    ),
    SpiritualAvatar(
        id = 11,
        name = "Golden Mandir",
        hindiName = "दिव्य मंदिर",
        symbolText = "🛕",
        bgGradient = listOf(Color(0xFF3D0C02), Color(0xFF8A1C07), Color(0xFFD4A373))
    ),
    SpiritualAvatar(
        id = 12,
        name = "Sacred Shankh",
        hindiName = "पवित्र शंख",
        symbolText = "🐚",
        bgGradient = listOf(Color(0xFF14213D), Color(0xFF005F73), Color(0xFF0A9396))
    ),
    SpiritualAvatar(
        id = 13,
        name = "Mayur Pankh",
        hindiName = "मयूर पंख",
        symbolText = "🦚",
        bgGradient = listOf(Color(0xFF00296B), Color(0xFF00509D), Color(0xFF0077B6))
    ),
    SpiritualAvatar(
        id = 14,
        name = "Himalayas",
        hindiName = "कैलाश पर्वत",
        symbolText = "🏔️",
        bgGradient = listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF374151))
    ),
    SpiritualAvatar(
        id = 15,
        name = "Guru Rishi",
        hindiName = "ऋषि मुनि",
        symbolText = "📜",
        bgGradient = listOf(Color(0xFF2C1A04), Color(0xFF593208), Color(0xFFA66311))
    ),
    SpiritualAvatar(
        id = 16,
        name = "Rudraksha Mala",
        hindiName = "रुद्राक्ष माला",
        symbolText = "📿",
        bgGradient = listOf(Color(0xFF1A0000), Color(0xFF4A0E17), Color(0xFF7A1C28))
    )
)

fun getSpiritualAvatar(id: Int): SpiritualAvatar {
    return SpiritualAvatarList.find { it.id == id } ?: SpiritualAvatarList.first()
}

@Composable
fun SpiritualAvatarGraphic(
    avatarId: Int,
    sizeDp: Dp = 52.dp,
    modifier: Modifier = Modifier,
    showBorder: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val avatar = remember(avatarId) { getSpiritualAvatar(avatarId) }
    val bgBrush = remember(avatar) { Brush.radialGradient(avatar.bgGradient) }
    val goldBorderBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD700),
                Color(0xFFFFA500),
                Color(0xFFFFD700)
            )
        )
    }
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(bgBrush)
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = (sizeDp.value * 0.04f).dp.coerceAtLeast(1.5.dp),
                        brush = goldBorderBrush,
                        shape = CircleShape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatar.symbolText,
            fontSize = (sizeDp.value * 0.48f).sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AvatarSelectionDialog(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF120E24)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Choose Devotional Avatar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "Select your spiritual avatar for Naam Jaap",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(320.dp)
                ) {
                    items(SpiritualAvatarList) { avatar ->
                        val isSelected = avatar.id == selectedAvatarId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onAvatarSelected(avatar.id)
                                }
                                .background(
                                    if (isSelected) Color(0xFFFFD700).copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .padding(4.dp)
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                SpiritualAvatarGraphic(
                                    avatarId = avatar.id,
                                    sizeDp = 52.dp,
                                    showBorder = isSelected
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = avatar.hindiName,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFD700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Done",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileEditDialog(
    currentName: String,
    currentAvatarId: Int,
    onSave: (String, Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var nameInput by remember { mutableStateOf(if (currentName.isBlank()) "Devotee" else currentName) }
    var selectedAvatarId by remember { mutableIntStateOf(currentAvatarId) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF120E24)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Edit Devotee Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "प्रोफ़ाइल एवं अवतार बदलें",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input Section
                Text(
                    text = "Devotee Name / साधक का नाम",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = { Text("Devotee", color = Color.White.copy(alpha = 0.4f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFFFFD700)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Grid Header
                Text(
                    text = "Choose Spiritual Avatar / अवतार चुनें",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(SpiritualAvatarList) { avatar ->
                        val isSelected = avatar.id == selectedAvatarId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedAvatarId = avatar.id }
                                .background(
                                    if (isSelected) Color(0xFFFFD700).copy(alpha = 0.18f)
                                    else Color.Transparent
                                )
                                .padding(4.dp)
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                SpiritualAvatarGraphic(
                                    avatarId = avatar.id,
                                    sizeDp = 48.dp,
                                    showBorder = isSelected
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.Black,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = avatar.hindiName,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Surface(
                    onClick = {
                        val finalName = if (nameInput.trim().isBlank()) "Devotee" else nameInput.trim()
                        onSave(finalName, selectedAvatarId)
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFD700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Save Changes / सहेजें",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

