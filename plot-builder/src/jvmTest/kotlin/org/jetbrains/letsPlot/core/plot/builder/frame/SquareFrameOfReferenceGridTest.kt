/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.identity
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Name.LP_MINIMAL
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MAJOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfoQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils.depthFirstTraversal
import kotlin.test.Test

class SquareFrameOfReferenceGridTest {
    @Test
    fun `with theme with border should not draw major gridlines close to edges but should draw their labels`() {
        val frameReferenceSvg = buildFrameOrReference(
            themeOptions = showBorder(true) + showMajorGrid(true) + showMinorGrid(false)
        )

        // Do not draw grid lines on the edge
        depthFirstTraversal(frameReferenceSvg)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { majorGridLines ->
                assertThat(majorGridLines).hasSize(10)
                assertThat(majorGridLines.all(SvgLineElement::isHorizontal)).isTrue()
                assertThat(majorGridLines.all { line -> line.y1!! in DoubleSpan(3.0, 597.0) }).isTrue()
            }

        // Yet all labels should be present
        depthFirstTraversal(frameReferenceSvg)
            .filterIsInstance<SvgTextNode>()
            .toList()
            .let { textNodes ->
                assertThat(textNodes).hasSize(12)
                assertThat(textNodes.map { it.textContent().get() }.toList())
                    .containsExactlyInAnyOrder("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
            }
    }

    @Test
    fun `with theme without border should draw all major gridlines`() {
        val frameReferenceSvg = buildFrameOrReference(
            themeOptions = showBorder(false) + showMajorGrid(true) + showMinorGrid(false)
        )

        // Do draw grid lines on the edge
        depthFirstTraversal(frameReferenceSvg)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { majorGridLines ->
                assertThat(majorGridLines).hasSize(12)
                assertThat(majorGridLines.first().y1).isEqualTo(600.0)
                assertThat(majorGridLines.last().y1).isEqualTo(0.0)
            }

        depthFirstTraversal(frameReferenceSvg)
            .filterIsInstance<SvgTextNode>()
            .toList()
            .let { textNodes ->
                assertThat(textNodes).hasSize(12)
                assertThat(textNodes.map { it.textContent().get() })
                    .containsExactlyInAnyOrder("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
            }
    }

    @Test
    fun `should draw minor gridlines if two or more major gridlines are present`() {
        val frameReferenceSvg = buildFrameOrReference(
            themeOptions = showBorder(false) + showMajorGrid(false) + showMinorGrid(true),
            size = DoubleVector(600, 180),
            domain = DoubleSpan(0.0, 360.0),
            breaks = listOf(0, 200)
        )
        depthFirstTraversal(frameReferenceSvg)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { minorGridLines ->
                assertThat(minorGridLines).hasSize(2)
                assertThat(minorGridLines.all(SvgLineElement::isHorizontal)).isTrue()
                assertThat(minorGridLines[0].y1).isEqualTo(130.0)
                assertThat(minorGridLines[1].y1).isEqualTo(30.0)
            }
    }

    @Test
    fun `with one major gridline should not draw minor gridlines`() {
        val frameReferenceSvg = buildFrameOrReference(
            themeOptions = showBorder(false) + showMajorGrid(false) + showMinorGrid(true),
            size = DoubleVector(600, 180),
            domain = DoubleSpan(0.0, 360.0),
            breaks = listOf(200)
        )
        depthFirstTraversal(frameReferenceSvg)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { minorGridLines ->
                assertThat(minorGridLines).isEmpty()
            }
    }

    @Test
    fun `without major gridlines should not draw minor gridlines`() {
        val container = buildFrameOrReference(
            themeOptions = showMajorGrid(true) + showMinorGrid(true),
            size = DoubleVector(600, 180),
            domain = DoubleSpan(0.0, 360.0),
            breaks = listOf()
        )
        depthFirstTraversal(container)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { minorGridLines ->
                assertThat(minorGridLines).isEmpty()
            }
    }

    private fun buildFrameOrReference(
        themeOptions: Map<String, Any> = emptyMap(),
        size: DoubleVector = DoubleVector(600.0, 600.0),
        breaks: List<Number> = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
        domain: DoubleSpan = DoubleSpan.encloseAll(breaks.map { it.toDouble() }),
    ): SvgNode {
        val theme = ThemeUtil.buildTheme(LP_MINIMAL, themeOptions)
        val transformedDomain = DoubleRectangle.hvRange(domain, domain)

        val unusedRect = DoubleRectangle.LTRB(0, 0, 0, 0)

        val scaleBreaks = ScaleBreaks(
            domainValues = breaks,
            transformedValues = breaks.map { it.toDouble() },
            labels = breaks.map { it.toString() },
        )

        val squareFrameOfReference = SquareFrameOfReference(
            hScaleBreaks = scaleBreaks,
            vScaleBreaks = scaleBreaks,
            adjustedDomain = transformedDomain,
            coord = Coords.create(
                CoordinatesMapper.create(
                    adjustedDomain = transformedDomain,
                    clientSize = size,
                    projection = identity(),
                    flipAxis = false
                )
            ),
            layoutInfo = TileLayoutInfo(
                offset = DoubleVector.ZERO,
                geomWithAxisBounds = unusedRect,
                geomOuterBounds = unusedRect,
                geomInnerBounds = unusedRect,
                axisInfos = AxisLayoutInfoQuad(
                    left = AxisLayoutInfo(
                        axisLength = size.y,
                        axisDomain = domain,
                        orientation = Orientation.LEFT,
                        axisBreaks = scaleBreaks,
                        tickLabelRotationAngle = 0.0,
                        tickLabelsBounds = unusedRect,
                    ),
                    right = null,
                    top = null,
                    bottom = null
                ),
                hAxisShown = true,
                vAxisShown = true,
                geomContentBounds = DoubleRectangle(DoubleVector.ZERO, size),
                trueIndex = 0,
            ),
            marginsLayout = GeomMarginsLayout(0.0, 0.0, 0.0, 0.0),
            theme = theme,
            flipAxis = false,
            plotContext = object : PlotContext {
                override val superscriptExponent: Boolean
                    get() = error("unexpected call")

                override fun hasScale(aes: Aes<*>): Boolean = error("unexpected call")
                override fun getScale(aes: Aes<*>): Scale = error("unexpected call")
                override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan = error("unexpected call")
                override fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String = error("unexpected call")
            }
        )

        val container = GroupComponent()
        squareFrameOfReference.repaintFrame()

        val axisAndGrid = (squareFrameOfReference.bottomGroup.rootGroup.children()
            .map { it } + squareFrameOfReference.topGroup.rootGroup.children().map { it })
            .map { it.removeFromParent(); it }


        container.rootGroup.children().addAll(axisAndGrid)
        return container.rootGroup
    }

}

private val SvgLineElement.y1: Double? get() = this.y1().get()
private fun SvgLineElement.isHorizontal(): Boolean = y1().get() == y2().get()

private fun showMajorGrid(isTrue: Boolean): Map<String, Any> {
    return if (isTrue) {
        mapOf(
            PANEL_GRID_MAJOR to mapOf(
                Elem.SIZE to 1.0,
                Elem.COLOR to SymbolicColor.GREY_3,
                Elem.BLANK to false
            )
        )
    } else {
        mapOf(
            PANEL_GRID_MAJOR to mapOf(
                Elem.BLANK to true
            )
        )
    }
}

private fun showMinorGrid(isTrue: Boolean): Map<String, Any> {
    return if (isTrue) {
        mapOf(
            PANEL_GRID_MINOR to mapOf(
                Elem.SIZE to 1.0,
                Elem.COLOR to SymbolicColor.GREY_3,
                Elem.BLANK to false
            )
        )
    } else {
        mapOf(
            PANEL_GRID_MINOR to mapOf(
                Elem.BLANK to true
            )
        )
    }
}

private fun showBorder(isTrue: Boolean): Map<String, Any> {
    return if (isTrue) {
        mapOf(
            PANEL_BORDER_RECT to mapOf(
                Elem.SIZE to 1.0,
                Elem.FILL to SymbolicColor.GREY_3,
                Elem.BLANK to false
            )
        )
    } else {
        mapOf(
            PANEL_BORDER_RECT to mapOf(
                Elem.BLANK to true
            )
        )
    }
}