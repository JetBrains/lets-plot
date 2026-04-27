/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

object PlotTooltipsSpecs {

    val ANCHOR_FOR_RECT_LIKE_GEOM = """
        |{
        |  "kind": "plot",
        |  "data": {
        |    "x": [ "a", "b", "c", "d" ],
        |    "ymin": [ 5.0, 7.0, 3.0, 5.0 ],
        |    "y": [ 6.5, 9.0, 4.5, 7.0 ],
        |    "ymax": [ 8.0, 11.0, 6.0, 9.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "pointrange",
        |      "mapping": { "x": "x", "y": "y", "ymin": "ymin", "ymax": "ymax" },
        |      "tooltips": { "tooltip_anchor": "top_right" }
        |    }
        |  ]
        |}        
    """.trimMargin()

    val POINT_AND_POINT_WITH_CROSSHAIR = """
        |{
        |  "kind": "plot",
        |  "ggtitle": { "text": "anchored: point + point" },
        |  "layers": [
        |    {
        |      "geom": "point", "color": "#6a3d9a", "size": 6.0, "alpha": 0.8,
        |      "data": {
        |        "x": [ 1.0, 1.8, 2.8, 3.3, 4.0 ],
        |        "y": [ 1.0, 2.2, 1.4, 2.8, 1.8 ],
        |        "layer": [ "A1", "A2", "A3", "A4", "A5" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": { "lines": [ "point A @layer" ], "tooltip_anchor": "top_left" }
        |    },
        |    {
        |      "geom": "point", "color": "#b15928", "size": 6.0, "alpha": 0.8,
        |      "data": {
        |        "x": [ 1.3, 2.0, 2.9, 3.6, 4.1 ],
        |        "y": [ 1.4, 2.0, 1.8, 2.5, 1.5 ],
        |        "layer": [ "B1", "B2", "B3", "B4", "B5" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": { "lines": [ "point B @layer" ], "tooltip_anchor": "top_right" }
        |    }
        |  ]
        |}
    """.trimMargin()

