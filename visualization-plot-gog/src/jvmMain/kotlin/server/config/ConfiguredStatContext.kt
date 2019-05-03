package jetbrains.datalore.visualization.plot.gog.server.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.data.StatContext
import jetbrains.datalore.visualization.plot.gog.core.data.TransformVar
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.assemble.TypedScaleProviderMap
import java.util.*

internal class ConfiguredStatContext(private val myDataFrames: List<DataFrame>, private val myScaleProviderMap: TypedScaleProviderMap) : StatContext {
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
            val lowerEnd = if (java.lang.Double.isFinite(domainLimits.lowerEndpoint()))
                domainLimits.lowerEndpoint()
            else
                range!!.lowerEndpoint()
            val upperEnd = if (java.lang.Double.isFinite(domainLimits.upperEndpoint()))
                domainLimits.upperEndpoint()
            else
                range!!.upperEndpoint()
            range = ClosedRange.closed(lowerEnd, upperEnd)
        }
        return range
    }
}
