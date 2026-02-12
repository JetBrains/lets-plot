package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiLineString
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Style
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer.Symbolizer.Companion.drawLine

internal class LineSymbolizer internal constructor(private val myStyle: Style) : Symbolizer {

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
        myStyle.lineDashOffset?.let(ctx::setLineDashOffset)
    }

    companion object {
        fun stringToLineJoin(join: String): LineJoin {
            return when (join) {
                BEVEL -> LineJoin.BEVEL
                ROUND -> LineJoin.ROUND
                MITER -> LineJoin.MITER
                else -> error("Unknown lineJoin type: $join")
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

        private const val BUTT = "butt"
        private const val ROUND = "round"
        private const val SQUARE = "square"
        private const val MITER = "miter"
        private const val BEVEL = "bevel"
    }
}