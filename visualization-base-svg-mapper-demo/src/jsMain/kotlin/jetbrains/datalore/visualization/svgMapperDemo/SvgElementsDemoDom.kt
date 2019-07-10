package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.visualization.base.browser.DomMapperDemoUtil
import jetbrains.datalore.visualization.svgMapperDemo.model.DemoModel

fun svgElementsDemo() {
    val svgRoot = DemoModel.createModel()
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), "root")
}