package jetbrains.datalore.visualization.plot.base.scale.breaks

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.visualization.plot.common.text.Formatter
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10

class NumericBreakFormatter(value: Double, step: Double, allowMetricPrefix: Boolean) : Function<Any, String> {
    private var myFormatter: (Any) -> String

    init {
        @Suppress("NAME_SHADOWING")
        var step = step
        if (value == 0.0) {
            // do not proceed because log10(0) = - Infinity
            myFormatter = Formatter.number("#,##0.#", false)
        } else {
            step = abs(step)
            if (step == 0.0) {
                step = value / 10
            }
            val domain10Power = log10(value)
            val step10Power = log10(step)

            var precision = -step10Power
            var scientificNotation = false
            if (domain10Power < 0 && step10Power < -4) {
                scientificNotation = true
                precision = domain10Power - step10Power
            } else if (domain10Power > 7 && step10Power > 2) {
                scientificNotation = true
                precision = domain10Power - step10Power
            }

            if (precision < 0) {
                precision = 0.0
            }
            precision = ceil(precision)

            val sb = StringBuilder()
            var useMetricPrefix = false
            if (scientificNotation) {
                if (domain10Power > 0 && allowMetricPrefix) {
                    useMetricPrefix = true
                    // generate 'engineering notation', in which the exponent is a multiple of three
                    sb.append("##0")
                } else {
                    sb.append("0")
                }
            } else {
                sb.append("#,##0")
            }

            if (precision > 0) {
                sb.append('.')
                var i = 0
                while (i < precision) {
                    sb.append('#')
                    i++
                }
            }

            if (scientificNotation) {
                sb.append("E0")
            }

            val pattern = sb.toString()
            myFormatter = Formatter.number(pattern, useMetricPrefix)
        }
    }

    override fun apply(value: Any): String {
        return myFormatter(value)
    }
}
