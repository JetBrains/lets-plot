/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.jvm.JvmOverloads
import kotlin.math.roundToInt

// ToDo: ubyte?
class Color @JvmOverloads constructor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255
) {
    init {
        require(
            red in 0..255 &&
            green in 0..255 &&
            blue in 0..255 &&
            alpha in 0..255
        ) { "Color components out of range: $this" }
    }

    fun changeAlpha(newAlpha: Int): Color {
        return Color(red, green, blue, newAlpha)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return other is Color &&
                red == other.red &&
                green == other.green &&
                blue == other.blue &&
                alpha == other.alpha
    }

    fun toCssColor(): String {
        return if (alpha == 255) {
            "rgb($red,$green,$blue)"
        } else {
            "rgba(" + red + "," + green + "," + blue + "," + alpha / 255.0 + ")"
        }
    }

    fun toHexColor(): String {
        return "#" + toColorPart(red) + toColorPart(green) + toColorPart(blue)
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + red
        result = 31 * result + green
        result = 31 * result + blue
        result = 31 * result + alpha
        return result
    }

    override fun toString(): String {
        return "color($red,$green,$blue,$alpha)"
    }

    companion object {
        val TRANSPARENT = Color(0, 0, 0, 0)
        val ALICE_BLUE = parseHex("#F0F8FF")
        val ANTIQUE_WHITE = parseHex("#FAEBD7")
        val AQUA = parseHex("#00FFFF")
        val AQUAMARINE = parseHex("#7FFFD4")
        val AZURE = parseHex("#F0FFFF")
        val BEIGE = parseHex("#F5F5DC")
        val BISQUE = parseHex("#FFE4C4")
        val BLACK = parseHex("#000000")
        val BLANCHED_ALMOND = parseHex("#FFEBCD")
        val BLUE = parseHex("#0000FF")
        val BLUE_VIOLET = parseHex("#8A2BE2")
        val BROWN = parseHex("#A52A2A")
        val BURLY_WOOD = parseHex("#DEB887")
        val CADET_BLUE = parseHex("#5F9EA0")
        val CHARTREUSE = parseHex("#7FFF00")
        val CHOCOLATE = parseHex("#D2691E")
        val CORAL = parseHex("#FF7F50")
        val CORNFLOWER_BLUE = parseHex("#6495ED")
        val CORNSILK = parseHex("#FFF8DC")
        val CRIMSON = parseHex("#DC143C")
        val CYAN = parseHex("#00FFFF")
        val DARK_BLUE = parseHex("#00008B")
        val DARK_CYAN = parseHex("#008B8B")
        val DARK_GOLDENROD = parseHex("#B8860B")
        val DARK_GRAY = parseHex("#555555")
        val DARK_GREEN = parseHex("#006400")
        val DARK_KHAKI = parseHex("#BDB76B")
        val DARK_MAGENTA = parseHex("#8B008B")
        val DARK_OLIVE_GREEN = parseHex("#556B2F")
        val DARK_ORANGE = parseHex("#FF8C00")
        val DARK_ORCHID = parseHex("#9932CC")
        val DARK_RED = parseHex("#8B0000")
        val DARK_SALMON = parseHex("#E9967A")
        val DARK_SEA_GREEN = parseHex("#8FBC8F")
        val DARK_SLATE_BLUE = parseHex("#483D8B")
        val DARK_SLATE_GRAY = parseHex("#2F4F4F")
        val DARK_TURQUOISE = parseHex("#00CED1")
        val DARK_VIOLET = parseHex("#9400D3")
        val DEEP_PINK = parseHex("#FF1493")
        val DEEP_SKY_BLUE = parseHex("#00BFFF")
        val DIM_GRAY = parseHex("#696969")
        val DODGER_BLUE = parseHex("#1E90FF")
        val FIREBRICK = parseHex("#B22222")
        val FLORAL_WHITE = parseHex("#FFFAF0")
        val FOREST_GREEN = parseHex("#228B22")
        val FUCHSIA = parseHex("#FF00FF")
        val GAINSBORO = parseHex("#DCDCDC")
        val GHOST_WHITE = parseHex("#F8F8FF")
        val GOLD = parseHex("#FFD700")
        val GOLDENROD = parseHex("#DAA520")
        val GRAY = parseHex("#808080")
        val GREEN = parseHex("#008000")
        val GREEN_YELLOW = parseHex("#ADFF2F")
        val HONEY_DEW = parseHex("#F0FFF0")
        val HOT_PINK = parseHex("#FF69B4")
        val INDIAN_RED = parseHex("#CD5C5C")
        val INDIGO = parseHex("#4B0082")
        val IVORY = parseHex("#FFFFF0")
        val KHAKI = parseHex("#F0E68C")
        val LAVENDER = parseHex("#E6E6FA")
        val LAVENDER_BLUSH = parseHex("#FFF0F5")
        val LAWN_GREEN = parseHex("#7CFC00")
        val LEMON_CHIFFON = parseHex("#FFFACD")
        val LIGHT_BLUE = parseHex("#ADD8E6")
        val LIGHT_CORAL = parseHex("#F08080")
        val LIGHT_CYAN = parseHex("#E0FFFF")
        val LIGHT_GOLDENROD = parseHex("#EEDD82")
        val LIGHT_GOLDENROD_YELLOW = parseHex("#FAFAD2")
        val LIGHT_GRAY = parseHex("#D3D3D3")
        val LIGHT_GREEN = parseHex("#90EE90")
        val LIGHT_MAGENTA = parseHex("#FFD2FF")
        val LIGHT_PINK = parseHex("#FFB6C1")
        val LIGHT_SALMON = parseHex("#FFA07A")
        val LIGHT_SEA_GREEN = parseHex("#20B2AA")
        val LIGHT_SKY_BLUE = parseHex("#87CEFA")
        val LIGHT_SLATE_BLUE = parseHex("#8470FF")
        val LIGHT_SLATE_GRAY = parseHex("#778899")
        val LIGHT_STEEL_BLUE = parseHex("#B0C4DE")
        val LIGHT_YELLOW = parseHex("#FFFFE0")
        val LIME = parseHex("#00FF00")
        val LIME_GREEN = parseHex("#32CD32")
        val LINEN = parseHex("#FAF0E6")
        val MAGENTA = parseHex("#FF00FF")
        val MAROON = parseHex("#800000")
        val MEDIUM_AQUAMARINE = parseHex("#66CDAA")
        val MEDIUM_BLUE = parseHex("#0000CD")
        val MEDIUM_ORCHID = parseHex("#BA55D3")
        val MEDIUM_PURPLE = parseHex("#9370DB")
        val MEDIUM_SEA_GREEN = parseHex("#3CB371")
        val MEDIUM_SLATE_BLUE = parseHex("#7B68EE")
        val MEDIUM_SPRING_GREEN = parseHex("#00FA9A")
        val MEDIUM_TURQUOISE = parseHex("#48D1CC")
        val MEDIUM_VIOLET_RED = parseHex("#C71585")
        val MIDNIGHT_BLUE = parseHex("#191970")
        val MINT_CREAM = parseHex("#F5FFFA")
        val MISTY_ROSE = parseHex("#FFE4E1")
        val MOCCASIN = parseHex("#FFE4B5")
        val NAVAJO_WHITE = parseHex("#FFDEAD")
        val NAVY = parseHex("#000080")
        val OLD_LACE = parseHex("#FDF5E6")
        val OLIVE = parseHex("#808000")
        val OLIVE_DRAB = parseHex("#6B8E23")
        val ORANGE = parseHex("#FFA500")
        val ORANGE_RED = parseHex("#FF4500")
        val ORCHID = parseHex("#DA70D6")
        val PACIFIC_BLUE = parseHex("#118ED8")
        val PALE_GOLDENROD = parseHex("#EEE8AA")
        val PALE_GREEN = parseHex("#98FB98")
        val PALE_TURQUOISE = parseHex("#AFEEEE")
        val PALE_VIOLET_RED = parseHex("#DB7093")
        val PAPAYA_WHIP = parseHex("#FFEFD5")
        val PEACH_PUFF = parseHex("#FFDAB9")
        val PERU = parseHex("#CD853F")
        val PINK = parseHex("#FFC0CB")
        val PLUM = parseHex("#DDA0DD")
        val POWDERBLUE = parseHex("#B0E0E6")
        val PURPLE = parseHex("#800080")
        val REBECCAPURPLE = parseHex("#663399")
        val RED = parseHex("#FF0000")
        val ROSY_BROWN = parseHex("#BC8F8F")
        val ROYAL_BLUE = parseHex("#4169E1")
        val SADDLE_BROWN = parseHex("#8B4513")
        val SALMON = parseHex("#FA8072")
        val SANDY_BROWN = parseHex("#F4A460")
        val SEA_GREEN = parseHex("#2E8B57")
        val SEA_SHELL = parseHex("#FFF5EE")
        val SIENNA = parseHex("#A0522D")
        val SILVER = parseHex("#C0C0C0")
        val SKY_BLUE = parseHex("#87CEEB")
        val SLATE_BLUE = parseHex("#6A5ACD")
        val SLATE_GRAY = parseHex("#708090")
        val SNOW = parseHex("#FFFAFA")
        val SPRING_GREEN = parseHex("#00FF7F")
        val STEEL_BLUE = parseHex("#4682B4")
        val TAN = parseHex("#D2B48C")
        val TEAL = parseHex("#008080")
        val THISTLE = parseHex("#D8BFD8")
        val TOMATO = parseHex("#FF6347")
        val TURQUOISE = parseHex("#40E0D0")
        val VIOLET = parseHex("#EE82EE")
        val VIOLET_RED = parseHex("#D02090")
        val WHEAT = parseHex("#F5DEB3")
        val WHITE = parseHex("#FFFFFF")
        val WHITE_SMOKE = parseHex("#F5F5F5")
        val YELLOW = parseHex("#FFFF00")
        val YELLOW_GREEN = parseHex("#9ACD32")

        fun gray(intensity: Int): Color {
            require(intensity in 0..100) { "Value must be between 0 and 100" }
            val value = (intensity * 255 / 100.0).roundToInt()
            return Color(value, value, value)
        }

        private const val RGB = "rgb"
        private const val COLOR = "color"
        private const val RGBA = "rgba"

        fun parseOrNull(string: String): Color? {
            return runCatching { parseHex(string) }.getOrNull()
                ?: runCatching { parseRGB(string) }.getOrNull()
        }

        fun parseRGB(text: String): Color {
            val firstParen = findNext(text, "(", 0)
            val prefix = text.substring(0, firstParen)

            val firstComma = findNext(text, ",", firstParen + 1)
            val secondComma = findNext(text, ",", firstComma + 1)

            var thirdComma = -1

            when {
                prefix == RGBA -> thirdComma = findNext(text, ",", secondComma + 1)
                prefix == COLOR -> thirdComma = text.indexOf(",", secondComma + 1)
                prefix != RGB -> throw IllegalArgumentException(text)
            }

            val lastParen = findNext(text, ")", thirdComma + 1)
            val red = text.substring(firstParen + 1, firstComma).trim { it <= ' ' }.toInt()
            val green = text.substring(firstComma + 1, secondComma).trim { it <= ' ' }.toInt()

            val blue: Int
            val alpha: Int
            if (thirdComma == -1) {
                blue = text.substring(secondComma + 1, lastParen).trim { it <= ' ' }.toInt()
                alpha = 255
            } else {
                blue = text.substring(secondComma + 1, thirdComma).trim { it <= ' ' }.toInt()
                alpha = (text.substring(thirdComma + 1, lastParen).trim { it <= ' ' }.toFloat() * 255).roundToInt()
            }

            return Color(red, green, blue, alpha)
        }

        private fun findNext(s: String, what: String, from: Int): Int {
            val result = s.indexOf(what, from)
            if (result == -1) {
                throw IllegalArgumentException("text=$s what=$what from=$from")
            }
            return result
        }

        fun parseHex(hexColor: String): Color {
            @Suppress("NAME_SHADOWING")
            var hexColor = hexColor

            require(hexColor.startsWith("#") && (hexColor.length == 4 || hexColor.length == 7)) {
                "Not a valid HEX value: $hexColor"
            }

            hexColor = hexColor.substring(1)
            if (hexColor.length == 3) {
                hexColor = hexColor.map { "$it$it" }.joinToString("")
            }

            val r = hexColor.substring(0, 2).toInt(16)
            val g = hexColor.substring(2, 4).toInt(16)
            val b = hexColor.substring(4, 6).toInt(16)
            return Color(r, g, b)
        }

        private fun toColorPart(value: Int): String {
            if (value < 0 || value > 255) {
                throw IllegalArgumentException("RGB color part must be in range [0..255] but was $value")
            }

            val result = value.toString(16)
            return if (result.length == 1) {
                "0$result"
            } else {
                result
            }
        }
    }
}