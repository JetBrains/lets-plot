/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.batik

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.anim.dom.SVGOMSVGElement

class SvgRootDocumentMapper(source: SvgSvgElement) : Mapper<SvgSvgElement, SVGOMDocument>(
    source,
    createDocument()
) {

    companion object {
        private fun createDocument(): SVGOMDocument {
            val impl = SVGDOMImplementation.getDOMImplementation()
            val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
            return impl.createDocument(svgNS, "svg", null) as SVGOMDocument
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