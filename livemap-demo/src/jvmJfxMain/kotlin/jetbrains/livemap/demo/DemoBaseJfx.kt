package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.vis.canvas.javaFx.JavafxGraphicsCanvasControlFactory

open class DemoBaseJfx(private val demoModelProvider: (CanvasControl) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    internal fun show() {
        val canvasControl = AwtCanvasControl(
            JavafxGraphicsCanvasControlFactory(1.0), size
        )

        demoModelProvider(canvasControl).show()

        AwtCanvasDemoUtil.showAwtCanvasControl("AWT Livemap Demo", canvasControl)
    }
}
