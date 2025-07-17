package org.jetbrains.letsPlot.pythonExtension.interop

import demo.svgMapping.model.ReferenceSvgModel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
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
        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvasControl = MagickCanvasControl(w = w, h = h, pixelDensity = 1.0, fontManager = embeddedFontsManager)
        SvgCanvasFigure(svg).mapToCanvas(canvasControl)

        val canvas = canvasControl.children.last() as MagickCanvas
        imageComparer.assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)
    }
}
