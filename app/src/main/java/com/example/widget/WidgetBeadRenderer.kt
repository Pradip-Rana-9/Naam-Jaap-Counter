package com.example.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object WidgetBeadRenderer {

    private val beadCompletedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val beadPendingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#1C283C")
    }

    private val activeGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val sumeruPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#F59E0B")
    }

    private val textNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private val textSubtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#94A3B8")
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }

    private val textActionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C084FC")
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    // Color gradient stops for completed beads (Spiritual orange -> gold -> pink -> purple -> cyan)
    private val gradientColors = intArrayOf(
        Color.parseColor("#F5820A"), // Saffron Orange
        Color.parseColor("#F59E0B"), // Amber Gold
        Color.parseColor("#EAB308"), // Yellow Gold
        Color.parseColor("#EC4899"), // Rose Pink
        Color.parseColor("#A855F7"), // Violet Purple
        Color.parseColor("#06B6D4")  // Cyan
    )

    private fun getBeadColor(index: Int, total: Int): Int {
        if (total <= 1) return gradientColors[0]
        val ratio = index.toFloat() / total.toFloat()
        val scaled = ratio * (gradientColors.size - 1)
        val idx = scaled.toInt().coerceIn(0, gradientColors.size - 2)
        val frac = scaled - idx
        val c1 = gradientColors[idx]
        val c2 = gradientColors[idx + 1]

        val r = (Color.red(c1) + (Color.red(c2) - Color.red(c1)) * frac).toInt()
        val g = (Color.green(c1) + (Color.green(c2) - Color.green(c1)) * frac).toInt()
        val b = (Color.blue(c1) + (Color.blue(c2) - Color.blue(c1)) * frac).toInt()
        return Color.rgb(r, g, b)
    }

    /**
     * Renders a circular Mala containing 108 beads and center count typography.
     * @param currentBead Number of beads completed in current mala (0..108)
     * @param isCompact If true, renders a compact layout without the "Tap to Jaap" footer
     * @param sizePx Pixel dimension (square width/height)
     */
    fun renderBeadCircle(
        currentBead: Int,
        isCompact: Boolean = false,
        sizePx: Int = 260
    ): Bitmap {
        val safeCount = currentBead.coerceIn(0, 108)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val radius = (sizePx / 2f) - (sizePx * 0.085f)

        val totalBeads = 108

        // Draw 108 beads along circular path starting from top (-90 degrees)
        for (i in 1..totalBeads) {
            val angleDeg = -90.0 + (i.toDouble() / totalBeads) * 360.0
            val angleRad = angleDeg * (PI / 180.0)
            val bx = (cx + radius * cos(angleRad)).toFloat()
            val by = (cy + radius * sin(angleRad)).toFloat()

            if (i <= safeCount) {
                // Completed bead
                val color = getBeadColor(i, totalBeads)
                beadCompletedPaint.color = color

                if (i == safeCount && safeCount > 0) {
                    // Active current bead with radiant halo glow
                    activeGlowPaint.color = color
                    activeGlowPaint.alpha = 90
                    canvas.drawCircle(bx, by, sizePx * 0.038f, activeGlowPaint)

                    beadCompletedPaint.color = Color.WHITE
                    canvas.drawCircle(bx, by, sizePx * 0.024f, beadCompletedPaint)
                } else {
                    val beadRadius = sizePx * 0.016f
                    canvas.drawCircle(bx, by, beadRadius, beadCompletedPaint)
                }
            } else {
                // Pending bead
                val beadRadius = sizePx * 0.013f
                canvas.drawCircle(bx, by, beadRadius, beadPendingPaint)
            }
        }

        // Draw Top Sumeru / Guru Bead
        val sumeruAngleRad = -90.0 * (PI / 180.0)
        val sx = (cx + radius * cos(sumeruAngleRad)).toFloat()
        val sy = (cy + radius * sin(sumeruAngleRad)).toFloat()

        activeGlowPaint.color = Color.parseColor("#F59E0B")
        activeGlowPaint.alpha = 80
        canvas.drawCircle(sx, sy, sizePx * 0.042f, activeGlowPaint)
        canvas.drawCircle(sx, sy, sizePx * 0.028f, sumeruPaint)

        // Center Typography
        if (isCompact) {
            // Compact view: "47", "/108"
            textNumberPaint.textSize = sizePx * 0.28f
            val countStr = safeCount.toString()
            val bounds = Rect()
            textNumberPaint.getTextBounds(countStr, 0, countStr.length, bounds)
            val countY = cy + (bounds.height() / 3f) - (sizePx * 0.04f)
            canvas.drawText(countStr, cx, countY, textNumberPaint)

            textSubtitlePaint.textSize = sizePx * 0.12f
            canvas.drawText("/ 108", cx, countY + (sizePx * 0.15f), textSubtitlePaint)
        } else {
            // Standard/Large view: "47", "of 108", "Tap to Jaap"
            textNumberPaint.textSize = sizePx * 0.29f
            val countStr = safeCount.toString()
            val bounds = Rect()
            textNumberPaint.getTextBounds(countStr, 0, countStr.length, bounds)
            val countY = cy - (sizePx * 0.02f)
            canvas.drawText(countStr, cx, countY, textNumberPaint)

            textSubtitlePaint.textSize = sizePx * 0.105f
            canvas.drawText("of 108", cx, countY + (sizePx * 0.13f), textSubtitlePaint)

            textActionPaint.textSize = sizePx * 0.09f
            canvas.drawText("Tap to Jaap", cx, countY + (sizePx * 0.26f), textActionPaint)
        }

        return bitmap
    }
}
