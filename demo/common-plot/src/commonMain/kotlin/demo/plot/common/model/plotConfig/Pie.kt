/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Pie {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            strValsInX(),
            // pie(hole = 0.0, useCountStat = false),
            // pie(hole = 0.2),
            // pie(hole = 0.5, withOrdering = true),
            //  withExplodes(addLabels = false),
            withExplodes(addLabels = true),
            withStrokeAndSpacerLines(),
            sizeUnit(),
            useFillBy(),
            exponentialNotation()
        )
    }

    private fun strValsInX(): MutableMap<String, Any> {
        val spec = """
            {
              "data": {
                "x": [ "a", "a", "a", "a", "a", "b", "b", "c", "c", "c"],
                "y": [ 1, 1, 1, 1, 1, 2, 2, 1.5, 1.5, 1.5],
                "s": [ 3, 1, 2, 1, 4, 1, 3, 3, 3, 1],
                "n": [ "a", "b", "a", "c", "a", "a", "b", "c", "a", "b"]
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "pie",
                  "mapping": {
                    "x": "x",
                    "y": "y",
                    "slice": "s",
                    "fill": "n"
                  },
                  "hole": 0.3,
                  "size": 10
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private val data = mapOf(
        "name" to ('A'..'H').toList() + 'B',
        "value" to listOf(160, 90, 34, 44, 21, 86, 15, 100, 20)
    )

    private fun pie(
        hole: Double,
        useCountStat: Boolean = true,
        withOrdering: Boolean = false
    ): MutableMap<String, Any> {
        val stat = if (useCountStat) "count2d" else "identity"
        val mapping = if (useCountStat) {
            "'fill': 'name', 'weight': 'value'"
        } else {
            "'fill': 'name', 'slice': 'value'"
        }
        val tooltipContent = if (useCountStat) {
            "'lines': [ '@|^fill', 'count|@{..count..} (@{..prop..})', 'total|@{..sum..}' ]," +
            "'formats': [{'field': '@{..prop..}', 'format': '.0%'}]"
        } else ""
        val ordering = if (useCountStat && withOrdering) {
            """, 'data_meta': {
              'mapping_annotations': [
                {
                  'aes': 'fill',
                  'annotation': 'as_discrete',
                  'parameters': {
                    'label':'name', 'order_by': '..count..', 'order': -1
                  }
                }
              ]
            }""".trimIndent()
        } else ""
        val spec = """
        {
          'kind': 'plot',
          'ggsize': {'width': 400, 'height': 300},
          'ggtitle': {'text' : 'stat=$stat ${if (withOrdering) "with ordering" else ""}; hole=$hole'},
          'theme': { 'line': 'blank', 'axis': 'blank' },
          'mapping': { $mapping },
          'layers': [
            {
              'geom': 'pie',
              'stat': '$stat',
              'hole': $hole,
              'tooltips': { $tooltipContent }
            }
          ]
          $ordering
        }""".trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }

    private fun sizeUnit(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {'text' : 'size_unit=x' },
              'mapping': {'fill': 'name', 'slice': 'value' },
              'layers': [
                {
                  'geom': 'pie', 
                  'size_unit': 'x',
                  'stat': 'identity',
                  'hole': 0.2
                }
              ],
              'coord': {"name": "fixed", "ratio": 1.0, "flip": "False"},
              'scales': [
                  {'aesthetic': 'x', 'limits': [-5, 5]},
                  {'aesthetic': 'y', 'limits': [-5, 5]}
              ]
            }""".trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }

    private fun withExplodes(addLabels: Boolean): MutableMap<String, Any> {
        val labels = if (addLabels) {
            ", 'labels': { 'lines': ['@group_names', '@count']}"
        } else ""
        val length = mapOf(
            "group_names" to listOf(
                "2-3 km",
                "3-5 km",
                "5-7 km",
                "7-10 km",
                "10-20 km",
                "20-50 km",
                "50-75 km",
                "75-100 km",
                ">100 km"
            ),
            "count" to listOf(1109, 696, 353, 192, 168, 86, 74, 65, 53),
            "explode" to listOf(0, 0, 0, 0.1, 0.1, 0.2, 0.3, 0.4, 0.6),
        )
        val spec = """
        {
          'kind': 'plot',
          'theme': { 'axis':'blank', 'line':'blank', 'legend_position':'none' },
          'mapping': { 'slice' : 'count', 'fill': 'group_names', 'explode': 'explode' },
          'layers': [
             {
                'geom': 'pie', 
                'stat': 'identity', 
                'size': 15, 
                'hole': 0.2,
                'stroke': 1.0,
                'color': 'black',
                'stroke_side': 'both',
                'spacer_width': 1.0,
                'spacer_color': 'black'
                $labels
             }
          ]
        }""".trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = length
        return plotSpec
    }

    private fun withStrokeAndSpacerLines(): MutableMap<String, Any> {
        val spec = """
        {
          'kind': 'plot',
          'ggtitle': {'text' : 'stroke aes + \'spacer_width\'=2.0 + \'stroke_side\'=\'both\''},
          'theme': { 'line': 'blank', 'axis': 'blank', 'flavor': 'solarized_light' },
          'mapping': { 
            'fill': 'name',
            'slice': 'value',
            'color': 'color',
            'stroke': 'stroke' 
          },
          'layers': [
            {
              'geom': 'pie',
              'stat': 'identity',
              'size': 20,
              'hole': 0.5,
              'spacer_width': 2.0,
              'stroke_side': 'both'
            }
          ],
          'scales': [
            {
              'aesthetic': 'color',
              'discrete': true,
              'scale_mapper_kind': 'color_brewer', 
              'palette': 'Dark2'
            }
          ]
        }""".trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "name" to ('A'..'C').toList(),
            "value" to listOf(50, 30, 60),
            "stroke" to listOf(6, 8, 10),
            "color" to ('a'..'c').toList(),
        )
        return plotSpec
    }

    private fun useFillBy(): MutableMap<String, Any> {
        val spec = """{
            'ggtitle': {'text' : 'fill_by=\'paint_a\''},            
            'data': {'color': ['a', 'b', 'c'], 's': [1, 2, 3]},
            'theme': {'name': 'classic', 'line': 'blank', 'axis': 'blank', 'legend_position': 'bottom' },
            'kind': 'plot',
            'layers': [
              {
                'geom': 'pie',
                'stat': 'identity',
                'mapping': {'paint_a': 'color','paint_b': 'color', 'slice': 's', 'size': 's' },
                'fill_by': 'paint_a',
                'color_by': 'paint_b'
              }
            ] ,
            'scales': [{'name': 'stroke color',
           'aesthetic': 'paint_b',
           'breaks': [2, 4, 7],
           'labels': ['red', 'green', 'blue'],
           'values': ['red', 'green', 'blue']}]
                }""".trimIndent()

        return parsePlotSpec(spec)
    }

    private fun exponentialNotation(): MutableMap<String, Any> {
        val spec = """
        {
            'theme': { 'axis':'blank', 'line':'blank', 'legend_position':'none' },
            'data': {
                'name': ['a', 'b', 'c'],
                'value': [
                   1e-05,
                   3.0000000000000004e-05,
                   6.000000000000001e-05
                ]
            },
            'kind': 'plot',
            'layers': [
              {
                'geom': 'pie',
                'mapping': {
                    'fill': 'name',
                    'weight': 'value'
                },
                'data_meta': {
                    'mapping_annotations': [
                       {
                         'aes': 'fill',
                         'annotation': 'as_discrete',
                         'parameters': {
                            'label': 'name',
                            'order_by': '..count..',
                            'order': null
                         }
                       }
                    ]
                },
                'labels': { 'lines': ['@name', '^slice', 'size=^size'] },
                'hole': 0.2,
                'size': 15.0
              } 
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}