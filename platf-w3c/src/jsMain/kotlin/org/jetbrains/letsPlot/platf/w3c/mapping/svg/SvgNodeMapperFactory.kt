/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg

import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MapperFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domUtil.DomUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.w3c.dom.Node

class SvgNodeMapperFactory(private val myPeer: SvgDomPeer): MapperFactory<SvgNode, Node> {

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Node> =
            when(source) {
                is SvgImageElement -> {
                    var s = source
                    if (s is SvgImageElementEx) {
                        s = s.asImageElement(RGBEncoderDom())
                    }

                    val pixelated = SvgImageElement()
                    SvgUtils.copyAttributes(s as SvgElement, pixelated)
                    pixelated.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, "image-rendering: pixelated;image-rendering: crisp-edges;")
                    s = pixelated

                    SvgElementMapper(
                        s,
                        DomUtil.generateElement(source),
                        myPeer
                    )
                }
                is SvgElement -> SvgElementMapper(
                    source,
                    DomUtil.generateElement(source),
                    myPeer
                )
                is SvgTextNode -> SvgTextNodeMapper(
                    source,
                    DomUtil.generateTextElement(source),
                    myPeer
                )
                is SvgSlimNode -> SvgNodeMapper(
                    source,
                    DomUtil.generateSlimNode(source),
                    myPeer
                )
                else -> throw IllegalStateException("Unsupported SvgNode ${source::class}")
            }
}