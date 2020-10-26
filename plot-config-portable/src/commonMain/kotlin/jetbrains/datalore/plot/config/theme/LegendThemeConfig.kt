/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.config.ConfigUtil
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION
import jetbrains.datalore.plot.config.OptionsAccessor

internal class LegendThemeConfig(options: Map<*, *>, defOptions: Map<*, *>) : OptionsAccessor(options, defOptions), LegendTheme {

    override fun keySize(): Double {
        return ThemeConfig.DEF.legend().keySize()
    }

    override fun margin(): Double {
        return ThemeConfig.DEF.legend().margin()
    }

    override fun padding(): Double {
        return ThemeConfig.DEF.legend().padding()
    }

    override fun position(): jetbrains.datalore.plot.builder.guide.LegendPosition {
        val o = get(LEGEND_POSITION)
        if (o is String) {
            when (o) {
                "right" -> return jetbrains.datalore.plot.builder.guide.LegendPosition.RIGHT
                "left" -> return jetbrains.datalore.plot.builder.guide.LegendPosition.LEFT
                "top" -> return jetbrains.datalore.plot.builder.guide.LegendPosition.TOP
                "bottom" -> return jetbrains.datalore.plot.builder.guide.LegendPosition.BOTTOM
                "none" -> return jetbrains.datalore.plot.builder.guide.LegendPosition.NONE
                else -> throw IllegalArgumentException("Illegal value '" +
                        o +
                        "', " + LEGEND_POSITION + " expected values are: left/right/top/bottom/none or or two-element numeric list")
            }
        } else if (o is List<*>) {
            val v = ConfigUtil.toNumericPair((o as List<*>?)!!)
            return jetbrains.datalore.plot.builder.guide.LegendPosition(v.x, v.y)
        } else if (o is jetbrains.datalore.plot.builder.guide.LegendPosition) {
            return o
        }
        return ThemeConfig.DEF.legend().position()
    }

    override fun justification(): jetbrains.datalore.plot.builder.guide.LegendJustification {
        // "center" or two-element numeric vector
        val o = get(LEGEND_JUSTIFICATION)
        if (o is String) {
            when (o) {
                "center" -> return jetbrains.datalore.plot.builder.guide.LegendJustification.CENTER
                else -> throw IllegalArgumentException("Illegal value '" +
                        o +
                        "', " + LEGEND_JUSTIFICATION + " expected values are: 'center' or two-element numeric list")
            }
        } else if (o is List<*>) {
            val v = ConfigUtil.toNumericPair((o as List<*>?)!!)
            return jetbrains.datalore.plot.builder.guide.LegendJustification(v.x, v.y)
        } else if (o is jetbrains.datalore.plot.builder.guide.LegendJustification) {
            return o
        }
        return ThemeConfig.DEF.legend().justification()
    }

    override fun direction(): jetbrains.datalore.plot.builder.guide.LegendDirection {
        // "horizontal" or "vertical"
        val o = get(LEGEND_DIRECTION)
        if (o is String) {
            when (o) {
                "horizontal" -> return jetbrains.datalore.plot.builder.guide.LegendDirection.HORIZONTAL
                "vertical" -> return jetbrains.datalore.plot.builder.guide.LegendDirection.VERTICAL
            }
        }
        return jetbrains.datalore.plot.builder.guide.LegendDirection.AUTO
    }

    override fun backgroundFill(): Color {
        return ThemeConfig.DEF.legend().backgroundFill()
    }
}
