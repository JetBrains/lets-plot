/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package jetbrains.datalore.plot.pythonExtension.pngj

import jetbrains.datalore.plot.PlotSvgExportPortable
import demoAndTestShared.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertTrue

internal class PlotSvgExportNativeTest {

    @Suppress("TestFunctionName")
    @Test
    fun ggsavePixelatedImageStyleLP778() {
        val spec = """
            {
              "data": {
                "x": [ -1.0, 1.0, -1.0, 1.0],
                "y": [ -1.0, -1.0, 1.0, 1.0],
                "z": [ 0.24, 0.94, 0.94, 0.24]
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

        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = RGBEncoderNative(),
            useCssPixelatedImageRendering = false
        ).let { assertTrue(it.contains("style=\"image-rendering: optimizeSpeed\"")) }

        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = RGBEncoderNative(),
            useCssPixelatedImageRendering = true
        ).let { assertTrue(it.contains("style=\"image-rendering: optimizeSpeed; image-rendering: pixelated\"")) }

    }


    @Suppress("TestFunctionName")
    @Test
    fun ggsaveExportImageToSvgFileLP778() {
        val spec = """
            {
              "data": {
                "x": [ -1.0, 1.0, -1.0, 1.0],
                "y": [ -1.0, -1.0, 1.0, 1.0],
                "z": [ 0.24, 0.94, 0.94, 0.24]
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
        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = RGBEncoderNative(),
            useCssPixelatedImageRendering = true
        ).let { assertTrue(it.contains("iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAFklEQVR4nGP49OH7/xD1/v8MIALEAQBnTQvDalokzwAAAABJRU5ErkJggg==")) }
    }

}
