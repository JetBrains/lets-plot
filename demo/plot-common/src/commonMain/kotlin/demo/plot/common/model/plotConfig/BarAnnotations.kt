/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class BarAnnotations {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            /*
            barPlot(grouped = false, flip = false),
            barPlot("dodge", flip = false),
            barPlot("stack", flip = false),
            barPlot("fill", flip = false),

            barPlot(grouped = false, flip = true),
            barPlot("dodge", flip = true),
            barPlot("stack", flip = true),
            barPlot("fill", flip = true),*
           */
            withNegativeValues("stack", flip = false),
            withNegativeValues("fill", flip = false),
            withNegativeValues("dodge", flip = false),

            withNegativeValues("stack", flip = true),
            withNegativeValues("fill", flip = true),
            withNegativeValues("dodge", flip = true),
        )
    }


    val data = mapOf(
        "x" to List(6) { 'A' } + List(51) { 'B' } + List(4) { 'D' },
        "v" to listOf('a', 'a', 'a', 'b', 'c', 'c') + List(50) { ('a'..'d').random() } + listOf('e') +
                listOf('a', 'a', 'b', 'c')
    )

    private fun barPlot(
        position: String = "stack",
        flip: Boolean = false,
        grouped: Boolean = true
    ): MutableMap<String, Any> {
        val coordSpec = when {
            flip -> "'coord': {'name': 'flip', 'flip': true},"
            else -> ""
        }
        val mappingSpec = "'x': 'x'" + if (grouped) ", 'fill': 'v'" else ""
        val spec = """
          {
             'kind': 'plot',
              $coordSpec            
             'mapping': { $mappingSpec },
             'layers': [
               {
                 'geom': 'bar',
                 'position': {'name': '$position'},
                 'labels': {
                    'lines': [ '@{..count..}' ],
                    'annotation_size': 15
                  },
                 'tooltips': 'none'
               }
            ]
        }""".trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }

    private val data2 = mapOf(
        "x" to (0..3).map { ('A'..'D').toList() }.flatten(),
        "group" to (0..3).map { ('A'..'D').map { "group$it" } }.flatten().sortedBy { it },
        //"value" to List(16) { (-2..6).random() }.map { if (it == 0) 0.5 else it }
        "value" to listOf(0.5, 5, -1.5, 3, 1, 2, 4, 0.5, -1, 6, 5, -0.2, 5, -2, 5, 4)
    )

    private fun withNegativeValues(position: String, flip: Boolean): MutableMap<String, Any> {
        val coordSpec = when {
            flip -> "'coord': {'name': 'flip', 'flip': true},"
            else -> ""
        }
        val spec = """
        {
          'mapping': { 'x': 'x', 'y': 'value', 'fill': 'group' },
           $coordSpec
          'kind': 'plot',
          'layers': [
            {
                'geom': 'bar',
                'stat': 'identity',
                'position': '$position',
                'labels': {
                    'lines': [
                        '@value'
                    ],
                    'annotation_size': 15
                },
                'color': 'pen',
                'size': 0.3
            },
            {
                'geom': 'hline',
                'tooltips': 'none',
                'yintercept': 0.0
            }
          ]
        }""".trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data2
        return plotSpec

    }
}