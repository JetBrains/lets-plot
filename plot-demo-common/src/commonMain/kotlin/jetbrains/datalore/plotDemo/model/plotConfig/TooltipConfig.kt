/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.data.Iris
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class TooltipConfig : PlotConfigDemoBase() {

    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            mpg(),
            basic(),
            redefineDefaults(),
            tooltipAesList(),
            tooltipEmptyList()
        )
    }

    private val aesX = "\$x"
    private val aesY = "\$y"
    private val aesColor = "\$color"
    private val aesFill = "\$fill"
    private val vehicleName = "\${var@vehicle name}"
    private val modelYear = "\${var@model year}"
    private val originCar = "\${var@origin of car}"


    private fun mpg(): Map<String, Any> {
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
                               'tooltip_lines': [  
                                    'x/y|$aesX x $aesY', 
                                    '$aesColor (miles per gallon)',
                                    'car \'$vehicleName\' ($originCar)',
                                    '@|19$modelYear',
                                    '@|$originCar',
                                    '#mpg data set'
                               ],
                               'tooltip_formats': [
                                    { 'field' : 'x', 'format' : '.1f' }, 
                                    { 'field' : 'y', 'format' : '{.2f}' }, 
                                    { 'field' : 'color', 'format' : '{.2f} text' }
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

    private fun redefineDefaults(): Map<String, Any> {
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
                                'tooltip_formats': [
                                    { 'field' : 'color', 'format' : 'is {.4f} (cm)' }
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
                           'tooltips' : {
                               'tooltip_lines': [  
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

    private fun tooltipEmptyList(): Map<String, Any> {
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
}