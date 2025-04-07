/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape

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
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, rectFactory)
        val rectangles = helper.createRectangles()
        rectangles.forEach { root.add(it) }
    }

    fun buildMidlines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        xAes: Aes<Double>,
        middleAes: Aes<Double>,
        sizeAes: Aes<Double>,
        widthUnit: DimensionUnit,
        geomHelper: GeomHelper,
        fatten: Double
    ) {
        val elementHelper = geomHelper.createSvgElementHelper()
        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(xAes) ?: continue
            val middle = p.finiteOrNull(middleAes) ?: continue
            val w = p.finiteOrNull(sizeAes) ?: continue

            val width = w * geomHelper.getUnitResolution(widthUnit, xAes)

            val (line) = elementHelper.createLine(
                DoubleVector(x - width / 2, middle),
                DoubleVector(x + width / 2, middle),
                p
            ) { AesScaling.strokeWidth(it) * fatten } ?: continue

            require(line is SvgShape)

            root.add(line)
        }
    }
    fun legendFactory(whiskers: Boolean): LegendKeyElementFactory =
        BoxLegendKeyElementFactory(whiskers)
}

private class BoxLegendKeyElementFactory(val whiskers: Boolean) :
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

