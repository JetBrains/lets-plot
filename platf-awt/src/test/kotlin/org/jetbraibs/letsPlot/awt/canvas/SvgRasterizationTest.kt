/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbraibs.letsPlot.awt.canvas

import demo.svgMapping.model.ReferenceSvgModel
import org.jetbrains.letsPlot.awt.canvas.AwtCanvas
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import kotlin.test.Test

class SvgRasterizationTest {
    @Test
    fun referenceTest() {
        val svg = ReferenceSvgModel.createModel()
        val svgFigure = SvgCanvasFigure(svg)
        val canvas = AwtCanvas.create(Vector(svgFigure.width, svgFigure.height), 1.0)

        svgFigure.render(canvas)

        //val canvasPane = CanvasPane(svgFigure)
        //val image = (canvasPane.components.first() as CanvasComponent).canvas.image
        //println(canvasPane)
        //val canvas = canvasControl.children.single() as MagickCanvas
        //assertImageEquals(expectedFileName, canvas.img!!)
        //
        //imageComparer.assertImageEquals("svg_reference_test.bmp", svg)
    }
}