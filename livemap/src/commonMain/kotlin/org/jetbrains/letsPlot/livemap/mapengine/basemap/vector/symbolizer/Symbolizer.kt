/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Style
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature
import kotlin.math.round

internal interface Symbolizer {
    fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit>
    fun applyTo(ctx: Context2d)

    companion object {
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

        internal fun setBaseStyle(ctx: Context2d, style: Style) {
            style.strokeWidth?.let(ctx::setLineWidth)
            style.fill?.let(ctx::setFillStyle)
            style.stroke?.let(ctx::setStrokeStyle)
        }

        internal fun Context2d.drawLine(line: List<Vec<*>>) {
            moveTo(round(line[0].x), round(line[0].y))

            for (i in 1 until line.size) {
                val point = line[i]
                lineTo(round(point.x), round(point.y))
            }
        }

        private const val LINE = "line"
        private const val POLYGON = "polygon"
        private const val POINT_TEXT = "point-text"
        private const val SHIELD_TEXT = "shield-text"
        private const val LINE_TEXT = "line-text"
    }
}
