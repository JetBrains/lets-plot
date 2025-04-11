import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import kotlin.math.PI
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@Ignore
class MagickContext2dTest {
    val imageComparer = ImageComparer(
        expectedDir = "src/nativeTest/resources/expected/",
        outDir = "build/image-test/diff/"
    )

    @Test
    fun nestedTranslates() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            fillStyle = "black"

            save()
            translate(0.0, 50.0)
            save()
            translate(33.0, 0.0)
            beginPath()
            arc(0.0, 0.0, 5.0, 0.0, 2 * PI)
            fill()
            restore()

            save()
            translate(66.0, 0.0)
            beginPath()
            arc(x = 0.0, y = 0.0, radius = 5.0, startAngle = 0.0, endAngle = 2 * PI)
            fill()
            restore()

            restore()
        }

        imageComparer.assertImageEquals("nested_translates.bmp", canvas.wand!!)
    }

    @Test
    fun multiPathFill() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            fillStyle = "orange"
            lineWidth = 5.0
            strokeStyle = "dark_blue"

            moveTo(50.0, 50.0)
            lineTo(125.0, 125.0)

            beginPath()
            moveTo(0.0, 0.0)
            lineTo(25.0, 25.0)
            lineTo(50.0, 0.0)
            closePath()

            moveTo(100.0, 100.0)
            lineTo(75.0, 75.0)
            lineTo(50.0, 100.0)
            closePath()

            fill()
            stroke()
        }
        imageComparer.assertImageEquals(
            expectedFileName = "multi_path_fill.bmp",
            actualWand = canvas.wand!!,
        )
    }

    @Test
    fun multiPathStroke() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            strokeStyle = "orange"
            lineWidth = 2.0

            beginPath()
            moveTo(0.0, 0.0)
            lineTo(25.0, 25.0)
            lineTo(50.0, 0.0)
            closePath()

            moveTo(100.0, 100.0)
            lineTo(75.0, 75.0)
            lineTo(50.0, 100.0)
            closePath()

            stroke()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "multi_path_stroke.bmp",
            actualWand = canvas.wand!!,
        )
    }

    @Test
    fun zigZagStroke() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            strokeStyle = "orange"
            fillStyle = "dark_blue"
            lineWidth = 3.0

            beginPath()
            moveTo(0.0, 0.0)
            lineTo(50.0, 25.0)
            lineTo(0.0, 50.0)
            lineTo(50.0, 75.0)
            lineTo(0.0, 100.0)

            moveTo(100.0, 0.0)
            lineTo(50.0, 25.0)
            lineTo(100.0, 50.0)
            lineTo(50.0, 75.0)
            lineTo(100.0, 100.0)

            stroke()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "zigzag_stroke.bmp",
            actualWand = canvas.wand!!,
        )
    }

    @Test
    fun zigZagFill() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            strokeStyle = "orange"
            fillStyle = "dark_blue"
            lineWidth = 1.0

            beginPath()
            moveTo(0.0, 0.0)
            lineTo(50.0, 25.0)
            lineTo(0.0, 50.0)
            lineTo(50.0, 75.0)
            lineTo(0.0, 100.0)
            closePath()

            moveTo(50.0, 0.0)
            lineTo(100.0, 25.0)
            lineTo(50.0, 50.0)
            lineTo(100.0, 75.0)
            lineTo(50.0, 100.0)
            closePath()

            fill()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "zigzag_fill.bmp",
            actualWand = canvas.wand!!,
        )
    }

    @Test
    fun circleStroke() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            strokeStyle = "orange"
            fillStyle = "dark_blue"
            lineWidth = 1.0

            beginPath()
            arc(x = 50.0, y = 50.0, radius = 40.0, startAngle = -PI, endAngle = 0.0)
            stroke()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "circle_stroke.bmp",
            actualWand = canvas.wand!!
        )
    }

    @Test
    fun circleFill() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            strokeStyle = "orange"
            fillStyle = "dark_blue"
            lineWidth = 1.0

            beginPath()
            arc(x = 50.0, y = 50.0, radius = 40.0, startAngle = -PI, endAngle = 0.0)
            closePath()

            fill()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "circle_fill.bmp",
            actualWand = canvas.wand!!
        )
    }

    @Test
    fun circleFillStroke() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            beginPath()
            arc(x = 50.0, y = 50.0, radius = 40.0, startAngle = -PI, endAngle = 0.0)
            closePath()

            fillStyle = "dark_blue"
            fill()

            strokeStyle = "red"
            setLineWidth(2.0)
            stroke()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "circle_fill_stroke.bmp",
            actualWand = canvas.wand!!
        )
    }

    @Test
    fun ellipse() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            fillStyle = "dark_blue"

            beginPath()
            ellipse(
                x = 50.0,
                y = 50.0,
                radiusX = 20.0,
                radiusY = 50.0,
                rotation = 0.0,
                startAngle = -PI,
                endAngle = -2 * PI,
                anticlockwise = true
            )
            closePath()
            fill()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "ellipse.bmp",
            actualWand = canvas.wand!!
        )
    }

    @Test
    fun ellipseWithRotation() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            fillStyle = "dark_blue"

            beginPath()
            ellipse(
                x = 50.0,
                y = 50.0,
                radiusX = 20.0,
                radiusY = 50.0,
                rotation = PI,
                startAngle = -PI,
                endAngle = -2 * PI,
                anticlockwise = true
            )
            closePath()
            fill()
        }

        canvas.saveBmp("ellipse_with_rotation.bmp")
    }

    @Test
    fun pathTransformOnBuild() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
           strokeStyle = "black"
            lineWidth = 2.0

            save()
            translate(50.0, 50.0)
            rotate(PI / 2)
            scale(0.5, 0.5)
            beginPath()
            moveTo(0.0, 0.0)
            lineTo(50.0, 50.0)
            restore()
            stroke()

        }

        imageComparer.assertImageEquals(
            expectedFileName = "path_transform_on_build.bmp",
            actualWand = canvas.wand!!
        )
    }

    @Test
    fun arcTransformsAfterRestore() {
        val canvas = MagickCanvas.create(100, 100)
        with(canvas.context2d) {
            fillStyle = "green"

            save()
            translate(75.0, 75.0)
            beginPath()
            scale(1.0, 0.5)
            arc(0.0, 0.0, 50.0, 0.0, 2 * PI)
            restore()

            fill()
        }

        imageComparer.assertImageEquals(
            expectedFileName = "arc_transform_after_restore.bmp",
            actualWand = canvas.wand!!
        )
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
