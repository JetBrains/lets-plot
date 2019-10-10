package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import jetbrains.datalore.vis.svgDemoModel.b.DemoModelB

fun svgElementsDemo() {
    val svgRoot = DemoModelB.createModel()
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), "root")
}