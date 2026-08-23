package com.example.ui.components

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun JaapLogoGraphic(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 160.dp,
    showCircularBadge: Boolean = false,
    animatedGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val cyanTop = Color(0xFF00E5FF)
    val blueMiddle = Color(0xFF2979FF)
    val purpleBottom = Color(0xFF7C4DFF)

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing aura background
        if (animatedGlow) {
            Box(
                modifier = Modifier
                    .size(sizeDp * 1.15f * pulseScale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                cyanTop.copy(alpha = 0.45f),
                                blueMiddle.copy(alpha = 0.30f),
                                purpleBottom.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        if (showCircularBadge) {
            Box(
                modifier = Modifier
                    .size(sizeDp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF140D2B),
                                Color(0xFF0B061A)
                            )
                        )
                    )
            )
        }

        // App Logo Image
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "Naam Jaap Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(sizeDp)
                .then(
                    if (showCircularBadge) Modifier.clip(CircleShape) else Modifier
                )
        )
    }
}
