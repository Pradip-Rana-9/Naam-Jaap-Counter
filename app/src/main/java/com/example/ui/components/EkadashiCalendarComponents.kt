package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminder.ReminderManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DevotionalEvent(
    val id: Int,
    val nameHindi: String,
    val nameEnglish: String,
    val dateString: String,
    val dayOfWeek: String,
    val pakshaHindi: String,
    val pakshaEnglish: String,
    val tithiHindi: String,
    val tithiEnglish: String,
    val category: String, // EKADASHI, VRAT, FESTIVAL
    val shortDescHindi: String,
    val shortDescEnglish: String,
    val fullDescHindi: String,
    val fullDescEnglish: String,
    val eatHindi: String,
    val eatEnglish: String,
    val notEatHindi: String,
    val notEatEnglish: String,
    val gitaReference: String,
    val mantraHindi: String,
    val mantraEnglish: String,
    val paranaTimeHindi: String,
    val paranaTimeEnglish: String,
    val startTime: String,
    val endTime: String,
    val targetTimestamp: Long
) {
    fun getName(lang: String): String = if (lang == "HINDI") nameHindi else nameEnglish
    fun getPaksha(lang: String): String = if (lang == "HINDI") pakshaHindi else pakshaEnglish
    fun getTithi(lang: String): String = if (lang == "HINDI") tithiHindi else tithiEnglish
    fun getShortDesc(lang: String): String = if (lang == "HINDI") shortDescHindi else shortDescEnglish
    fun getFullDesc(lang: String): String = if (lang == "HINDI") fullDescHindi else fullDescEnglish
    fun getEat(lang: String): String = if (lang == "HINDI") eatHindi else eatEnglish
    fun getNotEat(lang: String): String = if (lang == "HINDI") notEatHindi else notEatEnglish
    fun getMantra(lang: String): String = if (lang == "HINDI") mantraHindi else mantraEnglish
    fun getParanaTime(lang: String): String = if (lang == "HINDI") paranaTimeHindi else paranaTimeEnglish
}

fun getDaysRemaining(
    targetTimestamp: Long,
    timeZone: java.util.TimeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata"),
    currentEpochMs: Long = System.currentTimeMillis()
): Int {
    if (targetTimestamp <= currentEpochMs) return 0
    val tzOffsetNow = timeZone.getOffset(currentEpochMs)
    val tzOffsetTarget = timeZone.getOffset(targetTimestamp)
    val localDayNow = (currentEpochMs + tzOffsetNow) / 86400000L
    val localDayTarget = (targetTimestamp + tzOffsetTarget) / 86400000L
    return (localDayTarget - localDayNow).toInt().coerceAtLeast(0)
}

/**
 * Compact Dashboard Upcoming Events Section for Home Screen.
 * Displays only the next 2-3 upcoming devotional calendar events dynamically:
 * 1. The immediate next upcoming event (Featured Hero Card).
 * 2. The following 1-2 upcoming events (Compact Festival Cards).
 * Includes "View All" actions to open the full calendar.
 */
