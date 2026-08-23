package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GitaDataRepository
import java.util.Calendar

@Composable
fun DailyGitaWisdomCard(
    language: String = "HINGLISH",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }

    // Primary deterministic state for Daily Gita Wisdom
    var currentWisdom by remember(dayOfYear) {
        mutableStateOf(GitaDataRepository.getWisdomForDay(dayOfYear))
    }

    var showChapterReader by remember { mutableStateOf(false) }

    // Color Palette matching reference UI exactly
    val outerCardBg = Color(0xFF040914)
    val outerBorder = Color(0xFF0D2232)
    val innerCardBg = Color(0xFF050D18)
    val innerCardBorder = Color(0xFF0D2133)
    val cyanAccent = Color(0xFF00E5FF)
    val orangeAccent = Color(0xFFFF9800)
    val textColorPrimary = Color.White
    val textColorBody = Color(0xFFD1D5DB)
    val textColorMuted = Color(0xFF8B9CB0)

    if (showChapterReader) {
        GitaChapterReaderDialog(
            chapterNumber = currentWisdom.chapterNumber,
            targetShlokNumber = currentWisdom.shlokNumber,
            onDismiss = { showChapterReader = false }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = outerCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, outerBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ================= HEADER SECTION =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Book Badge + Title & Subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Bhagavad Gita Book Icon Badge Circle
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF061B29))
                            .border(1.dp, cyanAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        GitaBookIconGraphic(modifier = Modifier.size(30.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Daily Gita Wisdom",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Small decorative cyan diamond emblem
                            Text(
                                text = "❖",
                                fontSize = 12.sp,
                                color = cyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = currentWisdom.verseRefFull,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cyanAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: Copy Button & New Wisdom Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Square Copy Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF081724))
                            .border(1.dp, cyanAccent.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .clickable {
                                val clipText = "${currentWisdom.verseRefFull}\n\n${currentWisdom.sanskrit}\n\n\"${currentWisdom.hinglishExplanation}\"\n\n- Shrimad Bhagavad Gita"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Gita Wisdom", clipText))
                                Toast.makeText(context, "Shlok copied", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("btn_copy_gita_shlok"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Shlok",
                            tint = cyanAccent,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // New Wisdom Button
                    Box(
                        modifier = Modifier
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF221509))
                            .border(1.dp, orangeAccent.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                            .clickable {
                                currentWisdom = GitaDataRepository.getNextWisdom(currentWisdom)
                            }
                            .padding(horizontal = 12.dp)
                            .testTag("btn_new_gita_wisdom"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "New Wisdom",
                                tint = orangeAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "New Wisdom",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = orangeAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= ANIMATED CONTENT =================
            AnimatedContent(
                targetState = currentWisdom,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "GitaWisdomTransition"
            ) { wisdom ->
                Column {
                    // SECTION 1: SANSKRIT VERSE CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(innerCardBg)
                            .border(1.dp, innerCardBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        // Background Lotus Line Art Watermark
                        Box(
                            modifier = Modifier.matchParentSize(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            WatermarkLotusGraphic(
                                modifier = Modifier
                                    .size(110.dp)
                                    .padding(top = 4.dp, end = 4.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Saffron Opening Quotation Mark
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "“",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = orangeAccent,
                                    lineHeight = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Sanskrit Verse Text
                            Text(
                                text = wisdom.sanskrit,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Decorative Center Ornamental Line & Sun Emblem
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color.Transparent, innerCardBorder, orangeAccent.copy(alpha = 0.5f))
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                // Sun Mandala Rosette Graphic
                                SunMandalaGraphic(modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(orangeAccent.copy(alpha = 0.5f), innerCardBorder, Color.Transparent)
                                            )
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SECTION 2: HINGLISH EXPLANATION BOX
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(innerCardBg)
                            .border(1.dp, innerCardBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Top Row: Om Icon Badge + Vertical Line Accent + Text
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Om Badge Circle
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF071B29))
                                            .border(1.dp, cyanAccent.copy(alpha = 0.45f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "ॐ",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = orangeAccent
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Continuous Cyan Vertical Line Bar
                                    Box(
                                        modifier = Modifier
                                            .width(2.5.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(cyanAccent)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Detailed Hinglish Explanation Text
                                Text(
                                    text = wisdom.hinglishExplanation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = textColorBody,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Bottom Action Bar: Read Full Chapter X | Separator | Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Dynamic Read Full Chapter Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFF071A29))
                                        .border(1.dp, cyanAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                        .clickable { showChapterReader = true }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("btn_read_full_chapter"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = cyanAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Read Full Chapter ${wisdom.chapterNumber}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = cyanAccent
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = cyanAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                // Middle Vertical Separator Line
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(20.dp)
                                        .background(Color(0xFF132A3E))
                                )

                                // Share Section
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Share this wisdom",
                                        fontSize = 13.sp,
                                        color = textColorMuted
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(orangeAccent)
                                            .clickable {
                                                val shareText = "📜 Daily Gita Wisdom\n\n" +
                                                        "${wisdom.verseRefFull}\n\n" +
                                                        "${wisdom.sanskrit}\n\n" +
                                                        "\"${wisdom.hinglishExplanation}\"\n\n" +
                                                        "- Shrimad Bhagavad Gita"

                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(sendIntent, "Share Gita Wisdom")
                                                context.startActivity(shareIntent)
                                            }
                                            .testTag("btn_share_gita_wisdom"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
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
private fun GitaBookIconGraphic(modifier: Modifier = Modifier) {
    val bookGradientLeft = remember { Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF9800))) }
    val bookGradientRight = remember { Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFF57C00))) }
    val bookPathLeft = remember { Path() }
    val bookPathRight = remember { Path() }
    val outlineColor = remember { Color(0xFF00E5FF) }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        bookPathLeft.apply {
            reset()
            moveTo(w * 0.5f, h * 0.75f)
            cubicTo(w * 0.35f, h * 0.65f, w * 0.2f, h * 0.65f, w * 0.1f, h * 0.72f)
            lineTo(w * 0.1f, h * 0.32f)
            cubicTo(w * 0.2f, h * 0.25f, w * 0.35f, h * 0.25f, w * 0.5f, h * 0.35f)
            close()
        }

        bookPathRight.apply {
            reset()
            moveTo(w * 0.5f, h * 0.75f)
            cubicTo(w * 0.65f, h * 0.65f, w * 0.8f, h * 0.65f, w * 0.9f, h * 0.72f)
            lineTo(w * 0.9f, h * 0.32f)
            cubicTo(w * 0.8f, h * 0.25f, w * 0.65f, h * 0.25f, w * 0.5f, h * 0.35f)
            close()
        }

        // Golden Pages Gradient
        drawPath(
            path = bookPathLeft,
            brush = bookGradientLeft
        )
        drawPath(
            path = bookPathRight,
            brush = bookGradientRight
        )

        // Book Cyan Outline
        val stroke = Stroke(width = 1.5.dp.toPx())
        drawPath(
            path = bookPathLeft,
            color = outlineColor,
            style = stroke
        )
        drawPath(
            path = bookPathRight,
            color = outlineColor,
            style = stroke
        )
    }
}

@Composable
private fun SunMandalaGraphic(modifier: Modifier = Modifier) {
    val orange = remember { Color(0xFFFF9800) }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeWidth = 1.5.dp.toPx()

        // Center Sun Circle
        drawCircle(
            color = orange,
            radius = w * 0.22f
        )

        // 8 Radiating Sun Rays
        val rayCount = 8
        for (i in 0 until rayCount) {
            val angle = (i * (360f / rayCount)) * (Math.PI / 180f)
            val cosA = Math.cos(angle).toFloat()
            val sinA = Math.sin(angle).toFloat()
            val startX = cx + (w * 0.28f * cosA)
            val startY = cy + (h * 0.28f * sinA)
            val endX = cx + (w * 0.48f * cosA)
            val endY = cy + (h * 0.48f * sinA)

            drawLine(
                color = orange,
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
private fun WatermarkLotusGraphic(modifier: Modifier = Modifier) {
    val lineTint = remember { Color(0xFF00E5FF).copy(alpha = 0.08f) }
    val centerPetal = remember { Path() }
    val leftPetal = remember { Path() }
    val rightPetal = remember { Path() }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.dp.toPx())

        centerPetal.apply {
            reset()
            moveTo(w * 0.5f, h * 0.1f)
            quadraticTo(w * 0.8f, h * 0.4f, w * 0.5f, h * 0.9f)
            quadraticTo(w * 0.2f, h * 0.4f, w * 0.5f, h * 0.1f)
            close()
        }

        leftPetal.apply {
            reset()
            moveTo(w * 0.5f, h * 0.9f)
            quadraticTo(w * 0.1f, h * 0.5f, w * 0.2f, h * 0.2f)
            quadraticTo(w * 0.5f, h * 0.4f, w * 0.5f, h * 0.9f)
            close()
        }

        rightPetal.apply {
            reset()
            moveTo(w * 0.5f, h * 0.9f)
            quadraticTo(w * 0.9f, h * 0.5f, w * 0.8f, h * 0.2f)
            quadraticTo(w * 0.5f, h * 0.4f, w * 0.5f, h * 0.9f)
            close()
        }

        drawPath(path = centerPetal, color = lineTint, style = stroke)
        drawPath(path = leftPetal, color = lineTint, style = stroke)
        drawPath(path = rightPetal, color = lineTint, style = stroke)
    }
}
