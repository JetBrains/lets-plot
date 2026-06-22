/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

object PlotSpecs {
    val FACET_GRID_TOOLTIP = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 600.0, "height": 150.0 },
        |  "data": {
        |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 ],
        |    "c": [ "a", "b", "c", "d", "e", "f" ],
        |    "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0 ]
        |  },
        |  "facet": { "name": "grid", "x": "g", "x_order": 1.0, "y_order": 1.0 },
        |  "layers": [
        |    {
        |      "geom": "point",
        |      "mapping": { "x": "x", "color": "c" },
        |      "tooltips": { "lines": [ "^color" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val PAN_IN_PROGRESS_WITH_INCOMPLETE_BUFFER = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400, "height": 400 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 20.0, 40.0 ],
        |    "ylim": [ 0.0, 100.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "segment",
        |      "x": 0.0,
        |      "y": 0.0,
        |      "xend": 100.0,
        |      "yend": 100.0
        |    }
        |  ]
        |}
    """.trimMargin()

    val COMPOSITE_TOOLTIP = """
        |{
        |  "kind": "subplots",
        |  "ggsize": { "width": 600.0, "height": 150.0 },
        |  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
        |  "figures": [
        |    {
        |      "kind": "plot",
        |      "data": { "x": [ 1.0, 2.0, 3.0 ], "c": [ "a", "b", "c" ], "g": [ 0.0, 0.0, 0.0 ] },
        |      "layers": [
        |        {
        |          "geom": "point",
        |          "mapping": { "x": "x", "color": "c" },
        |          "tooltips": { "lines": [ "^color" ] }
        |        }
        |      ]
        |    },
        |    {
        |      "kind": "plot",
        |      "data": { "x": [ 4.0, 5.0, 6.0 ], "c": [ "d", "e", "f" ], "g": [ 1.0, 1.0, 1.0 ] },
        |      "layers": [
        |        {
        |          "geom": "point",
        |          "mapping": { "x": "x", "color": "c" },
        |          "tooltips": { "lines": [ "^color" ] }
        |        }
        |      ]
        |    }
        |  ]
        |}
    """.trimMargin()

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
            |          "layers": [ { "geom": "histogram", "bins": 3.0 }
            |          ]
            |        }
            |      ]
            |    }
            |  ]
            |}            
        """.trimMargin()

    val MERGED_TOOLTIP_WITH_TITLE = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ -1.0, 1.0 ],
        |    "ylim": [ -1.0, 1.0 ]
        |  },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ -0.2, 0.0, 0.2 ], "value": [ 18.37, 18.37, 18.37 ] },
        |      "color": "#4f8f5b",
        |      "size": 2.5,
        |      "tooltips": {
        |        "title": "Group 09",
        |        "lines": [ "value|@value" ]
        |      }
        |    },
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ 0.2, 0.0, -0.2 ], "value": [ 32.47, 32.47, 32.47 ] },
        |      "color": "#e78ad5",
        |      "size": 2.5,
        |      "tooltips": {
        |        "title": "Group 17",
        |        "lines": [ "value|@value" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ -1.0, 1.0 ],
        |    "ylim": [ -1.0, 1.0 ]
        |  },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ -0.2, 0.0, 0.2 ], "value": [ 18.37, 18.37, 18.37 ] },
        |      "color": "#4f8f5b",
        |      "size": 2.5,
        |      "tooltips": {
        |        "lines": [ "value|@value" ]
        |      }
        |    },
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ 0.2, 0.0, -0.2 ], "value": [ 32.47, 32.47, 32.47 ] },
        |      "color": "#e78ad5",
        |      "size": 2.5,
        |      "tooltips": {
        |        "lines": [ "value|@value" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP_FLIPPED_WITH_TITLE = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "coord": {
        |    "name": "flip",
        |    "flip": true,
        |    "xlim": [ -1.0, 1.0 ],
        |    "ylim": [ -1.0, 1.0 ]
        |  },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ -0.2, 0.0, 0.2 ], "value": [ 18.37, 18.37, 18.37 ] },
        |      "color": "#4f8f5b",
        |      "size": 2.5,
        |      "tooltips": {
        |        "tooltip_group": "flipped-lines",
        |        "title": "Group 09",
        |        "lines": [ "value|@value" ]
        |      }
        |    },
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ 0.2, 0.0, -0.2 ], "value": [ 32.47, 32.47, 32.47 ] },
        |      "color": "#e78ad5",
        |      "size": 2.5,
        |      "tooltips": {
        |        "tooltip_group": "flipped-lines",
        |        "title": "Group 17",
        |        "lines": [ "value|@value" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP_FLIPPED = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "coord": {
        |    "name": "flip",
        |    "flip": true,
        |    "xlim": [ -1.0, 1.0 ],
        |    "ylim": [ -1.0, 1.0 ]
        |  },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ -0.2, 0.0, 0.2 ], "value": [ 18.37, 18.37, 18.37 ] },
        |      "color": "#4f8f5b",
        |      "size": 2.5,
        |      "tooltips": {
        |        "tooltip_group": "flipped-lines",
        |        "lines": [ "value|@value" ]
        |      }
        |    },
        |    {
        |      "geom": "line",
        |      "data": { "x": [ -0.7, 0.0, 0.7 ], "y": [ 0.2, 0.0, -0.2 ], "value": [ 32.47, 32.47, 32.47 ] },
        |      "color": "#e78ad5",
        |      "size": 2.5,
        |      "tooltips": {
        |        "tooltip_group": "flipped-lines",
        |        "lines": [ "value|@value" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP_DISABLE_SPLITTING_WITH_TITLE = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
        |      },
        |      "color": "#4f8f5b",
        |      "se": true,
        |      "tooltips": {
        |        "title": "Group 09",
        |        "disable_splitting": true
        |      }
        |    },
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ 0.35, 0.2, 0.0, -0.1, 0.0, -0.1, -0.3 ]
        |      },
        |      "color": "#e78ad5",
        |      "se": true,
        |      "tooltips": {
        |        "title": "Group 17",
        |        "disable_splitting": true
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP_DISABLE_SPLITTING = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
        |      },
        |      "color": "#4f8f5b",
        |      "se": true,
        |      "tooltips": {
        |        "disable_splitting": true
        |      }
        |    },
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ 0.35, 0.2, 0.0, -0.1, 0.0, -0.1, -0.3 ]
        |      },
        |      "color": "#e78ad5",
        |      "se": true,
        |      "tooltips": {
        |        "disable_splitting": true
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP_DISABLE_SPLITTING_EXPLICIT_LINES_WITH_TITLE = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
        |      },
        |      "color": "#4f8f5b",
        |      "se": true,
        |      "tooltips": {
        |        "title": "Group 09",
        |        "disable_splitting": true,
        |        "lines": [ "y|@y" ]
        |      }
        |    },
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ 0.35, 0.2, 0.0, -0.1, 0.0, -0.1, -0.3 ]
        |      },
        |      "color": "#e78ad5",
        |      "se": true,
        |      "tooltips": {
        |        "title": "Group 17",
        |        "disable_splitting": true,
        |        "lines": [ "y|@y" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val MERGED_TOOLTIP_DISABLE_SPLITTING_EXPLICIT_LINES = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "theme": {
        |    "panel_grid": "blank",
        |    "tooltip_merge": true
        |  },
        |  "layers": [
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
        |      },
        |      "color": "#4f8f5b",
        |      "se": true,
        |      "tooltips": {
        |        "disable_splitting": true,
        |        "lines": [ "y|@y" ]
        |      }
        |    },
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ 0.35, 0.2, 0.0, -0.1, 0.0, -0.1, -0.3 ]
        |      },
        |      "color": "#e78ad5",
        |      "se": true,
        |      "tooltips": {
        |        "disable_splitting": true,
        |        "lines": [ "y|@y" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val SMOOTH_TOOLTIP_DISABLE_SPLITTING_EXPLICIT_LINES = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 320.0, "height": 240.0 },
        |  "mapping": { "x": "x", "y": "y" },
        |  "theme": {
        |    "panel_grid": "blank"
        |  },
        |  "layers": [
        |    {
        |      "geom": "smooth",
        |      "method": "loess",
        |      "data": {
        |        "x": [ -0.8, -0.5, -0.2, 0.0, 0.2, 0.5, 0.8 ],
        |        "y": [ -0.3, -0.1, 0.0, 0.1, 0.0, 0.2, 0.35 ]
        |      },
        |      "color": "#4f8f5b",
        |      "se": true,
        |      "tooltips": {
        |        "disable_splitting": true,
        |        "lines": [ "y|@y" ]
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

}
