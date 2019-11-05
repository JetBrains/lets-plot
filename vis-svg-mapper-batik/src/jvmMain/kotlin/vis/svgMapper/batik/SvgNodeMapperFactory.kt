/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.batik

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MapperFactory
import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svgMapper.RGBEncoderAwt
import org.apache.batik.dom.AbstractDocument
import org.apache.batik.dom.svg.SVGOMElement
import org.w3c.dom.Node
import org.w3c.dom.Text

internal class SvgNodeMapperFactory(private val myDoc: AbstractDocument, private val myPeer: SvgBatikPeer) : MapperFactory<SvgNode, Node> {

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Node> {
        var src = source
        val result: Mapper<out SvgNode, out Node>
        val target = Utils.newBatikNode(src, myDoc)

        if (src is SvgImageElementEx) {
            src = src.asImageElement(RGBEncoderAwt())
        }

        if (src is SvgImageElement) {
            // Workaround:
            // current Batik version (1.7) do not support "image-rendering: pixelated" style
            // to avoid exception remove 'style' attribute altogether
            val sourceBatik = SvgImageElement()
            SvgUtils.copyAttributes(src as SvgElement, sourceBatik)
            sourceBatik.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, null)
            src = sourceBatik
        }

        when (src) {
            is SvgElement -> result =
                SvgElementMapper(src, target as SVGOMElement, myDoc, myPeer)
            is SvgTextNode -> result =
                SvgTextNodeMapper(src, target as Text, myDoc, myPeer)
            else -> throw IllegalArgumentException("Unsupported SvgElement: " + src::class.simpleName)
        }

        return result
    }
}