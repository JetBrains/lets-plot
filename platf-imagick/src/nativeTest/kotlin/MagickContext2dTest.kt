import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class MagickContext2dTest {

    @Test
    fun zigZagStroke() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.setStrokeStyle(Color.RED)
        context.setFillStyle(Color.TRANSPARENT)
        context.setLineWidth(3.0)

        context.beginPath()
        context.moveTo(0.0, 0.0)
        context.lineTo(50.0, 25.0)
        context.lineTo(0.0, 50.0)
        context.lineTo(50.0, 75.0)
        context.lineTo(0.0, 100.0)

        context.moveTo(50.0, 0.0)
        context.lineTo(100.0, 25.0)
        context.lineTo(50.0, 50.0)
        context.lineTo(100.0, 75.0)
        context.lineTo(50.0, 100.0)

        context.stroke()

        canvas.saveBmp("zigzag_stroke.bmp")
    }

    @Test
    fun zigZagFill() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.setFillStyle(Color.RED)

        context.setStrokeStyle(Color.TRANSPARENT)
        context.setLineWidth(0.0)

        context.beginPath()
        context.moveTo(0.0, 0.0)
        context.lineTo(50.0, 25.0)
        context.lineTo(0.0, 50.0)
        context.lineTo(50.0, 75.0)
        context.lineTo(0.0, 100.0)
        context.closePath()

        context.moveTo(50.0, 0.0)
        context.lineTo(100.0, 25.0)
        context.lineTo(50.0, 50.0)
        context.lineTo(100.0, 75.0)
        context.lineTo(50.0, 100.0)
        context.closePath()

        context.fill()

        canvas.saveBmp("zigzag_fill.bmp")
    }
}