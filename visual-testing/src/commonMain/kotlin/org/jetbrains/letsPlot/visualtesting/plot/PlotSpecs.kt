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

    /*
        from lets_plot import *
        import random

        LetsPlot.setup_html()

        random.seed(42)
        data = {
            "x": [random.gauss(-1.0, 0.7) for _ in range(50)] +
                 [random.gauss(1.5, 0.9) for _ in range(70)]
        }

        p = (
            ggplot(data, aes(x="x"))
            + geom_histogram(
                aes(y="..density.."),
                bins=25,
                fill="#a8dadc",
                color="#457b9d",
                alpha=0.6,
                tooltips=layer_tooltips()
                    .title("Histogram")
                    .format("@x", ".2f")
                    .line("bin center|@x")
                    .line("count|@..count..")
                    .format("@..density..", ".4f")
                    .line("density|@..density..")
            )
            + geom_density(
                color="#d62828",
                size=1.2,
                tooltips=layer_tooltips()
                    .title("Density")
                    .format("@x", ".2f")
                    .line("x|@x")
                    .format("@..density..", ".4f")
                    .line("density|@..density..")
                    .format("@..count..", ".1f")
                    .line("scaled count|@..count..")
            )
        )

        p
     */
    val HISTOGRAM_DENSITY = """
        |{
        |  "kind": "plot",
        |  "data": {
        |    "x": [ -1.100, -1.121, -1.077, -0.508, -1.089, -2.048, -0.767, -1.187, -1.151, -0.918, -0.837, -0.185, -0.540, -0.922, -1.516, -1.710, -0.827, -0.082, -0.970, -1.074, -0.627, -2.017, -1.218, -0.656, -0.388, -1.168, -0.736, -0.826, -0.452, -1.779, -0.602, -2.060, -2.833, -1.424, -1.641, -0.386, -0.535, -1.853, -0.406, -1.701, -1.060, -1.205, -0.919, -0.426, -0.553, -0.755, -0.545, -0.665, -1.438, -1.502, 1.0770, 1.9493, 1.2748, 3.6021, 0.7626, 0.5110, 2.1916, 2.7796, 1.9551, 2.2522, 2.7837, 1.4153, 0.2193, 1.0211, 2.3576, 0.2006, 1.5301, 1.7279, 1.2159, 2.1512, 2.0227, 3.5892, 2.0579, 0.9515, 0.9943, 0.7515, 2.3570, 0.9898, 1.4367, 2.1743, 0.8488, 1.2357, -0.157, 0.5257, 0.9890, 1.8741, 2.5741, 1.4833, 1.7352, 1.6511, 2.4762, 2.3040, 1.7463, 0.5901, 2.3130, 1.8429, 2.6042, 1.4730, 3.2577, 1.1770, 2.9337, 1.6036, 1.0353, 0.4843, 1.3640, 2.7809, 2.2347, 2.1199, -0.638, 2.1398, 2.0002, 1.0050, 0.9353, 1.4979, 3.0523, 0.5504, 1.1149, 2.7256, 1.0984, 1.1721 ] },
        |  "mapping": { "x": "x" },
        |  "layers": [
        |    {
        |      "geom": "histogram", "bins": 25.0, "fill": "#a8dadc", "color": "#457b9d", "alpha": 0.6,
        |      "mapping": { "y": "..density.." },
        |      "tooltips": {
        |        "title": "Histogram",
        |        "formats": [
        |          { "field": "@x", "format": ".2f" },
        |          { "field": "@..density..", "format": ".4f" }
        |        ],
        |        "lines": [
        |          "bin center|@x",
        |          "count|@..count..",
        |          "density|@..density.."
        |        ]
        |      }
        |    },
        |    {
        |      "geom": "density", "color": "#d62828", "size": 1.2,
        |      "tooltips": {
        |        "title": "Density",
        |        "formats": [
        |          { "field": "@x", "format": ".2f" },
        |          { "field": "@..density..", "format": ".4f" },
        |          { "field": "@..count..", "format": ".1f" }
        |        ],
        |        "lines": [
        |          "x|@x",
        |          "density|@..density..",
        |          "scaled count|@..count.."
        |        ]
        |      }
        |    }
        |  ]
        |}""".trimMargin()
}
