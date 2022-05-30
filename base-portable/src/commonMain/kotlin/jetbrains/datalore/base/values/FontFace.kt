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

    companion object {
        val NORMAL = FontFace()
        val BOLD = FontFace(bold = true)
        val ITALIC = FontFace(italic = true)
        val BOLD_ITALIC = FontFace(bold = true, italic = true)

        fun fromString(str: String): FontFace {
            return when (str) {
                "bold" -> BOLD
                "italic" -> ITALIC
                "bold_italic" -> BOLD_ITALIC
                else -> NORMAL
            }
        }
    }
}