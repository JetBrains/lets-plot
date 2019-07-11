package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.svgDemoModel.a.DemoModelA

fun main() {
    val size = DoubleVector(500.0, 500.0)
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(size.x, size.y)
    svgRoot.children().add(svgGroup)
    val svgRoots = listOf(svgRoot)
    BatikMapperDemoFrame.showSvg(
        svgRoots,
        size,
        "Svg Elements (A)"
    )
}
