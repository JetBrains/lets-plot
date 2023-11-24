/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.util

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import org.jetbrains.letsPlot.core.spec.Option.Geom
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.PlotBase


class LayerOptions : Options() {
    var geom: GeomKind? by map(Layer.GEOM)
    var data: Map<String, List<Any?>>? by map(PlotBase.DATA)
    var mappings: Map<Aes<*>, String>? by map(PlotBase.MAPPING)
    var tooltipsOptions: TooltipsOptions? by map(Layer.TOOLTIPS)
    var samplingOptions: SamplingOptions? by map(Layer.SAMPLING)
    var stat: String? by map(Layer.STAT)
    var showLegend: Boolean? by map(Layer.SHOW_LEGEND)
    var position: String? by map(Layer.POS)

    // geom_text
    var sizeUnit: Aes<*>? by map(Geom.Text.SIZE_UNIT)
    var naText: String? by map(Geom.Text.NA_TEXT)
    var labelFormat: String? by map(Geom.Text.LABEL_FORMAT)

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

    fun <T> setParameter(name: String, v: T) { properties[name] = v }
}

fun layer(block: LayerOptions.() -> Unit) = LayerOptions().apply(block)
