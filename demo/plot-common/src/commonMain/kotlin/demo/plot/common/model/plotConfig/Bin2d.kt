/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal
import demoAndTestShared.parsePlotSpec

open class Bin2d {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            setBinCount(),
            setBinWidth(),
            setBinWidth_Weight(),
            setBinWidth_Weight_Density()
        )
    }


    companion object {

        private val DATA =
            data()  // make it stable between calls

        private fun data(): Map<String, List<*>> {
            val count = 200

            val xs = normal(count, 0.0, 5.0, 12)
            val ys = normal(count, 0.0, 1.0, 21)
            val weights = ArrayList<Double>()
            for (x in xs) {
                weights.add(if (x < 0.0) 2.0 else 0.5);
            }
            return mapOf(
                "x" to xs,
                "y" to ys,
                "weight" to weights
            )
        }


        //===========================


        fun basic(): MutableMap<String, Any> {
            val spec = """
                |   { 
                |      'kind': 'plot', 
                |      'mapping': { 
                |                'x': 'x', 
                |                'y': 'y' 
                |              },                    
                |      'layers': [ 
                |                  { 
                |                     'geom': 'bin2d' 
                |                  } 
                |              ] 
                |   }
            """.trimMargin()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun setBinCount(): MutableMap<String, Any> {
            val spec = """
                |   { 
                |      'kind': 'plot', 
                |      'mapping': { 
                |                'x': 'x', 
                |                'y': 'y' 
                |              },                    
                |      'layers': [ 
                |                  { 
                |                     'geom': 'bin2d',
                |                      'bins': [5,5]
                |                  } 
                |              ] 
                |   }
            """.trimMargin()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun setBinWidth(): MutableMap<String, Any> {
            val spec = """
                |   { 
                |      'kind': 'plot', 
                |      'mapping': { 
                |                'x': 'x', 
                |                'y': 'y' 
                |              },                    
                |      'layers': [ 
                |                  { 
                |                     'geom': 'bin2d',
                |                     'binwidth': [3,3]
                |                  } 
                |              ] 
                |   }
            """.trimMargin()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        @Suppress("FunctionName")
        fun setBinWidth_Weight(): MutableMap<String, Any> {
            val spec = """
                |   { 
                |      'kind': 'plot', 
                |      'mapping': { 
                |                'x': 'x', 
                |                'y': 'y' 
                |              },                    
                |      'layers': [ 
                |                  { 
                |                     'geom': 'bin2d',
                |                     'binwidth': [3,3],
                |                     'mapping': { 
                |                               'weight': 'weight' 
                |                             }                    
                |                  } 
                |              ] 
                |   }
            """.trimMargin()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        @Suppress("FunctionName")
        fun setBinWidth_Weight_Density(): MutableMap<String, Any> {
            val spec = """
                |   { 
                |      'kind': 'plot', 
                |      'mapping': { 
                |                'x': 'x', 
                |                'y': 'y' 
                |              },                    
                |      'layers': [ 
                |                  { 
                |                     'geom': 'bin2d',
                |                     'binwidth': [3,3],
                |                     'mapping': { 
                |                               'weight': 'weight', 
                |                               'fill': '..density..' 
                |                             }                    
                |                  } 
                |              ] 
                |   }
            """.trimMargin()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
