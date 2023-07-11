/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors

class ColorOptionConverter : Function<Any?, Color?> {
    override fun apply(value: Any?): Color? {
        if (value == null) {
            return null
        }
        if (value is Color) {
            return value
        }
        if (value is Number) {
            return TypedContinuousIdentityMappers.COLOR(value.toDouble())
        }

        try {
            return Colors.parseColor(value.toString())
        } catch (ignored: RuntimeException) {
            throw IllegalArgumentException("Can't convert to color: '$value' (${value::class.simpleName})")
        }
    }
}
