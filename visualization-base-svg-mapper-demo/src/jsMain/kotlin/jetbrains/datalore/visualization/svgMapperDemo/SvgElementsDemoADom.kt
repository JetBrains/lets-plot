package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.visualization.base.browser.DomMapperDemoUtil
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.svgDemoModel.a.DemoModelA

fun svgElementsDemoA() {
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(500.0, 500.0)
    svgRoot.children().add(svgGroup)

    DomMapperDemoUtil.mapToDom(listOf(svgRoot), "root")
}