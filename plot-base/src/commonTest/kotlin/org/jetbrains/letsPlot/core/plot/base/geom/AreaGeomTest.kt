/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.list
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.test.Test
import kotlin.test.assertEquals

class AreaGeomTest {
    @Test
    fun pointsAreOrderedByX() {
        val xs = listOf(1.0, 3.0, 2.0)
        val ys = listOf(4.0, 5.0, 2.0)
        val aes = AestheticsBuilder(xs.size)
            .x(list(xs))
            .y(list(ys))
            .build()
        val coord = Coords.DemoAndTest.create(
            aes.range(Aes.X)!!,
            aes.range(Aes.Y)!!,
            DoubleVector(400.0, 300.0)
        )
        val svgRoot = DummyRoot()

        AreaGeom().build(svgRoot, aes, PositionAdjustments.identity(), coord, EmptyGeomContext())

        val svgPath = (svgRoot.content[0] as SvgGElement).children()[0] as SvgPathElement
        val svgPathStr = svgPath.d().get().toString().trim()
            .replace(".0 ", " ") // remove trailing zeros
        val svgPathExpectedStr = "M0 100 L0 100 L200 300 L400 0 L400 500 L200 500 L0 500 Z"
        assertEquals(svgPathExpectedStr, svgPathStr)
    }
}

class EmptyGeomContext : GeomContext by BogusContext {
    override val flipped: Boolean = false
    override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()
}

class DummyRoot : SvgRoot {
    val content = mutableListOf<SvgNode>()
    override fun add(node: SvgNode) {
        content.add(node)
    }
}