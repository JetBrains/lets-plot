package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.junit.Test

class PointGeomTest: VisualPlotTestBase() {
    @Test
    fun sizeUnit() {
        val spec = """
            |{
            |  "kind": "subplots",
            |  "layout": { "ncol": 3.0, "nrow": 1.0, "name": "grid" },
            |  "ggsize": { "width": 700.0, "height": 300.0 },
            |  "figures": [
            |    {
            |      "kind": "plot",
            |      "data": { "x": [ 0.0 ], "y": [ 0.0 ], "s": [ 2.0 ] },
            |      "mapping": { "x": "x", "y": "y", "size": "s" },
            |      "layers": [ { "geom": "point" } ],
            |      "scales": [
            |        { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |        { "aesthetic": "x", "limits": [ -1.5, 1.5 ] },
            |        { "aesthetic": "y", "limits": [ -3.0, 3.0 ] }
            |      ]
            |    },
            |    {
            |      "kind": "plot",
            |      "data": { "x": [ 0.0 ], "y": [ 0.0 ], "s": [ 2.0 ] },
            |      "mapping": { "x": "x", "y": "y", "size": "s" },
            |      "layers": [ { "geom": "point", "size_unit": "x" } ],
            |      "scales": [
            |        { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |        { "aesthetic": "x", "limits": [ -1.5, 1.5 ] },
            |        { "aesthetic": "y", "limits": [ -3.0, 3.0 ] }
            |      ]
            |    },
            |    {
            |      "kind": "plot",
            |      "data": { "x": [0.0], "y": [0.0], "s": [2.0] },
            |      "mapping": { "x": "x", "y": "y", "size": "s" },
            |      "layers": [ { "geom": "point", "size_unit": "y" } ],
            |      "scales": [
            |        { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |        { "aesthetic": "x", "limits": [ -1.5, 1.5 ] },
            |        { "aesthetic": "y", "limits": [-3.0, 3.0] }
            |      ]
            |    }
            |  ]
            |}            
        """.trimMargin()

        assertPlot("geom_point_size_unit.png", parseJson(spec))
    }

    @Test
    fun defaultScaleSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 360.0, "height": 280.0 },
            |  "data": {
            |    "x_val": [ 1.0, 2.0, 3.0, 4.0, 5.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
            |    "y_val": [ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0 ],
            |    "magnitude": [ 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0 ]
            |  },
            |  "mapping": { "x": "x_val", "y": "y_val" },
            |  "layers": [ { "geom": "point", "mapping": { "size": "magnitude"} } ],
            |  "scales": [
            |    { "aesthetic": "size", "breaks": [ 5.0, 10.0, 20.0, 30.0, 40.0, 50.0, 50.0, 70.0, 80.0, 90.0, 100.0 ] },
            |    { "aesthetic": "y", "limits": [ -1.0, 2.0 ] }
            |  ],
            |  "theme": { "text": { "blank": true } }
            |}            
        """.trimMargin()

        assertPlot("geom_point_default_scale_size.png", parseJson(spec))
    }
}