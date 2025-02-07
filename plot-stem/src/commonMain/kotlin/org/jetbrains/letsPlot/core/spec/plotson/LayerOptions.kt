/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import org.jetbrains.letsPlot.core.spec.Option.Geom
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.PlotBase

open class LayerOptions(
    geomKind: GeomKind? = null
) : Options() {
    var geom: GeomKind? by map(Layer.GEOM)

    init {
        geom = geomKind
    }

    var data: Map<String, List<Any?>>? by map(PlotBase.DATA)
    var mapping: Mapping? by map(PlotBase.MAPPING)
    var dataMeta: DataMetaOptions? by map(Meta.DATA_META)
    var tooltipsOptions: TooltipsOptions? by map(Layer.TOOLTIPS)
    var samplingOptions: SamplingOptions? by map(Layer.SAMPLING)
    var stat: StatOptions? by map(Layer.STAT)
    var showLegend: Boolean? by map(Layer.SHOW_LEGEND)
    var position: PositionOptions? by map(Layer.POS)
    var orientation: String? by map(Layer.ORIENTATION)

    // hidden
    var marginal: Boolean? by map(Layer.MARGINAL)
    var marginSide: String? by map(Layer.Marginal.SIDE)
    var marginSize: Double? by map(Layer.Marginal.SIZE)

    // Aes

    var x: Double? by map(Aes.X)
    var y: Double? by map(Aes.Y)
    var z: Double? by map(Aes.Z)
    var color: Any? by map(Aes.COLOR)
    var fill: Any? by map(Aes.FILL)
    var alpha: Double? by map(Aes.ALPHA)
    var shape: PointShape? by map(Aes.SHAPE)
    var linetype: LineType? by map(Aes.LINETYPE)
    var size: Double? by map(Aes.SIZE)
    var stroke: Double? by map(Aes.STROKE)
    var linewidth: Double? by map(Aes.LINEWIDTH)
    var width: Double? by map(Aes.WIDTH)
    var height: Double? by map(Aes.HEIGHT)
    var violinwidth: Double? by map(Aes.VIOLINWIDTH)
    var weight: Double? by map(Aes.WEIGHT)
    var intercept: Double? by map(Aes.INTERCEPT)
    var slope: Double? by map(Aes.SLOPE)
    var xintercept: Double? by map(Aes.XINTERCEPT)
    var yintercept: Double? by map(Aes.YINTERCEPT)
    var lower: Double? by map(Aes.LOWER)
    var middle: Double? by map(Aes.MIDDLE)
    var upper: Double? by map(Aes.UPPER)
    var sample: Double? by map(Aes.SAMPLE)
    var quantile: Double? by map(Aes.QUANTILE)
    var xmin: Double? by map(Aes.XMIN)
    var xmax: Double? by map(Aes.XMAX)
    var ymin: Double? by map(Aes.YMIN)
    var ymax: Double? by map(Aes.YMAX)
    var xend: Double? by map(Aes.XEND)
    var yend: Double? by map(Aes.YEND)
    var mapId: Any? by map(Aes.MAP_ID)
    var frame: String? by map(Aes.FRAME)
    var speed: Double? by map(Aes.SPEED)
    var flow: Double? by map(Aes.FLOW)
    var label: Any? by map(Aes.LABEL)
    var family: String? by map(Aes.FAMILY)
    var fontface: String? by map(Aes.FONTFACE)
    var lineheight: Double? by map(Aes.LINEHEIGHT)
    var hjust: Any? by map(Aes.HJUST)
    var vjust: Any? by map(Aes.VJUST)
    var angle: Double? by map(Aes.ANGLE)
    var radius: Double? by map(Aes.RADIUS)
    var slice: Double? by map(Aes.SLICE)
    var explode: Double? by map(Aes.EXPLODE)

    fun const(aes: Aes<*>, value: Any?) {
        properties[toOption(aes)] = value
    }
}

class PointLayer : LayerOptions(GeomKind.POINT) {
    var sizeUnit: Aes<*>? by map(SIZE_UNIT)

    companion object {
        val SIZE_UNIT = PropSpec<Aes<*>>(Geom.Point.SIZE_UNIT)
    }
}

class BoxplotLayer : LayerOptions(GeomKind.BOX_PLOT) {
    var whiskerWidth: Double? by map(WHISKER_WIDTH)
    var fatten: Double? by map(FATTEN)

    companion object {
        val WHISKER_WIDTH = PropSpec<Double?>(Geom.Boxplot.WHISKER_WIDTH)
        val FATTEN = PropSpec<Double?>(Geom.Boxplot.FATTEN)
    }
}

class CrossbarLayer : LayerOptions(GeomKind.CROSS_BAR) {
    var fatten: Double? by map(FATTEN)

    companion object {
        val FATTEN = PropSpec<Double?>(Geom.CrossBar.FATTEN)
    }
}

class TextLayer : LayerOptions(GeomKind.TEXT) {
    var naText: String? by map(NA_TEXT.key)
    var labelFormat: String? by map(LABEL_FORMAT.key)
    var sizeUnit: Aes<*>? by map(SIZE_UNIT.key)

    companion object {
        val NA_TEXT = PropSpec<String?>(Geom.Text.NA_TEXT)
        val LABEL_FORMAT = PropSpec<String?>(Geom.Text.LABEL_FORMAT)
        val SIZE_UNIT = PropSpec<Aes<*>>(Geom.Text.SIZE_UNIT)
    }
}

class LiveMapLayer : LayerOptions(GeomKind.LIVE_MAP) {
    var tiles: TileOptions? by map(Geom.LiveMap.TILES)

    class TileOptions : Options() {
        var kind: String? by map(Geom.LiveMap.Tile.KIND)
        var url: String? by map(Geom.LiveMap.Tile.URL)
        var theme: String? by map(Geom.LiveMap.Tile.THEME)
        var attribution: String? by map(Geom.LiveMap.Tile.ATTRIBUTION)

    }

    companion object {
        fun vectorTiles(theme: String = Geom.LiveMap.Tile.THEME_COLOR) = TileOptions().apply {
            kind = Geom.LiveMap.Tile.KIND_VECTOR_LETS_PLOT
            this.theme = theme
            attribution = "<a href=\"https://lets-plot.org\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
            url = "wss://tiles.datalore.jetbrains.com"
        }

        fun liveMap(block: LiveMapLayer.() -> Unit) = LiveMapLayer().apply(block)
    }
}

fun layer(block: LayerOptions.() -> Unit) = LayerOptions().apply(block)
