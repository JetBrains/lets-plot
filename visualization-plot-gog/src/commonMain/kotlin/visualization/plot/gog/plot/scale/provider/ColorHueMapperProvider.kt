package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil
import jetbrains.datalore.visualization.plot.base.scale.Transform
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import kotlin.math.max
import kotlin.math.min

// http://docs.ggplot2.org/current/scale_hue.html
internal class ColorHueMapperProvider(hueRange: List<Double>?, chroma: Double?, luminance: Double?, startHue: Double?, direction: Double?, naValue: Color) : HSVColorMapperProvider(naValue) {

    private val myLowHSV: DoubleArray
    private val myHighHSV: DoubleArray

    init {
        val hr = normalizeHueRange(hueRange)
        val lowHue = hr[0]
        val highHue = hr[1]
        val saturation = if (chroma != null) chroma % 100 else DEF_SATURATION
        val value = if (luminance != null) luminance % 100 else DEF_VALUE
        val hue0 = if (startHue != null) startHue % 360 else DEF_START_HUE
        val clockwise = direction == null || direction != -1.0


        val hue1 = if (clockwise) highHue else lowHue
        myLowHSV = doubleArrayOf(hue0, saturation / 100, value / 100)
        myHighHSV = doubleArrayOf(hue1, saturation / 100, value / 100)
    }

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Color> {
        val domainValues = DataFrameUtil.distinctValues(data, variable)
        val highHSV = adjustHighHue(myLowHSV, myHighHSV, domainValues.size)
        return createDiscreteMapper(domainValues, myLowHSV, highHSV)
    }

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Color> {
        val domain = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans)
        val highHSV = adjustHighHue(myLowHSV, myHighHSV, 12)
        return createContinuousMapper(domain, myLowHSV, highHSV)
    }

    companion object {
        val DEFAULT = ColorHueMapperProvider(null, null, null, null, null, Color.GRAY)

        private const val DEF_SATURATION = 50.0
        private const val DEF_VALUE = 90.0
        private const val DEF_START_HUE = 0.0

        private fun normalizeHueRange(hueRange: List<Double>?): DoubleArray {
            val result = doubleArrayOf(0.0, 360.0)
            if (hueRange != null && hueRange.size == 2) {
                val v0 = hueRange[0] % 360
                val v1 = hueRange[1] % 360
                result[0] = min(v0, v1)
                result[1] = max(v0, v1)
            }
            return result
        }

        private fun adjustHighHue(lowHSV: DoubleArray, highHSV: DoubleArray, colorCount: Int): DoubleArray {
            if (highHSV[0] % 360 == lowHSV[0] % 360) {
                val segment = 360.0 / (colorCount + 1)
                val highHueAdjusted = lowHSV[0] + segment * colorCount
                return doubleArrayOf(highHueAdjusted, highHSV[1], highHSV[2])
            }
            return highHSV
        }
    }
}
