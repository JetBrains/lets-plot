/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme2

import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendDirection.HORIZONTAL
import jetbrains.datalore.plot.builder.guide.LegendDirection.VERTICAL
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_DIRECTION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_POSITION
import jetbrains.datalore.plot.config.ConfigUtil

internal object LegendThemeConfig2 {

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
