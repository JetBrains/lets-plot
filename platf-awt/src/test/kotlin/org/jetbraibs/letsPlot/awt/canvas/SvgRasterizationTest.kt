/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbraibs.letsPlot.awt.canvas

import demo.svgMapping.model.ReferenceSvgModel
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.awt.plot.VisualPlotTestBase
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import kotlin.test.Test

class SvgRasterizationTest : VisualPlotTestBase() {
    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel()
        val fig = SvgCanvasFigure(svg)
        val canvasPeer = AwtCanvasPeer()
        fig.mapToCanvas(canvasPeer)

        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvas = canvasPeer.createCanvas(w, h)
        val ctx = canvas.context2d
        fig.paint(canvas.context2d)

        imageComparer.assertBitmapEquals("svg_reference_test.png", canvas.takeSnapshot().bitmap)

        ctx.dispose()
    }
}