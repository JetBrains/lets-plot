/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import org.jetbrains.letsPlot.commons.intern.observable.collections.ObservableCollection
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.SimpleCollectionProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.jfx.util.toFxColor


internal class SvgTextElementMapper(
    source: SvgTextElement,
    target: TextLine,
    peer: SvgJfxPeer
) : SvgElementMapper<SvgTextElement, TextLine>(source, target, peer) {

    override fun applyStyle() {
        setFontProperties(target, peer.styleSheet)
    }

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        // Sync TextNodes, TextSpans
        val sourceTextRunProperty = sourceTextRunProperty(source.children(), peer.styleSheet)
        val targetTextRunProperty = targetTextRunProperty(target)
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextRunProperty,
                targetTextRunProperty
            )
        )
    }

    private fun setFontProperties(target: TextLine, styleSheet: StyleSheet?) {
        if (styleSheet == null) {
            return
        }

        // Note: in SVG style and CSS have higher priority than attributes.
        // We don't support this behavior because right now we never set
        // font and fill properties via attributes and style at the same time.
        val className = source.fullClass()
        if (className.isNotEmpty()) {
            val style = styleSheet.getTextStyle(className)

            val posture = if (style.face.italic) FontPosture.ITALIC else null
            val weight = if (style.face.bold) FontWeight.BOLD else null
            val familyList = style.family.split(",").map { it.trim(' ', '\"') }
            val family = familyList
                .map { Font.font(it, weight, posture, style.size) }
                .firstOrNull {
                    (if (style.face.italic) it.style.contains("italic", ignoreCase = true) else true) &&
                            (if (style.face.bold) it.style.contains("bold", ignoreCase = true) else true)
                }?.family
                ?: familyList.firstOrNull()

            target.fontFamily = family
            target.fontWeight = weight
            target.fontPosture = posture
            target.fontSize = style.size
            target.fill = Color.web(style.color.toHexColor())
        }
    }

    companion object {
        private fun sourceTextRunProperty(nodes: ObservableCollection<SvgNode>, styleSheet: StyleSheet?): ReadableProperty<List<TextLine.TextRun>> {
            fun handleTSpanElement(node: SvgTSpanElement): List<TextLine.TextRun> =
                node.children().map { child ->
                    require(child is SvgTextNode)
                    val fontScale = node.getAttribute("font-size").get()?.let {
                        require(it is String) { "font-size: only string value is supported, but was $it" }
                        require(it.endsWith("em")) { "font-size: only `em` value is supported, but was $it" }
                        it.removeSuffix("em").toDouble()
                    }

                    val baselineShift = node.getAttribute("baseline-shift").get()?.let {
                        when (it) {
                            "sub" -> TextLine.BaselineShift.SUB
                            "super" -> TextLine.BaselineShift.SUPER
                            else -> error("Unexpected baseline-shift value: $it")
                        }
                    }

                    val dy = node.getAttribute("dy").get()?.let {
                        require(it is String) { "dy: only string value is supported, but was $it" }
                        require(it.endsWith("em")) { "dy: only `em` value is supported, but was $it" }
                        it.removeSuffix("em").toDouble()
                    }

                    val style = styleSheet?.getTextStyle(node.fullClass())

                    val fill = when {
                        style == null -> null
                        style.isNoneColor -> null
                        else -> style.color
                    }

                    TextLine.TextRun(
                        text = child.textContent().get(),
                        fill = fill?.toFxColor(),
                        baselineShift = baselineShift,
                        fontScale = fontScale,
                        dy = dy
                    )
                }

            fun textRuns(nodes: ObservableCollection<SvgNode>): List<TextLine.TextRun> {
                return nodes.flatMap { node ->
                    val nodeTextRuns = when (node) {
                        is SvgTextNode -> listOf(TextLine.TextRun(node.textContent().get()))
                        is SvgTSpanElement -> handleTSpanElement(node)
                        is SvgAElement -> {
                            val href = node.getAttribute("href").get() as String
                            node.children().map { child ->
                                require(child is SvgTSpanElement)
                                handleTSpanElement(child)
                            }
                                .flatten()
                                .map { run -> run.copy(href = href) }
                        }

                        else -> error("Unexpected node type: ${node::class.simpleName}")
                    }

                    nodeTextRuns
                }
            }

            return object : SimpleCollectionProperty<SvgNode, List<TextLine.TextRun>>(nodes, textRuns(nodes)) {
                override val propExpr = "textRuns($collection)"
                override fun doGet() = textRuns(collection)
            }
        }

        private fun targetTextRunProperty(target: TextLine): WritableProperty<List<TextLine.TextRun>?> {
            return object : WritableProperty<List<TextLine.TextRun>?> {
                override fun set(value: List<TextLine.TextRun>?) {
                    target.content = value ?: emptyList()
                }
            }
        }
    }
}
