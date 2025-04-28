/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import demo.svgMapping.model.ReferenceSvgModel
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure

object ReferenceSvgDemo {
    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        val w = 500
        val h = 500
        val svgGroup = ReferenceSvgModel.createModel()
        val svgRoot = SvgSvgElement(w.toDouble(), h.toDouble())
        svgRoot.children().add(svgGroup)

        val canvasControl = MagickCanvasControl(w, h)
        SvgCanvasFigure(svgRoot).mapToCanvas(canvasControl)

        val canvas = canvasControl.children.single() as MagickCanvas

        // Save the image to a file
        val outputFilename = "svg_demo.bmp"
        if (ImageMagick.MagickWriteImage(canvas.img, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }

    }
}
