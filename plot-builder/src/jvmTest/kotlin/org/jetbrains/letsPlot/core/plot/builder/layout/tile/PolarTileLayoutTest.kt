/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProviderFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.FixedAxisBreaksProvider
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.config.ThemeConfig
import kotlin.math.roundToInt
import kotlin.test.Test

class PolarTileLayoutTest {
    @Test
    fun simple() {
        val preferredSize = DoubleVector(600, 400)
        doLayout(preferredSize, Thickness.ZERO).let {
            assertThat(it.geomOuterBounds.right).isLessThanOrEqualTo(preferredSize.x)
            assertThat(round(it.geomInnerBounds))
                .isEqualTo(round(it.geomOuterBounds))
                .isEqualTo(round(it.geomContentBounds))
                .isEqualTo(DoubleRectangle.XYWH(35, 0, 377, 377))
        }
    }

    @Test
    fun `horizontal padding with lots of spare horizontal space should increase width of plotting area`() {
        val preferredSize = DoubleVector(800, 400)
        doLayout(preferredSize, Thickness(left = 50.0, right = 50.0)).let {
            assertThat(it.geomOuterBounds.right).isLessThanOrEqualTo(preferredSize.x)
            assertThat(round(it.geomOuterBounds))
                .isEqualTo(round(it.geomInnerBounds))
                .isEqualTo(DoubleRectangle.XYWH(35, 0, 477, 377))

            assertThat(round(it.geomContentBounds)).isEqualTo(DoubleRectangle.XYWH(85, 0, 377, 377))

        }
    }

    @Test
    fun `horizontal padding with not enough horizontal space should decrease plotting area`() {
        val preferredSize = DoubleVector(800, 400)
        doLayout(preferredSize, Thickness(left = 250.0, right = 250.0)).let {
            assertThat(it.geomOuterBounds.right).isLessThanOrEqualTo(preferredSize.x)
            assertThat(round(it.geomOuterBounds))
                .isEqualTo(round(it.geomInnerBounds))
                .isEqualTo(DoubleRectangle.XYWH(35, 0, 765, 265))

            assertThat(round(it.geomContentBounds)).isEqualTo(DoubleRectangle.XYWH(285, 0, 265, 265))
        }
    }

    @Test
    fun `vertical padding with lots of spare vertical space should increase height of plotting area`() {
        val preferredSize = DoubleVector(600, 800)
        doLayout(preferredSize, Thickness(top = 50.0, bottom = 50.0)).let {
            assertThat(it.geomOuterBounds.right).isLessThanOrEqualTo(preferredSize.x)
            assertThat(round(it.geomOuterBounds))
                .isEqualTo(round(it.geomInnerBounds))
                .isEqualTo(DoubleRectangle.XYWH(35, 0, 565, 665))

            assertThat(round(it.geomContentBounds)).isEqualTo(DoubleRectangle.XYWH(35, 50, 565, 565))
        }
    }

    private fun doLayout(preferredSize: DoubleVector, thickness: Thickness): TileLayoutInfo {
        val coordProvider = PolarCoordProvider(xLim = null, yLim = null, flipped = false, start = 0.0, clockwise = true)
        val breaksProviderFactory: AxisBreaksProviderFactory =
            AxisBreaksProviderFactory.FixedBreaksProviderFactory(
                FixedAxisBreaksProvider(
                    ScaleBreaks(
                        domainValues = listOf(0.0, 0.25, 0.5, 0.75, 1.0),
                        transformedValues = listOf(0.0, 0.25, 0.5, 0.75, 1.0),
                        labels = listOf("0", "0.25", "0.5", "0.75", "1")
                    )
                )
            )

        val theme = ThemeConfig(fontFamilyRegistry = DefaultFontFamilyRegistry()).theme
        val left = AxisLayout(
            breaksProviderFactory = breaksProviderFactory,
            orientation = Orientation.LEFT,
            theme = theme.verticalAxis(flipAxis = false),
            polar = true
        )

        val bottom = AxisLayout(
            breaksProviderFactory = breaksProviderFactory,
            orientation = Orientation.BOTTOM,
            theme = theme.horizontalAxis(flipAxis = false),
            polar = true
        )

        val layout = PolarTileLayout(
            axisLayoutQuad = AxisLayoutQuad(left = left, right = null, top = null, bottom = bottom),
            hDomain = DoubleSpan.withLowerEnd(0.0, 1.0),
            vDomain = DoubleSpan.withLowerEnd(0.0, 1.0),
            marginsLayout = GeomMarginsLayout(0.0, 0.0, 0.0, 0.0),
            panelPadding = thickness
        )

        val layoutInfo = layout.doLayout(preferredSize, coordProvider)
        return layoutInfo
    }

    private fun round(rect: DoubleRectangle): DoubleRectangle {
        return DoubleRectangle(
            rect.left.roundToInt().toDouble(),
            rect.top.roundToInt().toDouble(),
            rect.width.roundToInt().toDouble(),
            rect.height.roundToInt().toDouble()
        )
    }
}