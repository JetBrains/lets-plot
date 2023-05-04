/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.batik

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MapperFactory
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgImageElementEx
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgTextNode
import jetbrains.datalore.vis.svgMapper.awt.RGBEncoderAwt
import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.dom.AbstractDocument
import org.w3c.dom.Node
import org.w3c.dom.Text

internal class SvgNodeMapperFactory(private val myDoc: AbstractDocument, private val myPeer: SvgBatikPeer) :
    MapperFactory<SvgNode, Node> {

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Node> {
        var src = source
        val target = Utils.newBatikNode(src, myDoc)

        if (src is SvgImageElementEx) {
            src = src.asImageElement(RGBEncoderAwt())
        }

        return when (src) {
            is SvgElement -> SvgElementMapper(src, target as SVGOMElement, myDoc, myPeer)
            is SvgTextNode -> SvgTextNodeMapper(src, target as Text, myDoc, myPeer)
            else -> (throw IllegalArgumentException("Unsupported SvgElement: " + src::class.simpleName))
        }
    }
}