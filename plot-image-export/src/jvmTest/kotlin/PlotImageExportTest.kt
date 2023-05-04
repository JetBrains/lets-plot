/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package jetbrains.datalore.plot

import kotlin.test.Test

class PlotImageExportTest {

    @Test
    fun specialSymbols() {
        val spec = mutableMapOf(
            "kind" to "plot",
            "mapping" to mapOf(
                "x" to listOf("""< & ' " \ / > ®"""),
                "y" to listOf(1.0)
            ),
            "layers" to listOf(
                mapOf(
                    "geom" to "bar",
                    "alpha" to 0.5
                )
            )
        )
        PlotImageExport.buildImageFromRawSpecs(spec, PlotImageExport.Format.PNG, 1.0, 144.0)
    }

    @Test
    fun `geom_raster() should not fail on image export`() {
        val spec = """
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
                  "scale_mapper_kind": "color_gradient"
                }
              ],
              "layers": [
                {
                  "geom": "raster",
                  "mapping": { "x": "x", "y": "y", "fill": "z" }
                }
              ]
            }
        """.trimIndent()
        PlotImageExport.buildImageFromRawSpecs(parsePlotSpec(spec), PlotImageExport.Format.PNG, 1.0, 144.0)
    }

    @Test
    fun `geom_imshow() should not fail on image export`() {
        val spec = mutableMapOf(
            "kind" to "plot",
            "layers" to listOf(
                mapOf(
                    "geom" to "image",
                    "xmin" to 0.0,
                    "xmax" to 60.0,
                    "ymin" to 0.0,
                    "ymax" to 20.0,
                    "href" to "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVR42mNgYGBo+P//fwMADAAD/kv6htYAAAAASUVORK5CYII="
                )
            )
        )
        PlotImageExport.buildImageFromRawSpecs(spec, PlotImageExport.Format.PNG, 1.0, 144.0)
    }
}
