package org.jetbrains.letsPlot.visualtesting.svg

import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import org.jetbrains.letsPlot.visualtesting.ImageComparer

abstract class SvgTestBase {
    abstract val imageComparer: ImageComparer
    abstract val canvasPeer: CanvasPeer

    fun assertSvg(expectedFileName: String, svg: SvgSvgElement) {
        val fig = SvgCanvasFigure(svg)

        fig.mapToCanvas(canvasPeer)

        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvas = canvasPeer.createCanvas(w, h)
        val ctx = canvas.context2d
        fig.paint(canvas.context2d)

        imageComparer.assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)

        ctx.dispose()
    }

}