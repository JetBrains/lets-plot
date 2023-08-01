/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class NamedSystemColors {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            withFlavor(null),
            withFlavor("solarized_light"),
            withFlavor("solarized_dark"),
            withFlavor("solarized_dark", background = "dark_blue"),
        )
    }

    private fun withFlavor(flavor: String?, background: String? = null): MutableMap<String, Any> {
        val spec = """{   
          "ggsize": { "width": 400, "height": 200 },
          "theme": {
            ${background?.let { "'plot_background': {'fill': '$background', 'blank': false}" }  ?: "" }
            ${if (background != null && flavor != null) "," else ""}
            ${flavor?.let { "'flavor': '$flavor'" } ?: ""}
          },
          "kind": "plot",
          "layers": [
            {
              "geom": "point",
              "size": 20,
              "x": 0.5, 
              "shape": 21,
              "stroke": 5,
              "color": "brush",
              "fill": "paper"
            },
            {
              "geom": "line",
              "data": { "x": [0, 1] },
              "mapping": { "x": "x" },
              "size": 2,
              "color": "pen"
            }
          ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}