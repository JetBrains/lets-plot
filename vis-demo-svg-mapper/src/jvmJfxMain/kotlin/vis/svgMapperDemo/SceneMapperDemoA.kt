package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgDemoModel.a.DemoModelA

fun main() {
    val size = DoubleVector(500.0, 500.0)
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(size.x, size.y)
    svgRoot.children().add(svgGroup)
    val svgRoots = listOf(svgRoot)
    SceneMapperDemoFrame.showSvg(
        svgRoots,
        emptyList(),
        size,
        "Svg Elements (A)"
    )
}
