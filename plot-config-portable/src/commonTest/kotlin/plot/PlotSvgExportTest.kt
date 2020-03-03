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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlotSvgExportTest {
    @BeforeTest
    fun setUp() {
        SvgUID.reset()
    }

    @Test
    fun svgFromSinglePlot() {
        @Suppress("MoveLambdaOutsideParentheses")
        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0),
            clipPathIdTransform = { currSuffix -> "$currSuffix++" }
        )

//        println(svg)

        assertEquals(withUpdatedClipPathId(EXPECTED_SINGLE_PLOT_SVG), svg)
    }

    @Test
    fun svgFromGGBunch() {
        @Suppress("MoveLambdaOutsideParentheses")
        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0),  // Ignored
            clipPathIdTransform = { currSuffix -> "$currSuffix++" }
        )

        assertEquals(withUpdatedClipPathId(EXPECTED_BUNCH_SVG), svg)

//        println(svg)
    }

    private fun withUpdatedClipPathId(s: String): String {
        return s.replace("lplt-clip0", "lplt-clip0++")
            .replace("lplt-clip1", "lplt-clip1++")
    }

}