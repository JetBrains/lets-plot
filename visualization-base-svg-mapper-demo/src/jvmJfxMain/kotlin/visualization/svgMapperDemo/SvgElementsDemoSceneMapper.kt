package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.swing.SceneMapperDemoFrame
import jetbrains.datalore.visualization.svgDemoModel.b.DemoModelB

fun main() {
    val svgRoots = listOf(DemoModelB.createModel())
    SceneMapperDemoFrame.showSvg(
        svgRoots,
        listOf("/svg-demo-model-b.css"),
        DoubleVector(500.0, 300.0),
        "Svg Elements (JFX SVG mapper)"
    )
}
