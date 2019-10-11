package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgDemoModel.b.DemoModelB
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

fun main() {
    val svgRoots = listOf(DemoModelB.createModel())
    BatikMapperDemoFrame.showSvg(
        svgRoots,
        DoubleVector(500.0, 300.0),
        "Svg Elements"
    )
}
