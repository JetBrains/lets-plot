/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal

open class Bin2d {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            setBinCount(),
            setBinWidth(),
            setBinWidth_Weight(),
            setBinWidth_Weight_Density(),
            binWidthCheck()
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

        private fun binWidthCheck(): MutableMap<String, Any> {
            val binWidth = 10.0
            val binHeight = 10.0
            val spec = """
              {
                'kind': 'plot',
                'data': {
                  'x': [0, 1, 9, 10],
                  'y': [0, 19, 1, 0]
                },
                'mapping': {
                  'x': 'x',
                  'y': 'y'
                },
                'ggtitle': {
                  'text': 'Tile size should be equal to [$binWidth, $binHeight]'
                },
                'layers': [
                  {
                    'geom': 'bin2d',
                    'binwidth': [$binWidth, $binHeight],
                    'size': 0.5
                  },
                  {
                    'geom': 'point',
                    'stat': 'bin2d',
                    'binwidth': [$binWidth, $binHeight],
                    'manual_key': {
                      'label': 'Grid node'
                    },
                    'color': 'orange',
                    'size': 5
                  },
                  {
                    'geom': 'point',
                    'manual_key': {
                      'label': 'Raw data point'
                    },
                    'color': 'red',
                    'size': 2.5
                  }
                ]
              }
            """.trimIndent()

            return HashMap(parsePlotSpec(spec))
        }
    }
}
