package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector

interface CanvasPeer : CanvasProvider, AnimationProvider {
    fun createCanvas(width: Int, height: Int): Canvas = createCanvas(Vector(width, height))
    fun createCanvas(size: Vector, contentScale: Double): Canvas
}