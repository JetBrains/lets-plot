/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.intern.observable.collections.ObservableCollection
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.SimpleCollectionProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.raster.mapping.svg.attr.SvgTSpanElementAttrMapping
import org.jetbrains.letsPlot.raster.mapping.svg.attr.SvgTextElementAttrMapping
import org.jetbrains.letsPlot.raster.shape.Element
import org.jetbrains.letsPlot.raster.shape.TSpan
import org.jetbrains.letsPlot.raster.shape.Text

internal class SvgTextElementMapper(
    source: SvgTextElement,
    target: Text,
    peer: SvgCanvasPeer
) : SvgElementMapper<SvgTextElement, Text>(source, target, peer) {

    private val myTextAttrSupport = TextAttributesSupport(target)

    override fun setTargetAttribute(name: String, value: Any?) {
        myTextAttrSupport.setAttribute(name, value)
    }

    override fun applyStyle() {
        setFontProperties(target, peer.styleSheet)
    }

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        // Sync TextNodes, TextSpans
        val sourceTextRunProperty = sourceTextRunProperty(source.children(), peer.styleSheet, peer.textMeasurer)
        val targetTextRunProperty = targetTextRunProperty(target)
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextRunProperty,
                targetTextRunProperty
            )
        )
    }

    private fun setFontProperties(target: Text, styleSheet: StyleSheet?) {
        if (styleSheet == null) {
            return
        }
        val className = source.fullClass()
        if (className.isNotEmpty()) {
            val style = styleSheet.getTextStyle(className)
            target.fill = style.color
            target.fontFamily = style.family.split(",").map { it.trim(' ', '"') }
            target.fontSize = style.size.toFloat()
            target.fontStyle = toFontStyle(style.face)
            target.fontWeight = toFontWeight(style.face)

            myTextAttrSupport.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, "fill:${style.color.toHexColor()};")
        }
    }

    private fun targetTextRunProperty(text: Text): WritableProperty<List<Element>?> {
        return object : WritableProperty<List<Element>?> {
            override fun set(value: List<Element>?) {
                text.children.clear()
                value?.forEach { text.children.add(it) }
                text.layoutChildren()
                //text.needLayout = true
            }
        }
    }


    companion object {
        private fun sourceTextRunProperty(
            nodes: ObservableCollection<SvgNode>,
            styleSheet: StyleSheet?,
            textMeasurer: TextMeasurer
            //fontManager: FontManager
        ): ReadableProperty<List<TSpan>> {
            fun toTSpans(nodes: ObservableCollection<SvgNode>): List<TSpan> {
                return nodes.flatMap { node ->
                    val nodeTextRuns = when (node) {
                        //is SvgTextNode -> listOf(TSpan(fontManager).apply { text = node.textContent().get() })
                        //is SvgTSpanElement -> handleTSpanElement(node, styleSheet, fontManager)
                        //is SvgAElement -> handleAElement(node, styleSheet, fontManager)
                        is SvgTextNode -> listOf(TSpan(textMeasurer).apply { text = node.textContent().get() })
                        is SvgTSpanElement -> handleTSpanElement(node, styleSheet, textMeasurer)
                        is SvgAElement -> handleAElement(node, styleSheet, textMeasurer)

                        else -> error("Unexpected node type: ${node::class.simpleName}")
                    }

                    nodeTextRuns
                }
            }

            return object : SimpleCollectionProperty<SvgNode, List<TSpan>>(nodes, toTSpans(nodes)) {
                override val propExpr = "textRuns($collection)"
                override fun doGet() = toTSpans(collection)
            }
        }


        private fun handleTSpanElement(
            node: SvgTSpanElement,
            styleSheet: StyleSheet?,
            textMeasurer: TextMeasurer
        ): List<TSpan> =
            node.children().map { child ->
                require(child is SvgTextNode)
                val style = styleSheet?.getTextStyle(node.fullClass())

                val tspan = TSpan(textMeasurer).apply {
                    text = child.textContent().get()
                    style?.safeColor?.let {
                        fill = it
                    }

                    SvgTSpanElementAttrMapping.setAttributes(this, node)
                }

                if (styleSheet == null) {
                    return@map tspan
                }
                val className = node.fullClass()
                if (className.isNotEmpty()) {
                    val classStyle = styleSheet.getTextStyle(className)
                    classStyle.safeColor?.let {
                        tspan.fill = it
                    }

                    classStyle.safeSize?.let {
                        tspan.fontSize = it.toFloat()
                    }

                    classStyle.safeFamily?.let {
                        tspan.fontFamily = it
                    }

                    tspan.fontStyle = toFontStyle(classStyle.face)
                    tspan.fontWeight = toFontWeight(classStyle.face)
                }

                return@map tspan
            }

        private fun handleAElement(node: SvgAElement, styleSheet: StyleSheet?, textMeasurer: TextMeasurer): List<TSpan> {
            val href = node.getAttribute("href").get() as String
            return node.children().flatMap { child ->
                require(child is SvgTSpanElement)
                handleTSpanElement(child, styleSheet, textMeasurer).onEach {
                    it.href = href
                }
            }
        }

        private class TextAttributesSupport(val target: Text) {
            private var mySvgTextAnchor: String? = null

            fun setAttribute(name: String, value: Any?) {
                if (name == SvgTextContent.TEXT_ANCHOR.name) {
                    mySvgTextAnchor = value as String?
                }
                SvgTextElementAttrMapping.setAttribute(target, name, value)
            }
        }

        fun toFontStyle(face: FontFace): FontStyle =
            when (face.italic) {
                true -> FontStyle.ITALIC
                false -> FontStyle.NORMAL
            }

        fun toFontWeight(face: FontFace): FontWeight =
            when (face.bold) {
                true -> FontWeight.BOLD
                false -> FontWeight.NORMAL
            }
    }
}
