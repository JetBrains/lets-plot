/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.util

import okio.Path.Companion.toPath
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_ANCHOR_MIDDLE
import org.jetbrains.letsPlot.nat.util.canvas.SkSnapshot
import org.jetbrains.letsPlot.nat.util.canvas.SkiaCanvasControl
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import org.jetbrains.skia.*
import kotlin.test.Test

class PlotPngExportNativeTest {

    @Test
    fun svgDoc() {
        try {
            // Stage 1
            val bitmap = Bitmap().apply {
                setImageInfo(
                    ImageInfo(
                        width = 400,
                        height = 400,
                        colorType = ColorType.RGBA_8888,
                        alphaType = ColorAlphaType.UNPREMUL,
                    )
                )
                allocPixels()
            }

            Canvas(bitmap).apply {
                clear(0xFF00FFFF.toInt())
                drawCircle(200.0f, 200.0f, 100.0f, Paint())
            }

            val image = Image.makeFromBitmap(bitmap)

            okio.FileSystem.SYSTEM.write("output.png".toPath()) {
                write(image.encodeToData(EncodedImageFormat.PNG)!!.bytes)
            }

            // Stage 2
            val svgRoot = SvgSvgElement(
                width = 200.0,
                height = 400.0
            ).apply {
                children().addAll(
                    listOf(
                        SvgRectElement(
                            x = 10.0,
                            y = 100.0,
                            width = 180.0,
                            height = 180.0
                        ).apply {
                            fill().set(SvgColors.ORANGE)
                        },
                        SvgCircleElement(
                            cx = 100.0,
                            cy = 190.0,
                            r = 50.0
                        ).apply {
                            fill().set(SvgColors.DARK_RED)
                        },
                        SvgTextElement(100.0, 195.0, "Hello, World").apply {
                            textAnchor().set(SVG_TEXT_ANCHOR_MIDDLE)
                            fillColor().set(org.jetbrains.letsPlot.commons.values.Color.WHITE)
                        },
                    )
                )
            }

            val ccc = SkiaCanvasControl(Vector(200, 400))
            val svgFigure = SvgCanvasFigure(svgRoot)
            svgFigure.mapToCanvas(ccc)
            val svgSnapshot = (svgFigure.makeSnapshot() as SkSnapshot).bitmap

            val svgImage = Image.makeFromBitmap(svgSnapshot)

            okio.FileSystem.SYSTEM.write("svg.png".toPath()) {
                write(svgImage.encodeToData(EncodedImageFormat.PNG)!!.bytes)
            }
            //snapshot.
            // Stage 3
            /*
        SvgSkikoView.render(svgRoot, canvas, Matrix33.IDENTITY)
        */

            println("Hello, world")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}