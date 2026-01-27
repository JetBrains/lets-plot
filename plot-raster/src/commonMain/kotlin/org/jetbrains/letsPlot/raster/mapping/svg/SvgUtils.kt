/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg


import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapGeom
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import org.jetbrains.letsPlot.raster.mapping.svg.attr.*
import org.jetbrains.letsPlot.raster.scene.*
import kotlin.reflect.KClass


internal object SvgUtils {
    @Suppress("UNCHECKED_CAST")
    private val ATTR_MAPPINGS: Map<KClass<out Node>, SvgAttrMapping<Node>> = mapOf(
        Pane::class to (SvgSvgAttrMapping as SvgAttrMapping<Node>),
        //StackPane::class to (SvgSvgAttrMapping as SvgAttrMapping<Element>),
        Group::class to (SvgGAttrMapping as SvgAttrMapping<Node>),
        Rectangle::class to (SvgRectAttrMapping as SvgAttrMapping<Node>),
        Line::class to (SvgLineAttrMapping as SvgAttrMapping<Node>),
        Ellipse::class to (SvgEllipseAttrMapping as SvgAttrMapping<Node>),
        Circle::class to (SvgCircleAttrMapping as SvgAttrMapping<Node>),
        Text::class to (SvgTextElementAttrMapping as SvgAttrMapping<Node>),
        Path::class to (SvgPathAttrMapping as SvgAttrMapping<Node>),
        Image::class to (SvgImageAttrMapping as SvgAttrMapping<Node>),
        TSpan::class to (SvgTSpanElementAttrMapping as SvgAttrMapping<Node>),
        CanvasNode::class to (SvgCanvasAttrMapping as SvgAttrMapping<Node>)
    )

    fun elementChildren(e: Node): MutableList<Node> {
        return object : AbstractMutableList<Node>() {
            override val size: Int
                get() = getChildren(e).size

            override fun get(index: Int): Node {
                return getChildren(e)[index]
            }

            override fun set(index: Int, element: Node): Node {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                return getChildren(e).set(index, element)
            }

            override fun add(index: Int, element: Node) {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                getChildren(e).add(index, element)
            }

            override fun removeAt(index: Int): Node {
                return getChildren(e).removeAt(index)
            }
        }
    }

    fun getChildren(parent: Node): MutableList<Node> {
        return when (parent) {
            is Group -> parent.children
            is Pane -> parent.children
            is Text -> parent.children
            else -> throw IllegalArgumentException("Unsupported parent type: ${parent::class.simpleName}")
        }
    }

    fun newElement(source: SvgNode, peer: SvgCanvasPeer): Node {
        return when (source) {
            is SvgEllipseElement -> Ellipse()
            is SvgCircleElement -> Circle()
            is SvgRectElement -> Rectangle()
            is SvgTextElement -> Text()
            is SvgPathElement -> Path()
            is SvgLineElement -> Line()
            is SvgSvgElement -> Pane()
            is SvgGElement -> Group()
            is SvgStyleElement -> Group()
//            is SvgAElement -> Group()
//            is SvgTextNode -> myDoc.createTextNode(null)
//            is SvgTSpanElement -> SVGOMTSpanElement(null, myDoc)
            is SvgDefsElement -> Group()
//            is SvgClipPathElement -> SVGOMClipPathElement(null, myDoc)
            is SvgImageElement -> Image()
            is LiveMapGeom.SvgCanvasFigureElement -> CanvasNode()
            else -> Group().also { println("SvgUtils.newElement: Unsupported source type: ${source::class.simpleName}") }
        }.also {
            it.peer = peer
        }
    }

    fun setAttribute(target: Node, name: String, value: Any?) {
        val attrMapping = ATTR_MAPPINGS[target::class]
        attrMapping?.setAttribute(target, name, value)
        //?: throw IllegalArgumentException("Unsupported target: ${target::class}")
            ?: println("Unsupported target: ${target::class}")
    }

    fun copyAttributes(source: SvgElement, target: SvgElement) {
        for (attributeSpec in source.attributeKeys) {
            @Suppress("UNCHECKED_CAST")
            val spec = attributeSpec as SvgAttributeSpec<Any?>
            target.setAttribute(spec, source.getAttribute(attributeSpec).get())
        }
    }

    /**
     * value : the color name (string) or SvgColor (jetbrains.datalore.vis.svg)
     */
    fun toColor(value: Any?): Color? {
        require(value != SvgColors.CURRENT_COLOR) { "currentColor is not supported" }

        if (value is Color) {
            return value
        }

        if (value == SvgColors.NONE) {
            return null
        }

        val colorString = value.toString()

        if (Colors.isColorName(colorString)) {
            return Colors.forName(colorString)
        }

        return Color.parseOrNull(colorString) ?: error("Unsupported color value: $colorString")
    }
}

val TextStyle.safeColor: Color? get() = if (isNoneColor) null else color
val TextStyle.safeSize: Double? get() = if (isNoneSize) null else size
