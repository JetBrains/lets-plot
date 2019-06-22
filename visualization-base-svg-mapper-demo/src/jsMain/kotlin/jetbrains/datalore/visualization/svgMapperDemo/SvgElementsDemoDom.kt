package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svgToDom.SvgRootDocumentMapper
import jetbrains.datalore.visualization.svgMapperDemo.model.DemoModel
import kotlin.browser.document

fun main() {
    val svgRoot = DemoModel.createModel()
    val mapper = SvgRootDocumentMapper(svgRoot)
    SvgNodeContainer(svgRoot)
    mapper.attachRoot()
    document.getElementById("root")!!.appendChild(mapper.target)
}