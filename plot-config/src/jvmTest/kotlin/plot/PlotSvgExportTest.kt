/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgUID
import junit.framework.TestCase
import kotlin.test.BeforeTest
import kotlin.test.Test

// ToDo: move to plot-config-portable (can't currently because depends on parsePlotSpec, JsonSupport)
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

        TestCase.assertEquals(withUpdatedClipPathId(EXPECTED_SINGLE_PLOT_SVG), svg)
    }

    @Test
    fun svgFromGGBunch() {
        @Suppress("MoveLambdaOutsideParentheses")
        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0),  // Ignored
            clipPathIdTransform = { currSuffix -> "$currSuffix++" }
        )

        TestCase.assertEquals(withUpdatedClipPathId(EXPECTED_BUNCH_SVG), svg)

//        println(svg)
    }
}