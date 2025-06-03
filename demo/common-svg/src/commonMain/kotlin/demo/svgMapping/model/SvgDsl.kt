/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.model

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx.Bitmap
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle


internal fun svgDocument(
    width: Number?,
    height: Number?,
    x: Number? = null,
    y: Number? = null,
    config: SvgSvgElement.() -> Unit = {},
): SvgSvgElement {
    val el = SvgSvgElement()
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    width?.let { el.width().set(it.toDouble()) }
    height?.let { el.height().set(it.toDouble()) }

    el.apply(config)
    return el
}

internal fun SvgNode.svg(
    width: Number?,
    height: Number?,
    x: Number? = null,
    y: Number? = null,
    id: String? = null,
    config: SvgSvgElement.() -> Unit = {},
): SvgSvgElement {
    val el = svgDocument(width, height, x, y, config)

    id?.let { el.id().set(it) }

    children().add(el)
    el.apply(config)
    return el
}

internal fun SvgNode.g(
    transform: SvgTransform? = null,
    visibility: SvgGraphicsElement.Visibility? = null,
    id: String? = null,
    config: SvgGElement.() -> Unit = {},
): SvgGElement {
    val el = SvgGElement()
    id?.let { el.id().set(it) }
    visibility?.let { el.visibility().set(it) }
    transform?.let { el.transform().set(it) }
    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.path(
    stroke: SvgColor? = null,
    fill: SvgColor? = null,
    pathData: SvgPathData? = null,
    strokeWidth: Number? = null,
    strokeOpacity: Number? = null,
    fillOpacity: Number? = null,
    transform: SvgTransform? = null,
    id: String? = null,
    config: SvgPathElement.() -> Unit = {},
): SvgPathElement {
    val el = pathData?.let { SvgPathElement(it) } ?: SvgPathElement()
    id?.let { el.id().set(it) }
    stroke?.let { el.stroke().set(it) }
    strokeOpacity?.let { el.strokeOpacity().set(it.toDouble()) }
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }
    fill?.let { el.fill().set(it) }
    fillOpacity?.let { el.fillOpacity().set(it.toDouble()) }
    transform?.let { el.transform().set(it) }

    el.apply(config)

    children().add(el)
    return el
}

