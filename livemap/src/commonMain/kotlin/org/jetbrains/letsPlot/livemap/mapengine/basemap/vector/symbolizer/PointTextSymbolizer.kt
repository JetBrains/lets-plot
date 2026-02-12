package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Style
import org.jetbrains.letsPlot.livemap.core.util.Geometries
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature

internal class PointTextSymbolizer internal constructor(
    private val myStyle: Style,
    private val symbolizerContext: SymbolizerContext
) : Symbolizer {
    private val font: Font
    private val wrapWidth = myStyle.wrapWidth
    private val labelField = myStyle.labelField ?: LABEL

    init {
        val fontSize = myStyle.size ?: 10.0
        val fontFamily = myStyle.fontFamily ?: Font.DEFAULT_FAMILY

        val fontStyle = myStyle.fontStyle?.let {
            when (it.contains("italic", ignoreCase = true)) {
                true -> FontStyle.ITALIC
                false -> FontStyle.NORMAL
            }
        } ?: FontStyle.NORMAL

        val fontWeight = myStyle.fontStyle?.let {
            when (it.contains("600|700|800|900|bold".toRegex(RegexOption.IGNORE_CASE))) {
                true -> FontWeight.BOLD
                false -> FontWeight.NORMAL
            }
        } ?: FontWeight.NORMAL

        font = Font(fontStyle, fontWeight, fontSize, fontFamily)
    }

    override fun applyTo(ctx: Context2d) {
        ctx.setFont(font)
        ctx.setTextAlign(TextAlign.CENTER)
        ctx.setTextBaseline(TextBaseline.MIDDLE)
        ctx.setStrokeMiterLimit(0.0)

        Symbolizer.setBaseStyle(ctx, myStyle)
    }

    override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
        val tasks = ArrayList<() -> Unit>()
        feature.tileGeometry.multiPoint
            .map { Geometries.floor(it) } // To make text look sharper
            .let { multiPoint ->
                getLabel(feature)?.let { label ->
                    val visiblePoints = multiPoint//.filter { symbolizerContext.isInsideTile(label, font, it, ctx) }
                    if (visiblePoints.isEmpty()) {
                        return tasks
                    }
                    tasks.add {
                        if (wrapWidth != null && wrapWidth > 0.0 && wrapWidth < symbolizerContext.measureTextWidth(label, font, ctx)) {
                            drawWrapText(ctx, visiblePoints, label, wrapWidth)
                        } else {
                            drawTextFast(ctx, visiblePoints, label)
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

    private fun drawTextFast(ctx: Context2d, multiPoint: List<Vec<*>>, label: String) {
        val width = symbolizerContext.measureTextWidth(label, font, ctx)

        multiPoint.forEach { point ->
            val bbox = bboxFromPoint(point, width, font.fontSize)

            if (!symbolizerContext.intersectsAnyLabel(bbox)) {
                ctx.strokeText(label, point.x, point.y)
                ctx.fillText(label, point.x, point.y)

                symbolizerContext.pinLabel(bbox)
            }
        }
    }

    private fun drawWrapText(ctx: Context2d, multiPoint: List<Vec<*>>, label: String, wrapWidth: Double) {
        var width = wrapWidth
        var words = splitLabel(label)
        var next = ArrayList<String>()
        val rows = ArrayList<String>()

        while (words.isNotEmpty()) {
            while (symbolizerContext.measureTextWidth(words.joinToString(" "), font, ctx) > width && words.size != 1) {
                next.add(0, words.removeAt(words.size - 1))
            }

            if (words.size == 1 && symbolizerContext.measureTextWidth(words[0], font, ctx) > width) {
                rows.add(words[0])
                width = symbolizerContext.measureTextWidth(words[0], font, ctx)
            } else {
                rows.add(words.joinToString(" "))
            }

            words = next
            next = ArrayList()
        }

        multiPoint.forEach { point ->
            val bbox = bboxFromPoint(point, width, font.fontSize)

            if (!symbolizerContext.intersectsAnyLabel(bbox)) {
                rows.forEachIndexed { i, row ->
                    val y = bbox.origin.y + font.fontSize / 2 + font.fontSize * i
                    ctx.strokeText(row, point.x, y)
                    ctx.fillText(row, point.x, y)
                }

                symbolizerContext.pinLabel(bbox)
            }
        }
    }

    private fun getLabel(feature: TileFeature): String? {
        return when (labelField) {
            SHORT -> feature.short
            LABEL -> feature.label
            else -> throw IllegalStateException("Unknown label field: $labelField")
        }
    }

    companion object {
        internal const val SHORT = "short"
        internal const val LABEL = "label"

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
    }
}