    val POINT_AND_POINT_WITH_CROSSHAIR_OVERLAP = """
        |{
        |  "kind": "plot",
        |  "ggtitle": { "text": "anchored: overlapping point + point" },
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 0.0, 4.0 ] },
        |    { "aesthetic": "y", "limits": [ 0.0, 4.0 ] }
        |  ],
        |  "layers": [
        |    {
        |      "geom": "point", "color": "#6a3d9a", "size": 30.0, "alpha": 0.75,
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ],
        |        "layer": [ "A" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": { "lines": [ "point A @layer" ], "tooltip_anchor": "top_left" }
        |    },
        |    {
        |      "geom": "point", "color": "#b15928", "size": 30.0, "alpha": 0.75,
        |      "data": {
        |        "x": [ 2.24 ],
        |        "y": [ 2.0 ],
        |        "layer": [ "B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": { "lines": [ "point B @layer" ], "tooltip_anchor": "top_right" }
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

    val BOXPLOT_AND_POINT = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "layers": [
        |    {
        |      "geom": "boxplot",
        |      "stat": "identity",
        |      "data": {
        |        "x": [ 1.0 ],
        |        "lower": [ 4.0 ],
        |        "middle": [ 5.0 ],
        |        "upper": [ 6.0 ],
        |        "ymin": [ 3.0 ],
        |        "ymax": [ 7.0 ],
        |        "width": [ 0.6 ]
        |      },
        |      "mapping": {
        |        "x": "x",
        |        "lower": "lower",
        |        "middle": "middle",
        |        "upper": "upper",
        |        "ymin": "ymin",
        |        "ymax": "ymax",
        |        "width": "width"
        |      },
        |      "fill": "#A8DADC",
        |      "color": "#1D3557"
        |    },
        |    {
        |      "geom": "point",
        |      "data": {
        |        "x": [ 1.0 ],
        |        "y": [ 5.0 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "color": "#E63946",
        |      "size": 12.0,
        |      "tooltips": { "lines": [ "point tooltip" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 0.0, 2.0 ] },
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val ERRORBAR_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "category": [ "A" ],
        |    "ymin": [ 3.0 ],
        |    "ymax": [ 7.0 ],
        |    "id": [ "errorbar" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "ymin" },
        |      { "type": "float", "column": "ymax" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "ymin", "ymax": "ymax" },
        |  "layers": [
        |    {
        |      "geom": "errorbar",
        |      "width": 0.25,
        |      "size": 2.0,
        |      "color": "#4E79A7",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val ERRORBAR_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "category": [ "A" ],
        |    "xmin": [ 3.0 ],
        |    "xmax": [ 7.0 ],
        |    "id": [ "errorbar" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "xmin" },
        |      { "type": "float", "column": "xmax" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "y": "category", "xmin": "xmin", "xmax": "xmax" },
        |  "layers": [
        |    {
        |      "geom": "errorbar",
        |      "height": 0.25,
        |      "size": 2.0,
        |      "color": "#4E79A7",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val CROSSBAR_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "category": [ "A" ],
        |    "lower": [ 3.0 ],
        |    "middle": [ 5.0 ],
        |    "upper": [ 7.0 ],
        |    "id": [ "crossbar" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "lower" },
        |      { "type": "float", "column": "middle" },
        |      { "type": "float", "column": "upper" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "lower", "y": "middle", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "crossbar",
        |      "width": 0.45,
        |      "fill": "#A8DADC",
        |      "color": "#1D3557",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val CROSSBAR_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": { "name": "flip", "flip": true },
        |  "data": {
        |    "category": [ "A" ],
        |    "lower": [ 3.0 ],
        |    "middle": [ 5.0 ],
        |    "upper": [ 7.0 ],
        |    "id": [ "crossbar" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "lower" },
        |      { "type": "float", "column": "middle" },
        |      { "type": "float", "column": "upper" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "lower", "y": "middle", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "crossbar",
        |      "width": 0.45,
        |      "fill": "#A8DADC",
        |      "color": "#1D3557",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val POINTRANGE_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "category": [ "A" ],
        |    "lower": [ 3.0 ],
        |    "middle": [ 5.0 ],
        |    "upper": [ 7.0 ],
        |    "id": [ "pointrange" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "lower" },
        |      { "type": "float", "column": "middle" },
        |      { "type": "float", "column": "upper" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "lower", "y": "middle", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "pointrange",
        |      "size": 2.0,
        |      "shape": 21.0,
        |      "fill": "#F4A261",
        |      "color": "#264653",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val POINTRANGE_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": { "name": "flip", "flip": true },
        |  "data": {
        |    "category": [ "A" ],
        |    "lower": [ 3.0 ],
        |    "middle": [ 5.0 ],
        |    "upper": [ 7.0 ],
        |    "id": [ "pointrange" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "lower" },
        |      { "type": "float", "column": "middle" },
        |      { "type": "float", "column": "upper" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "lower", "y": "middle", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "pointrange",
        |      "size": 2.0,
        |      "shape": 21.0,
        |      "fill": "#F4A261",
        |      "color": "#264653",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val LINERANGE_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "category": [ "A" ],
        |    "lower": [ 3.0 ],
        |    "upper": [ 7.0 ],
        |    "id": [ "linerange" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "lower" },
        |      { "type": "float", "column": "upper" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "lower", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "linerange",
        |      "size": 4.0,
        |      "color": "#6C584C",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val LINERANGE_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": { "name": "flip", "flip": true },
        |  "data": {
        |    "category": [ "A" ],
        |    "lower": [ 3.0 ],
        |    "upper": [ 7.0 ],
        |    "id": [ "linerange" ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "category" },
        |      { "type": "float", "column": "lower" },
        |      { "type": "float", "column": "upper" },
        |      { "type": "str", "column": "id" }
        |    ]
        |  },
        |  "mapping": { "x": "category", "ymin": "lower", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "linerange",
        |      "size": 4.0,
        |      "color": "#6C584C",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val RIBBON_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0 ],
        |    "lower": [ 3.0, 2.5, 2.0, 2.5, 3.0 ],
        |    "upper": [ 7.0, 7.5, 8.0, 7.5, 7.0 ],
        |    "id": [ "ribbon", "ribbon", "ribbon", "ribbon", "ribbon" ]
        |  },
        |  "mapping": { "x": "x", "ymin": "lower", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "ribbon",
        |      "fill": "#84A59D",
        |      "color": "#52796F",
        |      "alpha": 0.65,
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 1.0, 5.0 ] },
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val RIBBON_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": { "name": "flip", "flip": true },
        |  "data": {
        |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0 ],
        |    "lower": [ 3.0, 2.5, 2.0, 2.5, 3.0 ],
        |    "upper": [ 7.0, 7.5, 8.0, 7.5, 7.0 ],
        |    "id": [ "ribbon", "ribbon", "ribbon", "ribbon", "ribbon" ]
        |  },
        |  "mapping": { "x": "x", "ymin": "lower", "ymax": "upper" },
        |  "layers": [
        |    {
        |      "geom": "ribbon",
        |      "fill": "#84A59D",
        |      "color": "#52796F",
        |      "alpha": 0.65,
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 1.0, 5.0 ] },
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val DENSITY_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "x": [ -1.7, -1.3, -1.1, -0.8, -0.4, -0.2, 0.0, 0.1, 0.4, 0.7, 1.0, 1.3, 1.5, 1.8 ]
        |  },
        |  "mapping": { "x": "x" },
        |  "layers": [
        |    {
        |      "geom": "density",
        |      "fill": "#B8DE6F",
        |      "color": "#2D6A4F",
        |      "alpha": 0.6,
        |      "size": 1.5,
        |      "tooltips": { "lines": [ "density" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val DENSITY_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": { "name": "flip", "flip": true },
        |  "data": {
        |    "x": [ -1.7, -1.3, -1.1, -0.8, -0.4, -0.2, 0.0, 0.1, 0.4, 0.7, 1.0, 1.3, 1.5, 1.8 ]
        |  },
        |  "mapping": { "x": "x" },
        |  "layers": [
        |    {
        |      "geom": "density",
        |      "fill": "#B8DE6F",
        |      "color": "#2D6A4F",
        |      "alpha": 0.6,
        |      "size": 1.5,
        |      "tooltips": { "lines": [ "density" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val LOLLIPOP_VERTICAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "x": [ 1.0, 2.0, 3.0 ],
        |    "y": [ 4.0, 7.0, 5.0 ],
        |    "id": [ "lollipop A", "lollipop B", "lollipop C" ]
        |  },
        |  "mapping": { "x": "x", "y": "y" },
        |  "layers": [
        |    {
        |      "geom": "lollipop",
        |      "size": 6.0,
        |      "linewidth": 2.0,
        |      "stroke": 1.5,
        |      "shape": 21.0,
        |      "fill": "#F6BD60",
        |      "color": "#355070",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val LOLLIPOP_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": { "name": "flip", "flip": true },
        |  "data": {
        |    "x": [ 1.0, 2.0, 3.0 ],
        |    "y": [ 4.0, 7.0, 5.0 ],
        |    "id": [ "lollipop A", "lollipop B", "lollipop C" ]
        |  },
        |  "mapping": { "x": "x", "y": "y" },
        |  "layers": [
        |    {
        |      "geom": "lollipop",
        |      "size": 6.0,
        |      "linewidth": 2.0,
        |      "stroke": 1.5,
        |      "shape": 21.0,
        |      "fill": "#F6BD60",
        |      "color": "#355070",
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val PATH_DISTANCE_PRIORITY_IMPLICIT_LINE_GROUP = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 600.0, "height": 300.0 },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0 ],
        |        "y": [ 2.0, 2.2, 2.1, 2.3, 2.2 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "color": "#4E79A7",
        |      "size": 2.5,
        |      "tooltips": { "lines": [ "near line" ] }
        |    },
        |    {
        |      "geom": "line",
        |      "data": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0 ],
        |        "y": [ 8.0, 8.2, 8.1, 8.3, 8.2 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "color": "#E15759",
        |      "size": 2.5,
        |      "tooltips": { "lines": [ "far line" ] }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 0.0, 4.0 ] },
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

    val PATH_DISTANCE_PRIORITY_SEPARATE_GROUPS = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 600.0, "height": 300.0 },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0 ],
        |        "y": [ 2.0, 2.2, 2.1, 2.3, 2.2 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "color": "#4E79A7",
        |      "size": 2.5,
        |      "tooltips": {
        |        "lines": [ "lower" ],
        |        "tooltip_group": "lower"
        |      }
        |    },
        |    {
        |      "geom": "line",
        |      "data": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0 ],
        |        "y": [ 8.0, 8.2, 8.1, 8.3, 8.2 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "color": "#E15759",
        |      "size": 2.5,
        |      "tooltips": {
        |        "lines": [ "upper" ],
        |        "tooltip_group": "upper"
        |      }
        |    }
        |  ],
        |  "scales": [
        |    { "aesthetic": "x", "limits": [ 0.0, 4.0 ] },
        |    { "aesthetic": "y", "limits": [ 0.0, 10.0 ] }
        |  ]
        |}
    """.trimMargin()

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

