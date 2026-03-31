/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

data class Font(
    val fontStyle: FontStyle = FontStyle.NORMAL,
    val fontWeight: FontWeight = FontWeight.NORMAL,
    val fontSize: Double = DEFAULT_SIZE,
    val fontFamily: String = DEFAULT_FAMILY
) {

    enum class FontVariant {
        NORMAL,
        BOLD,
        ITALIC,
        BOLD_ITALIC;
    }

    val variant = when (fontWeight to fontStyle) {
        FontWeight.NORMAL to FontStyle.NORMAL -> FontVariant.NORMAL
        FontWeight.BOLD to FontStyle.NORMAL -> FontVariant.BOLD
        FontWeight.NORMAL to FontStyle.ITALIC -> FontVariant.ITALIC
        FontWeight.BOLD to FontStyle.ITALIC -> FontVariant.BOLD_ITALIC
        else -> FontVariant.NORMAL
    }

    companion object {
        const val DEFAULT_SIZE = 10.0
        const val DEFAULT_FAMILY = "serif"
    }
}
