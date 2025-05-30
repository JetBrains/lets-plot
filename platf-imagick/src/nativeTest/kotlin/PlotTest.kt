/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class PlotTest {
    private val outDir: String = getCurrentDir() + "/build/image-test/"
    private val expectedDir: String = getCurrentDir() + "/src/nativeTest/resources/expected/"

    init {
        mkDir(outDir)
    }

    private val imageComparer = ImageComparer(
        expectedDir = expectedDir,
        outDir = outDir
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

        imageComparer.assertImageEquals("plot_bar_test.bmp", spec)
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

        imageComparer.assertImageEquals("plot_polar_test.bmp", spec)
    }
}