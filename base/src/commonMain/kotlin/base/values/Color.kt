package jetbrains.datalore.base.values

import kotlin.jvm.JvmOverloads

class Color @JvmOverloads constructor(
        val red: Int,
        val green: Int,
        val blue: Int,
        val alpha: Int = 255
) {

    fun changeAlpha(newAlpha: Int): Color {
        return Color(red, green, blue, newAlpha)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Color) {
            return false
        }

        if (red != other.red) {
            return false
        }
        if (green != other.green) {
            return false
        }
        if (blue != other.blue) {
            return false
        }
        return alpha == other.alpha
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

        private const val RGB = "rgb"
        private const val COLOR = "color"
        private const val RGBA = "rgba"

        fun parseColor(text: String): Color {
            val firstParen = findNext(text, "(", 0)
            val prefix = text.substring(0, firstParen)

            val firstComma = findNext(text, ",", firstParen + 1)
            val secondComma = findNext(text, ",", firstComma + 1)

            var thirdComma = -1

            if (prefix == RGBA) {
                thirdComma = findNext(text, ",", secondComma + 1)
            } else if (prefix == COLOR) {
                thirdComma = text.indexOf(",", secondComma + 1)
            } else if (prefix != RGB) {
                throw IllegalArgumentException()
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
                alpha = text.substring(thirdComma + 1, lastParen).trim { it <= ' ' }.toInt()
            }

            return Color(red, green, blue, alpha)
        }

        private fun findNext(s: String, what: String, from: Int): Int {
            val result = s.indexOf(what, from)
            if (result == -1) {
                throw IllegalArgumentException()
            }
            return result
        }

        fun parseHex(hexColor: String): Color {
            @Suppress("NAME_SHADOWING")
            var hexColor = hexColor
            if (!hexColor.startsWith("#")) {
                throw IllegalArgumentException()
            }
            hexColor = hexColor.substring(1)
            if (hexColor.length != 6) {
                throw IllegalArgumentException()
            }
//            val r = Integer.valueOf(hexColor.substring(0, 2), 16)
//            val g = Integer.valueOf(hexColor.substring(2, 4), 16)
//            val b = Integer.valueOf(hexColor.substring(4, 6), 16)
            val r = hexColor.substring(0, 2).toInt(16)
            val g = hexColor.substring(2, 4).toInt(16)
            val b = hexColor.substring(4, 6).toInt(16)
            return Color(r, g, b)
        }

        private fun toColorPart(value: Int): String {
            if (value < 0 || value > 255) {
                throw IllegalArgumentException()
            }

//            val result = Integer.toHexString(value)
            val result = value.toString(16)
            return if (result.length == 1) {
                "0$result"
            } else {
                result
            }
        }
    }
}