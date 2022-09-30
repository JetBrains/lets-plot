/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.point.PointShape
import jetbrains.datalore.plot.config.Option.Geom
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.Mapping.toOption
import jetbrains.datalore.plot.config.Option.PlotBase
import kotlin.properties.ReadWriteProperty


class LayerOptions : Options<PlotOptions>() {
    var geom: GeomKind? by map(Layer.GEOM)
    var data: Map<String, List<Any?>>? by map(PlotBase.DATA)
    var mappings: Map<Aes<*>, String>? by map(PlotBase.MAPPING)
    var tooltipsOptions: TooltipsOptions? by map(Layer.TOOLTIPS)
    var samplingOptions: SamplingOptions? by map(Layer.SAMPLING)
    var stat: String? by map(Layer.STAT)
    var position: String? by map(Layer.POS)
    var sizeUnit: Aes<*>? by map(Geom.Text.SIZE_UNIT)
    var showLegend: Boolean? by map(Layer.SHOW_LEGEND)
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
    var symX: Double? by map(Aes.SYM_X)
    var symY: Double? by map(Aes.SYM_Y)

    inline operator fun <reified T> get(aes: Aes<T>): T = properties[toOption(aes)] as T
    operator fun <T> set(aes: Aes<T>, v: T) { properties[toOption(aes)] = v }

    fun <T> setParameter(name: String, v: T) { properties[name] = v }

    private inline fun <T, reified TValue> T.map(key: Aes<*>): ReadWriteProperty<T, TValue?> = map(toOption(key))
}

fun layer(block: LayerOptions.() -> Unit) = LayerOptions().apply(block)
