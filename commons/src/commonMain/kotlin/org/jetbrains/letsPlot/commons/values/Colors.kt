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

    private val grayscaleColors: Map<String, Color> = (0..100).associate { i ->
        "gray$i" to Color.gray(i)
    }

    private val transparentColors = mapOf<String, Color>(
        "transparent" to Color.TRANSPARENT,
        "blank" to Color.TRANSPARENT,
        "" to Color.TRANSPARENT
    )

    private val baseColors = mapOf<String, Color>(
        "aliceblue" to Color.ALICE_BLUE,
        "antiquewhite" to Color.ANTIQUE_WHITE,
        "aqua" to Color.AQUA,
        "aquamarine" to Color.AQUAMARINE,
        "azure" to Color.AZURE,
        "beige" to Color.BEIGE,
        "bisque" to Color.BISQUE,
        "black" to Color.BLACK,
        "blanchedalmond" to Color.BLANCHED_ALMOND,
        "blue" to Color.BLUE,
        "blueviolet" to Color.BLUE_VIOLET,
        "brown" to Color.BROWN,
        "burlywood" to Color.BURLY_WOOD,
        "cadetblue" to Color.CADET_BLUE,
        "chartreuse" to Color.CHARTREUSE,
        "chocolate" to Color.CHOCOLATE,
        "coral" to Color.CORAL,
        "cornflowerblue" to Color.CORNFLOWER_BLUE,
        "cornsilk" to Color.CORNSILK,
        "crimson" to Color.CRIMSON,
        "cyan" to Color.CYAN,
        "darkblue" to Color.DARK_BLUE,
        "darkcyan" to Color.DARK_CYAN,
        "darkgoldenrod" to Color.DARK_GOLDENROD,
        "darkgray" to Color.DARK_GRAY,
        "darkgreen" to Color.DARK_GREEN,
        "darkkhaki" to Color.DARK_KHAKI,
        "darkmagenta" to Color.DARK_MAGENTA,
        "darkolivegreen" to Color.DARK_OLIVE_GREEN,
        "darkorange" to Color.DARK_ORANGE,
        "darkorchid" to Color.DARK_ORCHID,
        "darkred" to Color.DARK_RED,
        "darksalmon" to Color.DARK_SALMON,
        "darkseagreen" to Color.DARK_SEA_GREEN,
        "darkslateblue" to Color.DARK_SLATE_BLUE,
        "darkslategray" to Color.DARK_SLATE_GRAY,
        "darkturquoise" to Color.DARK_TURQUOISE,
        "darkviolet" to Color.DARK_VIOLET,
        "deeppink" to Color.DEEP_PINK,
        "deepskyblue" to Color.DEEP_SKY_BLUE,
        "dimgray" to Color.DIM_GRAY,
        "dodgerblue" to Color.DODGER_BLUE,
        "firebrick" to Color.FIREBRICK,
        "floralwhite" to Color.FLORAL_WHITE,
        "forestgreen" to Color.FOREST_GREEN,
        "fuchsia" to Color.FUCHSIA,
        "gainsboro" to Color.GAINSBORO,
        "ghostwhite" to Color.GHOST_WHITE,
        "gold" to Color.GOLD,
        "goldenrod" to Color.GOLDENROD,
        "gray" to Color.GRAY,
        "green" to Color.GREEN,
        "greenyellow" to Color.GREEN_YELLOW,
        "honeydew" to Color.HONEY_DEW,
        "hotpink" to Color.HOT_PINK,
        "indianred" to Color.INDIAN_RED,
        "indigo" to Color.INDIGO,
        "ivory" to Color.IVORY,
        "khaki" to Color.KHAKI,
        "lavender" to Color.LAVENDER,
        "lavenderblush" to Color.LAVENDER_BLUSH,
        "lawngreen" to Color.LAWN_GREEN,
        "lemonchiffon" to Color.LEMON_CHIFFON,
        "lightblue" to Color.LIGHT_BLUE,
        "lightcoral" to Color.LIGHT_CORAL,
        "lightcyan" to Color.LIGHT_CYAN,
        "lightgoldenrod" to Color.LIGHT_GOLDENROD,
        "lightgoldenrodyellow" to Color.LIGHT_GOLDENROD_YELLOW,
        "lightgray" to Color.LIGHT_GRAY,
        "lightgreen" to Color.LIGHT_GREEN,
        "lightmagenta" to Color.LIGHT_MAGENTA,
        "lightpink" to Color.LIGHT_PINK,
        "lightsalmon" to Color.LIGHT_SALMON,
        "lightseagreen" to Color.LIGHT_SEA_GREEN,
        "lightskyblue" to Color.LIGHT_SKY_BLUE,
        "lightslateblue" to Color.LIGHT_SLATE_BLUE,
        "lightslategray" to Color.LIGHT_SLATE_GRAY,
        "lightsteelblue" to Color.LIGHT_STEEL_BLUE,
        "lightyellow" to Color.LIGHT_YELLOW,
        "lime" to Color.LIME,
        "limegreen" to Color.LIME_GREEN,
        "linen" to Color.LINEN,
        "magenta" to Color.MAGENTA,
        "maroon" to Color.MAROON,
        "mediumaquamarine" to Color.MEDIUM_AQUAMARINE,
        "mediumblue" to Color.MEDIUM_BLUE,
        "mediumorchid" to Color.MEDIUM_ORCHID,
        "mediumpurple" to Color.MEDIUM_PURPLE,
        "mediumseagreen" to Color.MEDIUM_SEA_GREEN,
        "mediumslateblue" to Color.MEDIUM_SLATE_BLUE,
        "mediumspringgreen" to Color.MEDIUM_SPRING_GREEN,
        "mediumturquoise" to Color.MEDIUM_TURQUOISE,
        "mediumvioletred" to Color.MEDIUM_VIOLET_RED,
        "midnightblue" to Color.MIDNIGHT_BLUE,
        "mintcream" to Color.MINT_CREAM,
        "mistyrose" to Color.MISTY_ROSE,
        "moccasin" to Color.MOCCASIN,
        "navajowhite" to Color.NAVAJO_WHITE,
        "navy" to Color.NAVY,
        "navyblue" to Color.NAVY,
        "oldlace" to Color.OLD_LACE,
        "olive" to Color.OLIVE,
        "olivedrab" to Color.OLIVE_DRAB,
        "orange" to Color.ORANGE,
        "orangered" to Color.ORANGE_RED,
        "orchid" to Color.ORCHID,
        "pacificblue" to Color.PACIFIC_BLUE,
        "palegoldenrod" to Color.PALE_GOLDENROD,
        "palegreen" to Color.PALE_GREEN,
        "paleturquoise" to Color.PALE_TURQUOISE,
        "palevioletred" to Color.PALE_VIOLET_RED,
        "papayawhip" to Color.PAPAYA_WHIP,
        "peachpuff" to Color.PEACH_PUFF,
        "peru" to Color.PERU,
        "pink" to Color.PINK,
        "plum" to Color.PLUM,
        "powderblue" to Color.POWDERBLUE,
        "purple" to Color.PURPLE,
        "rebeccapurple" to Color.REBECCAPURPLE,
        "red" to Color.RED,
        "rosybrown" to Color.ROSY_BROWN,
        "royalblue" to Color.ROYAL_BLUE,
        "saddlebrown" to Color.SADDLE_BROWN,
        "salmon" to Color.SALMON,
        "sandybrown" to Color.SANDY_BROWN,
        "seagreen" to Color.SEA_GREEN,
        "seashell" to Color.SEA_SHELL,
        "sienna" to Color.SIENNA,
        "silver" to Color.SILVER,
        "skyblue" to Color.SKY_BLUE,
        "slateblue" to Color.SLATE_BLUE,
        "slategray" to Color.SLATE_GRAY,
        "snow" to Color.SNOW,
        "springgreen" to Color.SPRING_GREEN,
        "steelblue" to Color.STEEL_BLUE,
        "tan" to Color.TAN,
        "teal" to Color.TEAL,
        "thistle" to Color.THISTLE,
        "tomato" to Color.TOMATO,
        "turquoise" to Color.TURQUOISE,
        "violet" to Color.VIOLET,
        "violetred" to Color.VIOLET_RED,
        "wheat" to Color.WHEAT,
        "white" to Color.WHITE,
        "whitesmoke" to Color.WHITE_SMOKE,
        "yellow" to Color.YELLOW,
        "yellowgreen" to Color.YELLOW_GREEN
    )

    private val namedColors = baseColors + grayscaleColors + transparentColors

    /**
     * @param c color string to parse. Accepted formats:
     *     - rgb(r, g, b)
     *     - rgba(r, g, b, a)
     *     - color(r, g, b, a)
     *     - #rrggbb
     *     - #rgb
     *     - white, green etc.
     */
    fun parseColor(c: String): Color {
        return when {
            c.indexOf('(') > 0 -> Color.parseRGB(c)
            c.startsWith("#") -> Color.parseHex(c)
            isColorName(c) -> forName(c)
            else -> throw IllegalArgumentException("Error parsing color value: $c")
        }
    }

    private fun normalizeColorName(name: String): String =
        name.replace("-", "")
            .replace("_", "")
            .replace("grey", "gray")
            .lowercase()

    fun isColorName(colorName: String): Boolean {
        return namedColors.containsKey(normalizeColorName(colorName))
    }

    fun forName(colorName: String): Color {
        return namedColors[normalizeColorName(colorName)] ?: throw IllegalArgumentException()
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

    fun contrastRatio(color: Color, other: Color): Double {
        val l1 = luminance(color)
        val l2 = luminance(other)
        return (max(l1, l2) + .05) / (min(l1, l2) + .05)
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
