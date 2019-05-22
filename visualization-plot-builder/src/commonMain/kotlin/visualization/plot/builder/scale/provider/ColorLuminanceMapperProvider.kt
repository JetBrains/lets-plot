package jetbrains.datalore.visualization.plot.builder.scale.provider

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Transform
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil
import jetbrains.datalore.visualization.plot.builder.scale.GuideMapper

class ColorLuminanceMapperProvider(start: Double?, end: Double?, naValue: Color) : HSVColorMapperProvider(naValue) {

    private val myLowHSV: DoubleArray
    private val myHighHSV: DoubleArray

    init {
        val value0 = start ?: DEF_START
        val value1 = end ?: DEF_END

        myLowHSV = doubleArrayOf(0.0, 0.0, value0 / 100)
        myHighHSV = doubleArrayOf(0.0, 0.0, value1 / 100)
    }

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Color> {
        val domainValues = DataFrameUtil.distinctValues(data, variable)
        return createDiscreteMapper(domainValues, myLowHSV, myHighHSV)
    }

    override fun createContinuousMapper(data: DataFrame,
                                        variable: DataFrame.Variable,
                                        lowerLimit: Double?,
                                        upperLimit: Double?,
                                        trans: Transform?): GuideMapper<Color> {

        val domain = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans)
        return createContinuousMapper(domain, myLowHSV, myHighHSV)
    }

    companion object {
        // http://docs.ggplot2.org/current/scale_hue.html
        private const val DEF_START = 20.0
        private const val DEF_END = 80.0
    }
}
