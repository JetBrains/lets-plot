/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.*

internal sealed class LineElement {
    abstract val element: SvgElement

    abstract fun setX(x: Double)
    abstract fun setY(baselineY: Double)
    abstract fun setVerticalAnchor(anchor: Text.VerticalAnchor?, fontSize: Double)
    abstract fun setHorizontalAnchor(anchor: Text.HorizontalAnchor)
    abstract fun applyColor(color: Color?)
    abstract fun applyStrokeColor(color: Color?)
    abstract fun applyStrokeWidth(px: Double)
    abstract fun applyStyle(styleAttr: String)
    abstract fun addClass(className: String)

    abstract fun canAbsorb(fresh: LineElement): Boolean
    abstract fun replaceChildrenFrom(fresh: LineElement)

    protected fun moveChildren(target: SvgElement, source: SvgElement) {
        val newChildren = source.children().toList()
        target.children().clear()
        newChildren.forEach { child ->
            child.removeFromParent()
            target.children().add(child)
        }
    }
}

internal class TextLine(private val text: SvgTextElement) : LineElement() {
    override val element: SvgElement get() = text

    override fun setX(x: Double) {
        text.x().set(x)
    }

    override fun setY(baselineY: Double) {
        text.y().set(baselineY)
    }

    override fun setVerticalAnchor(anchor: Text.VerticalAnchor?, fontSize: Double) {
        if (anchor != null) {
            text.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, Text.toDY(anchor))
        }
    }

    override fun setHorizontalAnchor(anchor: Text.HorizontalAnchor) {
        text.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, Text.toTextAnchor(anchor))
    }

    override fun applyColor(color: Color?) {
        text.fillColor().set(color)
    }

    override fun applyStrokeColor(color: Color?) {
        text.strokeColor().set(color)
    }

    override fun applyStrokeWidth(px: Double) {
        text.strokeWidth().set(px)
    }

    override fun applyStyle(styleAttr: String) {
        text.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr)
    }

    override fun addClass(className: String) {
        text.addClass(className)
    }

    override fun canAbsorb(fresh: LineElement): Boolean = fresh is TextLine

    override fun replaceChildrenFrom(fresh: LineElement) {
        moveChildren(text, (fresh as TextLine).text)
    }
}

internal class GroupLine(private val group: SvgGElement) : LineElement() {
    override val element: SvgElement get() = group

    private var x = 0.0
    private var baselineY = 0.0
    private var verticalPushPx = 0.0

    override fun setX(x: Double) {
        this.x = x
        rebuildTransform()
    }

    override fun setY(baselineY: Double) {
        this.baselineY = baselineY
        rebuildTransform()
    }

    override fun setVerticalAnchor(anchor: Text.VerticalAnchor?, fontSize: Double) {
        verticalPushPx = verticalAnchorDyEm(anchor) * fontSize
        rebuildTransform()
    }

    override fun setHorizontalAnchor(anchor: Text.HorizontalAnchor) {
    }

    override fun applyColor(color: Color?) {
        paintColor(group, color)
    }

    override fun applyStrokeColor(color: Color?) {
        paintStrokeColor(group, color)
    }

    override fun applyStrokeWidth(px: Double) {
        paintStrokeWidth(group, px)
    }

    override fun applyStyle(styleAttr: String) {
        paintStyle(group, styleAttr)
    }

    override fun addClass(className: String) {
        group.addClass(className)
    }

    override fun canAbsorb(fresh: LineElement): Boolean = fresh is GroupLine

    override fun replaceChildrenFrom(fresh: LineElement) {
        moveChildren(group, (fresh as GroupLine).group)
    }

    private fun rebuildTransform() {
        group.transform().set(SvgTransformBuilder().translate(x, baselineY + verticalPushPx).build())
    }

    // Vertical-anchor offset (em) that text lines receive via the SVG `dy` attribute
    // (Text.toDY -> SvgConstants.SVG_TEXT_DY_TOP / SVG_TEXT_DY_CENTER). Group lines have no `dy`,
    // so we add the matching SvgConstants offset to their transform instead.
    private fun verticalAnchorDyEm(anchor: Text.VerticalAnchor?): Double {
        return when (anchor) {
            Text.VerticalAnchor.TOP -> emValue(SvgConstants.SVG_TEXT_DY_TOP)
            Text.VerticalAnchor.CENTER -> emValue(SvgConstants.SVG_TEXT_DY_CENTER)
            else -> 0.0
        }
    }

    private fun emValue(dy: String): Double = dy.removeSuffix("em").toDouble()

    // Recursive descendant walk over a group's heterogeneous SVG contents.
    private fun paintColor(node: SvgElement, color: Color?) {
        when (node) {
            is SvgTextElement -> node.fillColor().set(color)
            is SvgPathElement -> {
                val isBBoxGuide = node.classAttribute().get()
                    ?.split(' ')
                    ?.contains(Latex.VECTOR_BBOX_CLASS) == true
                if (!isBBoxGuide) {
                    node.fillColor().set(color)
                }
            }
            is SvgGElement -> node.children().forEach { if (it is SvgElement) paintColor(it, color) }
        }
    }

    private fun paintStrokeColor(node: SvgElement, color: Color?) {
        when (node) {
            is SvgTextElement -> node.strokeColor().set(color)
            is SvgPathElement -> {
                val isBBoxGuide = node.classAttribute().get()
                    ?.split(' ')
                    ?.contains(Latex.VECTOR_BBOX_CLASS) == true
                if (!isBBoxGuide) {
                    node.strokeColor().set(color)
                }
            }
            is SvgGElement -> node.children().forEach { if (it is SvgElement) paintStrokeColor(it, color) }
        }
    }

    private fun paintStrokeWidth(node: SvgElement, px: Double) {
        when (node) {
            is SvgTextElement -> node.strokeWidth().set(px)
            is SvgPathElement -> {
                val isBBoxGuide = node.classAttribute().get()
                    ?.split(' ')
                    ?.contains(Latex.VECTOR_BBOX_CLASS) == true
                if (!isBBoxGuide) {
                    node.strokeWidth().set(px)
                }
            }
            is SvgGElement -> node.children().forEach { if (it is SvgElement) paintStrokeWidth(it, px) }
        }
    }

    private fun paintStyle(node: SvgElement, styleAttr: String) {
        when (node) {
            is SvgTextElement -> {
                val isFallback = node.classAttribute().get()
                    ?.split(' ')
                    ?.contains(Latex.VECTOR_TEXT_CLASS) == true
                if (!isFallback) {
                    node.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr)
                }
            }
            is SvgGElement -> node.children().forEach { if (it is SvgElement) paintStyle(it, styleAttr) }
        }
    }
}
