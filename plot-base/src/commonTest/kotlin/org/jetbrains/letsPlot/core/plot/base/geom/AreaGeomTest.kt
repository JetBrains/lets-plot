/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
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
        val list: (List<Double>) -> ((Int) -> Double) = { l -> { i -> l[i] } }
        val aes = AestheticsBuilder(3)
            .x(list(xs))
            .y(list(ys))
            .build()
        val domainX = aes.range(Aes.X)!!
        val domainY = aes.range(Aes.Y)!!
        val coord = Coords.DemoAndTest.create(
            DoubleSpan(domainX.lowerEnd, domainX.upperEnd),
            DoubleSpan(domainY.lowerEnd, domainY.upperEnd),
            DoubleVector(400.0, 300.0)
        )

        val svgRoot = DummyRoot()

        AreaGeom().build(svgRoot, aes, PositionAdjustments.identity(), coord, EmptyGeomContext())

        val svgPath = (svgRoot.content[0] as SvgGElement).children()[0] as SvgPathElement
        val svgPathList = PathElement.byString(svgPath.d().get().toString().trim())
        val svgPathExpectedList = PathElement.byString("M0.0 100.0 L0.0 100.0 L200.0 300.0 L400.0 0.0 L400.0 500.0 L200.0 500.0 L0.0 500.0 Z")
        assertEquals(svgPathExpectedList, svgPathList)
    }

    // To compare paths regardless of whether the numbers composing them are integers or doubles
    private data class PathElement(val action: Char?, val value: Double?) {
        companion object {
            fun byString(str: String): List<PathElement> {
                return str.split(" ").map { element ->
                    val action = element[0].takeIf { it.isLetter() }
                    val valueStr = action?.let { element.substring(1) } ?: element
                    val value = if (valueStr != "") {
                        valueStr.toDouble()
                    } else {
                        null
                    }
                    PathElement(action, value)
                }
            }
        }
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