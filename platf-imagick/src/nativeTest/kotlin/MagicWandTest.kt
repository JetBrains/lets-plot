import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.*
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// This test class is used to demonstrate the usage of the ImageMagick library
class MagicWandTest {

    fun checkDrawingWandError(wand: CPointer<ImageMagick.DrawingWand>?, str: String): Boolean {
        return true
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun simple() {
        // Initialize the MagickWand environment
        ImageMagick.MagickWandGenesis()
        // Create a MagickWand instance
        val wand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        try {
            val w = 50
            val h = 50

            // Set the canvas size and background color
            val background = ImageMagick.NewPixelWand()
            ImageMagick.PixelSetColor(background, "white")
            ImageMagick.MagickNewImage(wand, w.toULong(), h.toULong(), background)

            // Draw a rectangle
            val draw = ImageMagick.NewDrawingWand()
            val pixel = ImageMagick.NewPixelWand()

            // Set rectangle color
            ImageMagick.PixelSetColor(pixel, "orange")
            ImageMagick.DrawSetFillColor(draw, pixel)

            // Draw the rectangle
            ImageMagick.DrawRectangle(draw, 10.0, 10.0, 40.0, 40.0)

            // Set circle color
            ImageMagick.PixelSetColor(pixel, "red")
            ImageMagick.DrawSetFillColor(draw, pixel)

            // Draw the circle inside the rectangle
            ImageMagick.DrawCircle(draw, 25.0, 25.0, 25.0, 20.0)

            // Draw text with a font name
            ImageMagick.PixelSetColor(pixel, "black") // Set text color
            ImageMagick.DrawSetFillColor(draw, pixel)
            ImageMagick.DrawSetFont(draw, "DejaVu-Sans-Bold") // Use font name
            ImageMagick.DrawSetFontSize(draw, 36.0) // Set font size

            memScoped {
                val text: CValues<ByteVar> = "Hello, MagicWand!".cstr // Convert to C string
                //DrawAnnotation(draw, 150.0, 300.0, text.getPointer(this)) // Get pointer within scope
            }

            // Apply the drawing to the MagickWand
            ImageMagick.MagickDrawImage(wand, draw)

            // Never saves the image - this is just a test to check if the code compiles and runs
            val outputFilename = "magickwand_simple.bmp"
            if (false && ImageMagick.MagickWriteImage(wand, outputFilename) == ImageMagick.MagickFalse) {
                throw RuntimeException("Failed to write image")
            }

            println("Image saved to $outputFilename")

            val pixelData = ByteArray(w * h * 4) // RGBA 8-bit per channel
            memScoped {
                val byteBuffer = pixelData.refTo(0) // Pointer to ByteArray
                val success = ImageMagick.MagickExportImagePixels(
                    wand,
                    0, 0, w.toULong(), h.toULong(),
                    "RGBA", ImageMagick.StorageType.CharPixel, byteBuffer
                )
                require(success == ImageMagick.MagickTrue) { "Failed to export pixels" }

                val lines = pixelData.asSequence().windowed(4 * w.toInt(), 4 * h.toInt()).toList()
                val strLines = lines.map { line ->
                    line.windowed(4, 4).joinToString { pixel ->
                        pixel.joinToString(separator = "") { it.toHexString() }
                    }
                }
                val str = strLines.joinToString("\n")
                println("Simple demo")
                println("${strLines.size} * ${lines[0].size}")
                println(str)
            }

        } finally {
            // Clean up resources
            ImageMagick.DestroyMagickWand(wand)
            ImageMagick.MagickWandTerminus()
        }
    }

    @Test
    fun miterJoinWithOppositeSegments() {
        ImageMagick.MagickWandGenesis()
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        val backgroundWand = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundWand, "white")
        ImageMagick.MagickNewImage(magickWand, 100u, 100u, backgroundWand)

        val drawingWand = ImageMagick.NewDrawingWand()
        val strokeWand = ImageMagick.NewPixelWand()

        ImageMagick.PixelSetColor(strokeWand, "orange")
        ImageMagick.DrawSetStrokeColor(drawingWand, strokeWand)

        ImageMagick.DrawSetStrokeWidth(drawingWand, 20.0)

        // MiterJoin with a line in the opposite direction
        ImageMagick.DrawSetStrokeLineJoin(drawingWand, ImageMagick.LineJoin.MiterJoin)
        ImageMagick.DrawPathStart(drawingWand)
        ImageMagick.DrawPathMoveToAbsolute(drawingWand, 5.0, 30.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 95.0, 30.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 5.0, 30.0)
        ImageMagick.DrawPathFinish(drawingWand)

        // BevelJoin with a line in the opposite direction
        ImageMagick.DrawSetStrokeLineJoin(drawingWand, ImageMagick.LineJoin.BevelJoin)
        ImageMagick.DrawPathStart(drawingWand)
        ImageMagick.DrawPathMoveToAbsolute(drawingWand, 5.0, 70.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 95.0, 70.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 5.0, 70.0)
        ImageMagick.DrawPathFinish(drawingWand)


        ImageMagick.MagickDrawImage(magickWand, drawingWand)
        val outputFilename = "magickwand_miter_join_artifact.bmp"

        // Never saves the image - this is just a test to check if the code compiles and runs
        if (false && ImageMagick.MagickWriteImage(magickWand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }

        ImageMagick.DestroyPixelWand(backgroundWand)
        ImageMagick.DestroyPixelWand(strokeWand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyMagickWand(magickWand)
        ImageMagick.MagickWandTerminus()
    }

    @Test
    fun affine_ry() {
        ImageMagick.MagickWandGenesis()
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        val backgroundWand = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundWand, "white")
        ImageMagick.MagickNewImage(magickWand, 300u, 300u, backgroundWand)

        val drawingWand = ImageMagick.NewDrawingWand()
        val pixelWand = ImageMagick.NewPixelWand()

        ImageMagick.PixelSetColor(pixelWand, "black")
        ImageMagick.DrawSetFillColor(drawingWand, pixelWand)
        ImageMagick.DrawRectangle(drawingWand, 50.0, 50.0, 200.0, 200.0)

        memScoped {
            val m = alloc<ImageMagick.AffineMatrix>()
            m.sx = 1.0
            m.sy = 1.0
            m.ry = 0.3420201241970062
            m.rx = 0.0
            m.tx = 0.0
            m.ty = 0.0
            ImageMagick.DrawAffine(drawingWand, m.ptr)
        }

        ImageMagick.PixelSetColor(pixelWand, "green")
        ImageMagick.DrawSetFillColor(drawingWand, pixelWand)
        ImageMagick.DrawSetFillOpacity(drawingWand, 0.7)
        ImageMagick.DrawRectangle(drawingWand, 50.0, 50.0, 200.0, 200.0)

        ImageMagick.MagickDrawImage(magickWand, drawingWand)
        val outputFilename = "affine_ry.bmp"

        if (false && ImageMagick.MagickWriteImage(magickWand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }

        ImageMagick.DestroyPixelWand(backgroundWand)
        ImageMagick.DestroyPixelWand(pixelWand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyMagickWand(magickWand)
        ImageMagick.MagickWandTerminus()
    }

    @Test
    fun simpleBezierCurve() {
        ImageMagick.MagickWandGenesis()
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        val backgroundWand = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundWand, "white")
        ImageMagick.MagickNewImage(magickWand, 300u, 300u, backgroundWand)

        val drawingWand = ImageMagick.NewDrawingWand()
        val pixelWand = ImageMagick.NewPixelWand()

        ImageMagick.PixelSetColor(pixelWand, "black")
        ImageMagick.DrawSetFillColor(drawingWand, pixelWand)
        ImageMagick.DrawSetStrokeColor(drawingWand, pixelWand)
        ImageMagick.DrawSetStrokeWidth(drawingWand, 2.0)

        // Draw a simple Bezier curve
        memScoped {
            val bezierPoints = allocArray<ImageMagick.PointInfo>(3)
            bezierPoints[0].x = 50.0; bezierPoints[0].y = 50.0
            bezierPoints[1].x = 150.0; bezierPoints[1].y = 50.0
            bezierPoints[2].x = 150.0; bezierPoints[2].y = 150.0

            ImageMagick.DrawBezier(drawingWand, 3.convert(), bezierPoints)
        }

        ImageMagick.MagickDrawImage(magickWand, drawingWand)
        val outputFilename = "simple_bezier_curve.bmp"

        if (true && ImageMagick.MagickWriteImage(magickWand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }

        ImageMagick.DestroyPixelWand(backgroundWand)
        ImageMagick.DestroyPixelWand(pixelWand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyMagickWand(magickWand)
        ImageMagick.MagickWandTerminus()
    }

    @Test
    fun bezier() {
        val cp0 = DoubleVector(150.0, 100.0)
        val cp1 = DoubleVector(150.0, 127.614237)
        val cp2 = DoubleVector(127.614237, 150.0)
        val cp3 = DoubleVector(100.0, 150.0)

        ImageMagick.MagickWandGenesis()

        val drawingWand = ImageMagick.NewDrawingWand()
        ImageMagick.DrawPathStart(drawingWand)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, cp0.x, cp0.y)
        ImageMagick.DrawPathCurveToAbsolute(drawingWand, cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
        ImageMagick.DrawPathFinish(drawingWand)

        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        val backgroundWand = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundWand, "white")
        ImageMagick.MagickNewImage(magickWand, 500.convert(), 500.convert(), backgroundWand)
        ImageMagick.MagickDrawImage(magickWand, drawingWand)

        val outputFilename = "bezier.bmp"
        if (true && ImageMagick.MagickWriteImage(magickWand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }
        ImageMagick.DestroyPixelWand(backgroundWand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyMagickWand(magickWand)
        ImageMagick.MagickWandTerminus()
    }

    @Test
    fun ellipseViaBezier() {
        ImageMagick.MagickWandGenesis()

        val drawingWand = ImageMagick.NewDrawingWand()!!

        val x: Double = 150.0
        val y: Double = 375.0
        val radiusX: Double = 100.0
        val radiusY: Double = 100.0
        val rotation: Double = 0.0
        val startAngle: Double = -PI/2
        val endAngle: Double = -PI
        val anticlockwise: Boolean = true

        if (radiusX < 0 || radiusY < 0) {
            println("Warning: ellipse radii must be non-negative.")
            // Or potentially call drawEllipticalArcPathBezier with 0 radius which handles MoveTo
            drawEllipticalArcPathBezier(drawingWand, x, y, 0.0, 0.0, rotation, startAngle, 0.0) { x, y -> x to y }
            return
        }

        // Calculate normalized angles and the total sweep angle
        val (_, _, sweepAngle) = normalizeAnglesAndSweep(startAngle, endAngle, anticlockwise)

        // Define the arc path on the DrawingWand
        // We use the original startAngle here, as the sweepAngle dictates the full extent
        val pathDefinedOk = drawEllipticalArcPathBezier(
            drawingWand = drawingWand,
            cx = x,
            cy = y,
            rx = radiusX,
            ry = radiusY,
            rotation = rotation,
            arcStartAngle = startAngle, // Use original start angle
            sweepAngle = sweepAngle,    // Use calculated sweep
            transform = { x, y -> x to y }
        )

        if (!pathDefinedOk) {
            println("Warning: Failed to define elliptical arc path on DrawingWand.")
            // Error message was likely printed by checkDrawingWandError
        }


        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")
        val backgroundWand = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundWand, "white")
        ImageMagick.MagickNewImage(magickWand, 500.convert(), 500.convert(), backgroundWand)

        ImageMagick.MagickDrawImage(magickWand, drawingWand)
        val outputFilename = "ellipse_via_bezier_curves.bmp"
        if (true && ImageMagick.MagickWriteImage(magickWand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }
        ImageMagick.DestroyPixelWand(backgroundWand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyMagickWand(magickWand)
        ImageMagick.MagickWandTerminus()
    }

    /**
     * Calculates the start/end angles normalized to [0, 2*PI) and the total sweep angle.
     * Handles anticlockwise direction and angle wrapping.
     *
     * @return Triple(normalizedStart, normalizedEnd, sweepAngle)
     *         sweepAngle is positive for clockwise, negative for anticlockwise.
     */
    fun normalizeAnglesAndSweep(
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ): Triple<Double, Double, Double> {
        val twoPi = 2.0 * PI

        // Normalize angles to [0, 2*PI)
        val normStart = (startAngle % twoPi + twoPi) % twoPi
        val normEnd = (endAngle % twoPi + twoPi) % twoPi

        var sweep = if (!anticlockwise) { // Clockwise
            if (normEnd >= normStart) {
                normEnd - normStart
            } else {
                twoPi - normStart + normEnd // Wrap around
            }
        } else { // Anticlockwise
            if (normStart >= normEnd) {
                normStart - normEnd
            } else {
                twoPi - normEnd + normStart // Wrap around
            }
        }

        // Adjust sweep if the original angles indicated multiple full circles
        val angleDiff = endAngle - startAngle
        if (!anticlockwise && angleDiff >= twoPi) {
            sweep += floor(angleDiff / twoPi) * twoPi
        } else if (anticlockwise && angleDiff <= -twoPi) {
            // For anticlockwise, a large negative diff means more sweep
            sweep += floor(-angleDiff / twoPi) * twoPi // Add positive sweep magnitude
        }

        // Ensure non-zero sweep if angles are different but normalized same value (e.g. 0 and 2*PI)
        if (abs(sweep) < 1e-9 && abs(angleDiff) > 1e-9) {
            sweep = if (angleDiff > 0 && !anticlockwise || angleDiff < 0 && anticlockwise) twoPi else -twoPi
        }

        // Apply direction sign to sweep
        if (anticlockwise) {
            sweep = -abs(sweep) // Make sweep negative for anticlockwise
        } else {
            sweep = abs(sweep) // Make sweep positive for clockwise
        }


        return Triple(normStart, normEnd, sweep)
    }




    /**
     * Draws an elliptical arc path using Bezier curve approximation onto the DrawingWand.
     * Takes pre-calculated start angle and sweep angle.
     * Applies ellipse rotation/translation, then the canvas transform lambda.
     *
     * @param drawingWand The wand to draw on.
     * @param cx Ellipse center X.
     * @param cy Ellipse center Y.
     * @param rx Ellipse radius X.
     * @param ry Ellipse radius Y.
     * @param rotation Ellipse rotation in radians.
     * @param arcStartAngle Starting angle of the arc in radians (normalized or actual start).
     * @param sweepAngle Total sweep angle in radians (+ve clockwise, -ve anticlockwise).
     * @param transform Lambda to apply the current canvas transformation.
     * @return True if path definition succeeded without detected errors.
     */
    fun drawEllipticalArcPathBezier(
        drawingWand: CPointer<ImageMagick.DrawingWand>,
        cx: Double,
        cy: Double,
        rx: Double,
        ry: Double,
        rotation: Double,
        arcStartAngle: Double,
        sweepAngle: Double,
        transform: (x: Double, y: Double) -> Pair<Double, Double>
    ): Boolean {
        if (rx <= 0 || ry <= 0 || abs(sweepAngle) < 1e-9) {
            // Nothing to draw, but not an error state for path definition itself
            // Though, might need a single MoveTo if this is the only element? Handle in caller.
            // Let's just move to the start point in this case.
            val cosA = cos(arcStartAngle); val sinA = sin(arcStartAngle)
            val p0x_local = rx * cosA;    val p0y_local = ry * sinA
            val (p0e_x, p0e_y) = transformEllipsePoint(p0x_local, p0y_local, cx, cy, rotation)
            val (tp0x, tp0y) = transform(p0e_x, p0e_y)

            ImageMagick.DrawClearException(drawingWand)
            ImageMagick.DrawPathStart(drawingWand)
            ImageMagick.DrawPathMoveToAbsolute(drawingWand, tp0x, tp0y)
            ImageMagick.DrawPathFinish(drawingWand)
            return !checkDrawingWandError(drawingWand, "Degenerate Ellipse Arc MoveTo")
        }

        // Max angle per Bezier segment (e.g., 90 degrees)
        val maxAnglePerSegment = PI / 2.0
        val numSegments = ceil(abs(sweepAngle) / maxAnglePerSegment).toInt().coerceAtLeast(1)
        val deltaAngle = sweepAngle / numSegments.toDouble() // Angle step per segment (signed)
        val kappa = (4.0 / 3.0) * tan(abs(deltaAngle) / 4.0) // Bezier approximation factor

        // Clear any previous drawing wand exception state
        ImageMagick.DrawClearException(drawingWand)
        ImageMagick.DrawPathStart(drawingWand)

        var currentAngle = arcStartAngle
        var errorOccurred = false

        for (i in 0 until numSegments) {
            val angle1 = currentAngle
            val angle2 = currentAngle + deltaAngle

            memScoped {
                // Calculate points for this segment in ellipse local space (0,0 center, no rotation)
                val cosA1 = cos(angle1); val sinA1 = sin(angle1)
                val cosA2 = cos(angle2); val sinA2 = sin(angle2)

                val p0x_local = rx * cosA1;    val p0y_local = ry * sinA1 // Start P0
                val p3x_local = rx * cosA2;    val p3y_local = ry * sinA2 // End P3

                // Control points (local)
                val p1x_local = p0x_local - kappa * ry * sinA1 // Use ry for x tangent calc with ellipse aspect ratio
                val p1y_local = p0y_local + kappa * rx * cosA1 // Use rx for y tangent calc
                val p2x_local = p3x_local + kappa * ry * sinA2
                val p2y_local = p3y_local - kappa * rx * cosA2

                // Apply ellipse's own transform (rotation + translation)
                val (p0e_x, p0e_y) = transformEllipsePoint(p0x_local, p0y_local, cx, cy, rotation)
                val (p1e_x, p1e_y) = transformEllipsePoint(p1x_local, p1y_local, cx, cy, rotation)
                val (p2e_x, p2e_y) = transformEllipsePoint(p2x_local, p2y_local, cx, cy, rotation)
                val (p3e_x, p3e_y) = transformEllipsePoint(p3x_local, p3y_local, cx, cy, rotation)

                // Apply the current canvas transform
                val (tp0x, tp0y) = transform(p0e_x, p0e_y)
                val (tp1x, tp1y) = transform(p1e_x, p1e_y)
                val (tp2x, tp2y) = transform(p2e_x, p2e_y)
                val (tp3x, tp3y) = transform(p3e_x, p3e_y)

                println("Bezier segment $i: $tp0x, $tp0y -> $tp1x, $tp1y -> $tp2x, $tp2y -> $tp3x, $tp3y")

                // Add to DrawingWand path
                if (i == 0) {
                    ImageMagick.DrawPathMoveToAbsolute(drawingWand, tp0x, tp0y)
                }

                val bezierPoints = allocArray<ImageMagick.PointInfo>(3)
                bezierPoints[0].x = tp1x; bezierPoints[0].y = tp1y // Control Point 1
                bezierPoints[1].x = tp2x; bezierPoints[1].y = tp2y // Control Point 2
                bezierPoints[2].x = tp3x; bezierPoints[2].y = tp3y // End Point

                ImageMagick.DrawBezier(drawingWand, 3.convert(), bezierPoints)
            } // End memScoped

            currentAngle = angle2

            // Optional early exit on error
            // if (checkDrawingWandError(drawingWand, "DrawBezier segment $i")) { errorOccurred = true; break }
        } // End loop

        // DO NOT call DrawPathClose for an arc unless specifically intended
        ImageMagick.DrawPathFinish(drawingWand)

        // Check for errors accumulated during the path definition
        //if (checkDrawingWandError(drawingWand, "Elliptical Arc Bezier Path Definition")) {
        //    errorOccurred = true
        //}

        return !errorOccurred
    }

    // Helper function to apply ellipse rotation and translation
// (Assumes this exists or is added)
    private fun transformEllipsePoint(
        px: Double, py: Double, // Point in local ellipse space (center 0,0, no rotation)
        cx: Double, cy: Double, // Ellipse center
        rotation: Double // Ellipse rotation
    ): Pair<Double, Double> {
        val cosRot = cos(rotation)
        val sinRot = sin(rotation)
        val pxRotated = px * cosRot - py * sinRot
        val pyRotated = px * sinRot + py * cosRot
        return Pair(pxRotated + cx, pyRotated + cy)
    }
}
