/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import kotlin.random.Random

open class ScaleBrewerWithContinuousData : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            tiles(),
            tiles(
                limits = "'limits': [-1, 1]",
            ),
            tiles(
                breaks = "'breaks': [-1, -0.5, 0, 0.5, 1]"
            ),
            tiles(
                limits = "'limits': [-1, 1]",
                breaks = "'breaks': [-1, -0.5, 0, 0.5, 1]"
            ),
        )
    }


    companion object {
        fun data(n: Int): Map<String, Any> {
            val rand = Random(37)
            return mapOf(
                "x" to (0..n).toList(),
                "v" to (0..n).toList().map { rand.nextDouble(-1.0, 1.0) }
            )
        }

        private fun getSpec(limits: String?, breaks: String?): Map<String, Any> {
            val spec = """
{
    'mapping': {'x': 'x', 'fill': 'v'}, 
    'kind': 'plot', 
    'ggtitle': {'text': '${breaks?.let { "breaks" } ?: "no breaks"}, ${limits?.let { "limits" } ?: "no limits"}'}, 
    'scales':
        [
            {
                'aesthetic':'fill', 
                ${breaks?.let { breaks + "," } ?: ""}
                ${limits?.let { limits + "," } ?: ""}
                'palette': 'PRGn',
                'scale_mapper_kind': 'color_brewer'
            }
        ], 
    'layers':
        [
            {
                'mapping':{},
                'geom':'tile',
                'tooltips': {'tooltip_formats': [], 'tooltip_lines': ['^fill']}
            }
        ]
}                
            """.trimIndent()

            return parsePlotSpec(spec)
        }

        fun tiles(limits: String? = null, breaks: String? = null): Map<String, Any> {
            val spec = getSpec(limits, breaks)
            val specWithData = HashMap<String, Any>(spec)
            specWithData["data"] = data(10)
            return specWithData
        }
    }
}
