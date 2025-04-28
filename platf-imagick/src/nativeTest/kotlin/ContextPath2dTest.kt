
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickContext2d
import kotlin.math.PI
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


@Ignore
class ContextPath2dTest {
    private val outDir: String = getCurrentDir() + "/build/image-test/"
    private val expectedDir: String = getCurrentDir() + "/src/nativeTest/resources/expected/"
    private val w = 100.0
    private val h = 100.0

    private val strokeColor = "#000000"
    private val fillColor = "#000000"
    private val filledStrokeColor = "#000080"
    private val strokedFillColor = "#FFC000"

    init {
        mkDir(outDir)
    }

    val imageComparer = ImageComparer(
        expectedDir = expectedDir,
        outDir = outDir
    )

    fun createCanvas(): Pair<MagickCanvas, MagickContext2d> {
        val canvas = MagickCanvas.create(w, h)
        return canvas to canvas.context2d as MagickContext2d
    }


    @Test
    fun shearedEllipse() {
        val (canvas, ctx) = createCanvas()

        ctx.strokeStyle = strokeColor
        ctx.beginPath()
        ctx.save()
        ctx.transform(sx = 1.0, ry = 0.3420201241970062, rx = 0.0, sy = 1.0, tx = 0.0, ty = -30.0)
        ctx.ellipse(
            x = 50.0,
            y = 60.0,
            radiusX = 45.0,
            radiusY = 20.0,
            rotation = 0.0,
            startAngle = 0.0,
            endAngle = PI * 2,
            anticlockwise = true
        )
        ctx.restore()
        ctx.closePath()
        ctx.stroke()

        imageComparer.assertImageEquals("sheared_ellipse.bmp", canvas.img!!)
    }

    @Test
    fun shearedCircularArc() {
        val (canvas, ctx) = createCanvas()

        ctx.strokeStyle = strokeColor
        ctx.beginPath()
        ctx.save()
        ctx.transform(sx = 1.0, ry = 0.3420201241970062, rx = 0.0, sy = 1.0, tx = 0.0, ty = -30.0)
        ctx.moveTo(95.0, 95.0)
        ctx.lineTo(95.0, 5.0)
        ctx.ellipse(
            x = 95.0,
            y = 95.0,
            radiusX = 90.0,
            radiusY = 90.0,
            rotation = 0.0,
            startAngle = -PI / 2,
            endAngle = PI,
            anticlockwise = true
        )
        ctx.restore()
        ctx.closePath()
        ctx.stroke()

        imageComparer.assertImageEquals("sheared_circular_arc.bmp", canvas.img!!)
    }

    @Test
    fun nestedTranslates() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = fillColor

        ctx.save()
        ctx.translate(0.0, 50.0)
        ctx.save()
        ctx.translate(33.0, 0.0)
        ctx.beginPath()
        ctx.arc(0.0, 0.0, 5.0, 0.0, 2 * PI)
        ctx.fill()
        ctx.restore()

        ctx.save()
        ctx.translate(66.0, 0.0)
        ctx.beginPath()
        ctx.arc(x = 0.0, y = 0.0, radius = 5.0, startAngle = 0.0, endAngle = 2 * PI)
        ctx.fill()
        ctx.restore()

        ctx.restore()

