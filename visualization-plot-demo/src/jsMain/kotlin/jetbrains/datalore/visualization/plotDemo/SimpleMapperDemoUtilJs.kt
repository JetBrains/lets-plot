package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svgMapper.dom.SvgRootDocumentMapper
import org.w3c.dom.Node
import kotlin.browser.document

internal fun showSvg(svgRoots: List<SvgSvgElement>, size: DoubleVector) {
    val rootElement = document.getElementById("root")!!

    for (svg in svgRoots) {
        val div = document.createElement("div")
        rootElement.appendChild(div)
        showSvg(svg, size, div)
    }
}

private fun showSvg(svg: SvgSvgElement, size: DoubleVector, parent: Node) {
    val mapper = SvgRootDocumentMapper(svg)
    SvgNodeContainer(svg)
    mapper.attachRoot()
    parent.appendChild(mapper.target)
}
