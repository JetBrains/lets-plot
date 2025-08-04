package org.jetbrains.letsPlot.pythonExtension.interop

import demo.svgMapping.model.ReferenceSvgModel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
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
        val canvasPeer = MagickCanvasPeer(embeddedFontsManager)
        val svgFigure = SvgCanvasFigure()
        svgFigure.mapToCanvas(canvasPeer)

        svgFigure.svgSvgElement = svg

        val canvas = canvasPeer.createCanvas(
            svg.width().get() ?: error("SVG width is not specified"),
            svg.height().get() ?: error("SVG height is not specified")
        )

        svgFigure.draw(canvas.context2d)

        val snapshot = canvas.takeSnapshot()
        imageComparer.assertBitmapEquals(expectedFileName, snapshot.bitmap)

        snapshot.dispose()
        canvas.dispose()
    }
}