        imageComparer.assertImageEquals("nested_translates.bmp", canvas.img!!)
    }

    @Test
    fun multiPathFill() {
        val (canvas, ctx) = createCanvas()
        ctx.fillStyle = strokedFillColor
        ctx.strokeStyle = filledStrokeColor
        ctx.lineWidth = 5.0

        ctx.moveTo(50.0, 50.0)
        ctx.lineTo(125.0, 125.0)

        ctx.beginPath()
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(25.0, 25.0)
        ctx.lineTo(50.0, 0.0)
        ctx.closePath()

        ctx.moveTo(100.0, 100.0)
        ctx.lineTo(75.0, 75.0)
        ctx.lineTo(50.0, 100.0)
        ctx.closePath()

        ctx.fill()
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "multi_path_fill.bmp",
            actualWand = canvas.img!!,
        )
    }

    @Test
    fun multiPathStroke() {
        val (canvas, ctx) = createCanvas()

        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 2.0

        ctx.beginPath()
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(25.0, 25.0)
        ctx.lineTo(50.0, 0.0)
        ctx.closePath()

        ctx.moveTo(100.0, 100.0)
        ctx.lineTo(75.0, 75.0)
        ctx.lineTo(50.0, 100.0)
        ctx.closePath()

        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "multi_path_stroke.bmp",
            actualWand = canvas.img!!,
        )
    }

    @Test
    fun zigZagStroke() {
        val (canvas, ctx) = createCanvas()
        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 3.0

        ctx.beginPath()
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(50.0, 25.0)
        ctx.lineTo(0.0, 50.0)
        ctx.lineTo(50.0, 75.0)
        ctx.lineTo(0.0, 100.0)

        ctx.moveTo(100.0, 0.0)
        ctx.lineTo(50.0, 25.0)
        ctx.lineTo(100.0, 50.0)
        ctx.lineTo(50.0, 75.0)
        ctx.lineTo(100.0, 100.0)

        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "zigzag_stroke.bmp",
            actualWand = canvas.img!!,
        )
    }

    @Test
    fun zigZagFill() {
        val (canvas, ctx) = createCanvas()
        ctx.fillStyle = fillColor
        ctx.lineWidth = 1.0

        ctx.beginPath()
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(50.0, 25.0)
        ctx.lineTo(0.0, 50.0)
        ctx.lineTo(50.0, 75.0)
        ctx.lineTo(0.0, 100.0)
        ctx.closePath()

        ctx.moveTo(50.0, 0.0)
        ctx.lineTo(100.0, 25.0)
        ctx.lineTo(50.0, 50.0)
        ctx.lineTo(100.0, 75.0)
        ctx.lineTo(50.0, 100.0)
        ctx.closePath()

        ctx.fill()

        imageComparer.assertImageEquals(
            expectedFileName = "zigzag_fill.bmp",
            actualWand = canvas.img!!,
        )
    }

    @Test
    fun circleStroke() {
        val (canvas, ctx) = createCanvas()
        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 1.0

        ctx.beginPath()
        ctx.arc(x = 50.0, y = 50.0, radius = 40.0, startAngle = -PI, endAngle = 0.0)
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "circle_stroke.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun circleFill() {
        val (canvas, ctx) = createCanvas()
        ctx.fillStyle = fillColor
        ctx.lineWidth = 1.0

        ctx.beginPath()
        ctx.arc(x = 50.0, y = 50.0, radius = 40.0, startAngle = -PI, endAngle = 0.0)
        ctx.closePath()

        ctx.fill()

        imageComparer.assertImageEquals(
            expectedFileName = "circle_fill.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun circleFillStroke() {
        val (canvas, ctx) = createCanvas()
        ctx.beginPath()
        ctx.arc(x = 50.0, y = 50.0, radius = 40.0, startAngle = -PI, endAngle = 0.0)
        ctx.closePath()

        ctx.fillStyle = strokedFillColor
        ctx.fill()

        ctx.strokeStyle = filledStrokeColor
        ctx.setLineWidth(2.0)
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "circle_fill_stroke.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun ellipse() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = fillColor

        ctx.beginPath()
        //ctx.moveTo(50.0, 50.0)
        ctx.ellipse(
            x = 50.0,
            y = 50.0,
            radiusX = 20.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngle = -PI,
            endAngle = -2 * PI,
            anticlockwise = true
        )
        ctx.closePath()
        ctx.fill()

        imageComparer.assertImageEquals(
            expectedFileName = "ellipse.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun rotatedEllipse() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = fillColor
        ctx.beginPath()
        ctx.ellipse(
            x = 50.0,
            y = 50.0,
            radiusX = 20.0,
            radiusY = 50.0,
            rotation = PI / 4,
            startAngle = -PI,
            endAngle = -2 * PI,
            anticlockwise = true
        )
        ctx.closePath()
        ctx.fill()

        imageComparer.assertImageEquals(
            expectedFileName = "rotated_ellipse.bmp",
            actualWand = canvas.img!!
        )
    }


    @Test
    fun pathTransformOnBuild() {
        val (canvas, ctx) = createCanvas()
        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 2.0

        ctx.save()
        ctx.translate(50.0, 50.0)
        ctx.rotate(PI / 2)
        ctx.scale(0.5, 0.5)
        ctx.beginPath()
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(50.0, 50.0)
        ctx.restore()
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "path_transform_on_build.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun arcTransformsAfterRestore() {
        val (canvas, ctx) = createCanvas()
        ctx.fillStyle = fillColor

        ctx.save()
        ctx.translate(50.0, 50.0)
        ctx.beginPath()
        ctx.scale(1.0, 0.5)
        ctx.arc(0.0, 0.0, 50.0, 0.0, 2 * PI)
        ctx.restore()

        ctx.fill()

        imageComparer.assertImageEquals(
            expectedFileName = "arc_transform_after_restore.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun text_SkewTransform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = strokeColor
        ctx.strokeStyle = strokeColor
        ctx.setFont(Font(fontFamily = "Times New Roman", fontSize = 30.0))

        ctx.transform(sx = 1.0, ry = 0.0, rx = -0.33, sy = 1.0, tx = 0.0, ty = 0.0)

        ctx.beginPath()
        ctx.moveTo(w * 0.5, h * 0.5)
        ctx.lineTo(w * 0.5, h * 0.5 - 30)
        ctx.lineTo(w * 0.5 + 35, h * 0.5 - 30)
        ctx.lineTo(w * 0.5 + 35, h * 0.5)
        ctx.closePath()
        ctx.stroke()
        //ctx.clip()


        ctx.fillText("Test", w * 0.5, h * 0.5)

        imageComparer.assertImageEquals("text_skew_transform.bmp", canvas.img!!)
    }


    @Test
    fun simpleBezierCurve() {
        val (canvas, ctx) = createCanvas()

        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 2.0

        ctx.beginPath()
        ctx.moveTo(0.0, 0.0)
        ctx.bezierCurveTo(50.0, 0.0, 50.0, 100.0, 100.0, 100.0)
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "simple_bezier_curve.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun bezierCurveInsidePath() {
        val (canvas, ctx) = createCanvas()

        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 2.0

        ctx.beginPath();
        ctx.moveTo(0, 20);
        ctx.lineTo(20, 20);
        ctx.bezierCurveTo(20, 80, 80, 80, 80, 20);
        ctx.lineTo(100, 20);
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "bezier_curve_inside_path.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun ellipseInsidePath() {
        val (canvas, ctx) = createCanvas()

        ctx.strokeStyle = strokeColor
        ctx.lineWidth = 2.0

        ctx.beginPath();
        ctx.moveTo(100, 10);
        ctx.lineTo(80, 10);
        ctx.ellipse(50, 20, 20, 20, 0, 0, -PI, false);
        ctx.lineTo(20, 10);
        ctx.lineTo(0, 10);
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "ellipse_inside_path.bmp",
            actualWand = canvas.img!!
        )
    }

    @Test
    fun roundedRectWithCurves() {
        val (canvas, ctx) = createCanvas()

        ctx.lineWidth = 2.0
        ctx.strokeStyle = filledStrokeColor
        ctx.fillStyle = strokedFillColor

        ctx.translate(5, 5)
        ctx.scale(3.0)
        ctx.beginPath()
        ctx.moveTo(25.6086387569017, 21.0)
        ctx.bezierCurveTo(25.6086387569017, 21.0, 28.7586387569017, 21.0, 28.7586387569017, 17.85)
        ctx.lineTo(28.7586387569017, 3.15)
        ctx.bezierCurveTo(28.7586387569017, 3.15, 28.7586387569017, 0.0, 25.6086387569017, 0.0)
        ctx.lineTo(3.37605456734872, 0.0)
        ctx.bezierCurveTo(3.37605456734872, 0.0, 0.2260545673487, 0.0, 0.2260545673487, 3.15)
        ctx.lineTo(0.2260545673487, 17.85)
        ctx.bezierCurveTo(0.2260545673487, 17.85, 0.2260545673487, 21.0, 3.37605456734872, 21.0)
        ctx.closePath()
        ctx.fill()
        ctx.stroke()

        imageComparer.assertImageEquals(
            expectedFileName = "rounded_rect_with_curves.bmp",
            actualWand = canvas.img!!
        )
    }

}
