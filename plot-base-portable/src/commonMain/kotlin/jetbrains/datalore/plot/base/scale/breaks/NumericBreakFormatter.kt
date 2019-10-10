package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.numberFormat.NumberFormat
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10

class NumericBreakFormatter(value: Double, step: Double, allowMetricPrefix: Boolean) : Function<Any, String> {
    private var myFormatter: NumberFormat

    init {
        @Suppress("NAME_SHADOWING")
        var step = step
        var type = "f"
        var delimiter = ""
        if (value == 0.0) {
            // do not proceed because log10(0) = - Infinity
            myFormatter = NumberFormat("d")
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
                type = "e"
                precision = domain10Power - step10Power
            } else if (domain10Power > 7 && step10Power > 2) {
                scientificNotation = true
                precision = domain10Power - step10Power
            }

            if (precision < 0) {
                precision = 0.0
                type = "d"
            }
            precision = ceil(precision)

            if (scientificNotation) {
                type = if (domain10Power > 0 && allowMetricPrefix) {
                    // generate 'engineering notation', in which the exponent is a multiple of three
                    "s"
                } else {
                    "e"
                }
            } else {
                delimiter = ","
            }

            myFormatter = NumberFormat("$delimiter.${precision.toInt()}$type")
        }
    }

    override fun apply(value: Any): String = myFormatter.apply(value as Number)
}
