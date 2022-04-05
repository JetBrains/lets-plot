/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Font

class LabelMetrics2(val font: Font) {

    fun height(): Double {
        return font.size.toDouble()
    }

    fun width(text: String): Double {
        if (text.isEmpty()) {
            return 0.0
        }
        val options = getOptionsForFont(font.family.toString())
        val width = text.map { CharCategory.getCharRatio(it, options) }.sum() * font.size * options.fontRatio
        return if (font.isBold) {
            width * options.fontBoldRatio
        } else {
            width
        }
    }

    fun dimensions(text: String): DoubleVector {
        return DoubleVector(width(text), height())
    }

    fun dimensions(textLines: List<String>): DoubleVector {
        if (textLines.isEmpty()) {
            return DoubleVector.ZERO
        }

        fun DoubleVector.union(p: DoubleVector): DoubleVector {
            return DoubleVector(
                x = kotlin.math.max(x, p.x),
                y = y + p.y
            )
        }
        return textLines.map(::dimensions).fold(DoubleVector.ZERO) { acc, dv -> acc.union(dv) }
    }
}