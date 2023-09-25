/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class CoordPolarConfigs {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            textSpiral(),
            textSpiral(zeroExtend = true),
            geomRect(),
            geomBar(),
            path(),
        )
    }

    private fun path(): MutableMap<String, Any> {
        val spec = """
{
  "data": {
    "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 ],
    "y": [ 0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0, 360.0 ],
    "c": [ 42.0, 8.0, 21.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 ],
    "g": [ "0", "45", "90", "135", "180", "225", "270", "315", "360" ]
  },
  "mapping": { "x": "x", "y": "y" },
  "coord": { "name": "polar", "theta": "y" },
  "kind": "plot",
  "scales": [
    {
      "aesthetic": "x",
      "expand": [ 0.0, 0.0 ]
    },
    {
      "aesthetic": "y",
      "expand": [ 0.0, 0.0 ]
    }
  ],
  "layers": [
    {
      "geom": "path",
      "mapping": { "x": "x", "y": "y" }
    }
  ]
}
""".trimMargin()
        return parsePlotSpec(spec)
    }

    private fun geomRect(): MutableMap<String, Any> {
        val spec = """
{
  "coord": { "name": "polar", "theta": "y" },
  "kind": "plot",
  "layers": [
    {
      "geom": "rect",
      "xmin": 0.0,
      "xmax": 5.0,
      "ymin": 0.0,
      "ymax": 5.0,
      "fill": "red"
    },
    {
      "geom": "rect",
      "xmin": 0.0,
      "xmax": 5.0,
      "ymin": 5.0,
      "ymax": 7.5,
      "fill": "green"
    }
  ]
}
""".trimMargin()
        return parsePlotSpec(spec)
    }

    private fun geomBar(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "subplots",
  "layout": { "ncol": 3.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "kind": "plot",
      "layers": [
        {
          "geom": "bar",
          "mapping": { "fill": "foo" },
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": {
                  "label": "foo",
                  "order": -1.0
                }
              }
            ]
          },
          "size": 0.0
        }
      ]
    },
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "coord": { "name": "polar", "theta": "y" },
      "ggtitle": { "text": "coord_polar(theta=\'y\')" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "mapping": { "fill": "foo" },
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": {
                  "label": "foo",
                  "order": -1.0
                }
              }
            ]
          },
          "size": 0.0
        }
      ]
    },
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "coord": { "name": "polar", "theta": "x" },
      "ggtitle": { "text": "coord_polar(theta=\'x\')" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "mapping": { "fill": "foo" },
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": {
                  "label": "foo",
                  "order_by": null,
                  "order": -1.0
                }
              }
            ]
          },
          "size": 0.0
        }
      ]
    }
  ]
}
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun textSpiral(zeroExtend: Boolean = false): MutableMap<String, Any> {
        val spec = """
{
  "kind": "subplots",
  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "data": {
        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 ],
        "y": [ 0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0, 360.0 ],
        "l": [ "0", "45", "90", "135", "180", "225", "270", "315", "360" ]
      },
      "mapping": { "x": "x", "y": "y", "label": "l" },
      "kind": "plot",
      "scales": [
        { "name": "", "aesthetic": "x" },
        { "name": "", "aesthetic": "y" }
      ],
      "layers": [ { "geom": "label" } ]
    },
    {
      "data": {
        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 ],
        "y": [ 0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0, 360.0 ],
        "l": [ "0", "45", "90", "135", "180", "225", "270", "315", "360" ]
      },
      "mapping": { "x": "x", "y": "y", "label": "l" },
      "coord": { "name": "polar", "theta": "y" },
      "kind": "plot",
      "scales": [
        { "name": "", "aesthetic": "x" },
        { "name": "", "aesthetic": "y" },
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [ { "geom": "label" } ]
    }
  ]
}
""".trimMargin()

        val plotSpec = parsePlotSpec(spec)
        if (!zeroExtend) {
            plotSpec.remove("scales")
        }

        return plotSpec
    }
}
