package org.jetbrains.letsPlot.pythonExtension.interop

import demo.svgMapping.model.ReferenceSvgModel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure2
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class SvgTest {
    companion object {
        private val embeddedFontsManager by lazy { newEmbeddedFontsManager() }
        private val imageComparer by lazy { createImageComparer(embeddedFontsManager) }
    }

    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel()

        assertSvg("svg_reference_test.png", svg)
    }


    private fun assertSvg(expectedFileName: String, svg: SvgSvgElement) {
        val fig = SvgCanvasFigure2(svg)

        val canvasPeer = MagickCanvasPeer(pixelDensity = 1.0, fontManager = embeddedFontsManager)
        fig.mapToCanvas(canvasPeer)

        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvas = canvasPeer.createCanvas(w, h)
        fig.paint(canvas.context2d)

        imageComparer.assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)

        canvas.dispose()
        canvasPeer.dispose()
    }
}
