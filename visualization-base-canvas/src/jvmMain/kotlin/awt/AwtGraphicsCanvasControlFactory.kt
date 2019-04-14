package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControl
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControlFactory

class AwtGraphicsCanvasControlFactory(private val myPixelRatio: Double) : GraphicsCanvasControlFactory {

    override fun create(size: Vector, repaint: Runnable): GraphicsCanvasControl {
        return AwtGraphicsCanvasControl(size, repaint, myPixelRatio)
    }
}
