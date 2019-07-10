package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.swing.SceneMapperDemoFrame
import jetbrains.datalore.visualization.svgMapperDemo.model.DemoModel

fun main() {
    val svgRoots = listOf(DemoModel.createModel())
    SceneMapperDemoFrame.showSvg(
        svgRoots,
        listOf("/svg-mapper-demo.css"),
        DoubleVector(500.0, 300.0),
        "Svg Elements (JFX SVG mapper)"
    )
}
