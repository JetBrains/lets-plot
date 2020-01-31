/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class Bin2d : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            basic(),
            setBinCount(),
            setBinWidth(),
            setBinWidthMapWeights()
//            densityMapping()
        )
    }


    companion object {

        private val DATA =
            data()  // make it stable between calls

        private fun data(): Map<String, List<*>> {
            val count = 200

            val xs = DemoUtil.gauss(count, 12, 0.0, 5.0)
            val ys = DemoUtil.gauss(count, 21, 0.0, 1.0)
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


        fun basic(): Map<String, Any> {
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

        fun setBinCount(): Map<String, Any> {
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

        fun setBinWidth(): Map<String, Any> {
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

        fun setBinWidthMapWeights(): Map<String, Any> {
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

        fun withWeights(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'," +
                    "             'weight': 'weight'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun densityMapping(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'," +
                    "                  'mapping': {" +
                    "                            'y': '..density..'" +
                    "                          }," +
                    "                  'fill': 'orange'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec1 = HashMap(parsePlotSpec(spec))
            plotSpec1["data"] = DATA
            return plotSpec1
        }

    }
}
