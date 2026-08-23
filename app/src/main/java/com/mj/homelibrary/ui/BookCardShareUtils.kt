package com.mj.homelibrary.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.mj.homelibrary.R
import com.mj.homelibrary.data.entity.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

object BookCardShareUtils {

    suspend fun shareBookCard(
        context: Context,
        book: BookEntity,
        quote: String? = null,
    ) = withContext(Dispatchers.IO) {
        val width = 1080
        val height = 1440

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Rich Aesthetic Gradient Background
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(Color.parseColor("#1A1C24"), Color.parseColor("#12131A"), Color.parseColor("#232533")),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle accent glow circle in background
        val glowPaint = Paint().apply {
            color = Color.parseColor("#3B4371")
            alpha = 40
            maskFilter = android.graphics.BlurMaskFilter(180f, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawCircle(width * 0.8f, height * 0.2f, 300f, glowPaint)

        // Outer border frame
        val borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.parseColor("#383B4D")
        }
        canvas.drawRoundRect(RectF(40f, 40f, width - 40f, height - 40f), 32f, 32f, borderPaint)

        // 2. Header Seal
        val headerPaint = TextPaint().apply {
            color = Color.parseColor("#D4AF37") // Gold / Brass
            textSize = 28f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.2f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("GRANTHAPURA  •  HOME LIBRARY", width / 2f, 120f, headerPaint)

        // Divider rule
        val rulePaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            strokeWidth = 2f
            alpha = 120
        }
        canvas.drawLine(width / 2f - 120f, 145f, width / 2f + 120f, 145f, rulePaint)

        // 3. Book Cover Artwork
        val coverWidth = 320f
        val coverHeight = 480f
        val coverLeft = (width - coverWidth) / 2f
        val coverTop = 200f
        val coverRect = RectF(coverLeft, coverTop, coverLeft + coverWidth, coverTop + coverHeight)

        var coverBitmap: Bitmap? = null
        if (!book.coverImagePath.isNullOrBlank()) {
            val file = File(book.coverImagePath)
            if (file.exists()) {
                coverBitmap = BitmapFactory.decodeFile(file.absolutePath)
            }
        }

        // Draw shadow under cover
        val shadowPaint = Paint().apply {
            color = Color.BLACK
            alpha = 150
            maskFilter = android.graphics.BlurMaskFilter(24f, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(coverRect, 20f, 20f, shadowPaint)

        if (coverBitmap != null) {
            val scaledCover = Bitmap.createScaledBitmap(coverBitmap, coverWidth.roundToInt(), coverHeight.roundToInt(), true)
            val clipPath = Path().apply {
                addRoundRect(coverRect, 18f, 18f, Path.Direction.CW)
            }
            canvas.save()
            canvas.clipPath(clipPath)
            canvas.drawBitmap(scaledCover, coverLeft, coverTop, null)
            canvas.restore()
        } else {
            // Elegant placeholder spine/cover
            val placeholderBg = Paint().apply { color = Color.parseColor("#282C3F") }
            canvas.drawRoundRect(coverRect, 18f, 18f, placeholderBg)
            val placeholderText = TextPaint().apply {
                color = Color.WHITE
                textSize = 34f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(book.title.take(24), width / 2f, coverTop + coverHeight / 2f, placeholderText)
        }

        // 4. Book Title & Author
        var currentY = coverTop + coverHeight + 70f
        val titlePaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val titleLayout = StaticLayout.Builder.obtain(book.title, 0, book.title.length, titlePaint, width - 160)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setMaxLines(2)
            .build()

        canvas.save()
        canvas.translate(width / 2f, currentY)
        titleLayout.draw(canvas)
        canvas.restore()

        currentY += titleLayout.height + 20f

        val authorPaint = TextPaint().apply {
            color = Color.parseColor("#B0B3C7")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val authorText = if (book.authors.isNotEmpty()) book.authors.joinToString(", ") else "Unknown Author"
        canvas.drawText("by $authorText", width / 2f, currentY + 30f, authorPaint)
        currentY += 60f

        // 5. Star Rating
        val rating = book.rating ?: 0f
        if (rating > 0f) {
            val starPaint = TextPaint().apply {
                color = Color.parseColor("#FFC107")
                textSize = 36f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val stars = buildString {
                val fullStars = rating.toInt()
                for (i in 1..5) {
                    if (i <= fullStars) append("★ ") else append("☆ ")
                }
            }.trim()
            canvas.drawText(stars, width / 2f, currentY + 20f, starPaint)
            currentY += 50f
        }

        // 6. Featured Quote / Excerpt
        val quoteText = quote ?: book.notes?.takeIf { it.isNotBlank() }
        if (!quoteText.isNullOrBlank()) {
            val quoteBg = Paint().apply {
                color = Color.parseColor("#1E202B")
                style = Paint.Style.FILL
            }
            val quoteRect = RectF(100f, currentY, width - 100f, height - 120f)
            canvas.drawRoundRect(quoteRect, 20f, 20f, quoteBg)

            val quotePaint = TextPaint().apply {
                color = Color.parseColor("#E1E2EC")
                textSize = 30f
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                isAntiAlias = true
            }
            val formattedQuote = "“${quoteText.trim().take(220)}${if (quoteText.length > 220) "…" else ""}”"
            val quoteLayout = StaticLayout.Builder.obtain(formattedQuote, 0, formattedQuote.length, quotePaint, (quoteRect.width() - 60f).roundToInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setMaxLines(4)
                .build()

            canvas.save()
            canvas.translate(quoteRect.left + 30f, quoteRect.top + 24f)
            quoteLayout.draw(canvas)
            canvas.restore()
        }

        // 7. Footer Seal
        val footerPaint = TextPaint().apply {
            color = Color.parseColor("#6C6F82")
            textSize = 22f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            letterSpacing = 0.15f
        }
        canvas.drawText("CATALOGED WITH GRANTHAPURA", width / 2f, height - 60f, footerPaint)

        // 8. Save & Share Intent
        val shareDir = File(context.cacheDir, "share").apply { if (!exists()) mkdirs() }
        val shareFile = File(shareDir, "book_card_${book.id}_${System.currentTimeMillis()}.png")
        FileOutputStream(shareFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile,
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, "📚 \"${book.title}\" by ${book.authors.joinToString(", ")} (from my library in Granthapura)")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        withContext(Dispatchers.Main) {
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_book_card_title)))
        }
    }
}
