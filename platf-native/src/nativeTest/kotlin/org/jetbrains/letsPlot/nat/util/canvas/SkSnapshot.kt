package org.jetbrains.letsPlot.nat.util.canvas


import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.skia.Bitmap

class SkSnapshot(
    val bitmap: Bitmap
) : Canvas.Snapshot {
    override fun copy(): Canvas.Snapshot {
        TODO()
    }
}