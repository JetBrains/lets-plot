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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlotSvgExportPortableTest {
    @BeforeTest
    fun setUp() {
        SvgUID.setUpForTest()
    }

    @Test
    fun fuzzyImshow() {
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

        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            plotSize = DoubleVector(400.0, 300.0)
        )

        println(svg)
    }

    @Test
    @Ignore
    fun svgFromSinglePlot() {
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0)
        )

//        println(svg)

        assertEquals(EXPECTED_SINGLE_PLOT_SVG, svg)
    }

    @Test
    @Ignore
    fun svgFromGGBunch() {
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(400.0, 300.0)  // Ignored
        )

        assertEquals(EXPECTED_BUNCH_SVG, svg)

//        println(svg)
    }
}