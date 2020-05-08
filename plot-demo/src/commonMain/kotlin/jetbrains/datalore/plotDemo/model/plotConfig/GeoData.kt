/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class GeoData : PlotConfigDemoBase() {

    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            mapJoinDict(),
            mapJoinPair(),
            mapIdAndMapJoinNoneString(),
            emptyDataGdf(),
            emptyMapGdf(),
            geomText(),
            mixedShapesGeom("polygon"),
            mixedShapesGeom("point"),
            mixedShapesGeom("path"),
            mapRegionId()
        )
    }

    companion object {
        private const val pointA = """{\"type\": \"Point\", \"coordinates\": [12.0, 22.0]}"""
        private const val pointB = """{\"type\": \"Point\", \"coordinates\": [25.0, 11.0]}"""
        private const val lineA = """{\"type\": \"LineString\", \"coordinates\": [[15.0, 21.0], [29, 14], [33, 19]]}"""
        private const val lineB = """{\"type\": \"LineString\", \"coordinates\": [[3.0, 3.0], [7, 7], [10, 10]]}"""
        private const val multipolygon =
            """{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"""

        private fun mapJoinDict(): Map<String, Any> {
            val spec = """
{
  "ggsize": {
    "width": 500,
    "height": 300
  },
  "kind": "plot",
  "layers": [
    {
      "geom": "polygon",
      "data": {
        "Country": ["UK", "Germany", "France"],
        "Population": [66650000.0, 83020000.0, 66990000.0]
      },
      "mapping": {"fill": "Population"},
      "map": {
        "lon": [-2.598046, -2.37832, -2.46621, -3.169335, -1.938867, -0.576562, -0.31289, 0.873632, 0.082617, -2.598046, 7.685156, 9.926367, 13.661718, 14.101171, 11.464453, 12.870703, 8.564062, 8.651953, 6.80625, 6.938085, 7.685156, -2.246484, 2.367773, 7.245703, 5.268164, 6.586523, 2.895117, 2.279882, -0.532617, -0.356835, -2.246484],
        "lat": [ 51.030349, 51.797754, 53.94575, 54.561879, 55.193929, 53.816229, 52.924809, 52.525588, 51.113188, 51.030349, 53.294124, 54.049078, 53.60816, 51.305902, 50.221916, 48.679365, 48.007575, 49.485266, 50.024691, 51.552493, 53.294124, 48.095702, 50.586036, 48.795295, 46.365136, 44.169607, 43.663114, 43.088157, 43.631315, 46.51655, 48.095702],
        "country": [ "UK", "UK", "UK", "UK", "UK", "UK", "UK", "UK", "UK", "UK", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "France", "France", "France", "France", "France", "France", "France", "France", "France", "France"]
      },
      "map_join": ["Country", "country"],
      "map_data_meta": {"geodict": {}},
      "alpha": 0.3
    }
  ]
}                
            """.trimIndent()
            return parsePlotSpec(spec)
        }

        private fun mapJoinPair(): Map<String, Any> {
            val spec = """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "point", 
            |        "data": {"labels": ["A", "B"], "values": [12, 3]}, 
            |        "mapping": {"color": "values"}, 
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
            |        "map_join": ["labels", "map_names"], 
            |        "map": {
            |            "map_names": ["A", "B"], 
            |            "coord": ["$pointA", "$pointB"]
            |        }
            |    }]
            |}            
        """.trimMargin()

            return parsePlotSpec(spec)
        }

        private fun mapIdAndMapJoinNoneString(): Map<String, Any> {
            val spec = """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "point", 
            |        "data": {"labels": ["A", "B"], "values": [12, 3]}, 
            |        "mapping": {
            |            "color": "values", 
            |            "map_id": "labels"
            |        }, 
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
            |        "map_join": [null, "map_names"], 
            |        "map": {
            |            "map_names": ["A", "B"], 
            |            "coord": ["$pointA", "$pointB"]
            |        }
            |    }]
            |}            
        """.trimMargin()

            return parsePlotSpec(spec)
        }

        fun emptyDataGdf(): MutableMap<String, Any> {
            val spec = """
                |{
                |    "kind": "plot", 
                |    "layers": [{
                |        "geom": "polygon", 
                |        "mapping": {"label": "map_names"}, 
                |        "data_meta": {"geodataframe": {"geometry": "coord"}}, 
                |        "data": {
                |            "map_names": ["A", "B"], 
                |            "coord": ["$pointA", "$pointB"]
                |        } 
                |    }]
                |}    
                """.trimMargin()
            return parsePlotSpec(spec)
        }

        fun emptyMapGdf(): MutableMap<String, Any> {
            val spec = """
                |{
                |    "kind": "plot", 
                |    "layers": [{
                |        "geom": "polygon", 
                |        "mapping": {"label": "map_names"}, 
                |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
                |        "map": {
                |            "map_names": ["A", "B"], 
                |            "coord": ["$pointA", "$pointB"]
                |        } 
                |    }]
                |}    
                """.trimMargin()
            return parsePlotSpec(spec)
        }

        fun geomText(): MutableMap<String, Any> {
            val spec = """
                |{
                |    "kind": "plot", 
                |    "layers": [{
                |        "geom": "text", 
                |        "mapping": {"label": "map_names"}, 
                |        "data_meta": {"geodataframe": {"geometry": "coord"}}, 
                |        "data": {
                |            "map_names": ["A", "B"], 
                |            "coord": ["$pointA", "$pointB"]
                |        } 
                |    }]
                |}    
                """.trimMargin()
            return parsePlotSpec(spec)
        }

        fun mapRegionId(): MutableMap<String, Any> {
            val spec = """
                |{
                |    "kind": "plot", 
                |    "layers": [{
                |        "geom": "point", 
                |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
                |        "map_join": ["labels", "map_names"],
                |        "mapping": {
                |            "color": "values", 
                |            "map_id": "labels"
                |        }, 
                |        "data": {
                |            "labels": ["A", "B"], 
                |            "values": [12, 3]
                |        }, 
                |        "map": {
                |            "map_names": ["A", "B"], 
                |            "coord": [
                |                "$pointA", 
                |                "$pointB"
                |            ]
                |        }
                |    }]
                |}
                """.trimMargin()
            return parsePlotSpec(spec)
        }

        fun mixedShapesGeom(geomName: String): MutableMap<String, Any> {
            val plotSpec = """
                |{
                |    "kind": "plot", 
                |    "layers": [{
                |        "geom": "$geomName", 
                |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
                |        "map": {
                |            "id": ["MPolygon", "Point", "lineA", "lineB"], 
                |            "coord": ["$multipolygon", "$pointA", "$lineA", "$lineB"]
                |        }
                |    }]
                |}
                """.trimMargin()
            return parsePlotSpec(plotSpec)
        }
    }
}
