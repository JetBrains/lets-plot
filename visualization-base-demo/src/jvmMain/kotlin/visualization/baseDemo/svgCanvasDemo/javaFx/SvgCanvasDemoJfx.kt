package jetbrains.datalore.visualization.baseDemo.svgCanvasDemo.javaFx

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.CanvasUtil
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.demoUtils.jfx.CanvasRendererDemoFrame
import jetbrains.datalore.visualization.svgDemoModel.a.DemoModelA
import javax.swing.SwingUtilities


class SvgCanvasDemoJfx {

    companion object {
        private val DEVICE_PIXEL_RATIO = CanvasUtil.readDevicePixelRatio(2.0)
        private val SIZE = DoubleVector(800.0, 600.0)

        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { SvgCanvasDemoJfx().show() }
        }
    }

    private fun show() {
        val svgGroup = DemoModelA.createModel()

        val svgRoot = SvgSvgElement(SIZE.x, SIZE.y)
        svgRoot.children().add(svgGroup)
        val svgRoots = listOf(svgRoot)
        CanvasRendererDemoFrame.showSvg(svgRoots, SIZE, "SVG JavaFX canvas renderer")

    }
}