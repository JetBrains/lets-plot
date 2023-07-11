/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.jvm.JvmOverloads

class Font @JvmOverloads constructor(
    val family: FontFamily,
    val size: Int,
    val isBold: Boolean = false,
    val isItalic: Boolean = false
) {
    val isMonospased: Boolean = family.monospaced

    override fun toString(): String {
        return "$family $size ${if (isBold) "bold" else ""} ${if (isItalic) "italic" else ""}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Font) return false
        return family === other.family &&
                size == other.size &&
                isBold == other.isBold &&
                isItalic == other.isItalic
    }

    override fun hashCode(): Int {
        var result = family.hashCode()
        result = 31 * result + size
        result = 31 * result + if (isBold) 1 else 0
        result = 31 * result + if (isItalic) 1 else 0
        return result
    }
}