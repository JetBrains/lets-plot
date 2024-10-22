/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.getMaps

object VegaConfig {
    fun isVegaLiteSpec(opts: Map<String, Any>): Boolean {
        if (VegaOption.DATA !in opts) return false
        if (VegaOption.LAYER !in opts && VegaOption.MARK !in opts) return false
        if (VegaOption.LAYER in opts && opts.getMaps(VegaOption.LAYER)!!.any { VegaOption.MARK !in it }) return false

        return true
    }

    fun transform(map: MutableMap<String, Any>): MutableMap<String, Any> {
        return VegaPlotConverter.convert(map)
    }

    internal fun getPlotKind(opts: VegaSpecProp): VegaPlotKind {
        //if (!isVegaLiteSpec(opts)) {
        //    throw IllegalArgumentException("Not a Vega-Lite spec")
        //}

        return when {
            VegaOption.LAYER in opts -> VegaPlotKind.MULTI_LAYER
            VegaOption.MARK in opts -> VegaPlotKind.SINGLE_LAYER
            VegaOption.FACET in opts || VegaOption.REPEAT in opts -> VegaPlotKind.FACETED
            else -> throw IllegalArgumentException("VegaLite: Unsupported plot kind")
        }
    }
}
