package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.visualization.plot.base.scale.Mappers
import kotlin.math.abs

object ColorMapper {
    val NA_VALUE = Color.GRAY
    // http://docs.ggplot2.org/current/scale_gradient.html
    val DEF_GRADIENT_LOW = Color.parseHex("#132B43")
    val DEF_GRADIENT_HIGH = Color.parseHex("#56B1F7")

    fun gradientDefault(domain: ClosedRange<Double>): (Double?) -> Color {
        return gradient(domain, DEF_GRADIENT_LOW, DEF_GRADIENT_HIGH, NA_VALUE)
    }

    fun gradient(domain: ClosedRange<Double>, low: Color, high: Color, naColor: Color): (Double?) -> Color {
        return gradientHSV(domain, Colors.hsvFromRgb(low), Colors.hsvFromRgb(high), true, naColor)
    }

    fun gradientHSV(
            domain: ClosedRange<Double>, lowHSV: DoubleArray, highHSV: DoubleArray, autoHueDirection: Boolean, naColor: Color): (Double?) -> Color {

        var lowHue = lowHSV[0]
        var highHue = highHSV[0]

        val lowS = lowHSV[1]
        val highS = highHSV[1]

        // No hue if saturation is near zero
        if (lowS < 0.0001) {
            lowHue = highHue
        }
        if (highS < 0.0001) {
            highHue = lowHue
        }

        if (autoHueDirection) {
            val dH = abs(highHue - lowHue)
            if (dH > 180) {
                if (highHue >= lowHue) {
                    lowHue += 360.0
                } else {
                    highHue += 360.0
                }
            }
        }

        val mapperH = Mappers.linear(domain, lowHue, highHue, Double.NaN)
        val mapperS = Mappers.linear(domain, lowS, highS, Double.NaN)
        val mapperV = Mappers.linear(domain, lowHSV[2], highHSV[2], Double.NaN)

        return { input ->
            if (input == null || !domain.contains(input)) {
                naColor
            } else {
                val H = mapperH(input) % 360
                val S = mapperS(input)
                val V = mapperV(input)
                Colors.rgbFromHsv(H, S, V)
            }
        }
    }
}
