/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.ImageComparer
import kotlin.test.Test
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasProvider
import kotlin.org.jetbrains.letsPlot.pythonExtension.interop.MagickBitmapIO


/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class PlotTest {
    private val imageComparer = ImageComparer(
        suffix = getOSName(),
        expectedDir = getCurrentDir() + "/src/nativeImagickTest/resources/expected/",
        outDir = getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider,
        bitmapIO = MagickBitmapIO,
        tol = 1
    )

    @Test
    fun barPlot() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "time": ["Lunch", "Lunch", "Dinner", "Dinner", "Dinner"] },
            |  "theme": { "text": { "family": "fixed", "blank": false } },
            |  "mapping": {
            |    "x": "time",
            |    "color": "time",
            |    "fill": "time"
            |  },
            |  "layers": [
            |    {
            |      "geom": "bar",
            |      "alpha": "0.5"
            |    }
            |  ]
            |}""".trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_bar_test.bmp", plotSpec)
    }

    @Test
    fun polarPlot() {
            val spec = """
                |{
                |  "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
                |  "coord": { "name": "polar", "theta": "x" },
                |  "ggtitle": { "text": "position=dodge, coord_polar(theta=x)" },
                |  "theme": { "text": { "family": "fixed", "blank": false } },
                |  "kind": "plot",
                |  "scales": [
                |    { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
                |    { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
                |  ],
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "size": 0.0,
                |      "mapping": { "fill": "foo" },
                |      "position": "dodge",
                |      "data_meta": {
                |        "mapping_annotations": [
                |          {
                |            "aes": "fill",
                |            "annotation": "as_discrete",
                |            "parameters": { "label": "foo", "order": 1.0 }
                |          }
                |        ]
                |      }
                |    }
                |  ]
                |}               
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_polar_test.bmp", plotSpec)
    }


    fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Int? = null,
        height: Int? = null,
        scale: Double = 1.0
    ) {
        val bitmap = PlotReprGenerator.exportBitmap(plotSpec, width, height, scale)
        if (bitmap == null)  error("Failed to export bitmap from plot spec")
        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}