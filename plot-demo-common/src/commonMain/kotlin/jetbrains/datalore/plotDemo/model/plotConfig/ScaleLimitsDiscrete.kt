/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class ScaleLimitsDiscrete {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            tiles(),
            tilesLimitsReversed(),
            tilesNoLimitsFlagReverse(),
            tilesWithLimitsFlagReverse(),
        )
    }


    companion object {

        fun tiles(): MutableMap<String, Any> {
            return getSpec("{'x':['a', 'b', 'c', 'd', 'e']}", "['b', 'c', 'e']")
        }

        fun tilesLimitsReversed(): MutableMap<String, Any> {
            return getSpec("{'x':['a', 'b', 'c', 'd', 'e']}", "['e', 'c', 'b']")
        }

        fun tilesNoLimitsFlagReverse(): MutableMap<String, Any> {
            return getSpec("{'x':['a', 'b', 'c']}", null, reverse = true)
        }

        fun tilesWithLimitsFlagReverse(): MutableMap<String, Any> {
            return getSpec("{'x':['a', 'b', 'c']}", "['a', 'b', 'c']", reverse = true)
        }


        private fun getSpec(data: String, limits: String?, reverse: Boolean = false): MutableMap<String, Any> {

            val limitsDescr = limits?.let { "limits: $it" } ?: "no limits"
            val reverseDescr = if (reverse) ", reversed" else ""
            val title = "$data, x: $limitsDescr $reverseDescr".filterNot { it == '\'' }

            val spec = """
{
    'mapping':{}, 
    'data':$data, 
    'kind':'plot', 
    'scales':
        [
            {
                'aesthetic':'x', 
                'limits':$limits,
                'reverse':$reverse
            }
        ], 
    'layers':
        [
            {
                'mapping':{'x':'x', 'fill':'x'},
                'geom':'tile'
            }
        ],
    'ggtitle': {'text': "$title"}        
}                
            """.trimIndent()

            return parsePlotSpec(spec)
        }
    }
}
