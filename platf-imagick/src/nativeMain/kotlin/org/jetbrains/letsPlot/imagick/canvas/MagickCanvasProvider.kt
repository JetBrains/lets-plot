/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider

class MagickCanvasProvider(
    private val magickFontManager: MagickFontManager,
) : CanvasProvider {
    override fun createCanvas(size: Vector): MagickCanvas {
        return MagickCanvas.create(size.x, size.y, 1.0, magickFontManager)
    }

    override fun createSnapshot(bitmap: Bitmap): MagickSnapshot {
        return MagickSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        println("MagickCanvasControl.createSnapshot(dataUrl): dataUrl.size = ${dataUrl.length}")
        if (false) {
            if (!dataUrl.startsWith("data:image/png;base64,")) {
                throw IllegalArgumentException("Unsupported data URL format: $dataUrl")
            }
            val data = dataUrl.removePrefix("data:image/png;base64,")
            val pngData = Base64.decode(data)

            println("MagickCanvasControl.loadImageFromPngBytes: bytes.size = ${pngData.size}")
            val png = Png.decode(pngData)
            val img = MagickUtil.fromBitmap(png)

            return Asyncs.constant(MagickSnapshot(img))
        } else {
            val bitmap = Png.decodeDataImage(dataUrl)
            return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
        }
    }

    override fun decodePng(png: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        val img = MagickUtil.fromBitmap(Png.decode(png))
        return Asyncs.constant(MagickSnapshot(img))
    }
}