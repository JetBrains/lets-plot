/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.TextBaseline
import kotlin.math.max


class Text : RenderBox() {
    var text: Collection<String> by visualProp(emptyList())
    var color: Color by visualProp(Color.WHITE)
    var fontSize: Double by visualProp(10.0)
    var fontFamily by visualProp("serif")

    override fun updateState() {
        dimension = measureText()
    }

    override fun renderInternal(ctx: Context2d) {
        ctx.setFont(Font(fontSize = fontSize, fontFamily = fontFamily))
        ctx.setTextBaseline(TextBaseline.BOTTOM)
        ctx.setFillStyle(color)

        var y = fontSize
        for (s in text) {
            ctx.fillText(s, 0.0, y)
            y += fontSize
        }
    }

    private fun measureText(): DoubleVector {
        if (isDirty) {
            val font = Font(fontSize = fontSize, fontFamily = fontFamily)
            dimension = run {
                var maxWidth = 0.0
                for (s in text) {
                    maxWidth = max(maxWidth, graphics.measure(s, font).x)
                }
                DoubleVector(maxWidth, text.size * fontSize)
            }
            isDirty = false
        }

        return dimension
    }
}