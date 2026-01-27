package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector

interface CanvasPeer : CanvasProvider {
    fun createCanvas(size: Vector, contentScale: Double): Canvas
}