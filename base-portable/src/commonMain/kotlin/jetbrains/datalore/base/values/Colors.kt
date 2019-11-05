/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

import kotlin.jvm.JvmOverloads
import kotlin.math.*
import kotlin.random.Random

object Colors {
    private const val DEFAULT_FACTOR = 0.7

    private val colorsList = createColorsList()

    private fun createColorsList(): Map<String, Color> {
        val colorList = HashMap<String, Color>()
        colorList["white"] = Color.WHITE
        colorList["black"] = Color.BLACK
        colorList["light-gray"] = Color.LIGHT_GRAY
        colorList["very-light-gray"] = Color.VERY_LIGHT_GRAY
        colorList["gray"] = Color.GRAY
        colorList["red"] = Color.RED
        colorList["light-green"] = Color.LIGHT_GREEN
        colorList["green"] = Color.GREEN
        colorList["dark-green"] = Color.DARK_GREEN
        colorList["blue"] = Color.BLUE
        colorList["dark-blue"] = Color.DARK_BLUE
        colorList["light-blue"] = Color.LIGHT_BLUE
        colorList["yellow"] = Color.YELLOW
        colorList["light-yellow"] = Color.LIGHT_YELLOW
        colorList["very-light-yellow"] = Color.VERY_LIGHT_YELLOW
        colorList["magenta"] = Color.MAGENTA
        colorList["light-magenta"] = Color.LIGHT_MAGENTA
        colorList["dark-magenta"] = Color.DARK_MAGENTA
        colorList["cyan"] = Color.CYAN
        colorList["light-cyan"] = Color.LIGHT_CYAN
        colorList["orange"] = Color.ORANGE
        colorList["pink"] = Color.PINK
        colorList["light-pink"] = Color.LIGHT_PINK
        return colorList
    }

    fun isColorName(colorName: String): Boolean {
        return colorsList.containsKey(colorName.toLowerCase())
    }

    fun forName(colorName: String): Color {
        val res = colorsList[colorName.toLowerCase()]
        return if (res != null) {
            res
        } else {
            throw IllegalArgumentException()
        }
    }

    fun generateHueColor(): Double {
//        return 360 * Math.random()
        return 360 * Random.nextDouble()
    }

    fun generateColor(s: Double, v: Double): Color {
        return rgbFromHsv(360 * Random.nextDouble(), s, v)
    }

    @JvmOverloads
    fun rgbFromHsv(h: Double, s: Double, v: Double = 1.0): Color {
        val hd = h / 60
        val c = v * s
        val x = c * (1 - abs(hd % 2 - 1))

        var r = 0.0
        var g = 0.0
        var b = 0.0

        if (hd < 1) {
            r = c
            g = x
        } else if (hd < 2) {
            r = x
            g = c
        } else if (hd < 3) {
            g = c
            b = x
        } else if (hd < 4) {
            g = x
            b = c
        } else if (hd < 5) {
            r = x
            b = c
        } else {
            r = c
            b = x
        }

        val m = v - c
        return Color((255 * (r + m)).toInt(), (255 * (g + m)).toInt(), (255 * (b + m)).toInt())
    }

    fun hsvFromRgb(color: Color): DoubleArray {
        val scale = (1f / 255).toDouble()
        val r = color.red * scale
        val g = color.green * scale
        val b = color.blue * scale
        val min = min(r, min(g, b))
        val max = max(r, max(g, b))

        val v = if (max == 0.0) 0.0 else 1 - min / max
        val h: Double
        val div = 1f / (6 * (max - min))

        if (max == min) {
            h = 0.0
        } else if (max == r) {
            h = if (g >= b) (g - b) * div else 1 + (g - b) * div
        } else if (max == g) {
            h = 1f / 3 + (b - r) * div
        } else {
            h = 2f / 3 + (r - g) * div
        }

        return doubleArrayOf(360 * h, v, max)
    }

    @JvmOverloads
    fun darker(c: Color?, factor: Double = DEFAULT_FACTOR): Color? {
        return if (c != null) {
            Color(
                    max((c.red * factor).toInt(), 0),
                    max((c.green * factor).toInt(), 0),
                    max((c.blue * factor).toInt(), 0),
                    c.alpha)
        } else {
            null
        }
    }

    @JvmOverloads
    fun lighter(c: Color?, factor: Double = DEFAULT_FACTOR): Color? {
        if (c != null) {
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
                    alpha)
        } else {
            return null
        }
    }

    fun mimicTransparency(color: Color, alpha: Double, background: Color): Color {
        val red = (color.red * alpha + background.red * (1 - alpha)).toInt()
        val green = (color.green * alpha + background.green * (1 - alpha)).toInt()
        val blue = (color.blue * alpha + background.blue * (1 - alpha)).toInt()
        return Color(red, green, blue)
    }

    fun withOpacity(c: Color, opacity: Double): Color {
        return if (opacity < 1.0) {
            c.changeAlpha(max(0, min(255, round(255 * opacity).toInt())))
        } else c
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
        val colors = result as Array<Color>
        return colors
    }

//    fun colorPersister(defaultValue: Color): Persister<Color> {
//        return object : Persister<Color>() {
//            fun deserialize(value: String?): Color {
//                return if (value == null) {
//                    defaultValue
//                } else Color.parseColor(value)
//            }
//
//            fun serialize(value: Color): String? {
//                return if (value == defaultValue) {
//                    null
//                } else value.toString()
//            }
//
//            fun toString(): String {
//                return "colorPersister"
//            }
//        }
//    }
}
