/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.data.Iris
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class TooltipConfig: PlotConfigDemoBase()  {

    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            mpg(),
            basic(),
            tooltipAesList(),
            tooltipEmptyList()
        )
    }

    private val aesX =  "\$x"
    private val aesY =  "\$y"
    private val aesColor =  "\$color"
    private val aesFill =  "\$fill"
    private val vehicleName = "\${var@vehicle name}"
    private val modelYear =  "\${var@model year}"
    private val originCar = "\${var@origin of car}"

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
                            'tooltips': { 
                                'lines': [
                                           { 'value':[ '$aesX', '$aesY'], 'label' : 'x/y', 'format': '{.1f} x {.2f}' },
                                           { 'value':'$aesColor', 'label': '', 'format': '{.2f} (miles per gallon)' },
                                           { 'value': ['$vehicleName', '$originCar'], 'format' : 'car \'{}\' ({})'},
                                           { 'value': '$modelYear', 'label': '{}', 'format': '19{d}'},              
                                           '$originCar',
                                           '#mpg data set'
                                ]
                            }
                        }
                     ]
        }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec
    }

    private fun basic(): Map<String, Any> {
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

    private fun tooltipAesList(): Map<String, Any> {
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
                           'tooltips': {
                                         'lines': [
                                                     '$aesFill', 
                                                     { 'value':'$aesX', 'label' : 'length (x)' },
                                                     { 'value':'$aesY', 'label' : 'density (y)' },
                                                     { 'value':'$aesColor', 'label' : '' }
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

    private fun tooltipEmptyList(): Map<String, Any> {
        val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : 'Tooltip list = []'},
           'mapping': {
                         'x': 'sepal length (cm)',
                         'color': 'sepal width (cm)',
                         'fill': 'target'
                      },
           'layers': [
                        {
                           'geom': { 
                               'name': 'area',
                               'tooltips': { 'lines': []}
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
}