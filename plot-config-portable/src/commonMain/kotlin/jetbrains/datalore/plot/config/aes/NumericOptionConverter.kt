package jetbrains.datalore.plot.config.aes

import jetbrains.datalore.base.function.Function

internal class NumericOptionConverter : Function<Any?, Double?> {
    override fun apply(value: Any?): Double? {
        if (value == null) {
            return null
        }
        if (value is Number) {
            return value as? Double ?: value.toDouble()
        }
        try {
            return value.toString().toDouble()
        } catch (ignored: NumberFormatException) {
            throw IllegalArgumentException("Can't convert to number: '$value'")
        }

    }
}
