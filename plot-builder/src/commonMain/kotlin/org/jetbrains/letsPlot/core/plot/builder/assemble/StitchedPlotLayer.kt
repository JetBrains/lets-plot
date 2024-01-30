/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer

internal class StitchedPlotLayer constructor(
    private val geomLayers: List<GeomLayer>
) {
    val isYOrientation: Boolean
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].isYOrientation
        }

    fun getDataRange(variable: DataFrame.Variable): DoubleSpan? {
        check(isNumericData(variable)) { "Not numeric data [$variable]" }
        var result: DoubleSpan? = null
        for (layer in geomLayers) {
            val range = layer.dataFrame.range(variable)
            result = SeriesUtil.span(result, range)
        }
        return result
    }

    private fun isNumericData(variable: DataFrame.Variable): Boolean {
        check(geomLayers.isNotEmpty())
        for (layer in geomLayers) {
            if (!layer.dataFrame.isNumeric(variable)) {
                return false
            }
        }
        return true
    }

    internal fun getVariables(): Set<DataFrame.Variable> {
        check(geomLayers.isNotEmpty())
        return geomLayers[0].dataFrame.variables()
    }

    internal fun hasVariable(v: DataFrame.Variable): Boolean {
        check(geomLayers.isNotEmpty())
        return geomLayers[0].dataFrame.has(v)
    }
}
