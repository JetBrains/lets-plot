/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import jetbrains.datalore.plot.config.Option.Geom
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.PlotBase


class LayerOptions : Options() {
    var geom: GeomKind? by map(Layer.GEOM)
    var data: Map<String, List<Any?>>? by map(PlotBase.DATA)
    var mappings: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, String>? by map(PlotBase.MAPPING)
    var tooltipsOptions: TooltipsOptions? by map(Layer.TOOLTIPS)
    var samplingOptions: SamplingOptions? by map(Layer.SAMPLING)
    var stat: String? by map(Layer.STAT)
    var showLegend: Boolean? by map(Layer.SHOW_LEGEND)
    var position: String? by map(Layer.POS)

    // geom_text
    var sizeUnit: org.jetbrains.letsPlot.core.plot.base.Aes<*>? by map(Geom.Text.SIZE_UNIT)
    var naText: String? by map(Geom.Text.NA_TEXT)
    var labelFormat: String? by map(Geom.Text.LABEL_FORMAT)

    // Aes

    var x: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.X)
    var y: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.Y)
    var z: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.Z)
    var color: Any? by map(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR)
    var fill: Any? by map(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)
    var alpha: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA)
    var shape: PointShape? by map(org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE)
    var linetype: LineType? by map(org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE)
    var size: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.SIZE)
    var stroke: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.STROKE)
    var linewidth: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.LINEWIDTH)
    var width: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH)
    var height: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.HEIGHT)
    var violinwidth: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.VIOLINWIDTH)
    var weight: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.WEIGHT)
    var intercept: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.INTERCEPT)
    var slope: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.SLOPE)
    var xintercept: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.XINTERCEPT)
    var yintercept: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.YINTERCEPT)
    var lower: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.LOWER)
    var middle: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE)
    var upper: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.UPPER)
    var sample: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.SAMPLE)
    var quantile: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.QUANTILE)
    var xmin: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.XMIN)
    var xmax: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.XMAX)
    var ymin: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.YMIN)
    var ymax: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.YMAX)
    var xend: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.XEND)
    var yend: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.YEND)
    var mapId: Any? by map(org.jetbrains.letsPlot.core.plot.base.Aes.MAP_ID)
    var frame: String? by map(org.jetbrains.letsPlot.core.plot.base.Aes.FRAME)
    var speed: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.SPEED)
    var flow: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.FLOW)
    var label: Any? by map(org.jetbrains.letsPlot.core.plot.base.Aes.LABEL)
    var family: String? by map(org.jetbrains.letsPlot.core.plot.base.Aes.FAMILY)
    var fontface: String? by map(org.jetbrains.letsPlot.core.plot.base.Aes.FONTFACE)
    var lineheight: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.LINEHEIGHT)
    var hjust: Any? by map(org.jetbrains.letsPlot.core.plot.base.Aes.HJUST)
    var vjust: Any? by map(org.jetbrains.letsPlot.core.plot.base.Aes.VJUST)
    var angle: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.ANGLE)
    var slice: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.SLICE)
    var explode: Double? by map(org.jetbrains.letsPlot.core.plot.base.Aes.EXPLODE)

    fun <T> setParameter(name: String, v: T) { properties[name] = v }
}

fun layer(block: LayerOptions.() -> Unit) = LayerOptions().apply(block)
