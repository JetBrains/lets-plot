/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.mapping.svg

import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.anim.dom.SVGOMSVGElement
import org.apache.batik.util.ParsedURL
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

class SvgRootDocumentMapper(source: SvgSvgElement) : Mapper<SvgSvgElement, SVGOMDocument>(
    source,
    createDocument()
) {

    companion object {
        private fun createDocument(): SVGOMDocument {
            val impl = SVGDOMImplementation.getDOMImplementation()
            val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
            val svgDocument = impl.createDocument(svgNS, "svg", null) as SVGOMDocument

            // to avoid NPE on link click:
            // java.lang.NullPointerException: Cannot read field "data" because "other" is null
            svgDocument.parsedURL = ParsedURL("")
            return svgDocument
        }
    }

    private var myRootMapper: SvgElementMapper<SvgSvgElement, SVGOMSVGElement>? = null

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        if (!source.isAttached()) {
            throw IllegalStateException("Element must be attached")
        }
        val peer = SvgBatikPeer()
        source.container().setPeer(peer)

        myRootMapper = SvgElementMapper(
            source,
            target.documentElement as SVGOMSVGElement,
            target,
            peer
        )
        target.documentElement.setAttribute("shape-rendering", "geometricPrecision")
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