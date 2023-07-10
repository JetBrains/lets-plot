/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.shape.*
import javafx.scene.text.Text
import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr.*
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr.SvgAttrMapping
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr.SvgCircleAttrMapping
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr.SvgEllipseAttrMapping
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr.SvgGAttrMapping
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr.SvgImageAttrMapping
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
private val ATTR_MAPPINGS: Map<KClass<out Node>, SvgAttrMapping<Node>> = mapOf(
    Pane::class to (SvgSvgAttrMapping as SvgAttrMapping<Node>),
    StackPane::class to (SvgSvgAttrMapping as SvgAttrMapping<Node>),
    Group::class to (SvgGAttrMapping as SvgAttrMapping<Node>),
    Rectangle::class to (SvgRectAttrMapping as SvgAttrMapping<Node>),
    Line::class to (SvgLineAttrMapping as SvgAttrMapping<Node>),
    Ellipse::class to (SvgEllipseAttrMapping as SvgAttrMapping<Node>),
    Circle::class to (SvgCircleAttrMapping as SvgAttrMapping<Node>),
    Text::class to (SvgTextElementAttrMapping as SvgAttrMapping<Node>),
    SVGPath::class to (SvgPathAttrMapping as SvgAttrMapping<Node>),
    ImageView::class to (SvgImageAttrMapping as SvgAttrMapping<Node>)
)

internal object Utils {
    fun elementChildren(e: Parent): MutableList<Node> {
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

    fun getChildren(parent: Parent): ObservableList<Node> {
        return when (parent) {
            is Group -> parent.children
            is Pane -> parent.children
            else -> throw IllegalArgumentException("Unsupported parent type: ${parent.javaClass.simpleName}")
        }
    }

    fun newSceneNode(source: SvgNode): Node {
        return when (source) {
            is SvgEllipseElement -> Ellipse()
            is SvgCircleElement -> Circle()
            is SvgRectElement -> Rectangle()
            is SvgTextElement -> Text()
            is SvgPathElement -> SVGPath()
            is SvgLineElement -> Line()
            is SvgSvgElement -> Rectangle()
            is SvgGElement -> Group()
            is SvgStyleElement -> Group()          // ignore
//            is SvgTextNode -> myDoc.createTextNode(null)
//            is SvgTSpanElement -> SVGOMTSpanElement(null, myDoc)
            is SvgDefsElement -> Group() // ignore
//            is SvgClipPathElement -> SVGOMClipPathElement(null, myDoc)
            is SvgImageElement -> ImageView()
            else -> throw IllegalArgumentException("Unsupported source svg element: ${source.javaClass.simpleName}")
        }
    }

    fun getButton(evt: MouseEvent): Button {
        return when (evt.button) {
            PRIMARY -> Button.LEFT
            MIDDLE -> Button.MIDDLE
            SECONDARY -> Button.RIGHT
            else -> Button.NONE
        }
    }

    fun getModifiers(evt: MouseEvent): KeyModifiers {
        val ctrlKey = evt.isControlDown
        val altKey = evt.isAltDown
        val shiftKey = evt.isShiftDown
        val metaKey = evt.isMetaDown
        return KeyModifiers(ctrlKey, altKey, shiftKey, metaKey)
    }

    fun setAttribute(target: Node, name: String, value: Any?) {
        val attrMapping = ATTR_MAPPINGS[target::class]
        attrMapping?.setAttribute(target, name, value)
            ?: throw IllegalArgumentException("Unsupported target: ${target::class}")
    }
}