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
        val variable = DataFrameUtil.transformVarFor(aes)

        var scaleLimits: ClosedRange<Double>? = null
        if (scaleByAes.containsKey(aes)) {
            // We only need to access 'limits' so no 'real' data required
//            val emptyData = DataFrame.Builder()
//                .putNumeric(TransformVar.X, ArrayList())
//                .putNumeric(TransformVar.Y, ArrayList())
//                .build()
//            val scale = scaleByAes[aes].createScale(emptyData, variable)
            val scale = scaleByAes[aes]
            if (scale.isContinuousDomain && scale.hasDomainLimits()) {
                scaleLimits = scale.domainLimits!!
                if (SeriesUtil.isFinite(scaleLimits)) {
                    return scaleLimits
                }
            }
        }

        var dataRange = overallRange(variable, dataFrames)
        return if (scaleLimits == null) {
            dataRange
        } else if (dataRange == null) {
            scaleLimits
        } else {
            val lower = if (scaleLimits.lowerEnd.isFinite()) scaleLimits.lowerEnd else dataRange.lowerEnd
            val upper = if (scaleLimits.upperEnd.isFinite()) scaleLimits.upperEnd else dataRange.upperEnd
            ClosedRange(lower, upper)
        }
    }
}
