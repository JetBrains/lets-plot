/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.math.max


class Text : RenderBox() {
    var text: Collection<String> by visualProp(emptyList())
    var color: Color by visualProp(Color.WHITE)
    var fontSize: Double by visualProp(10.0)
    var fontFamily by visualProp("serif")

    protected override fun updateState() {
        dimension = measureText()
    }

    protected override fun renderInternal(ctx: Context2d) {
        ctx.setFont(Context2d.Font(fontSize = fontSize, fontFamily = fontFamily))
        ctx.setTextBaseline(Context2d.TextBaseline.BOTTOM)
        ctx.setFillStyle(color)

        var y = fontSize
        for (s in text) {
            ctx.fillText(s, 0.0, y)
            y += fontSize
        }
    }

    private fun measureText(): DoubleVector {
        if (isDirty) {
            val font = Context2d.Font(fontSize = fontSize, fontFamily = fontFamily)
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