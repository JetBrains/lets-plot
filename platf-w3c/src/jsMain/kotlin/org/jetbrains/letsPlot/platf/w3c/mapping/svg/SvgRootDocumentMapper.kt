/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg

import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.SVG_NAMESPACE_URI
import org.w3c.dom.svg.SVGSVGElement
import kotlinx.browser.document

class SvgRootDocumentMapper(source: SvgSvgElement): Mapper<SvgSvgElement, SVGSVGElement>(source,
    createDocument()
) {

    companion object {
        private fun createDocument(): SVGSVGElement {
            return document.createElementNS(SVG_NAMESPACE_URI, "svg") as SVGSVGElement
        }
    }

    private var myRootMapper: SvgElementMapper<SvgSvgElement, SVGSVGElement>? = null

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        if (!source.isAttached()) {
            throw IllegalStateException("Element must be attached")
        }

        val peer = SvgDomPeer()
        source.container().setPeer(peer)

        myRootMapper = SvgElementMapper(source, target, peer)
        target.setAttribute("shape-rendering", "geometricPrecision")
        myRootMapper!!.attachRoot()
    }

    override fun onDetach() {
        myRootMapper!!.detachRoot()
        myRootMapper = null

        if (source.isAttached()) {
            source.container().setPeer(null)
        }

        super.onDetach()
    }
}