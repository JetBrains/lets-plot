/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class GeoProjectionsPlotSpecs {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            mercator(),
            azimuthal(),
            conic(),
        )
    }

    private fun conic(): MutableMap<String, Any> {
        val spec = """
            {
  "kind": "subplots",
  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "coord": { "name": "map", "projection": "conic" },
      "ggtitle": { "text": "Empty" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "limits": [ -180.0, 180.0 ] },
        { "aesthetic": "y", "limits": [ -90.0, 90.0 ] }
      ],
      "layers": [
        { "geom": "path" }
      ]
    },
    {
      "coord": { "name": "map", "projection": "conic" },
      "ggtitle": { "text": "Graticule" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "limits": [ -180.0, 180.0 ] },
        { "aesthetic": "y", "limits": [ -90.0, 90.0 ] }
      ],
      "layers": [
        {
          "geom": "path",
          "map": {
            "coord": [
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 0.0], [0.0, 0.0], [0.0, 0.0], [180.0, 0.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 30.0], [0.0, 30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 30.0], [180.0, 30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -30.0], [0.0, -30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -30.0], [180.0, -30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 60.0], [0.0, 60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 60.0], [180.0, 60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -60.0], [0.0, -60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -60.0], [180.0, -60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 90.0], [0.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 90.0], [180.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -90.0], [0.0, -90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -90.0], [180.0, -90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -90.0], [0.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[30.0, -90.0], [30.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[60.0, -90.0], [60.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[90.0, -90.0], [90.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[120.0, -90.0], [120.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[150.0, -90.0], [150.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[180.0, -90.0], [180.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-30.0, -90.0], [-30.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-60.0, -90.0], [-60.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-90.0, -90.0], [-90.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-120.0, -90.0], [-120.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-150.0, -90.0], [-150.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -90.0], [-180.0, 90.0]]}"
            ]
          },
          "map_data_meta": { "geodataframe": { "geometry": "coord" } }
        }
      ]
    }
  ]
}"""
        return parsePlotSpec(spec)
    }

    private fun azimuthal(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "subplots",
  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "coord": { "name": "map", "projection": "azimuthal" },
      "ggtitle": { "text": "Empty" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "limits": [ -180.0, 180.0 ] },
        { "aesthetic": "y", "limits": [ -90.0, 90.0 ] }
      ],
      "layers": [ { "geom": "path" } ]
    },
    {
      "coord": { "name": "map", "projection": "azimuthal" },
      "ggtitle": { "text": "Graticule" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "limits": [ -180.0, 180.0 ] }, 
        { "aesthetic": "y", "limits": [ -90.0, 90.0 ] }
      ],
      "layers": [
        {
          "geom": "path",
          "map": {
            "coord": [
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 0.0], [0.0, 0.0], [0.0, 0.0], [180.0, 0.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 30.0], [0.0, 30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 30.0], [180.0, 30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -30.0], [0.0, -30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -30.0], [180.0, -30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 60.0], [0.0, 60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 60.0], [180.0, 60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -60.0], [0.0, -60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -60.0], [180.0, -60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 90.0], [0.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 90.0], [180.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -90.0], [0.0, -90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -90.0], [180.0, -90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -90.0], [0.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[30.0, -90.0], [30.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[60.0, -90.0], [60.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[90.0, -90.0], [90.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[120.0, -90.0], [120.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[150.0, -90.0], [150.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[180.0, -90.0], [180.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-30.0, -90.0], [-30.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-60.0, -90.0], [-60.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-90.0, -90.0], [-90.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-120.0, -90.0], [-120.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-150.0, -90.0], [-150.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -90.0], [-180.0, 90.0]]}"
            ]
          },
          "map_data_meta": { "geodataframe": { "geometry": "coord" } }
        }
      ]
    }
  ]
}
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun mercator(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "subplots",
  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
  "figures": [
    {
      "coord": { "name": "map", "projection": "mercator" },
      "ggtitle": { "text": "Empty" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "limits": [ -180.0, 180.0 ] },
        { "aesthetic": "y", "limits": [ -90.0, 90.0 ] }
      ],
      "layers": [ { "geom": "path" } ]
    },
    {
      "coord": { "name": "map", "projection": "mercator" },
      "ggtitle": { "text": "Graticule" },
      "kind": "plot",
      "scales": [
        { "aesthetic": "x", "limits": [ -180.0, 180.0 ] },
        { "aesthetic": "y", "limits": [ -90.0, 90.0 ] }
      ],
      "layers": [
        {
          "geom": "path",
          "map": {
            "coord": [
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 0.0], [0.0, 0.0], [0.0, 0.0], [180.0, 0.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 30.0], [0.0, 30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 30.0], [180.0, 30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -30.0], [0.0, -30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -30.0], [180.0, -30.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 60.0], [0.0, 60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 60.0], [180.0, 60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -60.0], [0.0, -60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -60.0], [180.0, -60.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, 90.0], [0.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, 90.0], [180.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -90.0], [0.0, -90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -90.0], [180.0, -90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[0.0, -90.0], [0.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[30.0, -90.0], [30.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[60.0, -90.0], [60.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[90.0, -90.0], [90.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[120.0, -90.0], [120.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[150.0, -90.0], [150.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[180.0, -90.0], [180.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-30.0, -90.0], [-30.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-60.0, -90.0], [-60.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-90.0, -90.0], [-90.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-120.0, -90.0], [-120.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-150.0, -90.0], [-150.0, 90.0]]}",
              "{\"type\": \"LineString\", \"coordinates\": [[-180.0, -90.0], [-180.0, 90.0]]}"
            ]
          },
          "map_data_meta": { "geodataframe": { "geometry": "coord" } }
        }
      ]
    }
  ]
}""".trimIndent()
        return parsePlotSpec(spec)
    }
}
