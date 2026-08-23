package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DevotionalCardExporter {

    fun generateCardBitmap(
        context: Context,
        cardType: String = "SANKALP", // "SANKALP" or "MILESTONE"
        aspectRatio: String = "9:16", // "9:16", "1:1", "16:9"
        templateId: String = "ROYAL_GOLD",
        userName: String = "Devotee",
        mantraName: String = "Radhe Radhe",
        dailyGoal: Int = 10,
        currentStreak: Int = 8,
        longestStreak: Int = 14,
        joinDate: String = "02 Aug 2026",
        totalBeads: Int = 4520,
        totalMalas: Int = 42,
        activeSankalpName: String = "21-Day Radhe Radhe Sankalp",
        sankalpCurrentDay: Int = 8,
        sankalpTotalDays: Int = 21,
        sankalpProgressPercent: Int = 38,
        latestBadgeName: String = "Pratham Bhakta",
        earnedBadgesCount: Int = 5,
        milestoneTitle: String = "10K Beads",
        milestoneDevTitle: String = "Japn Explorer",
        primaryColorInt: Int = Color.parseColor("#FFD700"),
        secondaryColorInt: Int = Color.parseColor("#FF9800")
    ): Bitmap {
        val (requestedWidth, requestedHeight) = when (aspectRatio) {
            "9:16" -> Pair(1080, 1920)
            "16:9" -> Pair(1920, 1080)
            else -> Pair(1080, 1080) // 1:1
        }

        val (bitmap, width, height) = createSafeBitmap(requestedWidth, requestedHeight)
        val canvas = Canvas(bitmap)

        // Colors configuration based on template
        val (bgColors, themeBorderColor, themeTextColor, themeSubTextColor) = when (templateId) {
            "VRINDAVAN" -> Tuple4(
                intArrayOf(Color.parseColor("#00221C"), Color.parseColor("#00382E"), Color.parseColor("#004D40"), Color.parseColor("#001510")),
                Color.parseColor("#80CBC4"), Color.WHITE, Color.parseColor("#E0F2F1")
            )
            "RADHA_LOTUS" -> Tuple4(
                intArrayOf(Color.parseColor("#2A001A"), Color.parseColor("#4A002E"), Color.parseColor("#6A0042"), Color.parseColor("#1A0010")),
                Color.parseColor("#FF80AB"), Color.WHITE, Color.parseColor("#FFD1DC")
            )
            "MIDNIGHT" -> Tuple4(
                intArrayOf(Color.parseColor("#050A1A"), Color.parseColor("#0A1535"), Color.parseColor("#101F4D"), Color.parseColor("#02040B")),
                Color.parseColor("#82B1FF"), Color.WHITE, Color.parseColor("#E3F2FD")
            )
            "TEMPLE_HERITAGE" -> Tuple4(
                intArrayOf(Color.parseColor("#1F0F03"), Color.parseColor("#381C08"), Color.parseColor("#522A0C"), Color.parseColor("#120801")),
                Color.parseColor("#FFFFB74D"), Color.WHITE, Color.parseColor("#FFE0B2")
            )
            "MINIMAL_WHITE" -> Tuple4(
                intArrayOf(Color.parseColor("#FFFDF5"), Color.parseColor("#FFF9E6"), Color.parseColor("#FFF3CC"), Color.parseColor("#FFFBF0")),
                Color.parseColor("#E65100"), Color.parseColor("#212121"), Color.parseColor("#E65100")
            )
            else -> Tuple4( // ROYAL_GOLD
                intArrayOf(Color.parseColor("#0C0414"), Color.parseColor("#1E0A2A"), Color.parseColor("#2E004F"), Color.parseColor("#0F0C20")),
                Color.parseColor("#FFD700"), Color.WHITE, Color.parseColor("#FFE082")
            )
        }

        // Background Gradient
        val bgPaint = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                bgColors,
                floatArrayOf(0f, 0.4f, 0.8f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Outer Decorative Frame
        val framePaint = Paint().apply {
            isAntiAlias = true
            color = themeBorderColor
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        val margin = 35f
        val frameRect = RectF(margin, margin, width - margin, height - margin)
        canvas.drawRoundRect(frameRect, 35f, 35f, framePaint)

        // Inner subtle frame
        val innerFramePaint = Paint(framePaint).apply {
            strokeWidth = 3f
            color = Color.parseColor("#80FFFFFF")
        }
        val innerMargin = 50f
        val innerFrameRect = RectF(innerMargin, innerMargin, width - innerMargin, height - innerMargin)
        canvas.drawRoundRect(innerFrameRect, 25f, 25f, innerFramePaint)

        // Corner Ornamental Flourishes
        val flourishPaint = Paint(framePaint).apply {
            style = Paint.Style.FILL
            color = themeBorderColor
        }
        canvas.drawCircle(innerMargin + 20f, innerMargin + 20f, 10f, flourishPaint)
        canvas.drawCircle(width - innerMargin - 20f, innerMargin + 20f, 10f, flourishPaint)
        canvas.drawCircle(innerMargin + 20f, height - innerMargin - 20f, 10f, flourishPaint)
        canvas.drawCircle(width - innerMargin - 20f, height - innerMargin - 20f, 10f, flourishPaint)

        // Sacred OM Header Circle
        val topY = if (aspectRatio == "16:9") 110f else 140f
        val omCircleRadius = if (aspectRatio == "16:9") 45f else 55f

        val omCirclePaint = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                width / 2f - omCircleRadius, topY - omCircleRadius,
                width / 2f + omCircleRadius, topY + omCircleRadius,
                themeBorderColor, Color.parseColor("#FF9800"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width / 2f, topY, omCircleRadius, omCirclePaint)

        val omTextPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#2A0D08")
            textSize = if (aspectRatio == "16:9") 45f else 55f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        canvas.drawText("ॐ", width / 2f, topY + (omCircleRadius * 0.35f), omTextPaint)

        // Header Title
        val subHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = themeBorderColor
            textSize = if (aspectRatio == "16:9") 22f else 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.12f
        }
        var currentY = topY + omCircleRadius + 35f
        canvas.drawText("❖ DEVOTIONAL ACHIEVEMENT ❖", width / 2f, currentY, subHeaderPaint)

        currentY += 30f
        val quoteSubPaint = Paint().apply {
            isAntiAlias = true
            color = themeSubTextColor
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Every Holy Name brings you closer to Krishna", width / 2f, currentY, quoteSubPaint)

        currentY += 45f

        // USER PROFILE BOX
        val boxWidth = width * 0.90f
        val profileBoxHeight = 140f
        val profileRect = RectF((width - boxWidth) / 2f, currentY, (width + boxWidth) / 2f, currentY + profileBoxHeight)

        val glassPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#33FFFFFF")
            style = Paint.Style.FILL
        }
        val glassBorder = Paint().apply {
            isAntiAlias = true
            color = themeBorderColor
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(profileRect, 25f, 25f, glassPaint)
        canvas.drawRoundRect(profileRect, 25f, 25f, glassBorder)

        // Avatar inside profile box
        val avatarRadius = 45f
        val avatarX = profileRect.left + 70f
        val avatarY = profileRect.centerY()

        val avatarBg = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                avatarX - avatarRadius, avatarY - avatarRadius,
                avatarX + avatarRadius, avatarY + avatarRadius,
                themeBorderColor, Color.parseColor("#FF9800"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(avatarX, avatarY, avatarRadius, avatarBg)

        val avatarTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🪶", avatarX, avatarY + 12f, avatarTextPaint)

        // Profile Name & Rank
        val profileNamePaint = Paint().apply {
            isAntiAlias = true
            color = themeTextColor
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(userName, avatarX + avatarRadius + 25f, avatarY - 5f, profileNamePaint)

        val rankPaint = Paint().apply {
            isAntiAlias = true
            color = themeSubTextColor
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("⭐ $latestBadgeName  •  📅 Since $joinDate", avatarX + avatarRadius + 25f, avatarY + 30f, rankPaint)

        currentY += profileBoxHeight + 25f

        // CURRENT MANTRA BANNER
        val mantraBoxHeight = 70f
        val mantraRect = RectF((width - boxWidth) / 2f, currentY, (width + boxWidth) / 2f, currentY + mantraBoxHeight)
        canvas.drawRoundRect(mantraRect, 20f, 20f, glassPaint)
        canvas.drawRoundRect(mantraRect, 20f, 20f, glassBorder)

        val mantraTextPaint = Paint().apply {
            isAntiAlias = true
            color = themeTextColor
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("📿 CURRENT MANTRA: $mantraName", width / 2f, currentY + 45f, mantraTextPaint)

        currentY += mantraBoxHeight + 25f

        // 2x2 STATISTICS GRID
        val statBoxWidth = (boxWidth - 30f) / 4f
        val statBoxHeight = 110f

        val statLabelPaint = Paint().apply {
            isAntiAlias = true
            color = themeSubTextColor
            textSize = 18f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val statValPaint = Paint().apply {
            isAntiAlias = true
            color = themeTextColor
            textSize = 32f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val statCols = listOf(
            Triple("🎯 GOAL", "$dailyGoal", "Malas"),
            Triple("🔥 STREAK", "$currentStreak", "Days"),
            Triple("🔵 BEADS", "$totalBeads", "Total"),
            Triple("⭐ BADGES", "$earnedBadgesCount", "Earned")
        )

        var statLeft = (width - boxWidth) / 2f
        statCols.forEach { col ->
            val colRect = RectF(statLeft, currentY, statLeft + statBoxWidth, currentY + statBoxHeight)
            canvas.drawRoundRect(colRect, 20f, 20f, glassPaint)
            canvas.drawRoundRect(colRect, 20f, 20f, glassBorder)

            canvas.drawText(col.first, colRect.centerX(), currentY + 30f, statLabelPaint)
            canvas.drawText(col.second, colRect.centerX(), currentY + 68f, statValPaint)
            canvas.drawText(col.third, colRect.centerX(), currentY + 95f, statLabelPaint)

            statLeft += statBoxWidth + 10f
        }

        currentY += statBoxHeight + 25f

        // ACTIVE SANKALP BOX
        val sankalpBoxHeight = 130f
        val sankalpRect = RectF((width - boxWidth) / 2f, currentY, (width + boxWidth) / 2f, currentY + sankalpBoxHeight)
        canvas.drawRoundRect(sankalpRect, 20f, 20f, glassPaint)
        canvas.drawRoundRect(sankalpRect, 20f, 20f, glassBorder)

        val sankalpTitlePaint = Paint().apply {
            isAntiAlias = true
            color = themeBorderColor
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("🚩 ACTIVE SANKALP: $activeSankalpName", sankalpRect.left + 25f, currentY + 40f, sankalpTitlePaint)

        // Progress Bar
        val barRect = RectF(sankalpRect.left + 25f, currentY + 55f, sankalpRect.right - 25f, currentY + 75f)
        val barBgPaint = Paint().apply { color = Color.parseColor("#33FFFFFF") }
        canvas.drawRoundRect(barRect, 10f, 10f, barBgPaint)

        val fillWidth = (barRect.width() * (sankalpProgressPercent / 100f)).coerceIn(10f, barRect.width())
        val fillRect = RectF(barRect.left, barRect.top, barRect.left + fillWidth, barRect.bottom)
        val fillPaint = Paint().apply { color = themeBorderColor }
        canvas.drawRoundRect(fillRect, 10f, 10f, fillPaint)

        val sankalpSubPaint = Paint().apply {
            isAntiAlias = true
            color = themeSubTextColor
            textSize = 20f
        }
        canvas.drawText("Day $sankalpCurrentDay of $sankalpTotalDays ($sankalpProgressPercent%)", sankalpRect.left + 25f, currentY + 108f, sankalpSubPaint)
        val remainingDays = (sankalpTotalDays - sankalpCurrentDay).coerceAtLeast(0)
        val rightTextPaint = Paint(sankalpSubPaint).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText("$remainingDays Days Remaining 🙏", sankalpRect.right - 25f, currentY + 108f, rightTextPaint)

        currentY += sankalpBoxHeight + 25f

        // BHAGAVAD GITA QUOTE BOX
        val gitaBoxHeight = 100f
        val gitaRect = RectF((width - boxWidth) / 2f, currentY, (width + boxWidth) / 2f, currentY + gitaBoxHeight)
        canvas.drawRoundRect(gitaRect, 20f, 20f, glassPaint)
        canvas.drawRoundRect(gitaRect, 20f, 20f, glassBorder)

        val quoteTextPaint = Paint().apply {
            isAntiAlias = true
            color = themeTextColor
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🪔 \"One who controls the mind finds supreme peace.\" 🪷", width / 2f, currentY + 42f, quoteTextPaint)

        val quoteRefPaint = Paint().apply {
            isAntiAlias = true
            color = themeBorderColor
            textSize = 20f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("– Bhagavad Gita 6.26 –", width / 2f, currentY + 78f, quoteRefPaint)

        // FOOTER & TIMESTAMP
        val footerY = height - 60f
        val footerPaint = Paint().apply {
            isAntiAlias = true
            color = themeBorderColor
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("🪷 Jaap Counter", margin + 30f, footerY, footerPaint)

        val dateStr = SimpleDateFormat("dd MMM yyyy | hh:mm a", Locale.getDefault()).format(Date())
        val datePaint = Paint().apply {
            isAntiAlias = true
            color = themeSubTextColor
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("📅 $dateStr", width / 2f, footerY, datePaint)

        val qrPaint = Paint().apply {
            isAntiAlias = true
            color = themeSubTextColor
            textSize = 18f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Scan to view my journey 📱", width - margin - 30f, footerY, qrPaint)

        return bitmap
    }

    private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    fun saveAsPng(context: Context, bitmap: Bitmap, fileNamePrefix: String): Pair<Uri?, String> {
        return saveImageToPublicStorage(context, bitmap, fileNamePrefix, "PNG")
    }

    fun saveAsJpg(context: Context, bitmap: Bitmap, fileNamePrefix: String): Pair<Uri?, String> {
        return saveImageToPublicStorage(context, bitmap, fileNamePrefix, "JPG")
    }

    fun saveAsPdf(context: Context, bitmap: Bitmap, fileNamePrefix: String): Pair<Uri?, String> {
        return savePdfToPublicStorage(context, bitmap, fileNamePrefix)
    }

    private fun saveImageToPublicStorage(
        context: Context,
        bitmap: Bitmap,
        fileNamePrefix: String,
        format: String
    ): Pair<Uri?, String> {
        try {
            val extension = if (format.uppercase() == "JPG" || format.uppercase() == "JPEG") "jpg" else "png"
            val mimeType = if (extension == "jpg") "image/jpeg" else "image/png"
            val compressFormat = if (extension == "jpg") Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG
            val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.$extension"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SankalpDevotional")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { os ->
                        bitmap.compress(compressFormat, 100, os)
                        os.flush()
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)

                    val publicPath = "File Manager ➔ Pictures ➔ SankalpDevotional ➔ $fileName"
                    Toast.makeText(context, "Saved to Pictures/SankalpDevotional!", Toast.LENGTH_SHORT).show()
                    return Pair(uri, publicPath)
                }
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, "SankalpDevotional")
                if (!appDir.exists()) appDir.mkdirs()

                val file = File(appDir, fileName)
                FileOutputStream(file).use { fos ->
                    bitmap.compress(compressFormat, 100, fos)
                    fos.flush()
                }

                // Register with MediaScanner
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(file)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)

                val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val publicPath = "File Manager ➔ Pictures ➔ SankalpDevotional ➔ $fileName"
                Toast.makeText(context, "Saved to Pictures/SankalpDevotional!", Toast.LENGTH_SHORT).show()
                return Pair(fileUri, publicPath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val fallbackUri = saveFallbackInternal(context, bitmap, "$fileNamePrefix.$format")
            Toast.makeText(context, "Saved image card: ${e.localizedMessage ?: "Success"}", Toast.LENGTH_SHORT).show()
            return Pair(fallbackUri, "File Manager ➔ Internal App Storage ➔ $fileNamePrefix")
        }
        return Pair(null, "Error saving file")
    }

    private fun savePdfToPublicStorage(
        context: Context,
        bitmap: Bitmap,
        fileNamePrefix: String
    ): Pair<Uri?, String> {
        try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDoc.finishPage(page)

            val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/SankalpDevotional")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { os ->
                        pdfDoc.writeTo(os)
                        os.flush()
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                    pdfDoc.close()

                    val publicPath = "File Manager ➔ Downloads ➔ SankalpDevotional ➔ $fileName"
                    Toast.makeText(context, "Saved PDF to Downloads/SankalpDevotional!", Toast.LENGTH_SHORT).show()
                    return Pair(uri, publicPath)
                }
                pdfDoc.close()
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val appDir = File(downloadsDir, "SankalpDevotional")
                if (!appDir.exists()) appDir.mkdirs()

                val file = File(appDir, fileName)
                FileOutputStream(file).use { fos ->
                    pdfDoc.writeTo(fos)
                    fos.flush()
                }
                pdfDoc.close()

                val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val publicPath = "File Manager ➔ Downloads ➔ SankalpDevotional ➔ $fileName"
                Toast.makeText(context, "Saved PDF to Downloads/SankalpDevotional!", Toast.LENGTH_SHORT).show()
                return Pair(fileUri, publicPath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return Pair(null, "Error saving PDF")
    }

    private fun saveFallbackInternal(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val file = getExportFile(context, fileName)
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            null
        }
    }

    private fun createSafeBitmap(targetW: Int, targetH: Int): Triple<Bitmap, Int, Int> {
        val candidates = listOf(
            Pair(targetW, targetH),
            Pair((targetW * 0.75f).toInt(), (targetH * 0.75f).toInt()),
            Pair((targetW * 0.5f).toInt(), (targetH * 0.5f).toInt())
        )
        for ((w, h) in candidates) {
            try {
                val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                return Triple(b, w, h)
            } catch (e: OutOfMemoryError) {
                System.gc()
            } catch (e: Exception) {
                // Ignore and try smaller candidate
            }
        }
        val minW = (targetW * 0.25f).toInt().coerceAtLeast(100)
        val minH = (targetH * 0.25f).toInt().coerceAtLeast(100)
        val fallback = Bitmap.createBitmap(minW, minH, Bitmap.Config.RGB_565)
        return Triple(fallback, minW, minH)
    }

    fun shareCard(context: Context, bitmap: Bitmap, title: String) {
        val (uri, _) = saveAsPng(context, bitmap, "Shared_Devotional_Card")
        if (uri == null) {
            Toast.makeText(context, "Unable to save card for sharing", Toast.LENGTH_SHORT).show()
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "Check out my spiritual Japa Sadhana milestone! 🙏 Radhe Radhe")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            val chooser = Intent.createChooser(shareIntent, "Share Devotional Card").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No app available to share image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getExportFile(context: Context, fileName: String): File {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()
        return File(exportDir, fileName)
    }
}
