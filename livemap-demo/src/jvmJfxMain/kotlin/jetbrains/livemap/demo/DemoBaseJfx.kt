package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasControl
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxGraphicsCanvasControlFactory

open class DemoBaseJfx(private val demoModelProvider: (CanvasControl) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    internal fun show() {
        val canvasControl = AwtCanvasControl(JavafxGraphicsCanvasControlFactory(1.0), size)

        demoModelProvider(canvasControl).show()

        AwtCanvasDemoUtil.showAwtCanvasControl("AWT Livemap Demo", canvasControl)
    }
}
