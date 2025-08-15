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
    val isNormal: Boolean = fontStyle == FontStyle.NORMAL && fontWeight == FontWeight.NORMAL
    val isBold: Boolean = fontStyle == FontStyle.NORMAL && fontWeight == FontWeight.BOLD
    val isItalic: Boolean = fontStyle == FontStyle.ITALIC && fontWeight == FontWeight.NORMAL
    val isBoldItalic: Boolean = fontStyle == FontStyle.ITALIC && fontWeight == FontWeight.BOLD

    companion object {
        const val DEFAULT_SIZE = 10.0
        const val DEFAULT_FAMILY = "serif"
    }
}
