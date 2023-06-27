/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.browser

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.dom.SvgRootDocumentMapper
import org.w3c.dom.Node
import kotlinx.browser.document

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