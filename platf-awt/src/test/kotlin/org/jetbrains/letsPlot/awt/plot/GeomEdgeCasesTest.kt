/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class GeomEdgeCasesTest : VisualPlotTestBase(expectedImagesSubdir = "geoms") {
    @Test
    fun `path with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "subplots",
            |  "layout": {
            |    "ncol": 3.0,
            |    "nrow": 1.0,
            |    "name": "grid"
            |  },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_path" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "path", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "x_na": [ null, 4.0, 1.0, 5.0, 2.0, null, 3.0, 7.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x_na",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_path NA in x" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "path", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y_na": [ null, 4.0, 3.0, 3.0, 2.0, null, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y_na",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_path NA in y" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "path", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    }
            |  ]
            |}
        """.trimMargin()
        )

        assertPlot("path_with_none.png", spec)
    }

    @Test
    fun `coord_polar and clip path`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "plot",
            |  "data": {
            |    "x": [ 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 ],
            |    "y": [ 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6 ],
            |    "g": [ 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 0, 0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "int", "column": "y" },
            |      { "type": "int", "column": "g" }
            |    ]
            |  },
            |  "theme": { "name": "grey" },
            |  "coord": { "name": "polar", "ylim": [ 0.0, 1.5 ] },
            |  "layers": [
            |    {
            |      "geom": "path",
            |      "mapping": { "x": "x", "y": "y", "group": "g", "size": "g" }
            |    }
            |  ]
            |}
        """.trimMargin()
        )

        assertPlot("plot_polar_clip_path.png", spec)
    }

    @Test
    fun `path with none coord polar`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, null, 0.0, null, 1.0, 2.0, null, 4.0, 5.0, 6.0 ],
            |    "y": [ null, 0.0, 0.5, 0.0, 0.0, 1.0, null, null, 0.5, 1.0 ],
            |    "c": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "float", "column": "x" },
            |      { "type": "float", "column": "y" },
            |      { "type": "int", "column": "c" }
            |    ]
            |  },
            |  "coord": { "name": "polar" },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [ { "geom": "path", "mapping": {}, "data_meta": {} } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("path_with_none_coord_polar.png", spec)
    }

    @Test
    fun `variadic path with none`() {
        val spec = parsePlotSpec(
            """
            |{
            | "data": {
            |    "x": [ null, null, 0.0, null, 1.0, 2.0, null, 4.0, 5.0, 6.0 ],
            |    "y": [ null, 0.0, 0.5, 0.0, 0.0, 1.0, null, null, 0.5, 1.0 ],
            |    "c": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "c"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "float", "column": "x" },
            |      { "type": "float", "column": "y" },
            |      { "type": "int", "column": "c" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    { "geom": "path", "mapping": {}, "data_meta": {}, "size": 3.0 },
            |    { "geom": "point", "mapping": {}, "data_meta": {}, "color": "red" }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("variadic_path_with_none.png", spec)
    }

    @Test
    fun `line with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "subplots",
            |  "layout": { "ncol": 3.0, "nrow": 1.0, "name": "grid" },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": { "x": "x", "y": "y", "color": "c" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_line" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "line", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "x_na": [ null, 4.0, 1.0, 5.0, 2.0, null, 3.0, 7.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": { "x": "x_na", "y": "y", "color": "c" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_line NA in x" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "line", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y_na": [ null, 4.0, 3.0, 3.0, 2.0, null, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": { "x": "x", "y": "y_na", "color": "c" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_line NA in y" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "line", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    }
            |  ]
            |}
        """.trimMargin()
        )

        assertPlot("line_with_none.png", spec)
    }

    @Test
    fun `step with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "subplots",
            |  "layout": { "ncol": 3.0, "nrow": 1.0, "name": "grid" },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": { "x": "x", "y": "y", "color": "c" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_step" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "step", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "x_na": [ null, 4.0, 1.0, 5.0, 2.0, null, 3.0, 7.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": { "x": "x_na", "y": "y", "color": "c" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_step NA in x" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "step", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y_na": [ null, 4.0, 3.0, 3.0, 2.0, null, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": { "x": "x", "y": "y_na", "color": "c" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "x_na" },
            |          { "type": "float", "column": "y_na" },
            |          { "type": "str", "column": "c" }
            |        ]
            |      },
            |      "ggtitle": { "text": "geom_step NA in y" },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [ { "geom": "step", "mapping": {}, "data_meta": {}, "linewidth": 1.0 } ],
            |      "metainfo_list": []
            |    }
            |  ]
            |}
        """.trimMargin()
        )

        assertPlot("step_with_none.png", spec)
    }

    @Test
    fun `polygon with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, 0.0, 1.0, 1.0, 0.0, 2.0, 3.0, null, null, 4.0, 5.0, null, 5.0, 4.0 ],
            |    "y": [ null, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.5, null, 1.0, 1.0 ],
            |    "id": [ "A", "A", "A", "A", "A", "B", "B", "B", "B", "C", "C", "C", "C", "C" ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "group": "id",
            |    "color": "id",
            |    "fill": "id"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "float", "column": "y" },
            |      { "type": "str", "column": "id" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [ { "aesthetic": "fill", "scale_mapper_kind": "color_hue" } ],
            |  "layers": [ { "geom": "polygon", "mapping": {}, "data_meta": {}, "size": 5.0 } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("polygon_with_none.png", spec)
    }

    @Test
    fun `density with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, -3.0, -2.9, -2.8, null, -1.0, -1.0, -0.8, null, 0.5, 0.6, 0.7, null, 3.0, 10.0, null ],
            |    "y": [ null, 2.0, 3.0, 2.0, null, 3.0, null, 4.0, 5.0, 0.0, 1.0, 2.0, 3.0, 4.0, 1.0, 5.0 ]
            |  },
            |  "mapping": { "x": "x" },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "float", "column": "x" },
            |      { "type": "int", "column": "y" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [ { "geom": "density", "mapping": {}, "data_meta": {} } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("density_with_none.png", spec)
    }

    @Test
    fun `density identity with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, -3.0, -2.9, -2.8, null, -1.0, -1.0, -0.8, null, 0.5, 0.6, 0.7, null, 3.0, 10.0, null ],
            |    "y": [ null, 2.0, 3.0, 2.0, null, 3.0, null, 4.0, 5.0, 0.0, 1.0, 2.0, 3.0, 4.0, 1.0, 5.0 ]
            |  },
            |  "mapping": { "x": "x", "y": "y" },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "float", "column": "x" },
            |      { "type": "int", "column": "y" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [ { "geom": "density", "stat": "identity", "mapping": {}, "data_meta": {} } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("density_identity_with_none.png", spec)
    }

    @Test
    fun `map with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, 0.0, 1.0, 1.0, 0.0, 2.0, 3.0, null, null, 4.0, 5.0, null, 5.0, 4.0 ],
            |    "y": [ null, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.5, null, 1.0, 1.0 ],
            |    "id": [ "A", "A", "A", "A", "A", "B", "B", "B", "B", "C", "C", "C", "C", "C" ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "group": "id",
            |    "color": "id",
            |    "fill": "id"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "float", "column": "y" },
            |      { "type": "str", "column": "id" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [ { "aesthetic": "fill", "scale_mapper_kind": "color_hue" } ],
            |  "layers": [ { "geom": "map", "mapping": {}, "data_meta": {} } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("map_with_none.png", spec)
    }

    @Test
    fun `contour with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0 ],
            |    "y": [ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 3.0, 4.0, 4.0, 4.0, 4.0, 4.0 ],
            |    "z": [ 0.0, 1.0, 4.0, 9.0, 16.0, 1.0, 2.0, 5.0, 10.0, 17.0, 4.0, 5.0, 8.0, 13.0, 20.0, 9.0, 10.0, 13.0, 18.0, 25.0, 16.0, 17.0, 20.0, 25.0, 32.0 ]
            |  },
            |  "mapping": { "x": "x", "y": "y", "z": "z" },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "int", "column": "y" },
            |      { "type": "int", "column": "z" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [ { "geom": "contour", "mapping": {}, "data_meta": {}, "bins": 6.0 } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        assertPlot("contour_with_none.png", spec)
    }
}
