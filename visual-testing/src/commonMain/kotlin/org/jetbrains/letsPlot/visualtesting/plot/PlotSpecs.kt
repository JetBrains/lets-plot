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

}