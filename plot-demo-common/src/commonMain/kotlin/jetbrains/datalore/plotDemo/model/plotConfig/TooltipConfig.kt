/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.data.Iris

class TooltipConfig {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            mpg(),
            basic(),
            redefineDefaults(),
            tooltipAesList(),
            tooltipEmptyList(),
            sideTooltips()
        )
    }

    private val aesX = "^x"
    private val aesY = "^y"
    private val aesColor = "^color"
    private val aesFill = "^fill"
    private val vehicleName = "@{vehicle name}"
    private val modelYear = "@{model year}"
    private val originCar = "@{origin of car}"


    private fun mpg(): MutableMap<String, Any> {
        val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : 'Tooltip configuration'},
           'mapping': {
                         'x': 'engine displacement (cu. inches)',
                         'y':  'engine horsepower',
                         'color': 'miles per gallon',
                         'shape': 'origin of car'
                      },
           'layers': [
                        {
                           'geom': 'point',
                           'tooltips' : {
                               'lines': [  
                                    '$aesColor (miles per gallon)',
                                    '@|$modelYear',
                                    'x/y|$aesX x $aesY',                                    
                                    '',
                                    '#mpg data set'
                               ],
                               'formats': [
                                    { 'field' : '$aesX', 'format' : '.1f' }, 
                                    { 'field' : '$aesY', 'format' : '.2f' }, 
                                    { 'field' : 'model year', 'format' : '19{.0f}' }
                               ],                           
                               'title': 'car \'$vehicleName\' ($originCar)'
                           }
                        }
                     ]
        }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec
    }

    private fun redefineDefaults(): MutableMap<String, Any> {
        val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : 'Define format for default tooltips'},
           'mapping': {
                         'x': 'sepal length (cm)',
                         'color': 'sepal width (cm)',
                         'fill': 'target'
                      },
           'layers': [
                        {
                           'geom': 'area',
                           'stat': 'density',
                           'tooltips' : {
                                'formats': [
                                    { 'field' : '$aesColor', 'format' : 'is {.4f} (cm)' }
                                 ]
                            }
                        }
                     ]
        }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : 'No tooltip list (default)'},
           'mapping': {
                         'x': 'sepal length (cm)',
                         'color': 'sepal width (cm)',
                         'fill': 'target'
                      },
           'layers': [
                        {
                           'geom': 'area',
                           'stat': 'density'
                        }
                     ]
        }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun tooltipAesList(): MutableMap<String, Any> {
        val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : 'Tooltip aes list'},
           'mapping': {
                         'x': 'sepal length (cm)',
                         'color': 'sepal width (cm)',
                         'fill': 'target'
                      },
           'layers': [
                        {
                           'geom': 'area',
                           'tooltips' : {
                               'lines': [  
                                    '@|$aesFill',   
                                    'length (x)|$aesX',
                                    'density (y)|$aesY',
                                    '$aesColor' 
                                ]
                           },
                           'stat': 'density'
                        }
                     ]
        }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun tooltipEmptyList(): MutableMap<String, Any> {
        val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : 'No tooltips'},
           'mapping': {
                         'x': 'sepal length (cm)',
                         'color': 'sepal width (cm)',
                         'fill': 'target'
                      },
           'layers': [
                        {
                           'geom': { 
                               'name': 'area',
                               'tooltips': 'none'
                            },
                           'stat': 'density'
                        }
                     ]
        }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun sideTooltips(): MutableMap<String, Any> {
        val spec = """{
      'data': {
            'hwy': [4.2, 11.5, 7.3, 5.8, 6.4, 10.0],
            'class': ["suv", "suv", "suv", "suv", "suv", "suv"]
      },
      'mapping': {
        'x': 'class',
        'y': 'hwy'
      },
      'kind': 'plot',
      'layers': [
        {
          'geom': 'boxplot',
          'tooltips': {
            'formats': [
              { 'field' : '^middle', 'format' : '{{mid}} = {.3f}' },
              { 'field' : '^Y', 'format' : '.2f' }
            ],
            'lines': [   
               'min, max|^ymin, ^ymax'
            ]
          }
        }
      ]
    }""".trimMargin()
        return HashMap(parsePlotSpec(spec))
    }
}