    val BAR_WITH_NEGATIVE_HEIGHT = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "x": [ "a", "b", "c", "d" ],
        |    "y": [ 3.0, -2.0, 4.0, -1.5 ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "x" },
        |      { "type": "float", "column": "y" }
        |    ]
        |  },
        |  "mapping": { "x": "x", "y": "y" },
        |  "layers": [
        |    {
        |      "geom": "bar",
        |      "stat": "identity",
        |      "tooltips": { "lines": [ "@x = @y" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "data": {
        |    "y": [ "a", "b", "c", "d" ],
        |    "x": [ 3.0, -2.0, 4.0, -1.5 ]
        |  },
        |  "data_meta": {
        |    "series_annotations": [
        |      { "type": "str", "column": "y" },
        |      { "type": "float", "column": "x" }
        |    ]
        |  },
        |  "mapping": { "x": "x", "y": "y" },
        |  "layers": [
        |    {
        |      "geom": "bar",
        |      "stat": "identity",
        |      "orientation": "y",
        |      "tooltips": { "lines": [ "@y = @x" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

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

    val POINT_AND_POLYGON = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "polygon",
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 1.0, 3.0, 3.0, 1.0, 1.0 ],
        |        "y": [ 1.0, 1.0, 3.0, 3.0, 1.0 ],
        |        "id": [ "polygon", "polygon", "polygon", "polygon", "polygon" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    },
        |    {
        |      "geom": "point",
        |      "size": 12.0,
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ],
        |        "id": [ "point" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "color": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val POINT_AND_BAR = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 5.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "bar",
        |      "stat": "identity",
        |      "width": 1.5,
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 4.0 ],
        |        "id": [ "bar" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    },
        |    {
        |      "geom": "point",
        |      "size": 12.0,
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ],
        |        "id": [ "point" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "color": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val POINT_AND_TEXT = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "point",
        |      "size": 12.0,
        |      "color": "#4E79A7",
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": { "lines": [ "point tooltip" ] }
        |    },
        |    {
        |      "geom": "text",
        |      "color": "#E15759",
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ],
        |        "label": [ "TXT" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "label": "label" }
        |    }
        |  ]
        |}
    """.trimMargin()

    val POINT_AND_LABEL = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "point",
        |      "size": 12.0,
        |      "color": "#59A14F",
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": { "lines": [ "point tooltip" ] }
        |    },
        |    {
        |      "geom": "label",
        |      "color": "#B07AA1",
        |      "fill": "#FCE5F6",
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 2.0 ],
        |        "label": [ "LBL" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "label": "label" }
        |    }
        |  ]
        |}
    """.trimMargin()

    val GROUPED_LINE_CLOSEST_BY_X = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 3.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": {
        |        "x": [ 0.0, 3.0, 1.0, 3.0, 0.0, 2.0 ],
        |        "y": [ 1.0, 1.0, 2.0, 2.0, 3.0, 3.0 ],
        |        "id": [ "line A", "line A", "line B", "line B", "line C", "line C" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "group": "id", "color": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val GROUPED_LINE_AND_POINT = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "line",
        |      "data": {
        |        "x": [ 0.0, 4.0, 0.0, 4.0 ],
        |        "y": [ 1.0, 1.0, 3.0, 3.0 ],
        |        "id": [ "A", "A", "B", "B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "group": "id", "color": "id" },
        |      "size": 2.0,
        |      "tooltips": { "lines": [ "line @id" ] }
        |    },
        |    {
        |      "geom": "point",
        |      "data": {
        |        "x": [ 2.0, 2.0 ],
        |        "y": [ 1.8, 2.2 ],
        |        "id": [ "A", "B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "color": "id" },
        |      "size": 16.0,
        |      "tooltips": { "lines": [ "point @id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val LOGICAL_GROUP_DIFFERENT_X_TOOLTIP = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.2 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "line", "color": "#4E79A7", "size": 2.0,
        |      "data": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0 ],
        |        "y": [ 1.0, 2.1, 1.6, 2.8, 2.0 ],
        |        "label": [ "line A", "line A", "line A", "line A", "line A" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": {
        |        "lines": [ "@label" ],
        |        "tooltip_group": "logical_group"
        |      }
        |    },
        |    {
        |      "geom": "line", "color": "#E15759", "size": 2.0,
        |      "data": {
        |        "x": [ 0.15, 1.15, 2.15, 3.15, 4.15 ],
        |        "y": [ 1.6, 2.7, 2.2, 3.4, 2.6 ],
        |        "label": [ "line B", "line B", "line B", "line B", "line B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": {
        |        "lines": [ "@label" ],
        |        "tooltip_group": "logical_group"
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val LOGICAL_GROUP_DIFFERENT_X_TOOLTIP_REVERSED_SIDES = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.7 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "line", "color": "#4E79A7", "size": 2.0,
        |      "data": {
        |        "x": [ 0.6, 1.6, 2.6, 3.6, 4.6 ],
        |        "y": [ 1.0, 2.1, 1.6, 2.8, 2.0 ],
        |        "label": [ "line A", "line A", "line A", "line A", "line A" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": {
        |        "lines": [ "@label" ],
        |        "tooltip_group": "logical_group"
        |      }
        |    },
        |    {
        |      "geom": "line", "color": "#E15759", "size": 2.0,
        |      "data": {
        |        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0 ],
        |        "y": [ 1.6, 2.7, 2.2, 3.4, 2.6 ],
        |        "label": [ "line B", "line B", "line B", "line B", "line B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y" },
        |      "tooltips": {
        |        "lines": [ "@label" ],
        |        "tooltip_group": "logical_group"
        |      }
        |    }
        |  ]
        |}
    """.trimMargin()

    val BAR_OVERLAPPED_MANY = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 7.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "bar",
        |      "stat": "identity",
        |      "width": 1.5,
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 2.0, 2.0, 2.0, 2.0, 2.0, 2.0 ],
        |        "y": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 ],
        |        "id": [ "bar 1", "bar 2", "bar 3", "bar 4", "bar 5", "bar 6" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val POLYGON_OVERLAPPED = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "polygon",
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 0.5, 2.5, 2.5, 0.5, 0.5 ],
        |        "y": [ 0.5, 0.5, 2.5, 2.5, 0.5 ],
        |        "id": [ "polygon A", "polygon A", "polygon A", "polygon A", "polygon A" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    },
        |    {
        |      "geom": "polygon",
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 1.5, 3.5, 3.5, 1.5, 1.5 ],
        |        "y": [ 1.5, 1.5, 3.5, 3.5, 1.5 ],
        |        "id": [ "polygon B", "polygon B", "polygon B", "polygon B", "polygon B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val BAR_OVERLAPPED = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 5.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "bar",
        |      "stat": "identity",
        |      "width": 1.5,
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 1.5 ],
        |        "y": [ 3.0 ],
        |        "id": [ "bar A" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    },
        |    {
        |      "geom": "bar",
        |      "stat": "identity",
        |      "width": 1.5,
        |      "alpha": 0.5,
        |      "data": {
        |        "x": [ 2.0 ],
        |        "y": [ 4.0 ],
        |        "id": [ "bar B" ]
        |      },
        |      "mapping": { "x": "x", "y": "y", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()

    val RECT_OVERLAPPED = """
        |{
        |  "kind": "plot",
        |  "ggsize": { "width": 400.0, "height": 300.0 },
        |  "coord": {
        |    "name": "cartesian",
        |    "xlim": [ 0.0, 4.0 ],
        |    "ylim": [ 0.0, 4.0 ]
        |  },
        |  "layers": [
        |    {
        |      "geom": "rect",
        |      "alpha": 0.5,
        |      "data": {
        |        "xmin": [ 0.5 ],
        |        "xmax": [ 2.5 ],
        |        "ymin": [ 0.5 ],
        |        "ymax": [ 2.5 ],
        |        "id": [ "rect A" ]
        |      },
        |      "mapping": { "xmin": "xmin", "xmax": "xmax", "ymin": "ymin", "ymax": "ymax", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    },
        |    {
        |      "geom": "rect",
        |      "alpha": 0.5,
        |      "data": {
        |        "xmin": [ 1.5 ],
        |        "xmax": [ 3.5 ],
        |        "ymin": [ 1.5 ],
        |        "ymax": [ 3.5 ],
        |        "id": [ "rect B" ]
        |      },
        |      "mapping": { "xmin": "xmin", "xmax": "xmax", "ymin": "ymin", "ymax": "ymax", "fill": "id" },
        |      "tooltips": { "lines": [ "@id" ] }
        |    }
        |  ]
        |}
    """.trimMargin()
}
