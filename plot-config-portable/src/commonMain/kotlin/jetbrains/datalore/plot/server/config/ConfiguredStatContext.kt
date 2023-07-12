/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

internal class ConfiguredStatContext(
    private val dataFrames: List<DataFrame>,
    private val transformByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Transform>,
    private val mappedStatVariables: List<DataFrame.Variable>
) : StatContext {

    private fun overallRange(variable: DataFrame.Variable, dataFrames: List<DataFrame>): DoubleSpan? {
        var range: DoubleSpan? = null
        for (dataFrame in dataFrames) {
            if (dataFrame.has(variable)) {
                range = SeriesUtil.span(range, dataFrame.range(variable))
            }
        }
        return range
    }

    override fun overallXRange(): DoubleSpan? {
        return overallRange(org.jetbrains.letsPlot.core.plot.base.Aes.X)
    }

    override fun overallYRange(): DoubleSpan? {
        return overallRange(org.jetbrains.letsPlot.core.plot.base.Aes.Y)
    }

    override fun mappedStatVariables(): List<DataFrame.Variable> {
        return mappedStatVariables
    }

    private fun overallRange(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DoubleSpan? {
        val transformVar = DataFrameUtil.transformVarFor(aes)

        val undefinedLimits = Pair(Double.NaN, Double.NaN)
        val transformedLimits: Pair<Double, Double> = if (transformByAes.containsKey(aes)) {
            val transform = transformByAes.getValue(aes)
            if (transform is ContinuousTransform) {
                ScaleUtil.transformedDefinedLimits(transform)
            } else {
                undefinedLimits
            }
        } else {
            undefinedLimits
        }

        val (limitsLower, limitsUpper) = transformedLimits
        val dataRange = overallRange(transformVar, dataFrames)
        val ends = if (dataRange != null) {
            val lower = if (limitsLower.isFinite()) limitsLower else dataRange.lowerEnd
            val upper = if (limitsUpper.isFinite()) limitsUpper else dataRange.upperEnd
            (lower to upper)
        } else if (SeriesUtil.allFinite(limitsLower, limitsUpper)) {
            (limitsLower to limitsUpper)
        } else {
            null
        }

        return ends?.let {
            DoubleSpan(ends.first, ends.second)
        }
    }
}
