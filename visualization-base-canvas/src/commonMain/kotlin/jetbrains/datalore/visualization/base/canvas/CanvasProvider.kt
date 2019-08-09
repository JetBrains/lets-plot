package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot

interface CanvasProvider {
    fun createCanvas(size: Vector): Canvas
    fun createSnapshot(dataUrl: String): Async<Snapshot>
}