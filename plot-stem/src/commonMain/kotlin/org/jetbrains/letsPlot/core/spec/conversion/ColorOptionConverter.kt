/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors

open class ColorOptionConverter : Function<Any?, Color?> {
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

class NamedSystemColorOptionConverter(private val namedSystemColors: NamedSystemColors): ColorOptionConverter() {
    override fun apply(value: Any?): Color? {
        if (value is String && NamedSystemColors.isSystemColorName(value)) {
            return namedSystemColors.getColor(value)
        }
        return super.apply(value)
    }
}