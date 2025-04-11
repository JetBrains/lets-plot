/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font

class TextMeasurer(private val myContext2d: Context2d) {
    fun measure(label: String, font: Font): DoubleVector {
        myContext2d.save()
        myContext2d.setFont(font)
        val width = myContext2d.measureTextWidth(label)
        myContext2d.restore()

        return DoubleVector(width, font.fontSize)
    }

    fun measure(label: List<String>, font: Font, lineHeight: Double): DoubleVector {
        myContext2d.save()
        myContext2d.setFont(font)

        val width = label.maxOf(myContext2d::measureTextWidth)
        myContext2d.restore()

        val height = lineHeight * (label.size - 1) + font.fontSize
        return DoubleVector(width, height)
    }
}