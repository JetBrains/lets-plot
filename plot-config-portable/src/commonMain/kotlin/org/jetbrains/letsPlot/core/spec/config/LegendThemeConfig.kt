/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection.HORIZONTAL
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection.VERTICAL
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_DIRECTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_JUSTIFICATION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_POSITION

internal object LegendThemeConfig {

    fun convertValue(key: String, value: Any): Any {
        return when (key) {
            LEGEND_POSITION -> toPosition(value)
            LEGEND_JUSTIFICATION -> toJustification(value)
            LEGEND_DIRECTION -> toDirection(value)
            else -> value
        }
    }


    private fun toPosition(value: Any): LegendPosition {
        return when (value) {
            is String -> {
                when (value) {
                    "right" -> LegendPosition.RIGHT
                    "left" -> LegendPosition.LEFT
                    "top" -> LegendPosition.TOP
                    "bottom" -> LegendPosition.BOTTOM
                    "none" -> LegendPosition.NONE
                    else -> throw IllegalArgumentException(
                        "Illegal value: '$value'.\n$LEGEND_POSITION " +
                                "expected value is either a string: left|right|top|bottom|none or two-element numeric list."
                    )
                }
            }
            is List<*> -> {
                val v = ConfigUtil.toNumericPair(value)
                LegendPosition(v.x, v.y)
            }
            is LegendPosition -> {
                value
            }
            else -> throw IllegalArgumentException(
                "Illegal value type: ${value::class.simpleName}.\n$LEGEND_POSITION " +
                        "expected value is either a string: left|right|top|bottom|none or two-element numeric list."
            )
        }
    }

    private fun toJustification(value: Any): LegendJustification {
        // "center" or two-element numeric vector
        return when (value) {
            is String -> {
                when (value) {
                    "center" -> LegendJustification.CENTER
                    else -> throw IllegalArgumentException(
                        "Illegal value '$value', $LEGEND_JUSTIFICATION expected values are: 'center' or two-element numeric list."
                    )
                }
            }
            is List<*> -> {
                val v = ConfigUtil.toNumericPair(value)
                LegendJustification(v.x, v.y)
            }
            is LegendJustification -> {
                value
            }
            else -> throw IllegalArgumentException(
                "Illegal value type: ${value::class.simpleName}, $LEGEND_JUSTIFICATION expected values are: 'center' or two-element numeric list."
            )
        }
    }

    private fun toDirection(value: Any): LegendDirection {
        // "horizontal" or "vertical"
        return when (value) {
            "horizontal" -> HORIZONTAL
            "vertical" -> VERTICAL
            else -> throw IllegalArgumentException(
                "Illegal value: $value, $LEGEND_DIRECTION. Expected values are: 'horizontal' or 'vertical'."
            )
        }
    }
}
