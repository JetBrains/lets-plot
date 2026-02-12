package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Style
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer.Symbolizer.Companion.drawLine

internal class PolygonSymbolizer internal constructor(private val myStyle: Style) : Symbolizer {

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
        Symbolizer.Companion.setBaseStyle(ctx, myStyle)
    }
}