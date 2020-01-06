/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.CssFontParser

class TextMeasurer(private val myContext2d: Context2d) {
    fun measure(label: String, font: String): DoubleVector {
        myContext2d.save()
        myContext2d.setFont(font)
        val width = myContext2d.measureText(label)
        myContext2d.restore()

        val parser =
            CssFontParser.create(font)
                ?: throw IllegalStateException("Could not parse css font string: $font")

        val fontSize = parser.fontSize
        val height = fontSize ?: 10.0

        return DoubleVector(width, height)
    }
}