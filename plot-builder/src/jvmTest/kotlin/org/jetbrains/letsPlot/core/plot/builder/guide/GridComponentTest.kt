/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils.breadthFirstTraversal
import kotlin.test.Test

class GridComponentTest {
    @Test
    fun `should not add lines close to the edge if theme has no borders`() {
        // Theme with border => grid lines near the border are not drawn
        val theme = ThemeUtil.buildTheme(ThemeOption.Name.R_LIGHT)

        val gridComponent = createGridComponent(theme)

        breadthFirstTraversal(gridComponent.rootGroup)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { gridLines ->
                assertThat(gridLines).hasSize(2)
                assertThat(gridLines[0].x1().get()).isEqualTo(0.0)
                assertThat(gridLines[0].x2().get()).isEqualTo(100.0)
                assertThat(gridLines[0].y1().get()).isEqualTo(4.0)

                assertThat(gridLines[1].x1().get()).isEqualTo(0.0)
                assertThat(gridLines[1].x2().get()).isEqualTo(100.0)
                assertThat(gridLines[1].y1().get()).isEqualTo(96.0)
            }
    }

    @Test
    fun `should add lines close to the edge if theme has no borders`() {
        // Theme without border (like BBC) => grid lines near the border are drawn
        val theme = ThemeUtil.buildTheme(ThemeOption.Name.LP_MINIMAL)

        val gridComponent = createGridComponent(theme)

        breadthFirstTraversal(gridComponent.rootGroup)
            .filterIsInstance<SvgLineElement>()
            .toList()
            .let { gridLines ->
                assertThat(gridLines).hasSize(4)

                assertThat(gridLines[0].x1().get()).isEqualTo(0.0)
                assertThat(gridLines[0].x2().get()).isEqualTo(100.0)
                assertThat(gridLines[0].y1().get()).isEqualTo(2.0)

                assertThat(gridLines[1].x1().get()).isEqualTo(0.0)
                assertThat(gridLines[1].x2().get()).isEqualTo(100.0)
                assertThat(gridLines[1].y1().get()).isEqualTo(4.0)

                assertThat(gridLines[2].x1().get()).isEqualTo(0.0)
                assertThat(gridLines[2].x2().get()).isEqualTo(100.0)
                assertThat(gridLines[2].y1().get()).isEqualTo(96.0)

                assertThat(gridLines[3].x1().get()).isEqualTo(0.0)
                assertThat(gridLines[3].x2().get()).isEqualTo(100.0)
                assertThat(gridLines[3].y1().get()).isEqualTo(98.0)
            }
    }

    private fun createGridComponent(theme: DefaultTheme): GridComponent {
        val gridComponent = GridComponent(
            majorGrid = listOf(
                hline(y = 2, x = 0 to 100), // too close to the border => NOT drawn
                hline(y = 4, x = 0 to 100), // not too close to the border => drawn
                hline(y = 96, x = 0 to 100), // not too close to the border => drawn
                hline(y = 98, x = 0 to 100), // too close to the border => NOT drawn
            ),
            minorGrid = emptyList(),
            orientation = Orientation.LEFT,
            isOrthogonal = true,
            geomContentBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector(100, 100)),
            gridTheme = theme.panel().gridY(),
            panelTheme = theme.panel()
        )

        gridComponent.ensureBuilt()
        return gridComponent
    }

    private fun hline(y: Number, x: Pair<Number, Number>) = listOf(DoubleVector(x.first, y), DoubleVector(x.second, y))
}