/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil.with_X_Y
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor
import jetbrains.datalore.plot.base.render.svg.Text.VerticalAnchor
import jetbrains.datalore.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx.Bitmap
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

class RasterGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = FilledSquareLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val iter = with_X_Y(aesthetics.dataPoints()).iterator()
        if (!iter.hasNext()) {
            return
        }
        val randomP = iter.next()
        val helper = GeomHelper(pos, coord, ctx)

        // Find size of image (row x col)
        val boundsXY = layerAesBounds(aesthetics)
        val stepX = ctx.getResolution(Aes.X)
        val stepY = ctx.getResolution(Aes.Y)
        require(stepX > SeriesUtil.TINY) { "x-step is too small: $stepX" }
        require(stepY > SeriesUtil.TINY) { "y-step is too small: $stepY" }
        val width = (round(boundsXY.dimension.x / stepX) + 1)
        val height = (round(boundsXY.dimension.y / stepY) + 1)

        if (width * height > 5000000) {
            val center = boundsXY.center
            val lines =
                arrayOf("Raster image size", "[$width X $height]", "exceeds capability", "of", "your imaging device")
            val fontSize = 12.0
            val lineHeight = fontSize + 4
            var y = center.y + lineHeight * lines.size / 2.0
            for (line in lines) {
                val label = TextLabel(line)
                label.textColor().set(Color.DARK_MAGENTA)
                label.textOpacity().set(0.5)
                label.setFontSize(fontSize)
                label.setFontWeight("bold")
                label.setHorizontalAnchor(HorizontalAnchor.MIDDLE)
                label.setVerticalAnchor(VerticalAnchor.CENTER)
                val loc = helper.toClient(center.x, y, randomP)!!
                label.moveTo(loc)
                root.add(label.rootGroup)
                y -= lineHeight
            }

            return
        }

        val cols = round(width).toInt()
        val rows = round(height).toInt()

        // translate to client coordinates
        // expand bounds by 1/2 step before the translation to adjust for the size of 'image pixel'
        val halfStep = DoubleVector(stepX * 0.5, stepY * 0.5)
        val corner0 = helper.toClient(boundsXY.origin.subtract(halfStep), randomP)!!
        val corner2 = helper.toClient(boundsXY.origin.add(boundsXY.dimension).add(halfStep), randomP)!!
        val invertedX = corner2.x < corner0.x
        val invertedY = corner2.y < corner0.y

        // Fill image data array with RGB values
        val x0 = boundsXY.origin.x
        val y0 = boundsXY.origin.y

        val argbValues = IntArray(cols * rows)
        for (p in with_X_Y(aesthetics.dataPoints())) {
            val x = p.x()
            val y = p.y()
            val alpha = p.alpha()
            val color = p.fill()

            var col = round((x!! - x0) / stepX).toInt()
            var row = round((y!! - y0) / stepY).toInt()

            if (invertedX) {
                col = cols - (col + 1)
            }

            if (invertedY) {
                row = rows - (row + 1)
            }

            argbValues[row * cols + col] = SvgUtils.toARGB(color!!, alpha!!)
        }

        val bitmap = Bitmap(cols, rows, argbValues)
        val svgImageElement = SvgImageElementEx(
            min(corner0.x, corner2.x), min(corner0.y, corner2.y),
            abs(corner0.x - corner2.x), abs(corner0.y - corner2.y),
            bitmap
        )
        root.add(svgImageElement)
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.WIDTH, // not rendered but required for correct x aes range computation
//                Aes.HEIGHT, // -- the same --
//                Aes.FILL,
//                Aes.ALPHA
//        )

        const val HANDLES_GROUPS = false
    }
}// ToDo: hjust, vjust [0..1] def .5

