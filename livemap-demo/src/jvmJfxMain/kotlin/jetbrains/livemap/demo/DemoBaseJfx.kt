package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.vis.canvas.javaFx.JavafxGraphicsCanvasControlFactory

open class DemoBaseJfx(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    internal fun show() {
        val canvasControl = AwtCanvasControl(
            JavafxGraphicsCanvasControlFactory(1.0), size
        )

        demoModelProvider(size.toDoubleVector()).show(canvasControl)

        AwtCanvasDemoUtil.showAwtCanvasControl("AWT Livemap Demo", canvasControl)
    }
}
