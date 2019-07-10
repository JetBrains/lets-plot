package jetbrains.datalore.visualization.baseDemo.svgCanvasDemo.javaFx

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.CanvasUtil
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasControl
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxGraphicsCanvasControlFactory
import jetbrains.datalore.visualization.base.svgToCanvas.SvgCanvasRenderer
import jetbrains.datalore.visualization.baseDemo.svgCanvasDemo.model.DemoModelA
import javax.swing.SwingUtilities

class SvgCanvasDemoJfx private constructor() {

    companion object {
        private val DEVICE_PIXEL_RATIO = CanvasUtil.readDevicePixelRatio(2.0)
        private val SIZE = Vector(800, 600)

        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { SvgCanvasDemoJfx().show() }
        }
    }

    private fun show() {
        val canvasControl = AwtCanvasControl(JavafxGraphicsCanvasControlFactory(DEVICE_PIXEL_RATIO), SIZE)

        SvgCanvasRenderer(DemoModelA.createModel(), canvasControl)

        AwtCanvasDemoUtil.showAwtCanvasControl("SvgCanvas JavaFX Demo", canvasControl)
    }
}