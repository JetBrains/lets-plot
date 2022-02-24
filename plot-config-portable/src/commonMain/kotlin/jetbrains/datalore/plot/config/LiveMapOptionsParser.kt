/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.Plot.LAYERS

class LiveMapOptionsParser {
    companion object {

        fun parseFromPlotSpec(plotSpec: Map<String, Any>): Map<*, *>? {
            fun Map<*, *>.isLiveMap(): Boolean = this[Option.Layer.GEOM] == Option.GeomName.LIVE_MAP

            val layers = plotSpec.getMaps(LAYERS)!!
            if (layers.any { it.isLiveMap() }) {
                require(layers.count { it.isLiveMap() } == 1) { "Only one geom_livemap is allowed per plot" }
                require(layers.first().isLiveMap()) { "geom_livemap should be a first geom" }
                return layers.first()
            }

            return null
        }
    }
}
