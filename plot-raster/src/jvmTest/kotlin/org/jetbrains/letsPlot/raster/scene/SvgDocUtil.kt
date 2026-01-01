/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import kotlin.math.roundToInt


internal object MeasuringCanvas : Canvas {
    override val context2d: Context2d = MeasuringContext2D.INSTANCE
    override val size: Vector get() = Vector(1, 1)
    override fun takeSnapshot() = error("Not supported")

    internal class MeasuringContext2D private constructor(
        private val state: ContextStateDelegate = ContextStateDelegate()
    ) : Context2d by state {
        override fun measureTextWidth(str: String): Double {
            return (str.length.toDouble() * state.getFont().fontSize * 0.6).roundToInt().toDouble()
        }

        override fun measureText(str: String): TextMetrics {
            val width = measureTextWidth(str)
            val fontSize = state.getFont().fontSize
            return TextMetrics(
                ascent = fontSize * 0.8,
                descent = fontSize * 0.2,
                bbox = DoubleRectangle.XYWH(x = 0.0, y = -fontSize * 0.8, width = width, height = fontSize)
            )
        }

        companion object {
            val INSTANCE = MeasuringContext2D()
        }
    }
}

internal fun mapSvg(textMeasuringCanvas: Canvas = MeasuringCanvas, builder: () -> SvgSvgElement): Pane {
    //val fontManager = FontManager()
    val svgDocument = builder()

    // attach root
    SvgNodeContainer(svgDocument)

    val canvasPeer = object : CanvasPeer {
        override fun createCanvas(size: Vector) = error("Not supported")
        override fun createCanvas(size: Vector, contentScale: Double): Canvas = error("Not supported")

        override fun createSnapshot(bitmap: Bitmap) = error("Not supported")
        override fun decodeDataImageUrl(dataUrl: String) = error("Not supported")
        override fun decodePng(png: ByteArray) = error("Not supported")
    }

    val svgCanvasPeer = SvgCanvasPeer(canvasPeer, textMeasuringCanvas)

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

internal fun SvgNode.style(
    cssText: String,
    id: String? = null,
    config: SvgStyleElement.() -> Unit = {},
): SvgStyleElement {
    val el = SvgStyleElement(object : SvgCssResource {
        override fun css(): String {
            return cssText
        }
    })

    id?.let { el.id().set(it) }
    el.apply(config)
    children().add(el)
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
    strokeWidth: Number? = null,
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
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }
    fill?.let { el.fill().set(it) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgNode.text(
    text: String? = null,
    x: Number? = null,
    y: Number? = null,
    fill: SvgColor? = null,
    stroke: SvgColor? = null,
    strokeWidth: Number? = null,
    anchor: String? = null,
    styleClass: String? = null,
    id: String? = null,
    config: SvgTextElement.() -> Unit = {},
): SvgTextElement {
    val el = SvgTextElement()
    id?.let { el.id().set(it) }
    text?.let { el.setTextNode(it) }
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    fill?.let { el.fill().set(it) }
    stroke?.let { el.stroke().set(it) }
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }
    anchor?.let { el.textAnchor().set(it) }
    styleClass?.let { el.addClass(it) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun SvgTextElement.tspan(
    text: String? = null,
    x: Number? = null,
    y: Number? = null,
    fill: SvgColor? = null,
    stroke: SvgColor? = null,
    strokeWidth: Number? = null,
    id: String? = null,
    config: SvgTSpanElement.() -> Unit = {},
): SvgTSpanElement {
    val el = SvgTSpanElement()
    id?.let { el.id().set(it) }
    x?.let { el.x().set(it.toDouble()) }
    y?.let { el.y().set(it.toDouble()) }
    text?.let { el.setText(it) }
    fill?.let { el.fill().set(it) }
    stroke?.let { el.stroke().set(it) }
    strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }

    el.apply(config)
    children().add(el)
    return el
}

internal fun translate(x: Number, y: Number): SvgTransform {
    return SvgTransformBuilder().translate(x.toDouble(), y.toDouble()).build()
}

internal inline fun <reified T> Container.findElement(id: String): T {
    return findElement(id) as T
}

internal fun Container.findElement(id: String): Node {
    return breadthFirstTraversal(this).first { it.id == id }
}


internal fun withTextMeasurer(measurer: (String, ContextStateDelegate) -> TextMetrics) : SvgCanvasPeer {
    val ctx = object : ContextStateDelegate(contentScale = 1.0) {
        override fun measureText(str: String): TextMetrics = measurer(str, this)
    }

    val canvas: Canvas = object : Canvas {
        override val context2d: Context2d = ctx
        override val size get() = TODO("Not yet implemented")
        override fun takeSnapshot() = TODO("Not yet implemented")
    }

    val canvasPeer: CanvasPeer = object : CanvasPeer {
        override fun createCanvas(size: Vector): Canvas = canvas
        override fun createCanvas(size: Vector, contentScale: Double) = TODO("Not yet implemented")
        override fun createSnapshot(bitmap: Bitmap) = TODO("Not yet implemented")
        override fun decodeDataImageUrl(dataUrl: String) = TODO("Not yet implemented")
        override fun decodePng(png: ByteArray) = TODO("Not yet implemented")
    }

    val svgCanvasPeer = SvgCanvasPeer(canvasPeer)
    return svgCanvasPeer
}
