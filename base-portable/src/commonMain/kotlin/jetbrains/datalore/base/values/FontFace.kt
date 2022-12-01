/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

class FontFace(
    val bold: Boolean = false,
    val italic: Boolean = false
) {
    operator fun plus(other: FontFace): FontFace {
        return FontFace(bold || other.bold, italic || other.italic)
    }

    override fun toString(): String {
        var s = ""
        if (bold) s += "bold"
        if (italic) s += " italic"
        return s
    }

    companion object {
        val NORMAL = FontFace()
        val BOLD = FontFace(bold = true)
        val ITALIC = FontFace(italic = true)
        val BOLD_ITALIC = FontFace(bold = true, italic = true)

        fun fromString(str: String): FontFace {
            fun fromValue(str: String): FontFace {
                return when (str) {
                    "bold" -> BOLD
                    "italic" -> ITALIC
                    else -> NORMAL
                }
            }
            return str.split(' ', '_', '.', '-')
                .filter(String::isNotEmpty)
                .map(::fromValue)
                .fold(FontFace(), FontFace::plus)
        }
    }
}