/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PlotHtmlExportTest {

    @Test
    fun `HTML with gggrid with subgggrids and blanks should not contain computation messages`() {
        val spec = """
                    |{
                    |  "kind": "subplots",
                    |  "layout": { "ncol": 2, "nrow": 2, "name": "grid" },
                    |  "figures": [
                    |    null,
                    |    {
                    |      "kind": "plot",
                    |      "layers": [
                    |        {
                    |          "geom": "point",
                    |          "mapping": {
                    |            "x": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                    |            "y": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
                    |          },
                    |          "sampling": { "name": "random", "n": 5 }
                    |        }
                    |      ]
                    |    },
                    |    {
                    |      "kind": "plot",
                    |      "layers": [
                    |        {
                    |          "geom": "point",
                    |          "mapping": {
                    |            "x": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                    |            "y": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
                    |          },
                    |          "sampling": { "name": "random", "n": 4 }
                    |        }
                    |      ]
                    |    },
                    |    {
                    |      "kind": "subplots",
                    |      "layout": { "ncol": 2,"nrow": 1, "name": "grid" },
                    |      "figures": [
                    |        {
                    |          "kind": "plot",
                    |          "layers": [
                    |            {
                    |              "geom": "point",
                    |              "mapping": {
                    |                "x": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                    |                "y": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
                    |              },
                    |              "sampling": { "name": "random", "n": 3 }
                    |            }
                    |          ]
                    |        },
                    |        null
                    |      ]
                    |    }
                    |  ]
                    |}        
                    |""".trimMargin()

        val outputHtml = PlotHtmlExport.buildHtmlFromRawSpecs(parsePlotSpec(spec), "")
        assertThat(outputHtml).doesNotContain("was applied to")
    }
}