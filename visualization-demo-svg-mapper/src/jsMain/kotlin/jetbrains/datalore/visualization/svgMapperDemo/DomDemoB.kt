package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.visualization.base.browser.DomMapperDemoUtil
import jetbrains.datalore.visualization.svgDemoModel.b.DemoModelB

fun svgElementsDemo() {
    val svgRoot = DemoModelB.createModel()
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), "root")
}