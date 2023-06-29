/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MapperFactory
import jetbrains.datalore.vis.svgMapper.awt.RGBEncoderAwt
import org.jetbrains.letsPlot.datamodel.svg.dom.*

internal class SvgNodeMapperFactory(private val peer: SvgJfxPeer) : MapperFactory<SvgNode, Node> {

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Node> {
        var src = source
        val target = Utils.newSceneNode(src)

        if (src is SvgImageElementEx) {
            src = src.asImageElement(RGBEncoderAwt())
        }

        if (src is SvgImageElement) {
            // Workaround:
            // current Batik version (1.7) do not support "image-rendering: pixelated" style
            // to avoid exception remove 'style' attribute altogether
            @Suppress("NAME_SHADOWING")
            val source = SvgImageElement()
            SvgUtils.copyAttributes(src as SvgElement, source)
            source.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, null)
            src = source
        }

        return when (src) {
            is SvgStyleElement -> SvgStyleElementMapper(src, target as Group, peer)
            is SvgGElement -> SvgGElementMapper(src, target as Group, peer)
            is SvgSvgElement -> SvgSvgElementMapper(src, peer)
            is SvgTextElement -> SvgTextElementMapper(src, target as Text, peer)
//            is SvgTextNode -> result = SvgTextNodeMapper(src, target as Text, myDoc, peer)
            is SvgImageElement -> SvgImageElementMapper(src, target as ImageView, peer)
            is SvgElement -> SvgElementMapper(src, target, peer)
            else -> throw IllegalArgumentException("Unsupported SvgElement: " + src::class.simpleName)
        }
    }
}