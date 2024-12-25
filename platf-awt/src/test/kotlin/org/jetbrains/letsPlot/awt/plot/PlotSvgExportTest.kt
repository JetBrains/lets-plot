/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.*
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


    @Test
    @Ignore("Disabled to have it in master and to not break the build.")
    fun `LP-942 gggrid composite plot is not visible if saved with ggsave`() {
        val spec = """
        |{
        |  "theme": { "name": "classic", "line": "blank", "axis": "blank" },
        |  "kind": "subplots",
        |  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
        |  "figures": [
        |    {
        |      "kind": "plot",
        |      "layers": [ { "geom": "point", "x": 0.0, "y": 0.0, "size": 24.0, "color": "red" } ]
        |    },
        |    {
        |      "kind": "subplots",
        |      "layout": { "ncol": 1.0, "nrow": 1.0, "name": "grid" },
        |      "figures": [
        |        {
        |          "kind": "plot",
        |          "layers": [ { "geom": "point", "x": 0.0, "y": 0.0, "size": 24.0, "color": "green" } ]
        |        }
        |      ]
        |    }
        |  ]
        |}
        """.trimMargin()

        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec = parsePlotSpec(spec))

        fun findAll(str: String, substr: String): List<Int> {
            val result = mutableListOf<Int>()
            var cur = 0
            while (true) {
                val i = str.indexOf(substr, cur)
                if (i == -1) break
                result += i
                cur = i + 1
            }
            return result
        }

        val svgTags = findAll(svg, "<svg ") + findAll(svg, "</svg>")

        val (lastOpenSvgTag, lastCloseSvgTag) = svgTags
            .sorted() // sort by position, to properly handle nested tags
            .drop(1).dropLast(1) // leave only inner svg elements
            .takeLast(2) // take last svg element

        // Not sure why we have 3 nested svg elements instead of 2. But it was like this before.
        assertEquals(6, svgTags.size)
        val lastSvgElement = svg.substring(lastOpenSvgTag, lastCloseSvgTag + "</svg>".length)

        // The last svg element started to contain <rect> element which covers the whole svg area of the composite plot.
        // Just keep how it was before - without <rect> element.
        // But it's better to not have that last empty <svg> element at all.
        assertEquals(0, findAll(lastSvgElement, "<rect").count())
    }

    @Test
    fun `LP-626 inconsistent number format`() {
        val spec = """
            |{
            |  "data": {
            |    "x": [0.0],
            |    "y": [0.0],
            |    "label": [717273.0]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "column": "x", "type": "int" },
            |      { "column": "y", "type": "int" },
            |      { "column": "label", "type": "int" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "label",
            |      "mapping": { "x": "x", "y": "y", "label": "label" }
            |    }
            |  ]
            |}""".trimMargin()

        PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            useCssPixelatedImageRendering = true
        ).let {
            // Double.toString() should not be used for formatting numbers.
            assertEquals(-1, it.indexOf("717273.0"))

            // NumberFormat should be used.
            assertTrue { it.indexOf("717,273") >= 0 }
        }
    }
}
