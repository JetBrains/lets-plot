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
        val WHITE = Color(255, 255, 255)
        val CONSOLE_WHITE = Color(204, 204, 204)
        val BLACK = Color(0, 0, 0)
        val LIGHT_GRAY = Color(192, 192, 192)
        val VERY_LIGHT_GRAY = Color(210, 210, 210)
        val GRAY = Color(128, 128, 128)
        val RED = Color(255, 0, 0)
        val LIGHT_GREEN = Color(210, 255, 210)
        val GREEN = Color(0, 255, 0)
        val DARK_GREEN = Color(0, 128, 0)
        val BLUE = Color(0, 0, 255)
        val DARK_BLUE = Color(0, 0, 128)
        val LIGHT_BLUE = Color(210, 210, 255)
        val YELLOW = Color(255, 255, 0)
        val CONSOLE_YELLOW = Color(174, 174, 36)
        val LIGHT_YELLOW = Color(255, 255, 128)
        val VERY_LIGHT_YELLOW = Color(255, 255, 210)
        val MAGENTA = Color(255, 0, 255)
        val LIGHT_MAGENTA = Color(255, 210, 255)
        val DARK_MAGENTA = Color(128, 0, 128)
        val CYAN = Color(0, 255, 255)
        val LIGHT_CYAN = Color(210, 255, 255)
        val ORANGE = Color(255, 192, 0)
        val PINK = Color(255, 175, 175)
        val LIGHT_PINK = Color(255, 210, 210)

        val PACIFIC_BLUE = parseHex("#118ED8")

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