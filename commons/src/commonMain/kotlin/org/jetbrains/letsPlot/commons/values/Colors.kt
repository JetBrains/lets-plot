/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.jvm.JvmOverloads
import kotlin.math.*
import kotlin.random.Random

object Colors {
    private const val DEFAULT_FACTOR = 0.7

    private val variantColors = mapOf<String, Color>(
        "dark_blue" to Color.DARK_BLUE,
        "dark_green" to Color.DARK_GREEN,
        "dark_magenta" to Color.DARK_MAGENTA,
        "light_blue" to Color.LIGHT_BLUE,
        "light_gray" to Color.LIGHT_GRAY,
        "light_green" to Color.LIGHT_GREEN,
        "light_yellow" to Color.LIGHT_YELLOW,
        "light_magenta" to Color.LIGHT_MAGENTA,
        "light_cyan" to Color.LIGHT_CYAN,
        "light_pink" to Color.LIGHT_PINK,
        "very_light_gray" to Color.VERY_LIGHT_GRAY,
        "very_light_yellow" to Color.VERY_LIGHT_YELLOW
    )
    private val namedColors = mapOf<String, Color>(
        "white" to Color.WHITE,
        "black" to Color.BLACK,
        "gray" to Color.GRAY,
        "red" to Color.RED,
        "green" to Color.GREEN,
        "blue" to Color.BLUE,
        "yellow" to Color.YELLOW,
        "magenta" to Color.MAGENTA,
        "cyan" to Color.CYAN,
        "orange" to Color.ORANGE,
        "pink" to Color.PINK
    ) +
            // light_gray
            variantColors +
            // light-gray
            variantColors.mapKeys { it.key.replace('_', '-') } +
            // lightgray
            variantColors.mapKeys { it.key.replace("_", "") }


    /**
     * @param c color string to parse. Accepted formats:
     *     - rgb(r, g, b)
     *     - rgba(r, g, b, a)
     *     - color(r, g, b, a)
     *     - #rrggbb
     *     - white, green etc.
     */
    fun parseColor(c: String): Color {
        return when {
            c.indexOf('(') > 0 -> Color.parseRGB(c)
            c.startsWith("#") -> Color.parseHex(c)
            isColorName(c) -> forName(c)
            else -> throw IllegalArgumentException("Error persing color value: $c")
        }
    }

    fun isColorName(colorName: String): Boolean {
        return namedColors.containsKey(colorName.lowercase())
    }

    fun forName(colorName: String): Color {
        return namedColors[colorName.lowercase()] ?: throw IllegalArgumentException()
    }

    fun generateHueColor(): Double {
        return 360 * Random.nextDouble()
    }

    fun generateColor(s: Double, v: Double): Color {
        return rgbFromHsv(360 * Random.nextDouble(), s, v)
    }

    /**
     * @param h hue, [0, 360] degree
     * @param s saturation, [0, 1]
     * @param v value, [0, 1]
     * @param alpha [0, 1], 0 - transparent and 1 - opaque.
     */
    @JvmOverloads
    fun rgbFromHsv(h: Double, s: Double, v: Double = 1.0, alpha: Double = 1.0): Color {
        val hd = h / 60
        val c = v * s
        val x = c * (1 - abs(hd % 2 - 1))

        var r = 0.0
        var g = 0.0
        var b = 0.0

        when {
            hd < 1 -> {
                r = c
                g = x
            }
            hd < 2 -> {
                r = x
                g = c
            }
            hd < 3 -> {
                g = c
                b = x
            }
            hd < 4 -> {
                g = x
                b = c
            }
            hd < 5 -> {
                r = x
                b = c
            }
            else -> {
                r = c
                b = x
            }
        }

        val m = v - c
        return Color(
            (255 * (r + m)).roundToInt(),
            (255 * (g + m)).roundToInt(),
            (255 * (b + m)).roundToInt(),
            (255 * alpha).roundToInt(),
        )
    }

    fun hsvFromRgb(color: Color): HSV {
        val scale = 1.0 / 255
        val r = color.red * scale
        val g = color.green * scale
        val b = color.blue * scale
        val min = min(r, min(g, b))
        val max = max(r, max(g, b))

        val v = if (max == 0.0) 0.0 else 1 - min / max
        val h: Double
        val div = 1f / (6 * (max - min))

        h = if (max == min) {
            0.0
        } else if (max == r) {
            if (g >= b) (g - b) * div else 1 + (g - b) * div
        } else if (max == g) {
            1f / 3 + (b - r) * div
        } else {
            2f / 3 + (r - g) * div
        }

        return HSV(
            hue = 360 * h,
            saturation = v,
            value = max
        )
    }

    @JvmOverloads
    fun darker(c: Color?, factor: Double = DEFAULT_FACTOR): Color? {
        return c?.let {
            Color(
                max((c.red * factor).toInt(), 0),
                max((c.green * factor).toInt(), 0),
                max((c.blue * factor).toInt(), 0),
                c.alpha
            )
        }
    }

    @JvmOverloads
    fun lighter(c: Color, factor: Double = DEFAULT_FACTOR): Color {
        var r = c.red
        var g = c.green
        var b = c.blue
        val alpha = c.alpha

        val i = (1.0 / (1.0 - factor)).toInt()
        if (r == 0 && g == 0 && b == 0) {
            return Color(i, i, i, alpha)
        }
        if (r > 0 && r < i) r = i
        if (g > 0 && g < i) g = i
        if (b > 0 && b < i) b = i

        return Color(
            min((r / factor).toInt(), 255),
            min((g / factor).toInt(), 255),
            min((b / factor).toInt(), 255),
            alpha
        )
    }

    fun mimicTransparency(color: Color, alpha: Double, background: Color): Color {
        val red = (color.red * alpha + background.red * (1 - alpha)).toInt()
        val green = (color.green * alpha + background.green * (1 - alpha)).toInt()
        val blue = (color.blue * alpha + background.blue * (1 - alpha)).toInt()
        return Color(red, green, blue)
    }

    fun withOpacity(c: Color, opacity: Double): Color {
        return c.changeAlpha(max(0, min(255, round(255 * opacity).toInt())))
    }

    fun contrast(color: Color, other: Color): Double {
        return (luminance(color) + .05) / (luminance(other) + .05)
    }

    fun luminance(color: Color): Double {
        return .2126 * colorLuminance(color.red) + .7152 * colorLuminance(color.green) + .0722 * colorLuminance(color.blue)
    }

    private fun colorLuminance(componentValue: Int): Double {
        return if (componentValue <= 10) componentValue / 3294.0 else (componentValue / 269.0 + .0513).pow(2.4)
    }

    fun solid(c: Color): Boolean {
        return c.alpha == 255
    }

    fun distributeEvenly(count: Int, saturation: Double): Array<Color> {
        val result = arrayOfNulls<Color>(count)

        val sector = 360 / count
        for (i in 0 until count) {
            result[i] = rgbFromHsv((sector * i).toDouble(), saturation)
        }
        @Suppress("UNCHECKED_CAST")
        return result as Array<Color>
    }
}
