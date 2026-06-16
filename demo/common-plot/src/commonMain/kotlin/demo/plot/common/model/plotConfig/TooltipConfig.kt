/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

class TooltipConfig {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            mpg(),
            basic(),
            redefineDefaults(),
            tooltipAesList(),
            tooltipEmptyList(),
            sideTooltips(),
            mergedTooltips(),
            mergedTooltipsDisableSplitting(),
            mergedTooltipsDisableSplittingExplicitLines(),
            tooltipLimitManyLines(),
            tooltipLimitManyLines(
                title = "Tooltip target limit disabled: merged lines",
                tooltipMerge = true,
                tooltipMaxCount = 0
            )
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
        plotSpec["data"] = demo.plot.common.data.AutoMpg.df
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

    private fun mergedTooltips(): MutableMap<String, Any> {
        val spec = """
        {
          'kind': 'plot',
          'ggtitle': {'text': 'Merged tooltips'},
          'theme': { 'tooltip_merge': true },
          'mapping': { 'x': 'x', 'y': 'y' },
          'coord': {
            'name': 'cartesian',
            'xlim': [ -1.0, 1.0 ],
            'ylim': [ -1.0, 1.0 ]
          },
          'layers': [
            {
              'geom': 'line',
              'data': { 'x': [ -0.7, 0.0, 0.7 ], 'y': [ -0.2, 0.0, 0.2 ], 'value': [ 18.37, 18.37, 18.37 ] },
              'color': '#4f8f5b',
              'size': 2.5,
              'tooltips': {
                'title': 'Group 09',
                'lines': [ 'value|@value' ]
              }
            },
            {
              'geom': 'line',
              'data': { 'x': [ -0.7, 0.0, 0.7 ], 'y': [ 0.2, 0.0, -0.2 ], 'value': [ 32.47, 32.47, 32.47 ] },
              'color': '#e78ad5',
              'size': 2.5,
              'tooltips': {
                'title': 'Group 17',
                'lines': [ 'value|@value' ]
              }
            }
          ]
        }
        """.trimIndent()
        return HashMap(parsePlotSpec(spec))
    }

    private fun mergedTooltipsDisableSplitting(): MutableMap<String, Any> {
        val spec = """
        {
          'kind': 'plot',
          'ggtitle': {'text': 'Merged tooltips with disable_splitting'},
          'theme': { 'tooltip_merge': true },
          'mapping': { 'x': 'x', 'y': 'y' },
          'layers': [
            {
              'geom': 'smooth',
              'method': 'loess',
              'data': {
                'x': [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
                'y': [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
              },
              'color': '#4f8f5b',
              'se': true,
              'tooltips': {
                'title': 'Group 09',
                'disable_splitting': true
              }
            },
            {
              'geom': 'smooth',
              'method': 'loess',
              'data': {
                'x': [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
                'y': [ 0.35, 0.2, 0.0, -0.1, 0.0, -0.1, -0.3 ]
              },
              'color': '#e78ad5',
              'se': true,
              'tooltips': {
                'title': 'Group 17',
                'disable_splitting': true
              }
            }
          ]
        }
        """.trimIndent()
        return HashMap(parsePlotSpec(spec))
    }

    private fun mergedTooltipsDisableSplittingExplicitLines(): MutableMap<String, Any> {
        val spec = """
        {
          'kind': 'plot',
          'ggtitle': {'text': 'Merged tooltips with disable_splitting and explicit lines'},
          'theme': { 'tooltip_merge': true },
          'mapping': { 'x': 'x', 'y': 'y' },
          'layers': [
            {
              'geom': 'smooth',
              'method': 'loess',
              'data': {
                'x': [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
                'y': [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
              },
              'color': '#4f8f5b',
              'se': true,
              'tooltips': {
                'title': 'Group 09',
                'disable_splitting': true,
                'lines': [ 'y|@y' ]
              }
            },
            {
              'geom': 'smooth',
              'method': 'loess',
              'data': {
                'x': [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
                'y': [ 0.35, 0.2, 0.0, -0.1, 0.0, -0.1, -0.3 ]
              },
              'color': '#e78ad5',
              'se': true,
              'tooltips': {
                'title': 'Group 17',
                'disable_splitting': true,
                'lines': [ 'y|@y' ]
              }
            }
          ]
        }
        """.trimIndent()
        return HashMap(parsePlotSpec(spec))
    }

    private fun tooltipLimitManyLines(
        title: String = "Tooltip target limit: closest line only",
        tooltipMerge: Boolean = false,
        tooltipMaxCount: Int? = null
    ): MutableMap<String, Any> {
        val colors = listOf(
            "#004586", "#ff420e", "#ffd320", "#579d1c", "#7e0021", "#83caff",
            "#314004", "#aecf00", "#4b1f6f", "#ff950e", "#c5000b", "#0084d1",
            "#008c48", "#8a004f", "#993f00", "#2bce48", "#ffcc99", "#808080",
            "#94ffb5", "#8f7c00", "#9dcc00", "#c20088", "#003380", "#ffa405",
            "#ffa8bb", "#426600", "#ff0010", "#5ef1f2", "#00998f", "#e0ff66"
        )
        val xs = ArrayList<Double>()
        val ys = ArrayList<Double>()
        val values = ArrayList<Double>()
        val samples = ArrayList<String>()
        colors.forEachIndexed { index, _ ->
            val sample = index + 1
            val y0 = 10.0 + (index % 12) * 0.22 - index * 0.035
            val slope = 0.00182 + (index % 9) * 0.000035
            val bend = if (index == 0) 0.000000025 else 0.0
            listOf(0.0, 3000.0, 6000.0, 9000.0, 12000.0).forEach { x ->
                val y = y0 + slope * x + bend * x * x
                xs.add(x)
                ys.add(y)
                values.add(y)
                samples.add("Sample ${sample.toString().padStart(2, '0')}")
            }
        }

        val theme = mutableMapOf<String, Any>(
            "panel_grid" to mapOf("color" to "#d8d2bf", "size" to 0.5),
            "plot_background" to mapOf("fill" to "#fbf3df"),
            "panel_background" to mapOf("fill" to "#fbf3df"),
            "legend_position" to "none"
        ).apply {
            if (tooltipMerge) {
                this["tooltip_merge"] = true
            }
            if (tooltipMaxCount != null) {
                this["tooltip_max_count"] = tooltipMaxCount
            }
        }

        return mutableMapOf(
            "kind" to "plot",
            "ggtitle" to mapOf("text" to title),
            "ggsize" to mapOf("width" to 760.0, "height" to 480.0),
            "mapping" to mapOf("x" to "x", "y" to "y"),
            "data" to mapOf(
                "x" to xs,
                "y" to ys,
                "value" to values,
                "sample" to samples
            ),
            "coord" to mapOf(
                "name" to "cartesian",
                "xlim" to listOf(-500.0, 12300.0),
                "ylim" to listOf(8.0, 58.0)
            ),
            "theme" to theme,
            "scales" to listOf(
                mapOf(
                    "aesthetic" to "color",
                    "values" to colors
                )
            ),
            "layers" to listOf(
                mapOf(
                    "geom" to "line",
                    "mapping" to mapOf("group" to "sample", "color" to "sample"),
                    "size" to 2.0,
                    "tooltips" to mapOf(
                        "title" to "@sample",
                        "formats" to listOf(mapOf("field" to "value", "format" to ".2f")),
                        "lines" to listOf("@value")
                    )
                )
            )
        )
    }
}
