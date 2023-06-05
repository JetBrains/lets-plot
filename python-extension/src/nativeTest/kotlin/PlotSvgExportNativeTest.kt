/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.vis.svgToString.SvgToString
import pngj.RGBEncoderNative
import kotlin.test.Test
import kotlin.test.assertTrue

internal class PlotSvgExportNativeTest {
    @Suppress("TestFunctionName")
    @Test
    fun ggsaveExportImageToSvgFileLP778() {
        val spec = """
            |{
            |  "mapping": {},
            |  "data_meta": {},
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [{
            |               "geom": "image",
            |               "mapping": {},
            |               "show_legend": true,
            |               "data_meta": {},
            |               "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGP4z8DwHwwZ/oOZvkDCF8jyBQCLFgnfUCS+/AAAAABJRU5ErkJggg==",
            |               "xmin": -0.5,
            |               "ymin": -0.5,
            |               "xmax": 2.5,
            |               "ymax": 1.5
            |               }],
            |  "metainfo_list": []
            |}
        """.trimMargin()

        var nativeEncoder = SvgToString(RGBEncoderNative(), false)

        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            nativeEncoder
        ).let { assertTrue(it.contains("iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGP4z8DwHwwZ/oOZvkDCF8jyBQCLFgnfUCS+/AAAAABJRU5ErkJggg==")) }
    }

    @Suppress("TestFunctionName")
    @Test
    fun ggsavePixelatedImageStyleLP778() {
        val spec = """
            |{
            |  "mapping": {},
            |  "data_meta": {},
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [{
            |               "geom": "image",
            |               "mapping": {},
            |               "show_legend": true,
            |               "data_meta": {},
            |               "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGP4z8DwHwwZ/oOZvkDCF8jyBQCLFgnfUCS+/AAAAABJRU5ErkJggg==",
            |               "xmin": -0.5,
            |               "ymin": -0.5,
            |               "xmax": 2.5,
            |               "ymax": 1.5
            |               }],
            |  "metainfo_list": []
            |}
        """.trimMargin()

        var nativeEncoder = SvgToString(RGBEncoderNative(), false)

        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            nativeEncoder
        ).let { assertTrue(it.contains("style=\"image-rendering: optimizeSpeed\"")) }

        nativeEncoder = SvgToString(RGBEncoderNative(), true)
        PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            nativeEncoder
        ).let { assertTrue(it.contains("style=\"image-rendering: optimizeSpeed; image-rendering: pixelated\"")) }

    }

}