@Composable
fun HomeUpcomingEventsSection(
    allEvents: List<DevotionalEvent>,
    language: String = "ENGLISH",
    onViewAll: () -> Unit,
    onSelectMantraForJaap: (String) -> Unit,
    onSetDedicatedGoal: (Int) -> Unit
) {
    var selectedEventForDetails by remember { mutableStateOf<DevotionalEvent?>(null) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val goldAccent = Color(0xFFFF9E00)

    val sortedEvents = remember(allEvents) { allEvents.sortedBy { it.targetTimestamp } }
    val featuredEvent = remember(sortedEvents) { sortedEvents.firstOrNull() }
    val nextUpcomingEvents = remember(sortedEvents) { sortedEvents.drop(1).take(3) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header with View All action
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
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Upcoming Calendar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (language == "HINDI") "आगामी व्रत एवं पर्व" else "Upcoming Events",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Text(
                        text = if (language == "HINDI") "शुभ उपवास दिन एवं वैष्णव पंचांग" else "Auspicious fasting days & panchang",
                        fontSize = 11.sp,
                        color = textColorSecondary
                    )
                }
            }

            TextButton(
                onClick = onViewAll,
                modifier = Modifier.testTag("btn_view_all_calendar")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == "HINDI") "सभी देखें" else "View all",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldAccent
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "View all",
                        tint = goldAccent,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 1. Featured Next Upcoming Vrat Hero Card (Compact with 1 primary action)
        featuredEvent?.let { event ->
            FeaturedEventHeroCard(
                event = event,
                language = language,
                onOpenDetails = { selectedEventForDetails = event },
                onChantMantra = { mantra -> onSelectMantraForJaap(mantra) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 2. Next 2-3 Upcoming Events (Horizontal Carousel)
        if (nextUpcomingEvents.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                nextUpcomingEvents.forEach { event ->
                    CompactUpcomingEventCard(
                        event = event,
                        language = language,
                        onClick = { selectedEventForDetails = event }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 3. Compact "View All Calendar" Action Button
        OutlinedButton(
            onClick = onViewAll,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_open_full_calendar"),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.45f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = activeColor.copy(alpha = 0.06f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = activeColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == "HINDI") "सम्पूर्ण एकादशी व व्रत पंचांग देखें (${allEvents.size}+)" else "View All Vrat & Ekadashi Calendar (${allEvents.size} Events)",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = activeColor,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }

    // Details Bottom Sheet for tapped events
    selectedEventForDetails?.let { event ->
        FestivalDetailsBottomSheet(
            event = event,
            language = language,
            onDismiss = { selectedEventForDetails = null },
            onChantMantra = { mantra ->
                onSelectMantraForJaap(mantra)
                selectedEventForDetails = null
            },
            onSetDedicatedGoal = { goal ->
                onSetDedicatedGoal(goal)
                selectedEventForDetails = null
            }
        )
    }
}

/**
 * Full-screen modal bottom sheet for displaying the complete Ekadashi and Vrat Calendar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullEkadashiCalendarBottomSheet(
    allEvents: List<DevotionalEvent>,
    language: String = "ENGLISH",
    onDismiss: () -> Unit,
    onSelectMantraForJaap: (String) -> Unit,
    onSetDedicatedGoal: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cardBg = Color(0xFF0F1522)
    val textColorPrimary = Color(0xFFF1F5F9)
    val textColorSecondary = Color(0xFF94A3B8)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = cardBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(16.dp)
        ) {
            // Header Bar with Close Action
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Full Calendar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == "HINDI") "सम्पूर्ण व्रत व एकादशी पंचांग" else "Full Ekadashi & Vrat Calendar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Text(
                            text = if (language == "HINDI") "वैष्णव कैलेंडर • ${allEvents.size} पर्व एवं उपवास" else "Vaishnava Panchang • ${allEvents.size} Sacred Events",
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_full_calendar")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textColorSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Full Calendar Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                EkadashiCalendarSection(
                    allEvents = allEvents,
                    language = language,
                    onSelectMantraForJaap = { mantra ->
                        onSelectMantraForJaap(mantra)
                        onDismiss()
                    },
                    onSetDedicatedGoal = { goal ->
                        onSetDedicatedGoal(goal)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun EkadashiCalendarSection(
    allEvents: List<DevotionalEvent>,
    language: String = "ENGLISH",
    onSelectMantraForJaap: (String) -> Unit,
    onSetDedicatedGoal: (Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedEventForDetails by remember { mutableStateOf<DevotionalEvent?>(null) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val sortedEvents = remember(allEvents) { allEvents.sortedBy { it.targetTimestamp } }
    val featuredEvent = remember(sortedEvents) { sortedEvents.firstOrNull() }

    val filteredEvents = remember(sortedEvents, selectedCategory) {
        when (selectedCategory) {
            "EKADASHI" -> sortedEvents.filter { it.category == "EKADASHI" }
            "VRAT" -> sortedEvents.filter { it.category == "VRAT" }
            "FESTIVAL" -> sortedEvents.filter { it.category == "FESTIVAL" }
            else -> sortedEvents
        }
    }

    val categories = remember(language) {
        if (language == "HINDI") {
            listOf(
                "ALL" to "✨ सभी कार्यक्रम",
                "EKADASHI" to "🌸 एकादशी व्रत",
                "VRAT" to "🌕 पूर्णिमा व व्रत",
                "FESTIVAL" to "🪔 त्योहार"
            )
        } else {
            listOf(
                "ALL" to "✨ All Events",
                "EKADASHI" to "🌸 Ekadashi Vrat",
                "VRAT" to "🌕 Purnima & Fasting",
                "FESTIVAL" to "🪔 Festivals"
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "HINDI") "एकादशी एवं व्रत कैलेंडर" else "Ekadashi & Vrat Calendar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Text(
                        text = if (language == "HINDI") "शुभ उपवास दिन एवं वैष्णव पंचांग" else "Auspicious fasting days & Vaishnava panchang",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FEATURED UPCOMING EVENT HERO CARD
        featuredEvent?.let { event ->
            FeaturedEventHeroCard(
                event = event,
                language = language,
                onOpenDetails = { selectedEventForDetails = event },
                onChantMantra = { mantra -> onSelectMantraForJaap(mantra) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // CATEGORY FILTER CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (catKey, catLabel) ->
                val isSel = selectedCategory == catKey
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSel) {
                                Brush.horizontalGradient(
                                    colors = listOf(activeColor, secondaryColor)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(cardBg, cardBg)
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isSel) activeColor else cardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategory = catKey }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = catLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) Color.White else textColorPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CARDS LIST
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filteredEvents.forEach { event ->
                FestivalCardItem(
                    event = event,
                    language = language,
                    onOpenDetails = { selectedEventForDetails = event },
                    onChantMantra = { mantra -> onSelectMantraForJaap(mantra) }
                )
            }
        }
    }

    // Details Bottom Sheet
    selectedEventForDetails?.let { event ->
        FestivalDetailsBottomSheet(
            event = event,
            language = language,
            onDismiss = { selectedEventForDetails = null },
            onChantMantra = { mantra ->
                onSelectMantraForJaap(mantra)
                selectedEventForDetails = null
            },
            onSetDedicatedGoal = { goal ->
                onSetDedicatedGoal(goal)
                selectedEventForDetails = null
            }
        )
    }
}

@Composable
fun FeaturedEventHeroCard(
    event: DevotionalEvent,
    language: String,
    onOpenDetails: () -> Unit,
    onChantMantra: (String) -> Unit
) {
    val diffDays = remember(event.targetTimestamp) { getDaysRemaining(event.targetTimestamp) }
    val daysLabel = remember(diffDays, language) {
        when (diffDays) {
            0 -> if (language == "HINDI") "आज!" else "TODAY!"
            1 -> if (language == "HINDI") "कल!" else "TOMORROW!"
            else -> if (language == "HINDI") "$diffDays दिन में" else "IN $diffDays DAYS"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF200B3B),
                            Color(0xFF0F1532)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    1.2.dp,
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFD54F), Color(0xFF00E5FF))
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33FFD54F))
                            .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "HINDI") "अगला व्रत" else "NEXT UPCOMING VRAT",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD54F),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when (diffDays) {
                                    0 -> Color(0xFFFF5252)
                                    1 -> Color(0xFFFF9800)
                                    else -> Color(0xFF00E5FF)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = daysLabel,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.getName(language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${event.dateString} (${event.dayOfWeek}) • ${event.getPaksha(language)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB3C5FF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Compact Parana Time Window row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1AFFFFFF))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "HINDI") "पारणा समय:" else "Parana Time:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = event.getParanaTime(language),
                                fontSize = 11.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // One Primary Action Button (Compact)
                Button(
                    onClick = onOpenDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD54F)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (language == "HINDI") "व्रत नियम एवं विवरण देखें →" else "Fasting Guide & Details →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactUpcomingEventCard(
    event: DevotionalEvent,
    language: String,
    onClick: () -> Unit
) {
    val diffDays = remember(event.targetTimestamp) { getDaysRemaining(event.targetTimestamp) }
    val daysLabel = remember(diffDays, language) {
        when (diffDays) {
            0 -> if (language == "HINDI") "आज!" else "Today!"
            1 -> if (language == "HINDI") "कल!" else "Tmrw!"
            else -> if (language == "HINDI") "$diffDays दिन" else "$diffDays days"
        }
    }

    val (badgeBg, badgeTint) = remember(event.category) {
        when (event.category) {
            "EKADASHI" -> Pair(Color(0xFFFFB74D), Color(0xFF6D4C00))
            "VRAT" -> Pair(Color(0xFF80DEEA), Color(0xFF004D40))
            else -> Pair(Color(0xFFE1BEE7), Color(0xFF4A148C))
        }
    }

    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (event.category == "EKADASHI") "एकादशी" else event.category.take(7),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTint
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFF9800).copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = daysLabel,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF9800)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.getName(language),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${event.dateString} (${event.dayOfWeek.take(3)})",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.getParanaTime(language).take(15),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FestivalCardItem(
    event: DevotionalEvent,
    language: String,
    onOpenDetails: () -> Unit,
    onChantMantra: (String) -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val diffDays = remember(event.targetTimestamp) { getDaysRemaining(event.targetTimestamp) }
    val daysLabel = remember(diffDays, language) {
        when (diffDays) {
            0 -> if (language == "HINDI") "आज!" else "Today!"
            1 -> if (language == "HINDI") "कल!" else "Tomorrow!"
            else -> if (language == "HINDI") "$diffDays दिन में" else "In $diffDays Days"
        }
    }

    val (badgeBg, badgeTint) = remember(event.category) {
        when (event.category) {
            "EKADASHI" -> Pair(Color(0xFFFFF3E0), Color(0xFFFF9800))
            "VRAT" -> Pair(Color(0xFFE0F7FA), Color(0xFF00ACC1))
            else -> Pair(Color(0xFFF3E5F5), Color(0xFFAB47BC))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (diffDays <= 2) activeColor else cardBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (event.category) {
                                "EKADASHI" -> Icons.Default.SelfImprovement
                                "VRAT" -> Icons.Default.LightMode
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = badgeTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = event.getName(language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${event.dateString} (${event.dayOfWeek})",
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (diffDays) {
                                0 -> Color(0xFFFF5252)
                                1 -> Color(0xFFFF9800)
                                else -> cardBorder
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = daysLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (diffDays <= 1) Color.White else textColorPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.getShortDesc(language),
                fontSize = 12.sp,
                color = textColorSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = secondaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == "HINDI") "पारणा: ${event.getParanaTime(language).take(15)}..." else "Parana: ${event.getParanaTime(language).take(15)}...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryColor
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { onChantMantra(event.getMantra(language)) },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (language == "HINDI") "📿 जाप" else "📿 Chant",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenDetails,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (language == "HINDI") "नियम →" else "Rules →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalDetailsBottomSheet(
    event: DevotionalEvent,
    language: String,
    onDismiss: () -> Unit,
    onChantMantra: (String) -> Unit,
    onSetDedicatedGoal: (Int) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showReminderDialog by remember { mutableStateOf(false) }
    var reminderStatusMsg by remember { mutableStateOf<String?>(null) }

    val activeColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outline
    val textColorPrimary = MaterialTheme.colorScheme.onSurface
    val textColorSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = cardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.getName(language),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Text(
                        text = "${event.dateString} (${event.dayOfWeek}) • ${event.getTithi(language)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryColor
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textColorSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showReminderDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "HINDI") "रिमाइंडर सेट करें" else "Set Reminder",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { onSetDedicatedGoal(16) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "HINDI") "16 माला लक्ष्य" else "Set 16 Malas Goal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            reminderStatusMsg?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✅ $msg",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = activeColor.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, activeColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "HINDI") "आज के लिए अनुशंसित सिद्ध मंत्र:" else "Recommended Siddha Mantra for Today:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = event.getMantra(language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onChantMantra(event.getMantra(language)) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                    ) {
                        Text(
                            text = if (language == "HINDI") "अभी जाप करें →" else "Chant This Mantra Now →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (language == "HINDI") "🍱 व्रत नियम एवं प्रसाद निर्देश" else "🍱 Fasting Rules & Prasadam Guide",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == "HINDI") "✅ अनुमत आहार (फलाहार व्रत):" else "✅ Allowed Food (Phalahar Fast):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.getEat(language),
                        fontSize = 12.sp,
                        color = textColorPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == "HINDI") "❌ वर्जित आहार (पूर्णतः निषेध):" else "❌ Forbidden Food (Strictly Prohibited):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.getNotEat(language),
                        fontSize = 12.sp,
                        color = textColorPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == "HINDI") "⌛ पारणा व्रत खोलने का समय:" else "⌛ Parana Break-Fast Window:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = secondaryColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.getParanaTime(language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColorPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (language == "HINDI") "📖 आध्यात्मिक महत्ता" else "📖 Spiritual Significance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = event.getFullDesc(language),
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = secondaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "HINDI") "📜 श्रीमद्भगवद्गीता सन्दर्भ" else "📜 Bhagavad Gita Reference",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.gitaReference,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColorPrimary
                    )
                }
            }
        }
    }

    if (showReminderDialog) {
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = {
                Text(
                    text = if (language == "HINDI") "व्रत रिमाइंडर सेट करें" else "Set Vrat Fasting Reminder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == "HINDI")
                        "${event.getName(language)} (${event.dateString}) के लिए अलार्म एवं रिमाइंडर नोटिफिकेशन सेट करें।"
                    else
                        "Schedule alarm & notification reminders for ${event.getName(language)} (${event.dateString}).",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        ReminderManager.scheduleVratReminder(
                            context = context,
                            eventId = event.id,
                            eventName = event.getName(language),
                            targetTimestamp = event.targetTimestamp
                        )
                        reminderStatusMsg = if (language == "HINDI") "रिमाइंडर सफलतापूर्वक शेड्यूल किया गया!" else "Reminder scheduled successfully!"
                        showReminderDialog = false
                    }
                ) {
                    Text(if (language == "HINDI") "हाँ, रिमाइंडर लगाएं" else "Set Reminder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReminderDialog = false }) {
                    Text(if (language == "HINDI") "रद्द करें" else "Cancel")
                }
            }
        )
    }
}
