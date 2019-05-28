package jetbrains.datalore.visualization.baseDemo.svgCanvasDemo.javaFx

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.CanvasUtil
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasControl
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxGraphicsCanvasControlFactory
import jetbrains.datalore.visualization.base.svgToCanvas.SvgCanvasRenderer
import jetbrains.datalore.visualization.baseDemo.svgCanvasDemo.model.DemoModel
import javax.swing.SwingUtilities

class SvgCanvasDemoJavafx private constructor() {

    companion object {
        private val DEVICE_PIXEL_RATIO = CanvasUtil.readDevicePixelRatio(2.0)
        private val SIZE = Vector(800, 600)

        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { SvgCanvasDemoJavafx().show() }
        }
    }

    private fun show() {
        val canvasControl = AwtCanvasControl(JavafxGraphicsCanvasControlFactory(DEVICE_PIXEL_RATIO), SIZE)

        SvgCanvasRenderer(DemoModel.createModel(), canvasControl)

        AwtCanvasDemoUtil.showAwtCanvasControl("SvgCanvas JavaFX Demo", canvasControl)
    }
}