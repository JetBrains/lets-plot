/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.commons.intern.function.Function

internal class NumericOptionConverter : Function<Any?, Double?> {
    override fun apply(value: Any?): Double? {
        if (value == null) {
            return null
        }
        if (value is Number) {
            return value.toDouble()
        }
        try {
            return value.toString().toDouble()
        } catch (ignored: NumberFormatException) {
            throw IllegalArgumentException("Can't convert to number: '$value'")
        }

    }
}
