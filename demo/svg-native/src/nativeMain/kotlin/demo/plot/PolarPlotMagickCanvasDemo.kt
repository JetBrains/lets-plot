/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import demo.savePlot
import demoAndTestShared.parsePlotSpec
import kotlinx.cinterop.ExperimentalForeignApi

object PolarPlotMagickCanvasDemo {
    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        savePlot(basic(), "polar_plot_basic.bmp")
    }


    fun basic(): MutableMap<String, Any> {
        val spec = """
{
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "coord": { "name": "polar", "theta": "x" },
      "ggtitle": { "text": "position=dodge, coord_polar(theta=x)" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "size": 0.0,
          "mapping": { "fill": "foo" },
          "position": "dodge",
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": { "label": "foo", "order": 1.0 }
              }
            ]
          }
        }
      ]
    }               
        """.trimIndent()

        return parsePlotSpec(spec)
    }
}
