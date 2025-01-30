/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase


class PlotOptions : Options(
    mutableMapOf(Meta.KIND to Meta.Kind.PLOT)
) {
    var data: Map<String, List<Any?>>? by map(PlotBase.DATA)
    var mappings: Map<Aes<*>, String>? by map(PlotBase.MAPPING)
    var dataMeta: DataMetaOptions? by map(Meta.DATA_META)
    var layerOptions: List<LayerOptions>? by map(Plot.LAYERS)
    var scaleOptions: List<ScaleOptions>? by map(Plot.SCALES)
    var guides: Map<Aes<*>, GuideOptions>? by map(Plot.GUIDES)
    var title: String? by map(Plot.TITLE)
    var coord: CoordOptions? by map(Plot.COORD)
    var themeOptions: ThemeOptions? by map(Plot.THEME)
    var size: Size? by map(Plot.SIZE)
    var computationMessages: List<String>? by map(Plot.COMPUTATION_MESSAGES)

    class Size : Options() {
        var width: Int? by map(Plot.WIDTH)
        var height: Int? by map(Plot.HEIGHT)
    }

    fun appendLayer(block: LayerOptions.() -> Unit) {
        appendLayer(LayerOptions().apply(block))
    }

    fun appendLayer(layerOptions: LayerOptions) {
        this.layerOptions = (this.layerOptions ?: emptyList()) + layerOptions
    }

    companion object {
        fun size(block: Size.() -> Unit) = Size().apply(block)
    }
}

fun plot(block: PlotOptions.() -> Unit) = PlotOptions().apply(block)
