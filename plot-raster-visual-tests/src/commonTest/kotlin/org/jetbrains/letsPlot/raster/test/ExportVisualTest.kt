package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.util.PlotExportCommon
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test

class ExportVisualTest : VisualTestBase() {

    @Test
    fun `with a long rendering time the race condition should not occur`() {
        // Test potential race condition in image export
        // Could be caused by unexpected use of EDT in the render process.
        val dim = sqrt(40_000.0).roundToInt()
        val rand = Random(12)
        val xs = mutableListOf<String>()
        val ys = mutableListOf<String>()
        val cs = mutableListOf<String>()

        (0..dim).map { x ->
            (0..dim).map { y ->
                xs.add(rand.nextDouble().toString())
                ys.add(rand.nextDouble().toString())
                cs.add(rand.nextDouble().toString())
            }
        }

        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "y": "y", "color": "col" },
            |      "size": 8.0,
            |      "alpha": 0.3,
            |      "sampling": "none",
            |      "data": {
            |        "x": [${xs.joinToString()}],
            |        "y": [${ys.joinToString()}],
            |        "col": [${cs.joinToString()}]
            |      }
            |    }
            |  ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_race_condition_test.png", plotSpec)
    }


    @Test
    fun plotExportImplicitSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 200x200 from ggsize is the size in pixels, scale = 1.0 means the bitmap will be 200x200 pixels
        assertPlot("plot_implicit_size_test.png", plotSpec)
    }

    @Test
    fun plotExportExplicitSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 3x3 inches with 300 DPI means the bitmap will be 900x900 pixels (3 * 300 = 900).
        assertPlot("plot_explicit_size_test.png", plotSpec, width = 3, height = 3)
    }

    @Test
    fun plotExportImplicitSizeScaled() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 200x200 is the size in pixels, scale = 2.0 means the bitmap will be 400x400 pixels
        assertPlot("plot_implicit_size_scaled_test.png", plotSpec, scale = 2.0)
    }

    @Test
    fun plotExportExplicitSizeScaled() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 3x3 inches with 300 DPI and scale = 2.0 means the bitmap will be 1800x1800 pixels (3 * 300 * 2 = 1800).
        assertPlot("plot_explicit_size_scaled_test.png", plotSpec, width = 3, height = 3, scale = 2.0)
    }

    @Test
    fun plot5x2cm96dpi() {
        val (w, h, dpi) = Triple(5, 2, 96)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 5x2cm is the size in centimeters, dpi = 96 means the bitmap will be 189x76 pixels (5 * 96 / 2.54 = 189, 2 * 96 / 2.54 = 76).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = PlotExportCommon.SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot5x2cm300dpi() {
        val (w, h, dpi) = Triple(5, 2, 300)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 5x2cm is the size in centimeters, dpi = 300 means the bitmap will be 591x238 pixels (5 * 300 / 2.54 = 591, 2 * 300 / 2.54 = 236).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = PlotExportCommon.SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot5x2cm300dpi2Xscale() {
        val (w, h, dpi) = Triple(5, 2, 300)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 5x2cm is the size in centimeters, dpi = 300 and scale = 2 means the bitmap will be 1181x475 pixels (5 * 300 / 2.54 * 2 = 1182, 2 * 300 / 2.54 * 2 = 472).
        assertPlot("plot_${w}x${h}cm${dpi}dpi2Xscale_test.png", plotSpec, width = w, height = h, unit = PlotExportCommon.SizeUnit.CM, dpi = dpi, scale=2)
    }

    @Test
    fun plot12x4cm96dpi() {
        val (w, h, dpi) = Triple(12, 4, 96)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 12x4cm is the size in centimeters, dpi = 96 means the bitmap will be 452x152 pixels (12 * 96 / 2.54 = 454, 4 * 96 / 2.54 = 152).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = PlotExportCommon.SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot12x4cm300dpi() {
        val (w, h, dpi) = Triple(12, 4, 300)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 12x4cm is the size in centimeters, dpi = 300.
        // Taking into account rounding errors while transforming cm -> logical size -> pixels,
        // the bitmap will be 1419x475 pixels (the exact size is 12 * 300 / 2.54 = 1417, 4 * 300 / 2.54 = 472).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = PlotExportCommon.SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot400pxx200Dpx() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 400x200 is the size in pixels, scale = 1.0 means the bitmap will be 400x200 pixels
        assertPlot("plot_400pxx200px_test.png", plotSpec)
    }

    @Test
    fun plot400pxx200Dpx150dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // In this case 400x200 is the size in pixels with 96 DPI.
        // Passing only DPI is useful for scaling the plot for printing but keeping plot size and layout intact.
        // For 150 DPI, the bitmap will be scaled to 625x313 pixels (400 * 150 / 96 = 625, 200 * 150 / 96 = 313).
        assertPlot("plot_400pxx200px150dpi_test.png", plotSpec, dpi = 150)
    }

    @Test
    fun plot400pxx200Dpx2Xscale() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_400pxx200px2Xscale_test.png", plotSpec, scale=2)
    }

    @Test
    fun `with dpi=NaN`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        // dpi is NaN, so the bitmap will be exported with the default scaling factor of 1.0
        assertPlot("plot_dpi_nan_test.png", plotSpec, dpi = Double.NaN)
    }
}

