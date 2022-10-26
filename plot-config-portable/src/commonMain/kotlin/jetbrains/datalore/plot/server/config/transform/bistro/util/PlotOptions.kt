/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.PlotBase


class PlotOptions : Options(
    mutableMapOf(Meta.KIND to Meta.Kind.PLOT)
) {
    var data: Map<String, List<Any>>? by map(PlotBase.DATA)
    var mappings: Map<String, String>? by map(PlotBase.MAPPING)
    var layerOptions: List<LayerOptions>? by map(Plot.LAYERS)
    var scaleOptions: List<ScaleOptions>? by map(Plot.SCALES)
    var title: String? by map(Plot.TITLE)
    var coord: CoordOptions? by map(Plot.COORD)
    var themeOptions: ThemeOptions? by map(Plot.THEME)
    var size: Size? by map(Plot.SIZE)

    class Size : Options() {
        var width: Int? by map(Plot.WIDTH)
        var height: Int? by map(Plot.HEIGHT)
    }

    companion object {
        fun size(block: Size.() -> Unit) = Size().apply(block)
    }
}

fun plot(block: PlotOptions.() -> Unit) = PlotOptions().apply(block)
