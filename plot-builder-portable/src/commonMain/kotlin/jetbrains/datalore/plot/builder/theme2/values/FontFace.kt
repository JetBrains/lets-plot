/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

class FontFace(
    val bold: Boolean = false,
    val italic: Boolean = false
) {
    companion object {
        val NORMAL = FontFace()
        val BOLD = FontFace(bold = true)
        val ITALIC = FontFace(italic = true)
        val BOLD_ITALIC = FontFace(bold = true, italic = true)
    }
}