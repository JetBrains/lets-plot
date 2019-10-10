package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgDemoModel.a.DemoModelA

fun svgElementsDemoA() {
    val svgGroup = DemoModelA.createModel()
    val svgRoot = SvgSvgElement(500.0, 500.0)
    svgRoot.children().add(svgGroup)

    DomMapperDemoUtil.mapToDom(listOf(svgRoot), "root")
}