/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class CoordPolarPlotSpecs {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            geomBarStack(),
            geomBarDodge(),
            path(),
            textSpiral(zeroExtend = true),
        )
    }

    private fun geomBarDodge(): MutableMap<String, Any> {
        val spec = """
            {
  "kind": "subplots",
  "layout": { "ncol": 3.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "kind": "plot",
      "layers": [
        { "geom": "bar", "mapping": { "fill": "foo" },
          "size": 0.0,
          "position": "dodge",
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": { "label": "foo","order": 1.0 }
              }
            ]
          }
        }
      ]
    },
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "coord": { "name": "polar", "theta": "y" },
      "ggtitle": { "text": "position=dodge, coord_polar(theta=y)" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "size": 0.0,
          "mapping": { "fill": "foo" },
          "position": "dodge",
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": { "label": "foo", "order": 1.0 }
              }
            ]
          }
        }
      ]
    },
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "coord": { "name": "polar", "theta": "x" },
      "ggtitle": { "text": "position=dodge, coord_polar(theta=x)" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "size": 0.0,
          "mapping": { "fill": "foo" },
          "position": "dodge",
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": { "label": "foo", "order": 1.0 }
              }
            ]
          }
        }
      ]
    }
  ]
}
"""
        return parsePlotSpec(spec)
    }


    private fun path(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "subplots",
  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "data": {
        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 ],
        "y": [ 0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0, 360.0 ],
        "l": [ "0", "45", "90", "135", "180", "225", "270", "315", "360" ],
        "g": [ "1", "1", "1", "2", "2", "2", "3", "3", "3" ]
      },
      "mapping": { "x": "x", "y": "y", "color": "y" },
      "coord": { "name": "polar" },
      "ggtitle": { "text": "coord_polar()" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "color", "palette": "GnBu", "scale_mapper_kind": "color_brewer" },
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        { "geom": "path", "size": 3.0 }
      ]
    },
    {
      "data": {
        "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 ],
        "y": [ 0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0, 360.0 ],
        "l": [ "0", "45", "90", "135", "180", "225", "270", "315", "360" ],
        "g": [ "1", "1", "1", "2", "2", "2", "3", "3", "3" ]
      },
      "mapping": { "x": "x", "y": "y", "color": "y" },
      "coord": { "name": "polar", "theta": "x" },
      "ggtitle": { "text": "coord_polar(), flat=True" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "color", "palette": "GnBu", "scale_mapper_kind": "color_brewer" },
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        { "geom": "path", "flat": true, "size": 3.0 }
      ]
    }
  ]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun geomBarStack(): MutableMap<String, Any> {
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
                "parameters": { "label": "foo", "order": 1.0 }
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
      "ggtitle": { "text": "coord_polar(theta=\"y\")" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "mapping": { "fill": "foo" },
          "size": 0.0,
          "data_meta": {
            "mapping_annotations": [
              { "aes": "fill", "annotation": "as_discrete", "parameters": { "label": "foo", "order": 1.0 } }
            ]
          }
        }
      ]
    },
    {
      "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
      "coord": { "name": "polar", "theta": "x" },
      "ggtitle": { "text": "coord_polar(theta=\"x\")" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
        { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
      ],
      "layers": [
        {
          "geom": "bar",
          "mapping": { "fill": "foo" },
          "size": 0.0,
          "data_meta": {
            "mapping_annotations": [
              {
                "aes": "fill",
                "annotation": "as_discrete",
                "parameters": { "label": "foo", "order_by": null, "order": 1.0 }
              }
            ]
          }
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
        "l": [ "(0, 0)", "(1, 45)", "(2, 90)", "(3, 135)", "(4, 180)", "(5, 225)", "(6, 270)", "(7, 315)", "(8, 360)" ]
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
