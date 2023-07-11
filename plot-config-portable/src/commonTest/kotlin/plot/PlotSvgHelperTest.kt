/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.testing.EXPECTED_BUNCH_SVG
import jetbrains.datalore.plot.testing.EXPECTED_SINGLE_PLOT_SVG
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlotSvgHelperTest {
    @Test
    fun svgSizeFromSinglePlotSvg() {
        val sizeFromSvg = PlotSvgHelper.fetchPlotSizeFromSvg(EXPECTED_SINGLE_PLOT_SVG)
        assertEquals(DoubleVector(400.0, 300.0), sizeFromSvg)
    }

    @Test
    fun svgSizeFromGGBunchSvg() {
        val sizeFromSvg = PlotSvgHelper.fetchPlotSizeFromSvg(EXPECTED_BUNCH_SVG)
        assertEquals(DoubleVector(300.0, 150.0), sizeFromSvg)
    }
}