/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.guide.LegendDirection.*
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.config.ConfigUtil
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION
import jetbrains.datalore.plot.config.OptionsAccessor

internal class LegendThemeConfig(
    options: Map<String, Any>,
    defOptions: Map<String, Any>
) : OptionsAccessor(options, defOptions), LegendTheme {

    override fun keySize(): Double {
        return ThemeConfig.DEF.legend().keySize()
    }

    override fun margin(): Double {
        return ThemeConfig.DEF.legend().margin()
    }

    override fun padding(): Double {
        return ThemeConfig.DEF.legend().padding()
    }

    override fun position(): LegendPosition {
        when (val o = get(LEGEND_POSITION)) {
            is String -> {
                return when (o) {
                    "right" -> LegendPosition.RIGHT
                    "left" -> LegendPosition.LEFT
                    "top" -> LegendPosition.TOP
                    "bottom" -> LegendPosition.BOTTOM
                    "none" -> LegendPosition.NONE
                    else -> throw IllegalArgumentException(
                        "Illegal value '" +
                                o +
                                "', " + LEGEND_POSITION + " expected values are: left/right/top/bottom/none or or two-element numeric list"
                    )
                }
            }
            is List<*> -> {
                val v = ConfigUtil.toNumericPair((o as List<*>?)!!)
                return LegendPosition(v.x, v.y)
            }
            is LegendPosition -> {
                return o
            }
            else -> return ThemeConfig.DEF.legend().position()
        }
    }

    override fun justification(): LegendJustification {
        // "center" or two-element numeric vector
        when (val o = get(LEGEND_JUSTIFICATION)) {
            is String -> {
                when (o) {
                    "center" -> return LegendJustification.CENTER
                    else -> throw IllegalArgumentException(
                        "Illegal value '$o', $LEGEND_JUSTIFICATION expected values are: 'center' or two-element numeric list"
                    )
                }
            }
            is List<*> -> {
                val v = ConfigUtil.toNumericPair((o as List<*>?)!!)
                return LegendJustification(v.x, v.y)
            }
            is LegendJustification -> {
                return o
            }
        }
        return ThemeConfig.DEF.legend().justification()
    }

    override fun direction(): jetbrains.datalore.plot.builder.guide.LegendDirection {
        // "horizontal" or "vertical"
        val o = get(LEGEND_DIRECTION)
        if (o is String) {
            when (o) {
                "horizontal" -> return HORIZONTAL
                "vertical" -> return VERTICAL
            }
        }
        return AUTO
    }

    override fun backgroundFill(): Color {
        return ThemeConfig.DEF.legend().backgroundFill()
    }
}
