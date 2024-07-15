/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.testing.doubleRectangleComparator
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.coord.FixedRatioCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProviderFactory
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.config.ThemeConfig
import kotlin.test.Test

class TopDownTileLayoutTest {
    @Test
    fun issue1074() {
        val containerSize = DoubleVector(600, 800)
        val li = doLayout(containerSize, marginLayout = GeomMarginsLayout(left = 0.0, bottom = 0.0, right = 0.5, top = 0.5))

        assertThat(li.geomOuterBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(13.9, 0.0, 586.0, 586.0))

        assertThat(li.geomInnerBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(li.geomContentBounds)
            .isEqualTo(DoubleRectangle.XYWH(13.98, 293.0, 293.0, 293.0))

        assertThat(li.axisInfos.left!!.axisBounds())
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(-13.98, 0.0, 13.98, 286.19))
    }

    @Test
    fun verticalPlotWithDefaultMarginalLayers() {
        val containerSize = DoubleVector(600, 800)
        val li = doLayout(containerSize, marginLayout = GeomMarginsLayout(left = 0.0, bottom = 0.0, right = 0.1, top = 0.1))

        assertThat(li.geomOuterBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(27.0, 0.0, 572.9, 572.9))

        assertThat(li.geomInnerBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(li.geomContentBounds)
            .isEqualTo(DoubleRectangle.XYWH(27.0, 57.2, 515.6, 515.6))

        assertThat(li.axisInfos.left!!.axisBounds())
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(-27.0, 0.0, 27.0, 509.9))
    }

    @Test
    fun verticalPlotWithLargeMarginalLayers() {
        val containerSize = DoubleVector(934, 524)

        val li = doLayout(containerSize, marginLayout = GeomMarginsLayout(left = 0.0, bottom = 0.0, right = 0.5, top = 0.5))

        assertThat(li.geomOuterBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(13.98, 0.0, 501, 501))

        assertThat(li.geomInnerBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(li.geomContentBounds)
            .isEqualTo(DoubleRectangle.XYWH(13.98, 250.5, 250.5, 250.5))

        assertThat(li.axisInfos.left!!.axisBounds())
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(-13.98, 0.0, 13.98, 245.61))
    }

    @Test
    fun verticalPlotWithoutMarginalLayers() {
        val containerSize = DoubleVector(600, 800)
        val li = doLayout(containerSize)

        assertThat(li.geomOuterBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(27.0, 0.0, 572.9, 572.9))

        assertThat(li.geomInnerBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(li.geomContentBounds)
            .isEqualTo(DoubleRectangle.XYWH(27.0, 0.0, 572.9, 572.9))

        assertThat(li.axisInfos.left!!.axisBounds())
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(-27.0, 0.0, 27.0, 565.8))
    }

    @Test
    fun verticalPlotWithPanelInset() {
        val containerSize = DoubleVector(600, 800)
        val li = doLayout(containerSize, panelInset = Thickness(20.0, 20.0, 20.0, 20.0))

        assertThat(li.geomOuterBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(27.0, 0.0, 572.9, 572.9))

        assertThat(li.geomInnerBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(27.0, 0.0, 572.9, 572.9))

        assertThat(li.geomContentBounds)
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(47.0, 20.0, 532.9, 532.9))

        assertThat(li.axisInfos.left!!.axisBounds())
            .usingComparator(doubleRectangleComparator(0.1))
            .isEqualTo(DoubleRectangle.XYWH(-27.0, 0.0, 27.0, 527.7))
    }

    private fun doLayout(
        containerSize: DoubleVector,
        panelInset: Thickness = Thickness.ZERO,
        marginLayout: GeomMarginsLayout = GeomMarginsLayout(0.0, 0.0, 0.0, 0.0),
        coordFixed: Boolean = true,
    ): TileLayoutInfo {
        val theme = ThemeConfig(fontFamilyRegistry = DefaultFontFamilyRegistry()).theme

        val breaksProviderFactory: AxisBreaksProviderFactory =
            AxisBreaksProviderFactory.AdaptableBreaksProviderFactory(
                Transforms.createBreaksGeneratorForTransformedDomain(
                    Transforms.IDENTITY,
                    providedFormatter = null,
                    superscriptExponent = false
                )

            )

        val left = AxisLayout(
            breaksProviderFactory = breaksProviderFactory,
            orientation = Orientation.LEFT,
            theme = theme.verticalAxis(flipAxis = false),
            polar = false
        )

        val bottom = AxisLayout(
            breaksProviderFactory = breaksProviderFactory,
            orientation = Orientation.BOTTOM,
            theme = theme.horizontalAxis(flipAxis = false),
            polar = false
        )

        val layout = TopDownTileLayout(
            axisLayoutQuad = AxisLayoutQuad(
                left = left,
                right = null,
                top = null,
                bottom = bottom
            ),
            hDomain = DoubleSpan(-0.2, 4.2),
            vDomain = DoubleSpan(-0.2, 4.2),
            marginsLayout = marginLayout,
            panelInset = panelInset
        )

        val coord = if (coordFixed) {
            FixedRatioCoordProvider(1.0, xLim = Pair(null, null), yLim = Pair(null, null), flipped = false)
        } else {
            error("Not implemented")
        }

        val li = layout.doLayout(containerSize, coord)

        return li
    }

}