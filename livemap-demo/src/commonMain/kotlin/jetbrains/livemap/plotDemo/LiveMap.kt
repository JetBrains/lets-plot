/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.plotDemo

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import kotlin.random.Random

class LiveMap : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            multiLayerTooltips(),
            mapJoinBar()
//            antiMeridian()
//            tooltips()
//            symbol_point(),
//            geom_point()
//            fourPointsTwoLayers(),
//            basic(),
//            bunch(),
//            facet()
        )
    }

    private fun multiLayerTooltips(): Map<String, Any> {
        val n = 10
        val rnd = Random(0)
        val data = """
            {
                "x": [${ (0..n).map { rnd.nextDouble(-2.0, 2.0) }.joinToString() }],
                "y": [${ (0..n).map { rnd.nextDouble(-2.0, 2.0) }.joinToString() }],
                "v": [${ (0..n).map { rnd.nextDouble(0.0, 200_000.0) }.joinToString() }],
                "age": [${ (0..n).map { rnd.nextInt(0, 70) }.joinToString() }]
            }
        """.trimIndent()

        val poly = """
            {
                "x": [-5.0, 5.0, 5.0, -5.0, -5.0],
                "y": [5.0, 5.0, -5.0, -5.0, 5.0]
            }
        """.trimIndent()

        val spec = """{
            "data": $data,
            "kind": "plot",
            "layers": [
                {
                    "geom": "livemap",
                    "tiles": {
                        "kind": "vector_lets_plot",
                        "url": "ws://10.0.0.127:3933",
                        "theme": "dark",
                        "attribution": "Map data <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap</a> contributors"
                    }
                },
                {
                    "geom": "polygon",
                    "data": $poly,
                    "mapping": { "x": "x", "y": "y" },
                    "fill": "#F8F4F0", 
                    "color": "#B71234",
                    "alpha": 0.5
                },
                {
                    "geom": "point",
                    "data": $data,
                    "mapping": { "x": "x", "y": "y", "size": "v", "color": "age" }
                }
            ]
        },
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun mapJoinBar(): MutableMap<String, Any> {
        val spec = """{
  "data": {
    "State": ["Alabama", "Alabama", "Alabama", "Alaska", "Alaska", "Alaska", "Arizona", "Arizona", "Arizona"],
    "Item": [ "State Debt", "Local Debt", "Gross State Product", "State Debt", "Local Debt", "Gross State Product", "State Debt", "Local Debt", "Gross State Product"],
    "$ B": [ 10.7, 26.1, 228.0, 5.9, 3.5, 55.7, 13.3, 30.5, 361.1]
  },
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "mapping": {
        "sym_x": "Item",
        "sym_y": "$ B",
        "fill": "Item"
      },
      "map_data_meta": {
        "geodataframe": {
          "geometry": "geometry"
        }
      },
      "display_mode": "pie",
      "tiles": {
        "kind": "raster_zxy",
        "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}@2x.png",
        "attribution": "© wikimedia.org"
      },
      "geocoding": {
        "url": "http://10.0.0.127:3020"
      },
      "map": {
        "State": [ "Alabama", "Alaska", "Arizona"],
        "Latitude": [ 32.806671, 61.370716, 33.729759],
        "Longitude": [ -86.79113000000001, -152.404419, -111.431221],
        "geometry": [
          "{\"type\": \"Point\", \"coordinates\": [-86.79113000000001, 32.806671]}",
          "{\"type\": \"Point\", \"coordinates\": [-152.404419, 61.370716]}",
          "{\"type\": \"Point\", \"coordinates\": [-111.431221, 33.729759]}"
        ]
      },
      "map_join": ["State", "State"]
    }
  ]
}""".trimIndent()

        return parsePlotSpec(spec)
    }


    private fun antiMeridian(): MutableMap<String, Any> {
        val spec = """{
  "data": null,
  "mapping": {
    "x": null,
    "y": null
  },
  "data_meta": {},
  "theme": {
    "axis_title": null,
    "axis_title_x": null,
    "axis_title_y": null,
    "axis_text": null,
    "axis_text_x": null,
    "axis_text_y": null,
    "axis_ticks": null,
    "axis_ticks_x": null,
    "axis_ticks_y": null,
    "axis_line": null,
    "axis_line_x": null,
    "axis_line_y": null,
    "legend_position": "none",
    "legend_justification": null,
    "legend_direction": null,
    "axis_tooltip": null,
    "axis_tooltip_x": null,
    "axis_tooltip_y": null
  },
  "kind": "plot",
  "scales": [],
  "layers": [
    {
      "geom": "livemap",
      "stat": null,
      "data": null,
      "mapping": {
        "x": null,
        "y": null
      },
      "position": null,
      "show_legend": null,
      "tooltips": null,
      "data_meta": {},
      "sampling": null,
      "display_mode": null,
      "location": null,
      "zoom": null,
      "projection": null,
      "geodesic": null,
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "ws://10.0.0.127:3933",
        "theme": null
      },
      "geocoding": {}
    },
    {
      "geom": "rect",
      "stat": null,
      "data": {
        "request": [
          "Russia",
          "Russia",
          "USA",
          "USA"
        ],
        "lonmin": [
          19.6389412879944,
          -180.0,
          144.618412256241,
          -180.0
        ],
        "latmin": [
          41.1850968003273,
          41.1850968003273,
          -14.3735490739346,
          -14.3735490739346
        ],
        "lonmax": [
          180.0,
          -168.997978270054,
          180.0,
          -64.564847946167
        ],
        "latmax": [
          81.8587204813957,
          81.8587204813957,
          71.3878083229065,
          71.3878083229065
        ],
        "found name": [
          "\u0420\u043e\u0441\u0441\u0438\u044f",
          "\u0420\u043e\u0441\u0441\u0438\u044f",
          "United States of America",
          "United States of America"
        ]
      },
      "mapping": {
        "x": null,
        "y": null,
        "xmin": "lonmin",
        "xmax": "lonmax",
        "ymin": "latmin",
        "ymax": "latmax",
        "fill": "found name"
      },
      "position": null,
      "show_legend": null,
      "tooltips": null,
      "data_meta": {},
      "sampling": null,
      "map": null,
      "map_join": null,
      "alpha": 0.3
    }
  ]
}""".trimIndent()
        return parsePlotSpec(spec)
    }
    private fun tooltips(): MutableMap<String, Any> {
        val spec = """
            {
              "data": {
                "request": ["Texas", "Nevada", "Iowa"],
                "lon": [-99.6829525269137, -116.666956541192, -93.1514127397129],
                "lat": [31.1685702949762, 38.5030842572451, 41.9395130127668],
                "found name": ["Texas", "Nevada", "Iowa"]
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "livemap",
                  "tiles": {
                    "kind": "vector_lets_plot",
                    "url": "ws://10.0.0.127:3933",
                    "theme": null
                  },
                  "geocoding": {}
                },
                {
                  "geom": "point",
                  "mapping": {
                    "x": "lon",
                    "y": "lat"
                  },
                  "tooltips": {
                    "lines": [
                      {
                        "value": "aes@x",
                        "label": null,
                        "format": "mean = {.4f}"
                      }
                    ]
                  },
                  "symbol": "point",
                  "size": 50
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

        fun symbol_point(): MutableMap<String, Any> {
            val spec = """
                {
                  "data": {
                    "request": ["Texas", "Nevada", "Iowa"],
                    "lon": [-99.6829525269137, -116.666956541192, -93.1514127397129],
                    "lat": [31.1685702949762, 38.5030842572451, 41.9395130127668],
                    "found name": ["Texas", "Nevada", "Iowa"]
                  },
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "livemap",
                      "mapping": {
                        "x": "lon",
                        "y": "lat"
                      },
                      "symbol": "point",
                      "tiles": {
                        "kind": "vector_lets_plot",
                        "url": "ws://10.0.0.127:3933",
                        "theme": null
                      },
                      "size": 50
                    }
                  ]
                }
            """.trimIndent()
            return parsePlotSpec(spec)
        }

        private fun geom_point(): MutableMap<String, Any> {
            val spec = """{
  "data": {
    "request": ["Texas", "Nevada", "Iowa"],
    "lon": [-99.6829525269137, -116.666956541192, -93.1514127397129],
    "lat": [31.1685702949762, 38.5030842572451, 41.9395130127668],
    "found name": ["Texas", "Nevada", "Iowa"]
  },
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "ws://10.0.0.127:3933",
        "theme": null
      }
    },
    {
      "geom": "point",
      "mapping": {
        "x": "lon",
        "y": "lat"
      },
      "symbol": "point",
      "size": 50
    }
  ]
}"""
            return parsePlotSpec(spec)
        }

        fun basic(): Map<String, Any> {
            val spec = """
                {
                    "kind": "plot", 
                    "layers": [
                        {
                            "geom": "livemap",
                            "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}                            
                        }
                    ]
                }
            """.trimIndent()

            return parsePlotSpec(spec)
        }
        
        fun facetBars(): Map<String, Any> {
            val spec = """{
                "ggtitle":{
                    "text":"Facet bars"
                },
                "data":{
                    "time":["Lunch", "Lunch", "Dinner", "Dinner", "Dinner"]
                },
                "facet":{
                    "name":"grid",
                    "x":"time",
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"bar",
                        "mapping":{
                            "x":"time",
                            "fill":"time"
                        }
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }
        
        fun points(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Points on map"}, 
                "data": {"lon": [-100.420313, -91.016016], "lat": [34.835461, 38.843142], "clr": ["one", "two"]}, 
                "kind": "plot", 
                "layers": [
                    {
                        "geom": "livemap", 
                        "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}
                    }, 
                    {
                        "geom": "point", 
                        "mapping": {"x": "lon", "y": "lat", "color": "clr"}, 
                        "size": 20
                    }
                ]
            }""".trimMargin()

            return parsePlotSpec(spec)
        }
        
        fun bunch(): Map<String, Any> {
            val spec = """{
                "kind": "ggbunch", 
                "items": [
                    {
                        "x": 0, 
                        "y": 0, 
                        "feature_spec": {
                            "kind": "plot", 
                            "layers": [
                                {
                                    "geom": "livemap", 
                                    "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}
                                }
                            ]
                        }
                    }, 
                    {
                        "x": 0, 
                        "y": 400, 
                        "feature_spec": {
                            "kind": "plot", 
                            "layers": [
                                {
                                    "geom": "livemap", 
                                    "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}
                                }
                            ]
                        }
                    }
                ]
            }""".trimIndent()

            return parsePlotSpec(spec)
            
        }

        fun facet(): Map<String, Any> {
            val spec = """{
                "data":{
                    "lon":[
                        -100.420313,
                        -91.016016
                    ],
                    "lat":[
                        34.835461,
                        38.843142
                    ]
                },
                "facet":{
                    "name":"grid",
                    "x":"lon",
                    "y":"lon"
                },
                "ggtitle":{
                    "text":"Two points"
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"livemap",
                        "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}
                    },
                    {
                        "geom":"point",
                        "mapping":{
                            "x":"lon",
                            "y":"lat",
                            "color":"lon"
                        },
                        "size":20
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }

        fun pointsWithZoomAndLocation(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Points with zoom and location"}, 
                "data": {
                    "lon": [-100.420313, -91.016016], 
                    "lat": [34.835461, 38.843142], 
                    "clr": ["one", "two"]
                }, 
                "kind": "plot", 
                "layers": [
                    {
                        "geom": "livemap", 
                        "location": {
                            "type": "coordinates",
                            "data": [25.878516, 58.317548, 33.590918, 60.884144]
                        },
                        "zoom": 10 
                    }, 
                    {
                        "geom": "point", 
                        "mapping": {"x": "lon", "y": "lat", "color": "clr"}, 
                        "size": 20
                    }
                ]
            }""".trimMargin()

            return parsePlotSpec(spec)
        }

        fun setLocation(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Set location"}, 
                "kind": "plot",
                "layers": [
                    {
                        "geom": "livemap",
                        "location": {
                            "type": "coordinates",
                            "data": [25.878516, 58.317548, 33.590918, 60.884144]
                        },
                        "tiles": null
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }

        fun setZoom(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Set zoom and default location"}, 
                "kind": "plot",
                "layers": [
                    {
                        "geom": "livemap",
                        "zoom": 4,
                        "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }
        
        fun wrongRasterTileUrl(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Wrong tile url"}, 
                "kind": "plot",
                "layers": [
                    {
                        "geom": "livemap",
                        "tiles": {"raster": "http://c.tile.stamen.com/tonerd/{x}/{y}.png"}
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }
        
        fun fourPointsTwoLayers(): Map<String, Any> {
            val spec = """{
                "data":{
                    "x":[29.777834, 29.778033],
                    "y":[59.991666, 59.988106],
                    "lonlat":["29.777834,59.991666", "29.778033,59.988106"],
                    "label":["one", "two"]
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"livemap",
                        "mapping":{
                            "x":"x",
                            "y":"y",
                            "color":"label"
                        },
                        "display_mode":"point",
                        "tiles":{ "kind":"raster_zxy", "url":"https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"},
                        "shape":19,
                        "size":21
                    },
                    {
                        "geom":"point",
                        "data":{
                            "lon":[29.703667, 29.72339],
                            "lat":[60.01668, 60.008983],
                            "label":["three", "four"]
                        },
                        "mapping":{
                            "x":"lon",
                            "y":"lat",
                            "color":"label"
                        },
                        "shape":19,
                        "size":21
                    }
                ]
            }
                """

            return parsePlotSpec(spec)
        }

        fun pointAndText(): Map<String, Any> {
            val spec = """{
                "data":{
                    "x":[29.777834, 29.778033],
                    "y":[59.991666, 59.988106]
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"livemap",
                        "tiles": {"kind": "raster_zxy", "url": "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png"}
                    },
                    {
                        "geom":"point",
                        "mapping":{
                            "x":"x",
                            "y":"y",
                            "color":"x"
                        },
                        "shape":19,
                        "size":21
                    },
                    {
                        "geom":"text",
                        "data":{
                            "lon":[29.72339],
                            "lat":[60.008983],
                            "label":["Kotlin"]
                        },
                        "mapping":{
                            "x":"lon",
                            "y":"lat",
                            "label":"label"
                        },
                        "size":18,
                        "color":"#900090",
                        "family":"serif",
                        "fontface":"italic bold",
                        "hjust":"middle",
                        "vjust":"center",
                        "angle":30
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }

}
