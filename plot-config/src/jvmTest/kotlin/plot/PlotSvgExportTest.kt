/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.testing.EXPECTED_BUNCH_SVG
import jetbrains.datalore.plot.testing.EXPECTED_SINGLE_PLOT_SVG
import jetbrains.datalore.plot.testing.rawSpec_GGBunch
import jetbrains.datalore.plot.testing.rawSpec_SinglePlot
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


internal class PlotSvgExportTest {
    @Before
    fun setUp() {
        SvgUID.setUpForTest()
    }

    @Test
    fun svgFromSinglePlot() {
        val svgImage = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0)
        )

//        println(svgImage)

        assertEquals(EXPECTED_SINGLE_PLOT_SVG, svgImage)
    }

    @Test
    fun svgFromGGBunch() {
        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0)  // Ignored
        )

        kotlin.test.assertEquals(EXPECTED_BUNCH_SVG, svg)
    }
}