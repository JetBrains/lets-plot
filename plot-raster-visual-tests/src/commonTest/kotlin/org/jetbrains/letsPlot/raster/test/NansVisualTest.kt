package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class NansVisualTest : VisualTestBase() {
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
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "c"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "path",
            |      "mapping": {},
            |      "data_meta": {},
            |      "size": 3.0
            |    },
            |    {
            |      "geom": "point",
            |      "mapping": {},
            |      "data_meta": {},
            |      "color": "red"
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("variadic_path_with_none.png", plotSpec)
    }

    @Test
    fun `area with none`() {
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
            |    "y": "c"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "c"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "area",
            |      "stat": "identity",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("area_with_none.png", plotSpec)
    }

    @Test
    fun `line with none`() {
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
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "c"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "line",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("line_with_none.png", plotSpec)
    }

    @Test
    fun `ribbon with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 ],
            |    "ymin": [ null, 0.0, 0.3, null, 0.2, null, 0.4, 0.6 ],
            |    "ymax": [ null, 0.5, 0.8, null, 0.7, 0.9, null, 1.0 ]
            |  },
            |  "mapping": {},
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "ymin"
            |      },
            |      {
            |        "type": "float",
            |        "column": "ymax"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "ribbon",
            |      "mapping": {
            |        "x": "x",
            |        "ymin": "ymin",
            |        "ymax": "ymax"
            |      },
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("ribbon_with_none.png", plotSpec)
    }

    @Test
    fun `area ridges with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, -1.0, -0.5, 0.0, 0.5, 1.0, null, -1.0, -0.5, 0.0, null, 0.5, 1.0, 2.0 ],
            |    "g": [ "A", "A", "A", "A", "A", "A", "A", "B", "B", "B", "B", "B", "B", "B" ],
            |    "h": [ null, 0.0, 0.6, 1.0, 0.6, 0.0, null, 0.0, 0.5, 0.8, null, 0.4, 0.0, 0.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "g",
            |    "height": "h",
            |    "group": "g"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "str",
            |        "column": "g"
            |      },
            |      {
            |        "type": "float",
            |        "column": "h"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "area_ridges",
            |      "stat": "identity",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("area_ridges_with_none.png", plotSpec)
    }

    @Test
    fun `smooth with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, -2.0, -1.0, 0.0, 1.0, 2.0, null, 3.0, 4.0, 5.0, null, 6.0 ],
            |    "y": [ null, 1.5, 0.5, 0.0, 0.6, 1.0, null, 1.2, 1.0, 0.8, null, 0.7 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "smooth",
            |      "mapping": {},
            |      "data_meta": {},
            |      "method": "loess"
            |    },
            |    {
            |      "geom": "point",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("smooth_with_none.png", plotSpec)
    }

    @Test
    fun `violin with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "grp": [ null, "A", "A", "A", "A", "B", "B", "B", "C", "C", "C", "D" ],
            |    "y": [ 0.3, null, 0.2, 0.5, 0.8, null, 0.4, 0.6, 0.1, null, 0.9, 0.7 ]
            |  },
            |  "mapping": {
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "str",
            |        "column": "grp"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "violin",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("violin_with_none.png", plotSpec)
    }

    @Test
    fun `variadic step with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ null, 0.0, 1.0, null, 2.0, 3.0, 4.0, null, 5.0, 6.0 ],
            |    "y": [ null, 0.0, 1.0, null, null, 0.6, null, 0.9, 0.9, 1.0 ],
            |    "c": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "c"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "c"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "step",
            |      "mapping": {},
            |      "data_meta": {},
            |      "size": 3.0
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("variadic_step_with_none.png", plotSpec)
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
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "str",
            |        "column": "id"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "scale_mapper_kind": "color_hue"
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "polygon",
            |      "mapping": {},
            |      "data_meta": {},
            |      "size": 5.0
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("polygon_with_none.png", plotSpec)
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
            |  "mapping": {
            |    "x": "x"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "int",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "density",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("density_with_none.png", plotSpec)
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
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "int",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "density",
            |      "stat": "identity",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("density_identity_with_none.png", plotSpec)
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
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "str",
            |        "column": "id"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "scale_mapper_kind": "color_hue"
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "map",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("map_with_none.png", plotSpec)
    }

    @Test
    fun `contour with none`() {
        val spec = parsePlotSpec(
            """
            |{
            |    "data": {
            |    "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0 ],
            |    "y": [ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 3.0, 4.0, 4.0, 4.0, 4.0, 4.0 ],
            |    "z": [ 0.0, 1.0, 4.0, 9.0, 16.0, 1.0, 2.0, 5.0, 10.0, 17.0, 4.0, 5.0, 8.0, 13.0, 20.0, 9.0, 10.0, 13.0, 18.0, 25.0, 16.0, 17.0, 20.0, 25.0, 32.0 ]
            |    },
            |    "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "z": "z"
            |    },
            |    "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "int",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "z"
            |      }
            |    ]
            |    },
            |    "kind": "plot",
            |    "scales": [],
            |    "layers": [
            |    {
            |      "geom": "contour",
            |      "mapping": {},
            |      "data_meta": {},
            |      "bins": 6.0
            |    }
            |    ],
            |    "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("contour_with_none.png", plotSpec)
    }
}