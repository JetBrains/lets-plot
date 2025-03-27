import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class MagickContext2dTest {
    @Test
    fun lineForwardAndBackward() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.strokeStyle = "orange"
        context.lineWidth = 20.0

        context.beginPath()
        context.moveTo(0.0, 30.0)
        context.lineTo(100.0, 30.0)
        context.lineTo(0.0, 30.0)
        context.stroke()

        context.beginPath()
        context.moveTo(0.0, 60.0)
        context.lineTo(100.0, 60.0)
        context.lineTo(50.0, 60.0)
        context.stroke()


        canvas.saveBmp("line_forward_backward.bmp")
    }

    @Test
    fun wideStrokes() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.strokeStyle = "orange"
        context.lineWidth = 20.0

        context.beginPath()
        context.moveTo(0.0, 10.0)
        context.lineTo(100.0, 10.0)
        context.closePath()
        context.stroke()

        context.beginPath()
        context.moveTo(0.0, 40.0)
        context.lineTo(100.0, 40.0)
        context.closePath()
        context.stroke()

        context.beginPath()
        context.moveTo(0.0, 70.0)
        context.lineTo(100.0, 70.0)
        context.closePath()
        context.stroke()

        canvas.saveBmp("wide_strokes.bmp")
    }

    @Test
    fun multiPathFill() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.fillStyle = "orange"
        context.lineWidth = 5.0
        context.strokeStyle = "dark_blue"
        context.moveTo(50.0, 50.0)
        context.lineTo(125.0, 125.0)


        context.beginPath()
        context.moveTo(0.0, 0.0)
        context.lineTo(25.0, 25.0)
        context.lineTo(50.0, 0.0)
        context.closePath()

        context.moveTo(100.0, 100.0)
        context.lineTo(75.0, 75.0)
        context.lineTo(50.0, 100.0)
        context.closePath()

        context.fill()
        context.stroke()

        canvas.saveBmp("multi_path_fill.bmp")
    }
    @Test
    fun multiPathStroke() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.strokeStyle = "orange"
        context.lineWidth = 2.0

        context.beginPath()
        context.moveTo(0.0, 0.0)
        context.lineTo(25.0, 25.0)
        context.lineTo(50.0, 0.0)
        context.closePath()

        context.moveTo(100.0, 100.0)
        context.lineTo(75.0, 75.0)
        context.lineTo(50.0, 100.0)
        context.closePath()

        context.stroke()

        canvas.saveBmp("multi_path_stroke.bmp")
    }

    @Test
    fun zigZagStroke() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.strokeStyle = "orange"
        context.fillStyle = "dark_blue"
        context.lineWidth = 3.0

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

        context.strokeStyle = "orange"
        context.fillStyle = "dark_blue"
        context.lineWidth = 1.0

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

        context.strokeStyle = "orange"
        context.fillStyle = "dark_blue"
        context.lineWidth = 1.0

        context.beginPath()
        context.arc(50.0, 50.0, 40.0, 0.0, 180.0)
        context.stroke()

        canvas.saveBmp("circle_stroke.bmp")
    }

    @Test
    fun circleFill() {
        val canvas = MagickCanvas.create(100, 100)
        val context = canvas.context2d

        context.strokeStyle = "orange"
        context.fillStyle = "dark_blue"
        context.lineWidth = 1.0

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

        context.beginPath()
        context.arc(50.0, 50.0, 40.0, 0.0, 180.0)
        context.closePath()

        context.fillStyle = "dark_blue"
        context.fill()

        context.strokeStyle = "red"
        context.setLineWidth(2.0)
        context.stroke()

        canvas.saveBmp("circle_fill_stroke.bmp")
    }

    @Test
    fun ellipse() {
        val canvas = MagickCanvas.create(200, 200)
        val context = canvas.context2d

        context.beginPath()
        context.moveTo(150.0, 175.0)
        context.lineTo(150.0, 75.0)

        // 100.0, 100.0, 0.0, 0u, 1u, startAngle=-90.0, endAngle=-180.0, anticlockwise=true
        context.ellipse(
            x = 150.0,
            y = 175.0,
            radiusX = 100.0,
            radiusY = 100.0,
            rotation = 0.0,
            startAngle = -90.0,
            endAngle = -180.0,
            anticlockwise = true
        )
        context.closePath()

        context.fillStyle = "dark_blue"
        context.fill()

        context.strokeStyle = "red"
        context.lineWidth = 4.0
        context.stroke()

        canvas.saveBmp("ellipse.bmp")
    }

    companion object {
        var Context2d.lineWidth: Double
            get() = error("lineWidth is write only")
            set(value) {
                setLineWidth(value)
            }

        var Context2d.fillStyle: Any?
            get() = error("fillStyle is write only")
            set(value) {
                val color = when (value) {
                    is Color -> value
                    is String -> Colors.parseColor(value)
                    null -> null
                    else -> error("Unsupported fill style: $value")
                }

                setFillStyle(color)
            }

        var Context2d.strokeStyle: Any?
            get() = error("strokeStyle is write only")
            set(value) {
                val color = when (value) {
                    is Color -> value
                    is String -> Colors.parseColor(value)
                    null -> null
                    else -> error("Unsupported fill style: $value")
                }

                setStrokeStyle(color)
            }
    }
}
