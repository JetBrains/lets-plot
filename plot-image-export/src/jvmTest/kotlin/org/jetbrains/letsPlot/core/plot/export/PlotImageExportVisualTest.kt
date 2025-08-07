package org.jetbrains.letsPlot.core.plot.export

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.MonolithicCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.MonolithicCommon.SizeUnit.CM
import javax.imageio.ImageIO
import kotlin.test.Test

class PlotImageExportVisualTest {

    // Do not render texts to not get any font-related mismatches on different platforms.
    fun MutableMap<String, Any>.themeTextBlank(): MutableMap<String, Any> {
        this[Option.Plot.THEME] = mapOf(
            "text" to mapOf("blank" to true)
        )
        return this
    }

    fun createImageComparer(): ImageComparer {
        return ImageComparer(
            canvasProvider = AwtTestCanvasProvider(),
            bitmapIO = AwtBitmapIO,
            expectedDir = System.getProperty("user.dir") + "/src/jvmTest/resources/expected/",
            outDir = System.getProperty("user.dir") + "/build/reports/"
        )
    }

    private val imageComparer by lazy { createImageComparer() }

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

        // 5x2cm is the size in centimeters, dpi = 96 means the bitmap will be 189x76 pixels (5 * 96 / 2.54 = 189, 2 * 96 / 2.54 = 76).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

        // 5x2cm is the size in centimeters, dpi = 300 means the bitmap will be 591x238 pixels (5 * 300 / 2.54 = 591, 2 * 300 / 2.54 = 236).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

        // 5x2cm is the size in centimeters, dpi = 300 and scale = 2 means the bitmap will be 1181x475 pixels (5 * 300 / 2.54 * 2 = 1182, 2 * 300 / 2.54 * 2 = 472).
        assertPlot("plot_${w}x${h}cm${dpi}dpi2Xscale_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi, scale=2)
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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

        // 12x4cm is the size in centimeters, dpi = 96 means the bitmap will be 452x152 pixels (12 * 96 / 2.54 = 454, 4 * 96 / 2.54 = 152).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

        // 12x4cm is the size in centimeters, dpi = 300.
        // Taking into account rounding errors while transforming cm -> logical size -> pixels,
        // the bitmap will be 1419x475 pixels (the exact size is 12 * 300 / 2.54 = 1417, 4 * 300 / 2.54 = 472).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

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

        val plotSpec = parsePlotSpec(spec).themeTextBlank()

        assertPlot("plot_400pxx200px2Xscale_test.png", plotSpec, scale=2)
    }



    @Test
    fun `geom_raster() should not fail on image export`() {
        val spec = parsePlotSpec("""
            {
              "data": {
                "x": [ -1.0, 1.0, -1.0, 1.0 ],
                "y": [ -1.0, -1.0, 1.0, 1.0 ],
                "z": [ 0.024, 0.094, 0.094, 0.024 ]
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "fill",
                  "low": "#54278f",
                  "high": "#f2f0f7",
                  "scale_mapper_kind": "color_gradient",
                  "guide": "none"
                }
              ],
              "layers": [
                {
                  "geom": "raster",
                  "mapping": { "x": "x", "y": "y", "fill": "z" }
                }
              ]
            }
        """.trimIndent())
            .themeTextBlank()

        assertPlot("geom_raster_export_test.png", spec)
    }

    @Test
    fun `geom_imshow() should not fail on image export`() {
        val spec = parsePlotSpec("""
            |{
            |    "kind": "plot",
            |    "layers": [
            |        {
            |            "geom": "image",
            |            "xmin": 0.0,
            |            "xmax": 60.0,
            |            "ymin": 0.0,
            |            "ymax": 20.0,
            |            "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVR42mNgYGBo+P//fwMADAAD/kv6htYAAAAASUVORK5CYII="
            |        }
            |    ]
            |}
        """.trimMargin())
            .themeTextBlank()

        assertPlot("geom_imshow_export_test.png", spec)
    }

    private fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = null
    ) {
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val imageData = PlotImageExport.buildImageFromRawSpecs(
            plotSpec = plotSpec,
            format = PlotImageExport.Format.PNG,
            scalingFactor = scale ?: 1.0,
            targetDPI = dpi,
            plotSize = plotSize,
            unit = unit
        )
        val image = ImageIO.read(imageData.bytes.inputStream())
        val bitmap = BitmapUtil.fromBufferedImage(image)


        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }

}