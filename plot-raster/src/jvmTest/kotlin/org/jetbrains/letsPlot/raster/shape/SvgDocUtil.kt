/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper


internal fun mapSvg(builder: () -> SvgSvgElement): Pane {
    //val fontManager = FontManager()
    val svgDocument = builder()

    // attach root
    SvgNodeContainer(svgDocument)

    val canvasPeer = object : CanvasPeer {
        override fun createCanvas(size: Vector) = TODO("Not yet implemented")
        override fun createSnapshot(bitmap: Bitmap) = TODO("Not yet implemented")
        override fun decodeDataImageUrl(dataUrl: String) = TODO("Not yet implemented")
        override fun decodePng(png: ByteArray) = TODO("Not yet implemented")
        override fun measureText(text: String, font: Font) = TODO("Not yet implemented")
        override fun dispose() = TODO("Not yet implemented")
    }

    val svgCanvasPeer = SvgCanvasPeer(canvasPeer)

    //val rootMapper = SvgSvgElementMapper(svgDocument, SvgSkiaPeer(fontManager))
    val rootMapper = SvgSvgElementMapper(svgDocument, svgCanvasPeer)
    rootMapper.attachRoot(MappingContext())
    return rootMapper.target
}

internal fun svgDocument(
    x: Number? = null,
    y: Number? = null,
    width: Number? = null,
    height: Number? = null,
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
    x: Number? = null,
    y: Number? = null,
    width: Number? = null,
    height: Number? = null,
    id: String? = null,
    config: SvgSvgElement.() -> Unit = {},
): SvgSvgElement {
    val el = svgDocument(x, y, width, height, config)

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
    strokeOpacity: Number? = null,
    strokeWidth: Number? = null,
    fill: SvgColor? = null,
    fillOpacity: Number? = null,
    pathData: SvgPathData? = null,
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

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.text(
    text: String? = null,
    x: Number? = null,
    y: Number? = null,
    styleClass: String? = null,
    id: String? = null,
    config: SvgTextElement.() -> Unit = {},
): SvgTextElement {
    val el = SvgTextElement()
    id?.let { el.id().set(it) }
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    text?.let { el.setTextNode(it) }
    styleClass?.let { el.addClass(it) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun translate(x: Number, y: Number): SvgTransform {
    return SvgTransformBuilder().translate(x.toDouble(), y.toDouble()).build()
}

internal inline fun <reified T> Container.element(id: String): T {
    return element(id) as T
}

internal fun Container.element(id: String): Element {
    return breadthFirstTraversal(this).first { it.id == id }
}
