/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors

class ColorOptionConverter constructor(
    private val pen: Color,
    private val paper: Color,
    private val brush: Color,
) : Function<Any?, Color?> {
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

        val str = value.toString()
        if (SystemColor.canParse(str)) {
            return when (SystemColor.parse(str)) {
                SystemColor.PEN -> pen
                SystemColor.PAPER -> paper
                SystemColor.BRUSH -> brush
            }
        }

        try {
            return Colors.parseColor(str)
        } catch (ignored: RuntimeException) {
            throw IllegalArgumentException("Can't convert to color: '$value' (${value::class.simpleName})")
        }
    }

    companion object {
        val demoAndTest: ColorOptionConverter = ColorOptionConverter(Color.CYAN, Color.CYAN, Color.CYAN)
    }
}