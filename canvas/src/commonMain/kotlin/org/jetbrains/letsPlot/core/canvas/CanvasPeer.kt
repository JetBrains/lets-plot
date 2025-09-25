package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot

interface CanvasPeer: Disposable {
    fun createCanvas(size: Vector): Canvas
    fun createSnapshot(bitmap: Bitmap): Snapshot

    fun decodeDataImageUrl(dataUrl: String): Async<Snapshot>
    fun decodePng(png: ByteArray): Async<Snapshot>

    fun measureText(text: String, font: Font): TextMetrics
}