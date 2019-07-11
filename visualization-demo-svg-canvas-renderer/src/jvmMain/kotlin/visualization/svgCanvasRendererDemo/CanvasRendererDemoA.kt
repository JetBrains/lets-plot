package jetbrains.datalore.visualization.svgCanvasRendererDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.demoUtils.jfx.CanvasRendererDemoFrame
import jetbrains.datalore.visualization.svgDemoModel.a.DemoModelA
import javax.swing.SwingUtilities


fun main() {
    SwingUtilities.invokeLater {
        val size = DoubleVector(800.0, 600.0)
        val svgGroup = DemoModelA.createModel()
        val svgRoot = SvgSvgElement(size.x, size.y)
        svgRoot.children().add(svgGroup)
        val svgRoots = listOf(svgRoot)
        CanvasRendererDemoFrame.showSvg(svgRoots, size, "SVG JavaFX canvas renderer")
    }


}
