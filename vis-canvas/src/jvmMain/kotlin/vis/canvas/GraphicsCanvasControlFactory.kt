package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.geometry.Vector

interface GraphicsCanvasControlFactory {
    fun create(size: Vector, repaint: Runnable): GraphicsCanvasControl
}
