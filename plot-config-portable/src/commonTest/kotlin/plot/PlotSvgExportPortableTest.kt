/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.testing.EXPECTED_BUNCH_SVG
import jetbrains.datalore.plot.testing.EXPECTED_SINGLE_PLOT_SVG
import jetbrains.datalore.plot.testing.rawSpec_GGBunch
import jetbrains.datalore.plot.testing.rawSpec_SinglePlot
import org.jetbrains.letsPlot.datamodel.mapping.svg.util.UnsupportedRGBEncoder
import kotlin.test.*

internal class PlotSvgExportPortableTest {
    @BeforeTest
    fun setUp() {
        SvgUID.setUpForTest()
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

        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = UnsupportedRGBEncoder(),
            useCssPixelatedImageRendering = false
        ).let { assertTrue(it.contains("style=\"image-rendering: optimizeSpeed\"")) }

        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = UnsupportedRGBEncoder(),
            useCssPixelatedImageRendering = true
        ).let { assertTrue(it.contains("style=\"image-rendering: optimizeSpeed; image-rendering: pixelated\"")) }
    }

    @Test
    @Ignore
    fun svgFromSinglePlot() {
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0),
            rgbEncoder = UnsupportedRGBEncoder(),
            useCssPixelatedImageRendering = false
        )

//        println(svg)

        assertEquals(EXPECTED_SINGLE_PLOT_SVG, svg)
    }

    @Test
    @Ignore
    fun svgFromGGBunch() {
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0),  // Ignored
            rgbEncoder = UnsupportedRGBEncoder(),
            useCssPixelatedImageRendering = false
        )

        assertEquals(EXPECTED_BUNCH_SVG, svg)

//        println(svg)
    }
}