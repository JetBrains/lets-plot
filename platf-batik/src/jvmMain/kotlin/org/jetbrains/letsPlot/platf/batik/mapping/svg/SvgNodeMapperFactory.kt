/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.batik.mapping.svg

import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MapperFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
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