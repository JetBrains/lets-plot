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
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class ConfiguredStatContext(
    private val myDataFrames: List<DataFrame>,
    private val myScaleProviderMap: TypedScaleProviderMap
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

        var domainLimits: ClosedRange<Double>? = null
        if (myScaleProviderMap.containsKey(aes)) {
            // We only need to access 'limits' so no 'real' data required
            val emptyData = DataFrame.Builder()
                    .putNumeric(TransformVar.X, ArrayList())
                    .putNumeric(TransformVar.Y, ArrayList())
                    .build()
            val scale = myScaleProviderMap[aes].createScale(emptyData, variable)
            if (scale.isContinuousDomain && scale.hasDomainLimits()) {
                domainLimits = scale.domainLimits
                if (SeriesUtil.isFinite(domainLimits)) {
                    return domainLimits
                }
            }
        }

        var range = overallRange(variable, myDataFrames)
        if (domainLimits != null) {
            val lowerEnd = if (domainLimits.lowerEndpoint().isFinite())
                domainLimits.lowerEndpoint()
            else
                range!!.lowerEndpoint()
            val upperEnd = if (domainLimits.upperEndpoint().isFinite())
                domainLimits.upperEndpoint()
            else
                range!!.upperEndpoint()
            range = ClosedRange.closed(lowerEnd, upperEnd)
        }
        return range
    }
}
