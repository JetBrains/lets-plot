/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.getMaps
import kotlin.collections.contains

object VegaConfig {
    fun isVegaLiteSpec(opts: Map<String, Any>): Boolean {
        if (Option.DATA !in opts) return false
        if (Option.LAYER !in opts && Option.MARK !in opts) return false
        if (Option.LAYER in opts && opts.getMaps(Option.LAYER)!!.any { Option.MARK !in it }) return false

        return true
    }

    fun transform(map: MutableMap<String, Any>): MutableMap<String, Any> {
        return Transform.transform(map)
    }

    internal fun getPlotKind(opts: Map<String, Any>): VegaPlotKind {
        if (!isVegaLiteSpec(opts)) {
            throw IllegalArgumentException("Not a Vega-Lite spec")
        }

        return when {
            Option.LAYER in opts -> VegaPlotKind.MULTI_LAYER
            Option.MARK in opts -> VegaPlotKind.SINGLE_LAYER
            Option.FACET in opts || Option.REPEAT in opts -> VegaPlotKind.FACETED
            else -> throw IllegalArgumentException("VegaLite: Unsupported plot kind")
        }
    }

}