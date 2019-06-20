package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.svgMapperDemo.model.DemoModel

fun main() {
    val svgRoots = listOf(DemoModel.createModel())
    BatikMapperDemoFrame.showSvg(
        svgRoots,
        DoubleVector(500.0, 300.0),
        "Svg Elements"
    )
}
