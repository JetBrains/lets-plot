/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class BarAnnotations {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            barPlot(grouped = false, flip = false),
            barPlot(grouped = false, flip = false, reversed = true),

            barPlot("dodge", flip = false),
            barPlot("stack", flip = false),
            barPlot("stack", flip = false, yLim = "[3, 30]"),
            //barPlot("fill", flip = false),

            //barPlot(grouped = false, flip = true),
            //barPlot("dodge", flip = true),
            //barPlot("stack", flip = true),
            barPlot("fill", flip = true),
            //barPlot("fill", flip = true, yLim = "[0.4, 0.8]"),

            withNegativeValues("stack", flip = false),
            withNegativeValues("stack", flip = false, yLim = "[2, 8]"),
            //withNegativeValues("fill", flip = false),
            //withNegativeValues("dodge", flip = false),

            //withNegativeValues("stack", flip = true),
            //withNegativeValues("fill", flip = true),
            //withNegativeValues("fill", flip = true, yLim = "[-0.5, 0.5]"),
            //withNegativeValues("dodge", flip = true),
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
        grouped: Boolean = true,
        yLim: String? = null,
        reversed: Boolean = false
    ): MutableMap<String, Any> {
        val coordSpec = when {
            flip -> "'coord': {'name': 'flip', 'flip': true, 'ylim': $yLim},"
            yLim != null -> "'coord': {'name': 'cartesian', 'ylim': $yLim},"
            else -> ""
        }
        val mappingSpec = "'x': 'x'" + if (grouped) ", 'fill': 'v'" else ""
        val scales = if (reversed) "'scales': [{'aesthetic': 'y', 'trans': 'reverse'}]," else ""

        val spec = """
          {
             'kind': 'plot',
             'ggtitle': { 'text': 'position=\'$position\', coord_cartesian(ylim=$yLim)'},
              $coordSpec
             'mapping': { $mappingSpec },
             $scales
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

    private fun withNegativeValues(
        position: String,
        flip: Boolean,
        yLim: String? = null,
        reversed: Boolean = false
    ): MutableMap<String, Any> {
        val coordSpec = when {
            flip -> "'coord': {'name': 'flip', 'flip': true, 'ylim': $yLim},"
            yLim != null -> "'coord': {'name': 'cartesian', 'ylim': $yLim},"
            else -> ""
        }
        val scales = if (reversed) "'scales': [{'aesthetic': 'y', 'trans': 'reverse'}]," else ""
        val spec = """
        {
          'kind': 'plot',
          'ggtitle': { 'text': 'position=\'$position\', coord_cartesian(ylim=$yLim)'},
          'mapping': { 'x': 'x', 'y': 'value', 'fill': 'group' },
           $coordSpec
           $scales
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