internal fun SvgNode.rect(
    x: Number? = null,
    y: Number? = null,
    width: Number? = null,
    height: Number? = null,
    stroke: SvgColor? = null,
    fill: SvgColor? = null,
    strokeWidth: Number? = null,
    id: String? = null,
    config: SvgRectElement.() -> Unit = {},
): SvgRectElement {
    val el = SvgRectElement()
    id?.let { el.id().set(it) }
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    width?.let { el.width().set(it.toDouble()) }
    height?.let { el.height().set(it.toDouble()) }
    stroke?.let { el.stroke().set(it) }
    fill?.let { el.fill().set(it) }
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.circle(
    cx: Number? = null,
    cy: Number? = null,
    r: Number? = null,
    stroke: SvgColor? = null,
    fill: SvgColor? = null,
    id: String? = null,
    config: SvgCircleElement.() -> Unit = {},
): SvgCircleElement {
    val el = SvgCircleElement()
    id?.let { el.id().set(it) }
    cx?.let { el.cx().set(it.toDouble()) }
    cy?.let { el.cy().set(it.toDouble()) }
    r?.let { el.r().set(it.toDouble()) }
    stroke?.let { el.stroke().set(it) }
    fill?.let { el.fill().set(it) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.line(
    x1: Number? = null,
    y1: Number? = null,
    x2: Number? = null,
    y2: Number? = null,
    stroke: SvgColor? = null,
    strokeWidth: Number? = null,
    id: String? = null,
    config: SvgLineElement.() -> Unit = {},
): SvgLineElement {
    val el = SvgLineElement()
    id?.let { el.id().set(it) }
    x1?.let { el.x1().set(it.toDouble()) }
    y1?.let { el.y1().set(it.toDouble()) }
    x2?.let { el.x2().set(it.toDouble()) }
    y2?.let { el.y2().set(it.toDouble()) }
    stroke?.let { el.stroke().set(it) }
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.text(
    text: String? = null,
    x: Number? = null,
    y: Number? = null,
    fill: SvgColor? = null,
    styleClass: String? = null,
    id: String? = null,
    config: SvgTextElement.() -> Unit = {},
): SvgTextElement {
    val el = SvgTextElement()
    text?.let { el.setTextNode(it) }
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    fill?.let { el.fill().set(it) }
    styleClass?.let { el.addClass(it) }
    id?.let { el.id().set(it) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgTextElement.tspan(
    text: String,
    x: Number? = null,
    y: Number? = null,
    dy: String? = null, // only em is supported
    fontSize: String? = null, // only percent is supported
    baselineShift: String? = null, // only percent is supported
    fill: SvgColor? = null,
    stroke: SvgColor? = null,
    strokeWidth: Number? = null,
    styleClass: String? = null,
    config: SvgTSpanElement.() -> Unit = {},
): SvgTSpanElement {
    val el = SvgTSpanElement(text)
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    dy?.let { el.setAttribute("dy", it) }
    baselineShift?.let { el.setAttribute("baseline-shift", it) }
    fontSize?.let { el.setAttribute("font-size", it) }
    fill?.let { el.fill().set(it) }
    stroke?.let { el.stroke().set(it) }
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }
    styleClass?.let { el.addClass(styleClass) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun translate(x: Number, y: Number): SvgTransform {
    return SvgTransformBuilder().translate(x.toDouble(), y.toDouble()).build()
}


internal fun SvgNode.style(
    resource: SvgCssResource
): SvgStyleElement {
    val el = SvgStyleElement(resource)
    children().add(el)
    return el
}

internal fun SvgNode.style(
    resource: Map<String, TextStyle>
): SvgStyleElement {
    val el = SvgStyleElement(object : SvgCssResource {
        override fun css(): String {
            return StyleSheet(resource, defaultFamily = FontFamily.SERIF.toString()).toCSS()
        }
    })
    children().add(el)
    return el
}



internal fun SvgNode.slimG(
    capacity: Int,
    transform: Any? = null,
    config: SvgSlimGroup.() -> Unit = {},
): SvgSlimGroup {
    val el = if (transform == null) SvgSlimElements.g(capacity) else SvgSlimElements.g(capacity, transform)
    el.apply(config)

    val g = SvgGElement()
    g.isPrebuiltSubtree = true
    g.children().add(el.asDummySvgNode())
    children().add(g)
    return el
}

internal fun SvgSlimGroup.slimLine(
    x1: Double,
    y1: Double,
    x2: Double,
    y2: Double,
    stroke: Color? = null,
    strokeWidth: Number? = null,
    config: SvgSlimShape.() -> Unit = {},
    ): SvgSlimShape {
    val el = SvgSlimElements.line(x1, y1, x2, y2)
    stroke?.let { el.setStroke(it, 1.0) }
    strokeWidth?.let { el.setStrokeWidth(it.toDouble()) }
    el.apply(config)
    el.appendTo(this)
    return el
}

internal fun SvgSlimGroup.slimRect(
    x: Number,
    y: Number,
    width: Number,
    height: Number,
    stroke: Color? = null,
    fill: Color? = null,
    strokeWidth: Number? = null,
    config: SvgSlimShape.() -> Unit = {},
    ): SvgSlimShape {
    val el = SvgSlimElements.rect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    stroke?.let { el.setStroke(it, 1.0) }
    fill?.let { el.setFill(it, 1.0) }
    strokeWidth?.let { el.setStrokeWidth(it.toDouble()) }

    el.apply(config)
    el.appendTo(this)
    return el
}

internal fun SvgSlimGroup.slimCircle(
    cx: Number,
    cy: Number,
    r: Number,
    stroke: Color? = null,
    fill: Color? = null,
    strokeWidth: Number? = null,
    config: SvgSlimShape.() -> Unit = {},
    ): SvgSlimShape {
    val el = SvgSlimElements.circle(cx.toDouble(), cy.toDouble(), r.toDouble())
    stroke?.let { el.setStroke(it, 1.0) }
    fill?.let { el.setFill(it, 1.0) }
    strokeWidth?.let { el.setStrokeWidth(it.toDouble()) }

    el.apply(config)
    el.appendTo(this)
    return el
}

internal fun SvgSlimGroup.slimPath(
    pathData: SvgPathData,
    stroke: Color? = null,
    fill: Color? = null,
    strokeWidth: Number? = null,
    config: SvgSlimShape.() -> Unit = {},
    ): SvgSlimShape {
    val el = SvgSlimElements.path(pathData)
    stroke?.let { el.setStroke(it, 1.0) }
    fill?.let { el.setFill(it, 1.0) }
    strokeWidth?.let { el.setStrokeWidth(it.toDouble()) }
    el.apply(config)
    el.appendTo(this)
    return el
}

internal fun SvgNode.image(
    href: String? = null,
    x: Number? = null,
    y: Number? = null,
    width: Number? = null,
    height: Number? = null,
    id: String? = null,
    config: SvgImageElement.() -> Unit = {},
): SvgImageElement {
    val el = SvgImageElement()
    href?.let { el.href().set(SvgUtils.pngDataURI(it)) }
    id?.let { el.id().set(it) }
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    width?.let { el.width().set(it.toDouble()) }
    height?.let { el.height().set(it.toDouble()) }


    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.bitmap(
    bitmap: Bitmap,
    x: Number,
    y: Number,
    width: Number,
    height: Number,
    id: String? = null,
    config: SvgImageElementEx.() -> Unit = {},
) : SvgImageElementEx {
    val el = SvgImageElementEx(
        x = x.toDouble(),
        y = y.toDouble(),
        width = width.toDouble(),
        height = height.toDouble(),
        myBitmap = bitmap
    )

    id?.let { el.id().set(it) }
    el.apply(config)
    children().add(el)
    return el
}
