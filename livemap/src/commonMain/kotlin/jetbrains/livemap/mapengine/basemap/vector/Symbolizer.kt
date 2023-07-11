/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.MultiLineString
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Ring
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.vis.canvas.*
import jetbrains.gis.tileprotocol.mapConfig.Style
import jetbrains.livemap.Client
import kotlin.math.round

internal interface Symbolizer {

    fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit>
    fun applyTo(ctx: Context2d)

    fun Context2d.drawLine(line: List<Vec<*>>) {
        moveTo(round(line[0].x), round(line[0].y))

        for (i in 1 until line.size) {
            val point = line[i]
            lineTo(round(point.x), round(point.y))
        }
    }

    class PolygonSymbolizer internal constructor(private val myStyle: Style) : Symbolizer {

        private fun Context2d.drawMultiPolygon(multiPolygon: MultiPolygon<*>) {
            beginPath()
            for (polygon in multiPolygon) {
                for (ring: Ring<*> in polygon) {
                    drawLine(ring)
                }
            }
        }

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
            val tasks = ArrayList<() -> Unit>()
            feature.tileGeometry.multiPolygon.let {
                tasks.add { ctx.drawMultiPolygon(it) }
                tasks.add(ctx::fill)
            }
            return tasks
        }

        override fun applyTo(ctx: Context2d) {
            setBaseStyle(ctx, myStyle)
        }
    }

    class LineSymbolizer internal constructor(private val myStyle: Style) : Symbolizer {

        private fun Context2d.drawMultiLine(multiLine: MultiLineString<Client>) {
            beginPath()
            multiLine.forEach { drawLine(it) }
        }

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
            val tasks = ArrayList<() -> Unit>()

            feature.tileGeometry.multiLineString.let {
                tasks.add { ctx.drawMultiLine(it) }
                tasks.add(ctx::stroke)
            }

            return tasks
        }

        override fun applyTo(ctx: Context2d) {
            myStyle.stroke?.let(ctx::setStrokeStyle)
            myStyle.strokeWidth?.let(ctx::setLineWidth)

            myStyle.lineCap?.let { ctx.setLineCap(stringToLineCap(it)) }
            myStyle.lineJoin?.let { ctx.setLineJoin(stringToLineJoin(it)) }
            myStyle.lineDash?.let { ctx.setLineDash(it.toDoubleArray()) }
        }
    }

    class PointTextSymbolizer internal constructor(
        private val myStyle: Style,
        private val myLabelBounds: MutableList<DoubleRectangle>
    ) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
            val tasks = ArrayList<() -> Unit>()
            feature.tileGeometry.multiPoint.let { multiPoint ->
                getLabel(feature)?.let { label ->
                    tasks.add {

                        val wrapWidth = myStyle.wrapWidth

                        if (wrapWidth != null && wrapWidth > 0.0 && wrapWidth < ctx.measureText(label)) {
                            ctx.drawWrapText(multiPoint, label, wrapWidth)
                        } else {
                            ctx.drawTextFast(multiPoint, label)
                        }
                    }
                }
            }
            return tasks
        }

        private fun bboxFromPoint(point: Vec<*>, width: Double, height: Double): DoubleRectangle {
            return DoubleRectangle.span(
                DoubleVector(
                    point.x - width / 2,
                    point.y - height / 2
                ),
                DoubleVector(
                    point.x + width / 2,
                    point.y + height / 2
                )
            )
        }

        private fun Context2d.drawTextFast(multiPoint: List<Vec<*>>, label: String) {
            val width = measureText(label)
            val height = myStyle.size ?: 10.0

            multiPoint.forEach { point ->
                val bbox = bboxFromPoint(point, width, height)

                if (!labelInBounds(bbox)) {
                    strokeText(label, point.x, point.y)
                    fillText(label, point.x, point.y)

                    myLabelBounds.add(bbox)
                }
            }
        }

        private fun Context2d.drawWrapText(multiPoint: List<Vec<*>>, label: String, wrapWidth: Double) {
            var width = wrapWidth
            var words = splitLabel(label)
            var next = ArrayList<String>()
            val rows = ArrayList<String>()

            val height = myStyle.size ?: 10.0

            while (words.isNotEmpty()) {
                while (measureText(words.joinToString(" ")) > width && words.size != 1) {
                    next.add(0, words.removeAt(words.size - 1))
                }

                if (words.size == 1 && measureText(words[0]) > width) {
                    rows.add(words[0])
                    width = measureText(words[0])
                } else {
                    rows.add(words.joinToString(" "))
                }

                words = next
                next = ArrayList()
            }

            multiPoint.forEach { point ->
                val bbox = bboxFromPoint(point, width, height)

                if (!labelInBounds(bbox)) {
                    rows.forEachIndexed { i, row ->
                        val y = bbox.origin.y + height / 2 + height * i
                        strokeText(row, point.x, y)
                        fillText(row, point.x, y)
                    }

                    myLabelBounds.add(bbox)
                }
            }
        }

        private fun labelInBounds(bbox: DoubleRectangle): Boolean {
            return myLabelBounds.find(bbox::intersects) != null
        }

        private fun getLabel(feature: TileFeature): String? =
            when (val labelField = myStyle.labelField ?: LABEL) {
                SHORT -> feature.short
                LABEL -> feature.label
                else -> throw IllegalStateException("Unknown label field: $labelField")
            }

        override fun applyTo(ctx: Context2d) {
            ctx.setFont(
                Font(
                    myStyle.fontStyle?.fontStyle,
                    myStyle.fontStyle?.fontWeight,
                    myStyle.size,
                    myStyle.fontFamily
                )
            )
            ctx.setTextAlign(TextAlign.CENTER)
            ctx.setTextBaseline(TextBaseline.MIDDLE)

            setBaseStyle(ctx, myStyle)
        }
    }

    class ShieldTextSymbolizer internal constructor(
        @Suppress("UNUSED_PARAMETER") style: Style,
        @Suppress("UNUSED_PARAMETER") labelBounds: List<DoubleRectangle>
    ) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
            return emptyList()
        }

        override fun applyTo(ctx: Context2d) {

        }
    }

    class LineTextSymbolizer internal constructor(
        @Suppress("UNUSED_PARAMETER") style: Style,
        @Suppress("UNUSED_PARAMETER") labelBounds: List<DoubleRectangle>
    ) : Symbolizer {

        override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
            return emptyList()
        }

        override fun applyTo(ctx: Context2d) {

        }
    }

    companion object {
        private const val BUTT = "butt"
        private const val ROUND = "round"
        private const val SQUARE = "square"
        private const val MITER = "miter"
        private const val BEVEL = "bevel"

        private const val LINE = "line"
        private const val POLYGON = "polygon"
        private const val POINT_TEXT = "point-text"
        private const val SHIELD_TEXT = "shield-text"
        private const val LINE_TEXT = "line-text"
        private const val SHORT = "short"
        private const val LABEL = "label"

        fun create(style: Style, labelBounds: MutableList<DoubleRectangle>): Symbolizer {
            return when (style.type) {
                LINE -> LineSymbolizer(style)
                POLYGON -> PolygonSymbolizer(style)
                POINT_TEXT -> PointTextSymbolizer(style, labelBounds)
                SHIELD_TEXT -> ShieldTextSymbolizer(style, labelBounds)
                LINE_TEXT -> LineTextSymbolizer(style, labelBounds)
                null -> error("Empty symbolizer type.")
                else -> error("Unknown symbolizer type.")
            }
        }

        fun stringToLineCap(cap: String): LineCap {
            return when (cap) {
                BUTT -> LineCap.BUTT
                ROUND -> LineCap.ROUND
                SQUARE -> LineCap.SQUARE
                else -> error("Unknown lineCap type: $cap")
            }
        }

        fun stringToLineJoin(join: String): LineJoin {
            return when (join) {
                BEVEL -> LineJoin.BEVEL
                ROUND -> LineJoin.ROUND
                MITER -> LineJoin.MITER
                else -> error("Unknown lineJoin type: $join")
            }
        }

        fun splitLabel(label: String): MutableList<String> {
            val splitted = ArrayList<String>()
            val characters = "-',.)!?"
            var lastIndex = 0

            for (i in label.indices) {
                if (' ' == label[i]) {
                    if (lastIndex != i) {
                        splitted.add(label.substring(lastIndex, i))
                    }
                    lastIndex = i + 1
                } else if (characters.indexOf(label[i]) != -1) {
                    splitted.add(label.substring(lastIndex, i + 1))
                    lastIndex = i + 1
                }
            }

            if (lastIndex != label.length) {
                splitted.add(label.substring(lastIndex))
            }

            return splitted
        }

        fun setBaseStyle(ctx: Context2d, style: Style) {
            style.strokeWidth?.let(ctx::setLineWidth)
            style.fill?.let(ctx::setFillStyle)
            style.stroke?.let(ctx::setStrokeStyle)
        }
    }
}

private val String.fontStyle: FontStyle
    get() = when (contains("italic", ignoreCase = true)) {
        true -> FontStyle.ITALIC
        false -> FontStyle.NORMAL
    }

private val String.fontWeight: FontWeight
    get() = when (contains("600|700|800|900|bold".toRegex(RegexOption.IGNORE_CASE))) {
        true -> FontWeight.BOLD
        false -> FontWeight.NORMAL
    }
