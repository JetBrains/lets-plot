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
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Name.LP_MINIMAL
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_RECT
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
    fun `with theme with border should not add gridlines close to edges and add ALL labels`() {
        val theme = ThemeUtil.buildTheme(
            themeName = LP_MINIMAL,
            userOptions = mapOf(
                PANEL_BORDER_RECT to mapOf(
                    Elem.SIZE to 2.0,
                    Elem.FILL to SymbolicColor.GREY_3,
                    Elem.BLANK to false
                )
            ),
        )

        val container = buildFrameOrReference(theme)

        // Do not draw grid lines on the edge
        depthFirstTraversal(container)
            .filterIsInstance<SvgLineElement>()
            .let { lines ->
                assertThat(lines.count()).isEqualTo(10)
                assertThat(lines.none { line -> line.y1().get()!! >= (600 - 3) || line.y1().get()!! <= 3 })
            }

        depthFirstTraversal(container)
            .filterIsInstance<SvgTextNode>()
            .let { textNodes ->
                assertThat(textNodes.count()).isEqualTo(12)
                assertThat(textNodes.map { it.textContent().get() }.toList())
                    .containsExactlyInAnyOrder("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
            }
    }

    @Test
    fun `with theme without border should not drop gridlines close to edges`() {
        // LP_MINIMAL theme has no border
        val theme = ThemeUtil.buildTheme(themeName = LP_MINIMAL)
        val container = buildFrameOrReference(theme)

        // Do not draw grid lines on the edge
        depthFirstTraversal(container)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { lines ->
                assertThat(lines).hasSize(12)
                assertThat(lines.any { line -> line.y1().get()!! >= (600 - 3) } ).isTrue()
                assertThat(lines.any { line -> line.y1().get()!! <= 3 } ).isTrue()
            }

        depthFirstTraversal(container)
            .filterIsInstance<SvgTextNode>()
            .toList()
            .let { textNodes ->
                assertThat(textNodes).hasSize(12)
                assertThat(textNodes.map { it.textContent().get() })
                    .containsExactlyInAnyOrder("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
            }
    }

    private fun buildFrameOrReference(theme: Theme): SvgNode {
        val values = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val transformedValues = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1)
        val transformedRange = DoubleSpan.encloseAll(transformedValues)
        val transformedDomain = DoubleRectangle.hvRange(transformedRange, transformedRange)

        val scaleBreaks = ScaleBreaks(
            domainValues = values,
            transformedValues = transformedValues,
            labels = values.map { it.toString() },
        )

        val squareFrameOfReference = SquareFrameOfReference(
            hScaleBreaks = scaleBreaks,
            vScaleBreaks = scaleBreaks,
            adjustedDomain = transformedDomain,
            coord = Coords.create(
                CoordinatesMapper.create(
                    adjustedDomain = transformedDomain,
                    clientSize = DoubleVector(600, 600),
                    projection = identity(),
                    flipAxis = false
                )
            ),
            layoutInfo = TileLayoutInfo(
                offset = DoubleVector.ZERO,
                geomWithAxisBounds = DoubleRectangle.LTRB(0, 0, 600, 600),
                geomOuterBounds = DoubleRectangle.LTRB(0, 0, 600, 600),
                geomInnerBounds = DoubleRectangle.LTRB(0, 0, 600, 600),
                axisInfos = AxisLayoutInfoQuad(
                    left = AxisLayoutInfo(
                        axisLength = 600.0,
                        axisDomain = transformedRange,
                        orientation = Orientation.LEFT,
                        axisBreaks = scaleBreaks,
                        tickLabelRotationAngle = 0.0,
                        tickLabelsBounds = DoubleRectangle.LTRB(0, 0, 30, 600),
                    ),
                    right = null,
                    top = null,
                    bottom = null
                ),
                hAxisShown = true,
                vAxisShown = true,
                geomContentBounds = DoubleRectangle.LTRB(0, 0, 600, 600),
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
        squareFrameOfReference.drawBeforeGeomLayer(container)
        squareFrameOfReference.drawAfterGeomLayer(container)
        return container.rootGroup
    }
}
