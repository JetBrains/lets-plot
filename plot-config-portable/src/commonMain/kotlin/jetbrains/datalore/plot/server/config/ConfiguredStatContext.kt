/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class ConfiguredStatContext(
    private val dataFrames: List<DataFrame>,
    private val scaleByAes: TypedScaleMap
) : StatContext {

    private fun overallRange(variable: DataFrame.Variable, dataFrames: List<DataFrame>): ClosedRange<Double>? {
        var range: ClosedRange<Double>? = null
        for (dataFrame in dataFrames) {
            if (dataFrame.has(variable)) {
                range = SeriesUtil.span(range, dataFrame.range(variable))
            }
        }
        return range
    }

    override fun overallXRange(): ClosedRange<Double>? {
        return overallRange(Aes.X)
    }

    override fun overallYRange(): ClosedRange<Double>? {
        return overallRange(Aes.Y)
    }

    private fun overallRange(aes: Aes<*>): ClosedRange<Double>? {
        val transformVar = DataFrameUtil.transformVarFor(aes)

        val undefinedLimits = Pair(Double.NaN, Double.NaN)
        val transformedLimits: Pair<Double, Double> = if (scaleByAes.containsKey(aes)) {
            val scale = scaleByAes[aes]
            if (scale.isContinuousDomain) {
                ScaleUtil.transformedDefinedLimits(scale)
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
            ClosedRange(ends.first, ends.second)
        }
    }
}
