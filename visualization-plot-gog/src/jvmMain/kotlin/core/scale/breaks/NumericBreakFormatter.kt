package jetbrains.datalore.visualization.plot.gog.core.scale.breaks

import jetbrains.datalore.visualization.plot.gog.common.text.Formatter

import java.util.function.Function

class NumericBreakFormatter(value: Double, step: Double, allowMetricPrefix: Boolean) : Function<Any, String> {
    private var myFormatter: Function<Any, String>

    init {
        var step = step
        if (value == 0.0) {
            // do not proceed because log10(0) = - Infinity
            myFormatter = Formatter.number("#,##0.#", false)
        } else {
            step = Math.abs(step)
            if (step == 0.0) {
                step = value / 10
            }
            val domain10Power = Math.log10(value)
            val step10Power = Math.log10(step)

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
            precision = Math.ceil(precision)

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

    override fun apply(o: Any): String {
        return myFormatter.apply(o)
    }
}
