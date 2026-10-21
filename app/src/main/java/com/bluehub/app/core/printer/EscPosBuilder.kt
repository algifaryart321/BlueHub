package com.bluehub.app.core.printer

import android.graphics.Bitmap
import android.graphics.Color
import java.io.ByteArrayOutputStream

class EscPosBuilder {
    private val buffer = ByteArrayOutputStream()

    enum class Alignment {
        LEFT, CENTER, RIGHT
    }

    enum class PaperWidth(val charsPerLine: Int, val dotsWidth: Int) {
        MM58(32, 384),
        MM80(48, 576)
    }

    init {
        reset()
    }

    fun reset(): EscPosBuilder {
        buffer.reset()
        // ESC @: Initialize printer
        buffer.write(byteArrayOf(0x1B, 0x40))
        return this
    }

    fun setAlignment(alignment: Alignment): EscPosBuilder {
        val alignByte = when (alignment) {
            Alignment.LEFT -> 0x00.toByte()
            Alignment.CENTER -> 0x01.toByte()
            Alignment.RIGHT -> 0x02.toByte()
        }
        buffer.write(byteArrayOf(0x1B, 0x61, alignByte))
        return this
    }

    fun setBold(enabled: Boolean): EscPosBuilder {
        val boldByte = if (enabled) 0x01.toByte() else 0x00.toByte()
        buffer.write(byteArrayOf(0x1B, 0x45, boldByte))
        return this
    }

    fun setTextSize(doubleWidth: Boolean, doubleHeight: Boolean): EscPosBuilder {
        var sizeByte = 0
        if (doubleWidth) sizeByte = sizeByte or 0x20
        if (doubleHeight) sizeByte = sizeByte or 0x01
        buffer.write(byteArrayOf(0x1D, 0x21, sizeByte.toByte()))
        return this
    }

    fun text(text: String): EscPosBuilder {
        buffer.write(text.toByteArray(Charsets.ISO_8859_1))
        return this
    }

    fun textLine(line: String): EscPosBuilder {
        text(line)
        lineFeed()
        return this
    }

    fun lineFeed(lines: Int = 1): EscPosBuilder {
        repeat(lines) {
            buffer.write(byteArrayOf(0x0A))
        }
        return this
    }

    fun printDivider(char: Char = '-', width: PaperWidth = PaperWidth.MM58): EscPosBuilder {
        textLine(char.toString().repeat(width.charsPerLine))
        return this
    }

    fun printKeyValue(key: String, value: String, width: PaperWidth = PaperWidth.MM58): EscPosBuilder {
        val totalLen = width.charsPerLine
        val spaceLen = (totalLen - key.length - value.length).coerceAtLeast(1)
        val line = key + " ".repeat(spaceLen) + value
        textLine(line)
        return this
    }

    fun feedAndCut(): EscPosBuilder {
        lineFeed(4)
        // GS V 66 0: Partial Cut
        buffer.write(byteArrayOf(0x1D, 0x56, 0x42, 0x00))
        return this
    }

    /**
     * Converts a Bitmap into ESC/POS Raster bit-image (GS v 0) using Floyd-Steinberg dithering
     */
    fun printImage(bitmap: Bitmap, targetWidth: Int = 384): EscPosBuilder {
        val scaled = Bitmap.createScaledBitmap(
            bitmap,
            targetWidth,
            (bitmap.height * (targetWidth.toFloat() / bitmap.width)).toInt(),
            true
        )

        val width = scaled.width
        val height = scaled.height
        val widthBytes = (width + 7) / 8

        // Header for GS v 0
        // GS v 0 m xL xH yL yH
        buffer.write(byteArrayOf(
            0x1D, 0x76, 0x30, 0x00,
            (widthBytes and 0xFF).toByte(),
            ((widthBytes shr 8) and 0xFF).toByte(),
            (height and 0xFF).toByte(),
            ((height shr 8) and 0xFF).toByte()
        ))

        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)

        val imageData = ByteArray(widthBytes * height)
        var byteIdx = 0

        for (y in 0 until height) {
            var currentByte = 0
            var bitCount = 0
            for (x in 0 until width) {
                val pixel = pixels[y * width + x]
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                val luminance = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

                // Thresholding: black pixel = 1, white = 0
                val bit = if (luminance < 128) 1 else 0
                currentByte = (currentByte shl 1) or bit
                bitCount++

                if (bitCount == 8) {
                    imageData[byteIdx++] = currentByte.toByte()
                    currentByte = 0
                    bitCount = 0
                }
            }
            if (bitCount > 0) {
                currentByte = currentByte shl (8 - bitCount)
                imageData[byteIdx++] = currentByte.toByte()
            }
        }

        buffer.write(imageData)
        lineFeed(2)
        return this
    }

    fun build(): ByteArray {
        return buffer.toByteArray()
    }
}
