/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer

class DomCanvasPeer(
    val fontManager: DomFontManager = DomFontManager.DEFAULT
) : CanvasPeer {
    override fun createCanvas(
        size: Vector,
        contentScale: Double
    ): Canvas {
        return DomCanvas.create(size, contentScale, fontManager)
    }

    override fun createCanvas(size: Vector): Canvas {
        return DomCanvas.create(size, 1.0, fontManager)
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
         val htmlCanvasElement = BitmapUtil.toHTMLCanvasElement(bitmap)
        return DomCanvas.DomSnapshot(htmlCanvasElement, Vector(bitmap.width, bitmap.height))
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }
}
