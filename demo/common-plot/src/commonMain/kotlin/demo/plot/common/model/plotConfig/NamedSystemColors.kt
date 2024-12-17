/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class NamedSystemColors {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            pieChart(),
            pieChart(flavor = "darcula"),
            pieChart(flavor = "solarized_light"),
            pieChart(flavor = "solarized_dark"),
            pieChart(flavor = "high_contrast_light"),
            pieChart(flavor = "high_contrast_dark"),
            pieChart(flavor = "darcula", customColors = "'geom': { 'pen': 'red' }")
        )
    }

    private fun pieChart(
        theme: String = "grey",
        flavor: String? = null,
        customColors: String? = null,
    ): MutableMap<String, Any> {
        val flavorOpts = flavor?.let { "'flavor': '$flavor'" } ?: ""

        val themeSettings = listOf(
            "'name': '$theme'",
            customColors ?: "",
            flavorOpts
        )
            .filter(String::isNotEmpty)
            .joinToString()

        val spec = """
            {
              'data': {
                'name': ['pen', 'brush', 'paper']
              },
              'theme': { $themeSettings },
              'ggtitle': { 'text': 'theme=$theme, flavor=$flavor, custom colors=${customColors != null}' },
              'kind': 'plot',
              'scales': [
                {
                  'aesthetic': 'fill',
                  'values': ['pen', 'brush', 'paper']
                }
              ],
              'layers': [
                {
                  'geom': 'pie',
                  'stat': 'identity',
                  'mapping': {
                    'fill': 'name'
                  },
                  'tooltips': 'none',
                  'labels': {
                    'lines': ['@name']
                  },
                  'color': 'pen'
                }
              ]
            }""".trimIndent()
        return parsePlotSpec(spec)
    }

    private fun example(
        theme: String = "light",
        flavor: String? = null,
        background: String? = null
    ): MutableMap<String, Any> {
        val themeSettings = listOf(
            "'name': '$theme'",
            background?.let { "'plot_background': {'fill': '$background', 'blank': false}" } ?: "",
            flavor?.let { "'flavor': '$flavor'" } ?: ""
        ).filter(String::isNotEmpty).joinToString()

        val spec = """{   
          "ggsize": { "width": 400, "height": 200 },
          "ggtitle": { "text": "theme=$theme, flavor=$flavor
                                point: \'brush\'+\'paper\'; line: \'pen\'" }, 
          "theme": { $themeSettings },
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