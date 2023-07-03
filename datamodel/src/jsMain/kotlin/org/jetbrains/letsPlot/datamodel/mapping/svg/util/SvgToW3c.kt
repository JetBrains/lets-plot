/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.svg.util

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.mapping.svg.w3c.SvgRootDocumentMapper
import org.w3c.dom.Node
import kotlinx.browser.document

object SvgToW3c {
    fun generateDom(svgRoots: List<SvgSvgElement>, parentNodeId: String) {
        val rootElement = document.getElementById(parentNodeId)
            ?: throw IllegalStateException("Parent node '$parentNodeId' wasn't found")

        for (svg in svgRoots) {
            val div = document.createElement("div")
            rootElement.appendChild(div)
            generateDom(svg, div)
        }
    }

    private fun generateDom(svg: SvgSvgElement, parent: Node) {
        val mapper = SvgRootDocumentMapper(svg)
        SvgNodeContainer(svg)
        mapper.attachRoot()
        parent.appendChild(mapper.target)
    }
}