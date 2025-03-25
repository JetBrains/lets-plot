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

        context.setStrokeStyle(Color.ORANGE)
        context.setFillStyle(Color.DARK_BLUE)
        context.setLineWidth(1.0)
        context.setLineWidth(3.0)

        context.beginPath()
        context.moveTo(0.0, 0.0)
        context.lineTo(50.0, 25.0)
        context.lineTo(0.0, 50.0)
        context.lineTo(50.0, 75.0)
        context.lineTo(0.0, 100.0)

        context.moveTo(100.0, 0.0)
        context.lineTo(50.0, 25.0)
        context.lineTo(100.0, 50.0)
        context.lineTo(50.0, 75.0)
        context.lineTo(100.0, 100.0)

        context.stroke()

        canvas.saveBmp("zigzag_stroke.bmp")
    }

    @Test
    fun zigZagFill() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.setStrokeStyle(Color.ORANGE)
        context.setFillStyle(Color.DARK_BLUE)
        context.setLineWidth(1.0)

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

    @Test
    fun circleStroke() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.setStrokeStyle(Color.ORANGE)
        context.setFillStyle(Color.DARK_BLUE)
        context.setLineWidth(1.0)

        context.beginPath()
        context.arc(50.0, 50.0, 40.0, 0.0, 180.0)
        context.stroke()

        canvas.saveBmp("circle_stroke.bmp")
    }

    @Test
    fun circleFill() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.setStrokeStyle(Color.ORANGE)
        context.setFillStyle(Color.DARK_BLUE)
        context.setLineWidth(1.0)

        context.setStrokeStyle(Color.ORANGE)
        context.beginPath()
        context.arc(50.0, 50.0, 40.0, 0.0, 180.0)
        context.closePath()

        context.fill()

        canvas.saveBmp("circle_fill.bmp")
    }

    @Test
    fun circleFillStroke() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.setStrokeStyle(Color.ORANGE)
        context.setFillStyle(Color.DARK_BLUE)
        context.setLineWidth(2.0)

        context.setStrokeStyle(Color.ORANGE)
        context.beginPath()
        context.arc(50.0, 50.0, 40.0, 0.0, 180.0)
        context.closePath()

        context.fill()
        context.stroke()

        canvas.saveBmp("circle_fill_stroke.bmp")
    }
}
