/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.base.geometry.Vector


class PlotOptions private constructor(
    b: Builder
) {
    val data: Map<String, List<Any>>?
    val mappings: Map<String, String>?
    val layerOptions: List<LayerOptions>
    val title: String?
    val coord: CoordOptions?
    val scaleOptions: List<ScaleOptions>
    val themeOptions: ThemeOptions?
    val size: Vector?

    companion object {
        val Empty = Builder().build()
        fun builder() = Builder()
    }

    class Builder(
        var data: Map<String, List<Any>>? = null,
        var mappings: Map<String, String>? = null,
        var title: String? = null,
        var coord: CoordOptions? = null,
        var themeOptions: ThemeOptions? = null,
        var size: Vector? = null
    ) {
        val layerOptions: MutableList<LayerOptions> = mutableListOf<LayerOptions>()
        val scaleOptions: MutableList<ScaleOptions> = mutableListOf<ScaleOptions>()

        fun build(): PlotOptions = PlotOptions(this)
    }

    init {
        data = b.data?.toMap()
        mappings = b.mappings?.toMap()
        layerOptions = b.layerOptions.toList()
        scaleOptions = b.scaleOptions.toList()
        title = b.title
        coord = b.coord
        themeOptions = b.themeOptions
        size = b.size
    }
}
