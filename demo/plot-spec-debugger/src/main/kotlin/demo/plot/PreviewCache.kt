/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import javax.swing.ImageIcon

internal object PreviewCache {
    const val THUMB_WIDTH = 54
    const val THUMB_HEIGHT = 36
    const val FULL_WIDTH = 480
    const val FULL_HEIGHT = 320

    // Cell renderers repaint frequently; avoid hitting the disk on every paint.
    private val iconCache = ConcurrentHashMap<String, ImageIcon>()
    private val thumbMisses = ConcurrentHashMap.newKeySet<String>()
    private val fullMisses = ConcurrentHashMap.newKeySet<String>()

    fun loadIcon(spec: String): ImageIcon? {
        val key = hash(spec)
        iconCache[key]?.let { return it }
        if (key in thumbMisses) return null

        val file = AppPaths.previewsDir.resolve("$key.thumb.png")
        if (!file.isFile) {
            thumbMisses.add(key)
            return null
        }
        return try {
            val img = ImageIO.read(file) ?: run {
                thumbMisses.add(key)
                return null
            }
            ImageIcon(img).also { iconCache[key] = it }
        } catch (_: Exception) {
            thumbMisses.add(key)
            null
        }
    }

    fun loadFull(spec: String): BufferedImage? {
        val key = hash(spec)
        if (key in fullMisses) return null
        val file = AppPaths.previewsDir.resolve("$key.full.png")
        if (!file.isFile) {
            fullMisses.add(key)
            return null
        }
        return try {
            ImageIO.read(file) ?: run {
                fullMisses.add(key)
                null
            }
        } catch (_: Exception) {
            fullMisses.add(key)
            null
        }
    }

    fun save(spec: String, source: BufferedImage) {
        val key = hash(spec)
        val dir = AppPaths.previewsDir
        try {
            dir.mkdirs()
            val thumb = fitLetterbox(source, THUMB_WIDTH, THUMB_HEIGHT)
            val full = fitLetterbox(source, FULL_WIDTH, FULL_HEIGHT)
            ImageIO.write(thumb, "png", dir.resolve("$key.thumb.png"))
            ImageIO.write(full, "png", dir.resolve("$key.full.png"))
            iconCache[key] = ImageIcon(thumb)
            thumbMisses.remove(key)
            fullMisses.remove(key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hash(spec: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(spec.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun fitLetterbox(source: BufferedImage, canvasW: Int, canvasH: Int): BufferedImage {
        val scale = minOf(canvasW.toDouble() / source.width, canvasH.toDouble() / source.height).coerceAtMost(1.0)
        val w = (source.width * scale).toInt().coerceAtLeast(1)
        val h = (source.height * scale).toInt().coerceAtLeast(1)
        val out = BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_RGB)
        val g = out.createGraphics()
        try {
            g.color = Color.WHITE
            g.fillRect(0, 0, canvasW, canvasH)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            g.drawImage(source, (canvasW - w) / 2, (canvasH - h) / 2, w, h, null)
        } finally {
            g.dispose()
        }
        return out
    }
}
