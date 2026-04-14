/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

object PlotSpecs {
    val COMPOSITE_NESTED = """
            |{
            |  "kind": "subplots", 
            |  "ggsize": { "width": 400.0, "height": 300.0 }, 
            |  "layout": { "ncol": 1.0, "nrow": 2.0, "name": "grid" }, 
            |  "figures": [
            |    {
            |      "kind": "plot", 
            |      "ggsize": { "width": 200.0, "height": 150.0 }, 
            |      "data": {
            |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ], 
            |        "y": [ 0.49, -0.13, 0.64, 1.52, -0.23, -0.23, 1.57, 0.76, -0.46, 0.54 ]
            |      }, 
            |      "mapping": { "x": "x", "y": "y" }, 
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "int", "column": "x" }, 
            |          { "type": "float", "column": "y" }
            |        ]
            |      }, 
            |      "layers": [ { "geom": "line" } ]
            |    }, 
            |    {
            |      "kind": "subplots", 
            |      "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" }, 
            |      "figures": [
            |        {
            |          "kind": "plot",
            |          "ggsize": { "width": 200.0, "height": 150.0 }, 
            |          "data": {
            |            "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ], 
            |            "y": [ 0.49, -0.13, 0.64, 1.52, -0.23, -0.23, 1.57, 0.76, -0.46, 0.54 ]
            |          }, 
            |          "mapping": { "x": "x", "y": "y" }, 
            |          "data_meta": {
            |            "series_annotations": [
            |              { "type": "int", "column": "x" }, 
            |              { "type": "float", "column": "y" }
            |            ]
            |          }, 
            |          "layers": [ { "geom": "point" } ]
            |        }, 
            |        {
            |          "kind": "plot", 
            |          "ggsize": { "width": 200.0, "height": 150.0 }, 
            |          "data": {
            |            "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ], 
            |            "y": [ 0.49, -0.13, 0.64, 1.52, -0.23, -0.23, 1.57, 0.76, -0.46, 0.54 ]
            |          }, 
            |          "mapping": { "x": "x", "y": "y" }, 
            |          "data_meta": {
            |            "series_annotations": [
            |              { "type": "int", "column": "x" }, 
            |              { "type": "float", "column": "y" }
            |            ]
            |          }, 
            |          "layers": [ { "geom": "histogram", "bins": 3.0 }
            |          ]
            |        }
            |      ]
            |    }
            |  ]
            |}            
        """.trimMargin()

    val POINT_AND_LINE = """
        |{
        |  "kind": "plot",
        |  "layers": [
        |    {
        |      "geom": "point",
        |      "mapping": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
        |        "y": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 ]
        |      },
        |      "tooltips": { "variables": [ "x" ] }
        |    },
        |    {
        |      "geom": "line",
        |      "mapping": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
        |        "y": [ 0.0, 5.0, 2.0, 7.0, 1.0, 8.0 ]
        |      }
        |    }
        |  ]
        |}
        """.trimMargin()

    /*
        df1 = {
            "x1": [ 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0 ],
            "y1": [ 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0 ]
        }

        df2 = {
            "x2": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
            "y2": [ 0.0, 5.0, 2.0, 7.0, 1.0, 8.0 ]
        }

        p = ggplot() + \
            geom_line(aes(x='x1', y='y1'), data=df1, color='red', tooltips='none', linetype=3) + \
            geom_line(aes(x='x2', y='y2'), data=df2, color='blue', tooltips='none', linetype=3) + \
            geom_point(aes(x='x1', y='y1'), data=df1, color='red', tooltips=layer_tooltips(['y1'])) + \
            geom_point(aes(x='x2', y='y2'), data=df2, color='blue', tooltips=layer_tooltips(['y2']))
        p
     */
    val POINT_AND_POINT = """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "line",
            |      "data": {
            |        "x1": [ 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0 ],
            |        "y1": [ 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0 ]
            |      },
            |      "mapping": { "x": "x1", "y": "y1" },
            |      "tooltips": "none",
            |      "color": "red",
            |      "linetype": 3.0
            |    },
            |    {
            |      "geom": "line",
            |      "data": {
            |        "x2": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
            |        "y2": [ 0.0, 5.0, 2.0, 7.0, 1.0, 8.0 ]
            |      },
            |      "mapping": { "x": "x2", "y": "y2" },
            |      "tooltips": "none",
            |      "color": "blue",
            |      "linetype": 3.0
            |    },
            |    {
            |      "geom": "point",
            |      "data": {
            |        "x1": [ 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0 ],
            |        "y1": [ 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0 ]
            |      },
            |      "mapping": { "x": "x1", "y": "y1" },
            |      "tooltips": { "variables": [ "y1" ] },
            |      "color": "red"
            |    },
            |    {
            |      "geom": "point",
            |      "data": {
            |        "x2": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
            |        "y2": [ 0.0, 5.0, 2.0, 7.0, 1.0, 8.0 ]
            |      },
            |      "mapping": { "x": "x2", "y": "y2" },
            |      "tooltips": { "variables": [ "y2" ] },
            |      "color": "blue"
            |    }
            |  ]
            |}
        """.trimMargin()


