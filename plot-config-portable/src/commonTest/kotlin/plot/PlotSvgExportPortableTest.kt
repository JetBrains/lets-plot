/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.annotation.IgnoreJs
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.testing.EXPECTED_BUNCH_SVG
import jetbrains.datalore.plot.testing.EXPECTED_SINGLE_PLOT_SVG
import jetbrains.datalore.plot.testing.rawSpec_GGBunch
import jetbrains.datalore.plot.testing.rawSpec_SinglePlot
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlotSvgExportPortableTest {
    @BeforeTest
    fun setUp() {
        SvgUID.setUpForTest()
    }

    @Test
    @IgnoreJs
    fun svgFromSinglePlot() {
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0)
        )

//        println(svg)

        assertEquals(EXPECTED_SINGLE_PLOT_SVG, svg)
    }

    @Test
    @IgnoreJs
    fun svgFromGGBunch() {
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0)  // Ignored
        )

        assertEquals(EXPECTED_BUNCH_SVG, svg)

//        println(svg)
    }
}