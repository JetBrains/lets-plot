/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.plot

import demoAndTestShared.parsePlotSpec
import demoAndTestShared.EXPECTED_BUNCH_SVG
import demoAndTestShared.EXPECTED_SINGLE_PLOT_SVG
import demoAndTestShared.rawSpec_GGBunch
import demoAndTestShared.rawSpec_SinglePlot
import junit.framework.TestCase.assertEquals
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgUID
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertTrue


internal class PlotSvgExportTest {
    @Before
    fun setUp() {
        SvgUID.setUpForTest()
    }

    // ToDo: temporarily ignore.
    @Test
    @Ignore
    fun svgFromSinglePlot() {
        val svgImage = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0)
        )

//        println(svgImage)

        assertEquals(EXPECTED_SINGLE_PLOT_SVG, svgImage)
    }

    // ToDo: temporarily ignore.
    @Test
    @Ignore
    fun svgFromGGBunch() {
        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0)  // Ignored
        )

//        println(svg)

        kotlin.test.assertEquals(EXPECTED_BUNCH_SVG, svg)
    }


    @Suppress("TestFunctionName")
    @Test
    fun LPK188_geomImshow_to_SVG_produces_fuzzy_picture() {
        val spec = """
            |{
            |   'kind': 'plot',
            |   'layers': [
            |       {
            |           'geom': 'image',
            |           'show_legend': true,
            |           'href': 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGP4z8DwHwwZ/oOZvkDCF8jyBQCLFgnfUCS+/AAAAABJRU5ErkJggg==',
            |           'xmin': -0.5,
            |           'ymin': -0.5,
            |           'xmax': 2.5,
            |           'ymax': 1.5
            |       }
            |   ]
            |}
        """.trimMargin()

        PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            useCssPixelatedImageRendering = false
        ).let {
            assertTrue(it.contains("style=\"image-rendering: optimizeSpeed\""))
        }

        PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            useCssPixelatedImageRendering = true
        ).let {
            assertTrue(it.contains("style=\"image-rendering: optimizeSpeed; image-rendering: pixelated\""))
        }
    }
}