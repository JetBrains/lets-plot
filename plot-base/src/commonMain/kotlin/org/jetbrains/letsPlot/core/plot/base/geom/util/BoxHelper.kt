/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

object BoxHelper {
    fun buildBoxes(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        rectFactory: (DataPointAesthetics) -> DoubleRectangle?
    ) {
        // rectangles
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles(rectFactory)
        rectangles.forEach { root.add(it) }
    }

    fun buildMidlines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        middleAesthetic: Aes<Double>,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        fatten: Double
    ) {
        val elementHelper = geomHelper.createSvgElementHelper()
        for (p in GeomUtil.withDefined(
            aesthetics.dataPoints(),
            Aes.X,
            middleAesthetic,
            Aes.WIDTH
        )) {
            val x = p.x()!!
            val middle = p[middleAesthetic]!!
            val width = p.width()!! * ctx.getResolution(Aes.X)

            val line = elementHelper.createLine(
                DoubleVector(x - width / 2, middle),
                DoubleVector(x + width / 2, middle),
                p
            )!!

            // adjust thickness
            val thickness = line.strokeWidth().get()!!
            line.strokeWidth().set(thickness * fatten)

            root.add(line)
        }
    }
    fun legendFactory(whiskers: Boolean): LegendKeyElementFactory =
        CrossBarLegendKeyElementFactory(whiskers)
}

private class CrossBarLegendKeyElementFactory(val whiskers: Boolean) :
    LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val whiskerSize = .2

        val strokeWidth = AesScaling.strokeWidth(p)
        val width = (size.x - strokeWidth) * .8 // a bit narrower
        val height = size.y - strokeWidth
        val x = (size.x - width) / 2
        val y = strokeWidth / 2


        // box
        var boxHeight = height
        var boxY = y
        if (whiskers) {
            boxHeight = height * (1 - 2 * whiskerSize)
            boxY = y + height * whiskerSize
        }

        val rect = SvgRectElement(
            x,
            boxY,
            width,
            boxHeight
        )
        GeomHelper.decorate(rect, p)

        // lines
        val middleY = y + height * .5
        val middle = SvgLineElement(x, middleY, x + width, middleY)
        GeomHelper.decorate(middle, p)

        val g = SvgGElement()
        g.children().add(rect)
        g.children().add(middle)

        if (whiskers) {
            val middleX = x + width * .5
            val lowerWhisker =
                SvgLineElement(middleX, y + height * (1 - whiskerSize), middleX, y + height)
            GeomHelper.decorate(lowerWhisker, p)
            val upperWhisker = SvgLineElement(middleX, y, middleX, y + height * whiskerSize)
            GeomHelper.decorate(upperWhisker, p)
            g.children().add(lowerWhisker)
            g.children().add(upperWhisker)
        }

        return g
    }
}

