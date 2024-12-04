/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.Option

object VegaConfig {
    fun isVegaLiteSpec(opts: Map<String, Any>): Boolean {
        return VegaOption.DATA in opts && Option.Meta.KIND !in opts
    }

    fun transform(map: MutableMap<String, Any?>): MutableMap<String, Any> {
        return VegaPlotConverter.convert(map)
    }

    internal fun getPlotKind(opts: Map<*, *>): VegaPlotKind {
        //if (!isVegaLiteSpec(opts)) {
        //    throw IllegalArgumentException("Not a Vega-Lite spec")
        //}

        return when {
            VegaOption.LAYER in opts -> VegaPlotKind.MULTI_LAYER
            VegaOption.MARK in opts -> VegaPlotKind.SINGLE_LAYER

            VegaOption.FACET in opts -> throw IllegalArgumentException("VegaLite: Facet is not supported")
            VegaOption.REPEAT in opts -> throw IllegalArgumentException("VegaLite: Repeat is not supported")
            VegaOption.VCONCAT in opts -> throw IllegalArgumentException("VegaLite: VConcat is not supported")
            VegaOption.HCONCAT in opts -> throw IllegalArgumentException("VegaLite: HConcat is not supported")
            VegaOption.CONCAT in opts -> throw IllegalArgumentException("VegaLite: Concat is not supported")

            VegaOption.CONFIG in opts -> throw IllegalArgumentException("VegaLite: Config is not supported")
            else -> throw IllegalArgumentException("VegaLite: Unknown plot kind. No 'mark' or 'layer' found.")
        }
    }
}
