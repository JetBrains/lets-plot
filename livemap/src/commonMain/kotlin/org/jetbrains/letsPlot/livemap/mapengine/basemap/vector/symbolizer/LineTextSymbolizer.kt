package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.gis.tileprotocol.mapConfig.Style
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature

internal class LineTextSymbolizer internal constructor(
    @Suppress("UNUSED_PARAMETER") style: Style,
    @Suppress("UNUSED_PARAMETER") labelBounds: List<DoubleRectangle>
) : Symbolizer {

    override fun createDrawTasks(ctx: Context2d, feature: TileFeature): List<() -> Unit> {
        return emptyList()
    }

    override fun applyTo(ctx: Context2d) {

    }
}