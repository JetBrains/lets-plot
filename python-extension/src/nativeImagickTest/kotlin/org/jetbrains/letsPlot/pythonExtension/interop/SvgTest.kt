package org.jetbrains.letsPlot.pythonExtension.interop

import demo.svgMapping.model.ReferenceSvgModel
import demoAndTestShared.ImageComparer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasProvider
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import kotlin.org.jetbrains.letsPlot.pythonExtension.interop.MagickBitmapIO
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class SvgTest {
    private val imageComparer = ImageComparer(
        suffix = getOSName(),
        expectedDir = getCurrentDir() + "/src/nativeImagickTest/resources/expected/",
        outDir = getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider,
        bitmapIO = MagickBitmapIO,
        tol = 1
    )

    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel()

        assertSvg("svg_reference_test.bmp", svg)
    }


    fun assertSvg(expectedFileName: String, svg: SvgSvgElement) {
        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvasControl = MagickCanvasControl(w = w, h = h, pixelDensity = 1.0, fontManager = MagickFontManager.DEFAULT)
        SvgCanvasFigure(svg).mapToCanvas(canvasControl)

        val canvas = canvasControl.children.single() as MagickCanvas
        imageComparer.assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)
    }

}
