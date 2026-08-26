package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GitaDataRepository
import com.example.data.GitaShlokItem

@Composable
fun GitaChapterReaderDialog(
    chapterNumber: Int,
    targetShlokNumber: Int? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val chapterEnglish = GitaDataRepository.getChapterNameEnglish(chapterNumber)
    val chapterSanskrit = GitaDataRepository.getChapterNameSanskrit(chapterNumber)
    val shloks = remember(chapterNumber) { GitaDataRepository.getShloksForChapter(chapterNumber) }

    // State: Selected language mode: "Sanskrit", "Hinglish", "Both"
    var selectedLanguageMode by remember { mutableStateOf("Both") }
    var searchQuery by remember { mutableStateOf("") }

    // View state: selected shlok index for Detail View, or null for Chapter List View
    var activeShlokIndex by remember(targetShlokNumber, shloks) {
        val initialIndex = if (targetShlokNumber != null) {
            val found = shloks.indexOfFirst { it.shlokNumber == targetShlokNumber }
            if (found >= 0) found else 0
        } else {
            0
        }
        mutableStateOf<Int?>(if (targetShlokNumber != null) initialIndex else null)
    }

    val darkBg = Color(0xFF040914)
    val cardBg = Color(0xFF050D18)
    val borderCyan = Color(0xFF0D2133)
    val cyanAccent = Color(0xFF00E5FF)
    val orangeAccent = Color(0xFFFF9800)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = darkBg
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, borderCyan, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                if (activeShlokIndex != null && activeShlokIndex!! in shloks.indices) {
                    // ==========================================
                    // SHLOK DETAIL VIEW
                    // ==========================================
                    val currentIndex = activeShlokIndex!!
                    val currentShlok = shloks[currentIndex]
                    val totalCount = shloks.size

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Navigation Header (Removed Bookmark & Font Size symbols as requested)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = { activeShlokIndex = null },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0A1524))
                                        .testTag("btn_gita_reader_back_to_list")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back to Chapter List",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Shlok ${currentShlok.shlokNumber} of $totalCount",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Ch ${currentShlok.chapterNumber} • $chapterEnglish",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Right Close Button ONLY (Bookmark and 'TT' text size symbols removed per user request)
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("btn_gita_reader_close")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Toggle Pills [Sanskrit] [Hinglish] [Both]
                        LanguageToggleSelector(
                            selectedMode = selectedLanguageMode,
                            onSelectMode = { selectedLanguageMode = it }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Scrollable Shlok Content
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                // 1. Main Shlok Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, borderCyan)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Title
                                        Text(
                                            text = "Shlok ${currentShlok.chapterNumber}.${currentShlok.shlokNumber}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = orangeAccent
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Ornamental Line
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .height(1.dp)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color.Transparent, orangeAccent, Color.Transparent)
                                                    )
                                                )
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Sanskrit Text
                                        if (selectedLanguageMode == "Sanskrit" || selectedLanguageMode == "Both") {
                                            Text(
                                                text = currentShlok.sanskrit,
                                                fontSize = 19.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 28.sp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }

                                        // Dynamic Transliteration specific to this shlok
                                        if (currentShlok.transliteration.isNotEmpty() && (selectedLanguageMode == "Hinglish" || selectedLanguageMode == "Both")) {
                                            Text(
                                                text = currentShlok.transliteration,
                                                fontSize = 13.sp,
                                                color = Color(0xFF94A3B8),
                                                textAlign = TextAlign.Center,
                                                lineHeight = 19.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Copy Icon Button on bottom right
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val clipText = "${currentShlok.verseRefFull}\n\n" +
                                                            "${currentShlok.sanskrit}\n\n" +
                                                            (if (currentShlok.transliteration.isNotEmpty()) "${currentShlok.transliteration}\n\n" else "") +
                                                            "Meaning (Hinglish):\n${currentShlok.hinglishMeaning}\n\n" +
                                                            "Explanation:\n${currentShlok.simpleExplanation}"
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Gita Shlok", clipText))
                                                    Toast.makeText(context, "Shlok copied to clipboard", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF0A1826))
                                                    .testTag("btn_gita_shlok_copy")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy Shlok",
                                                    tint = cyanAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. Hinglish Meaning Section (Dynamic and specific to current shlok)
                            if (selectedLanguageMode == "Hinglish" || selectedLanguageMode == "Both") {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = cardBg),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, borderCyan)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(cyanAccent)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Hinglish Meaning",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = cyanAccent
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = currentShlok.hinglishMeaning.ifEmpty { currentShlok.hinglishExplanation },
                                                fontSize = 14.sp,
                                                color = Color(0xFFCBD5E1),
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }

                                // 3. Simple Explanation Section (Bada aur Saral Explanation)
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = cardBg),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, borderCyan)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(orangeAccent)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Simple Explanation (विस्तृत व सरल व्याख्या)",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = orangeAccent
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = currentShlok.simpleExplanation.ifEmpty { currentShlok.hinglishExplanation },
                                                fontSize = 14.sp,
                                                color = Color(0xFFCBD5E1),
                                                lineHeight = 23.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Navigation Bar: [← Previous]  [X / Y]  [Next →]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val canGoPrev = currentIndex > 0
                            val canGoNext = currentIndex < shloks.size - 1

                            // Previous Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (canGoPrev) Color(0xFF131D2E) else Color(0xFF090F1B))
                                    .border(
                                        1.dp,
                                        if (canGoPrev) orangeAccent.copy(alpha = 0.7f) else borderCyan,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable(enabled = canGoPrev) {
                                        if (canGoPrev) {
                                            activeShlokIndex = currentIndex - 1
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .testTag("btn_gita_reader_prev"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous",
                                        tint = if (canGoPrev) orangeAccent else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Previous",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canGoPrev) orangeAccent else Color.Gray
                                    )
                                }
                            }

                            // Shlok Progress Indicator
                            Text(
                                text = "${currentIndex + 1} / $totalCount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Next Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (canGoNext) Color(0xFF131D2E) else Color(0xFF090F1B))
                                    .border(
                                        1.dp,
                                        if (canGoNext) orangeAccent.copy(alpha = 0.7f) else borderCyan,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable(enabled = canGoNext) {
                                        if (canGoNext) {
                                            activeShlokIndex = currentIndex + 1
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .testTag("btn_gita_reader_next"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Next",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canGoNext) orangeAccent else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next",
                                        tint = if (canGoNext) orangeAccent else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // CHAPTER SHLOK LIST VIEW
                    // ==========================================
                    val filteredShloks = remember(shloks, searchQuery) {
                        if (searchQuery.isBlank()) {
                            shloks
                        } else {
                            shloks.filter {
                                it.sanskrit.contains(searchQuery, ignoreCase = true) ||
                                        it.hinglishMeaning.contains(searchQuery, ignoreCase = true) ||
                                        it.simpleExplanation.contains(searchQuery, ignoreCase = true) ||
                                        it.shlokNumber.toString() == searchQuery
                            }
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0A1524))
                                        .testTag("btn_gita_list_back")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Chapter $chapterNumber • $chapterSanskrit",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = chapterEnglish,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = orangeAccent,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0C1929))
                                        .border(1.dp, borderCyan, RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${shloks.size} Shlokas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = cyanAccent
                                    )
                                }

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.testTag("btn_gita_list_close")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Language Toggle Pills [Sanskrit] [Hinglish] [Both]
                        LanguageToggleSelector(
                            selectedMode = selectedLanguageMode,
                            onSelectMode = { selectedLanguageMode = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Search Input Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search shlok...", fontSize = 13.sp, color = Color(0xFF64748B)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_gita_search"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF070E1A),
                                unfocusedContainerColor = Color(0xFF070E1A),
                                focusedBorderColor = cyanAccent,
                                unfocusedBorderColor = borderCyan,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Shlok List
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(
                                items = filteredShloks,
                                key = { _, item -> "${item.chapterNumber}_${item.shlokNumber}" }
                            ) { _, item ->
                                val actualIndex = shloks.indexOf(item)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { activeShlokIndex = actualIndex }
                                        .testTag("card_shlok_item_${item.shlokNumber}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, borderCyan)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Circle Badge Index
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF091B29))
                                                .border(1.dp, cyanAccent.copy(alpha = 0.5f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${item.shlokNumber}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = cyanAccent
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Text preview
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.sanskrit,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.hinglishMeaning.ifEmpty { item.simpleExplanation },
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = orangeAccent,
                                            modifier = Modifier.size(20.dp)
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
}

@Composable
private fun LanguageToggleSelector(
    selectedMode: String,
    onSelectMode: (String) -> Unit
) {
    val modes = listOf("Sanskrit", "Hinglish", "Both")
    val cardBg = Color(0xFF060E1B)
    val borderCyan = Color(0xFF0D2133)
    val orangeAccent = Color(0xFFFF9800)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, borderCyan, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        modes.forEach { mode ->
            val isSelected = selectedMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF221509) else Color.Transparent)
                    .border(
                        1.dp,
                        if (isSelected) orangeAccent else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectMode(mode) }
                    .padding(vertical = 8.dp)
                    .testTag("btn_lang_toggle_$mode"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) orangeAccent else Color(0xFF94A3B8)
                )
            }
        }
    }
}
