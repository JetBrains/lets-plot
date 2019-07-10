package jetbrains.datalore.visualization.base.browser

import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svgMapper.dom.SvgRootDocumentMapper
import org.w3c.dom.Node
import kotlin.browser.document

object DomMapperDemoUtil {
    fun mapToDom(svgRoots: List<SvgSvgElement>, parentNodeId: String) {
        val rootElement = document.getElementById(parentNodeId)
            ?: throw IllegalStateException("Parent node '$parentNodeId' wasn't found")

        for (svg in svgRoots) {
            val div = document.createElement("div")
            rootElement.appendChild(div)
            mapToDom(svg, div)
        }
    }

    private fun mapToDom(svg: SvgSvgElement, parent: Node) {
        val mapper = SvgRootDocumentMapper(svg)
        SvgNodeContainer(svg)
        mapper.attachRoot()
        parent.appendChild(mapper.target)
    }
}