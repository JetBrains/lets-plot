package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot

interface CanvasPeer {
    fun createCanvas(width: Int, height: Int): Canvas = createCanvas(Vector(width, height))
    fun createCanvas(size: Vector): Canvas
    fun createCanvas(size: Vector, contentScale: Double): Canvas
    fun createSnapshot(bitmap: Bitmap): Snapshot

    fun decodeDataImageUrl(dataUrl: String): Async<Snapshot>
    fun decodePng(png: ByteArray): Async<Snapshot>
}