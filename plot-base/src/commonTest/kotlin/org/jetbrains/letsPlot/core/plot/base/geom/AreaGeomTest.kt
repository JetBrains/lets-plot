/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.NullPlotContext
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.Annotation
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.presentation.PlotLabelSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
        val groupComponent = GroupComponent()
        val domainX = aes.range(Aes.X)!!
        val domainY = aes.range(Aes.Y)!!
        val coord = Coords.DemoAndTest.create(
            DoubleSpan(domainX.lowerEnd, domainX.upperEnd),
            DoubleSpan(domainY.lowerEnd, domainY.upperEnd),
            DoubleVector(400.0, 300.0)
        )

        val layer = org.jetbrains.letsPlot.core.plot.builder.SvgLayerRenderer(
            aes,
            AreaGeom(),
            PositionAdjustments.identity(),
            coord,
            EmptyGeomContext()
        )
        groupComponent.add(layer.rootGroup)

        val firstSvgPath: SvgPathElement? = firstPathOrNull(groupComponent.rootGroup)
        assertNotNull(firstSvgPath)
        val svgPathStr = firstSvgPath.d().get().toString().trim()
        assertEquals("M0.0 100.0 L0.0 100.0 L200.0 300.0 L400.0 0.0 L400.0 500.0 L200.0 500.0 L0.0 500.0 Z", svgPathStr)
    }

    private fun firstPathOrNull(element: SvgGraphicsElement): SvgPathElement? {
        if (element is SvgPathElement) return element
        if (element is SvgGElement) {
            for (child in element.children()) {
                if (child !is SvgGraphicsElement) continue
                val path = firstPathOrNull(child)
                if (path != null) return path
            }
        }
        return null
    }
}

class EmptyGeomContext : GeomContext {
    override val flipped: Boolean = false
    override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()
    override val annotation: Annotation? = null
    override val backgroundColor: Color = Color.WHITE
    override val plotContext: PlotContext = NullPlotContext

    override fun getResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getAesBounds(): DoubleRectangle {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        throw IllegalStateException("Not available in an empty geom context")
    }


    override fun getDefaultFormatter(aes: Aes<*>): (Any) -> String {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getDefaultFormatter(varName: String): (Any) -> String {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun isMappedAes(aes: Aes<*>): Boolean = false
    override fun estimateTextSize(
        text: String,
        family: String,
        size: Double,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector {
        @Suppress("NAME_SHADOWING")
        val family = DefaultFontFamilyRegistry().get(family)
        return PlotLabelSpec(
            Font(
                family = family,
                size = size.toInt(),
                isBold = isBold,
                isItalic = isItalic
            ),
        ).dimensions(text)
    }
}