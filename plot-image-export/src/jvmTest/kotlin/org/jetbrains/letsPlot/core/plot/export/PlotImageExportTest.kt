/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package org.jetbrains.letsPlot.core.plot.export

import demoAndTestShared.parsePlotSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.core.plot.export.PlotImageExport.Format
import java.awt.Desktop
import java.io.File
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class PlotImageExportTest {

    @Test
    fun superExportToPngTest() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ 0.0, 1e-05, 2e-05, 3.0000000000000004e-05, 4e-05 ],
            |    "y": [ 0.0, 0.0, 0.0, 0.0, 0.0 ]
            |  },
            |  "mapping": { "x": "x", "y": "y" },
            |  "ggtitle": { "text": "exponent_format=pow" },
            |  "kind": "plot",
            |  "layers": [ { "geom": "point" } ],
            |  "theme": { "exponent_format": "pow" }
            |}""".trimMargin()
        )

        val imageData = PlotImageExport.buildImageFromRawSpecs(spec, Format.PNG)
        assertThat(imageData.bytes).isNotEmpty
        //imageData.show(".png")
    }

    @Test
    fun specialSymbols() {
        val spec = parsePlotSpec("""
            |{
            |  "kind": "plot",
            |  "mapping": {
            |    "x": ["< & ' \" \\ / > ®"],
            |    "y": [1.0]
            |  },
            |  "layers": [ { "geom": "bar", "alpha": 0.5 } ]
            |}""".trimMargin())
        val imageData = PlotImageExport.buildImageFromRawSpecs(spec, Format.PNG)
        assertThat(imageData.bytes).isNotEmpty
        //imageData.show(".png")
    }

    @Test
    fun jpg() {
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

        val imageData = PlotImageExport.buildImageFromRawSpecs(spec, Format.JPEG(0.8))
        assertThat(imageData.bytes).isNotEmpty
        //imageData.show(".jpg")
    }

    @Test
    fun tiff() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val imageData = PlotImageExport.buildImageFromRawSpecs(
            parsePlotSpec(spec),
            Format.TIFF
        )

        assertThat(imageData.bytes).isNotEmpty
        //imageData.show(".tiff")
    }

    @Suppress("unused")
    private fun PlotImageExport.ImageData.show(ext: String) {
        File.createTempFile("plot", "." + ext.trimStart('.')).apply {
            writeBytes(bytes)
            println("Plot saved to $absolutePath")
            Desktop.getDesktop().open(this)
        }
    }
}
