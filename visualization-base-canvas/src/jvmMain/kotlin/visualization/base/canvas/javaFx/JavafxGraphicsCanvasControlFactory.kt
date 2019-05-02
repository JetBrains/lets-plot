package jetbrains.datalore.visualization.base.canvas.javaFx

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControl
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControlFactory

class JavafxGraphicsCanvasControlFactory(private val myPixelRatio: Double) : GraphicsCanvasControlFactory {

    override fun create(size: Vector, repaint: Runnable): GraphicsCanvasControl {
        return JavafxGraphicsCanvasControl(size, repaint, myPixelRatio)
    }
}