    /*
        data = {
            'x': [1, 2, 3, 4, 5],
            'y1': [13, 15, 14, 18, 17],
            'y2': [1, 2, 5, 4, 6],
            'y3': [5.5, 10, 7, 10.5, 13]
        }

        p = ggplot(data, aes(x='x')) + \
            geom_line(aes(y='y1'), color='red', size=1,
                      tooltips=layer_tooltips()
                      .format('@y1', '.1f')
                      .line('Series A: @y1')) + \
            geom_line(aes(y='y2'), color='blue', size=1,
                      tooltips=layer_tooltips()
                      .format('@y2', '.2f')
                      .line('Series B: @y2')) + \
            geom_smooth(aes(y='y3'), method='loess', color='green', size=1, se=False,
                        tooltips=layer_tooltips()
                        .format('@y3', '.2f')
                        .line('Series C: @y3')) + \
            geom_point(aes(y='y1'), color='red', tooltips=['y1']) + \
            geom_point(aes(y='y2'), color='blue', tooltips=['y2'])

        p
     */
    val POINT_LINE_SMOOTH = """
        |{
        |  "kind": "plot",
        |  "data": {
        |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0 ],
        |    "y1": [ 13.0, 15.0, 14.0, 18.0, 17.0 ],
        |    "y2": [ 1.0, 2.0, 5.0, 4.0, 6.0 ],
        |    "y3": [ 5.5, 10.0, 7.0, 10.5, 13.0 ]
        |  },
        |  "mapping": { "x": "x" },
        |  "layers": [
        |    {
        |      "geom": "line", "color": "red", "size": 1.0, 
        |      "mapping": { "y": "y1" },
        |      "tooltips": { "formats": [ { "field": "@y1", "format": ".1f" } ],
        |        "lines": [ "Series A: @y1" ],
        |        "tooltip_group": "__auto_line_group__"
        |      }
        |    },
        |    {
        |      "geom": "line", "color": "blue", "size": 1.0,
        |      "mapping": { "y": "y2" },
        |      "tooltips": {
        |        "formats": [ { "field": "@y2", "format": ".2f" } ],
        |        "lines": [ "Series B: @y2" ],
        |        "tooltip_group": "__auto_line_group__"
        |      }
        |    },
        |    {
        |      "geom": "smooth", "method": "loess", "se": false, "color": "green", "size": 1.0,
        |      "mapping": { "y": "y3" },
        |      "tooltips": {
        |        "formats": [ { "field": "@y3", "format": ".2f" } ],
        |        "lines": [ "Series C: @y3" ],
        |        "tooltip_group": "__auto_line_group__"
        |      }
        |    },
        |    {
        |      "geom": "point", "color": "red",
        |      "mapping": { "y": "y1" },
        |      "tooltips": { "variables": [ "y1" ] }
        |    },
        |    {
        |      "geom": "point", "color": "blue",
        |      "mapping": { "y": "y2" },
        |      "tooltips": { "variables": [ "y2" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val HISTOGRAM_DENSITY = """
        |{
        |  "kind": "plot",
        |  "data": {
        |    "sepal_length": [ 6.0, 5.7, 5.2, 4.5, 6.5, 4.6, 5.7, 4.9, 5.5, 5.4, 4.8, 4.6, 6.0, 6.7, 5.5, 5.4, 6.8, 6.7, 7.7, 4.7, 5.6, 7.3, 5.8, 6.3, 6.3, 5.5, 5.8, 5.2, 5.1, 5.8, 7.2, 5.1, 5.1, 6.9, 6.3, 6.1, 6.0, 6.2, 5.0, 5.1, 6.3, 4.4, 5.6, 5.9, 5.2, 5.8, 4.4, 4.3, 6.4, 5.0, 7.2, 5.6, 6.7, 5.0, 5.4, 5.1, 4.8, 6.4, 4.6, 5.8, 6.4, 6.3, 6.4, 5.3, 6.7, 6.4, 6.5, 6.2, 6.2, 6.9, 4.9, 6.1, 5.5, 5.5, 5.0, 5.4, 7.2, 5.0, 4.6, 5.0, 5.6, 5.9, 6.1, 6.9, 7.7, 5.0, 6.1, 5.7, 5.0, 7.6, 6.7, 5.1, 6.7, 4.9, 6.5, 5.7, 6.3, 5.9, 7.0, 7.1, 5.7, 5.1, 5.1, 7.4, 5.4, 6.6, 5.0, 4.9 ],
        |    "sepal_width": [ 3.4, 3.0, 2.7, 2.3, 3.0, 3.6, 2.6, 3.0, 2.5, 3.0, 3.4, 3.1, 3.0, 3.1, 2.4, 3.4, 3.0, 3.3, 2.8, 3.2, 3.0, 2.9, 2.8, 2.3, 2.7, 2.4, 2.6, 4.1, 3.3, 2.7, 3.2, 3.8, 3.7, 3.1, 2.5, 3.0, 2.2, 2.9, 2.3, 3.4, 2.8, 2.9, 2.7, 3.0, 3.5, 2.7, 3.0, 3.0, 3.2, 3.5, 3.6, 3.0, 3.3, 3.6, 3.9, 3.8, 3.4, 3.1, 3.2, 4.0, 3.2, 3.3, 2.9, 3.7, 3.0, 2.7, 2.8, 3.4, 2.8, 3.2, 2.5, 2.9, 2.6, 4.2, 3.5, 3.9, 3.0, 3.4, 3.4, 3.0, 2.8, 3.2, 2.6, 3.1, 3.8, 2.0, 2.8, 4.4, 3.2, 3.0, 3.1, 3.5, 3.1, 2.4, 3.0, 2.8, 3.4, 3.0, 3.2, 3.0, 2.9, 2.5, 3.5, 2.8, 3.7, 2.9, 3.3, 3.1 ],
        |    "petal_length": [ 4.5, 4.2, 3.9, 1.3, 5.5, 1.0, 3.5, 1.4, 4.0, 4.5, 1.9, 1.5, 4.8, 4.7, 3.7, 1.7, 5.5, 5.7, 6.7, 1.3, 4.5, 6.3, 5.1, 4.4, 4.9, 3.8, 4.0, 1.5, 1.7, 4.1, 6.0, 1.9, 1.5, 5.4, 5.0, 4.6, 5.0, 4.3, 3.3, 1.5, 5.1, 1.4, 4.2, 5.1, 1.5, 5.1, 1.3, 1.1, 5.3, 1.3, 6.1, 4.1, 5.7, 1.4, 1.3, 1.6, 1.6, 5.5, 1.4, 1.2, 4.5, 6.0, 4.3, 1.5, 5.0, 5.3, 4.6, 5.4, 4.8, 5.7, 4.5, 4.7, 4.4, 1.4, 1.6, 1.7, 5.8, 1.5, 1.4, 1.6, 4.9, 4.8, 5.6, 4.9, 6.7, 3.5, 4.0, 1.5, 1.2, 6.6, 4.4, 1.4, 5.6, 3.3, 5.2, 4.1, 5.6, 4.2, 4.7, 5.9, 4.2, 3.0, 1.4, 6.1, 1.5, 4.6, 1.4, 1.5 ],
        |    "petal_width": [ 1.6, 1.2, 1.4, 0.3, 1.8, 0.2, 1.0, 0.2, 1.3, 1.5, 0.2, 0.2, 1.8, 1.5, 1.0, 0.2, 2.1, 2.1, 2.0, 0.2, 1.5, 1.8, 2.4, 1.3, 1.8, 1.1, 1.2, 0.1, 0.5, 1.0, 1.8, 0.4, 0.4, 2.1, 1.9, 1.4, 1.5, 1.3, 1.0, 0.2, 1.5, 0.2, 1.3, 1.8, 0.2, 1.9, 0.2, 0.1, 2.3, 0.3, 2.5, 1.3, 2.5, 0.2, 0.4, 0.2, 0.2, 1.8, 0.2, 0.2, 1.5, 2.5, 1.3, 0.2, 1.7, 1.9, 1.5, 2.3, 1.8, 2.3, 1.7, 1.4, 1.2, 0.2, 0.6, 0.4, 1.6, 0.2, 0.3, 0.2, 2.0, 1.8, 1.4, 1.5, 2.2, 1.0, 1.3, 0.4, 0.2, 2.1, 1.4, 0.2, 2.4, 1.0, 2.0, 1.3, 2.4, 1.5, 1.4, 2.1, 1.3, 1.1, 0.3, 1.9, 0.2, 1.3, 0.2, 0.1 ]
        |  },
        |  "ggsize": { "width": 500.0, "height": 500.0 },
        |  "layers": [
        |    { 
        |      "geom": "histogram", 
        |      "mapping": { "x": "sepal_length" } 
        |    },
        |    {
        |      "geom": "density", "color": "red", "linetype": "dashed", "size": 1.0,
        |      "mapping": { "x": "sepal_length", "y": "..density.." }
        |    }
        |  ]
        |}        
    """.trimMargin()
}
