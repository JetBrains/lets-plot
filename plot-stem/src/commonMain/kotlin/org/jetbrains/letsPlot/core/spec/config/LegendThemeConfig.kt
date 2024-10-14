/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.guide.LegendArrangement
import org.jetbrains.letsPlot.core.plot.base.guide.LegendBoxJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX_JUST
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_DIRECTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_JUSTIFICATION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_POSITION
import org.jetbrains.letsPlot.core.spec.Option.Theme

internal object LegendThemeConfig {

    fun convertValue(key: String, value: Any): Any {
        return when (key) {
            LEGEND_POSITION -> toPosition(value)
            LEGEND_JUSTIFICATION -> toJustification(value)
            LEGEND_DIRECTION -> toDirection(value)
            LEGEND_BOX -> toArrangement(value)
            LEGEND_BOX_JUST -> toBoxJustification(value)
            else -> value
        }
    }


    private fun toPosition(value: Any): LegendPosition {
        return when (value) {
            is String -> {
                when (value) {
                    Theme.Legend.POSITION_LEFT -> LegendPosition.LEFT
                    Theme.Legend.POSITION_RIGHT -> LegendPosition.RIGHT
                    Theme.Legend.POSITION_TOP -> LegendPosition.TOP
                    Theme.Legend.POSITION_BOTTOM -> LegendPosition.BOTTOM
                    Theme.Legend.POSITION_NONE -> LegendPosition.NONE
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
                    Theme.Legend.JUSTIFICATION_CENTER -> LegendJustification.CENTER
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
            Theme.Legend.DIRECTION_HORIZONTAL -> LegendDirection.HORIZONTAL
            Theme.Legend.DIRECTION_VERTICAL -> LegendDirection.VERTICAL
            else -> throw IllegalArgumentException(
                "Illegal value: $value, $LEGEND_DIRECTION. Expected values are: 'horizontal' or 'vertical'."
            )
        }
    }

    private fun toArrangement(value: Any): LegendArrangement {
        // "horizontal" or "vertical"
        return when (value) {
            Theme.Legend.ARRANGEMENT_HORIZONTAL -> LegendArrangement.HORIZONTAL
            Theme.Legend.ARRANGEMENT_VERTICAL -> LegendArrangement.VERTICAL
            else -> throw IllegalArgumentException(
                "Illegal value: $value, $LEGEND_BOX. Expected values are: 'horizontal' or 'vertical'."
            )
        }
    }

    private fun toBoxJustification(value: Any): LegendBoxJustification {
        return when (value) {
            Theme.Legend.JUSTIFICATION_LEFT -> LegendBoxJustification.LEFT
            Theme.Legend.JUSTIFICATION_RIGHT -> LegendBoxJustification.RIGHT
            Theme.Legend.JUSTIFICATION_TOP -> LegendBoxJustification.TOP
            Theme.Legend.JUSTIFICATION_BOTTOM -> LegendBoxJustification.BOTTOM
            Theme.Legend.JUSTIFICATION_CENTER -> LegendBoxJustification.CENTER
            else -> throw IllegalArgumentException(
                "Illegal value: $value, $LEGEND_BOX_JUST. Expected values are: 'left', 'right', 'top', 'bottom', 'center'."
            )
        }
    }
}
