/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class GeoData {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            mapGeoDataFrame_MapJoin(),
            mapGeoDataFrame_NoMapJoin_MixedShapes("polygon"),
            mapGeoDataFrame_NoMapJoin_MixedShapes("point"),
            mapGeoDataFrame_NoMapJoin_MixedShapes("path"),
            dataGeoDataFrame_NoMapJoin_GeomText(),
        )
    }

    companion object {
        private const val pointA = """{\"type\": \"Point\", \"coordinates\": [0.0, 0.0]}"""
        private const val pointB = """{\"type\": \"Point\", \"coordinates\": [10.0, 10.0]}"""
        private const val lineA = """{\"type\": \"LineString\", \"coordinates\": [[15.0, 21.0], [29, 14], [33, 19]]}"""
        private const val lineB = """{\"type\": \"LineString\", \"coordinates\": [[3.0, 3.0], [7, 7], [10, 10]]}"""
        private const val multipolygon =
            """{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"""


        private fun mapGeoDataFrame_MapJoin(): MutableMap<String, Any> {
            val spec = """
            |{
            |    "ggtitle": {"text": "mapGeoDataFrame_MapJoin"},
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "point", 
            |        "data": {"labels": ["A", "B"], "values": [12, 3]}, 
            |        "mapping": {"color": "values"}, 
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
            |        "map_join": [["labels"], ["map_names"]], 
            |        "map": {
            |            "map_names": ["A", "B"], 
            |            "coord": ["$pointA", "$pointB"]
            |        }
            |    }]
            |}            
        """.trimMargin()

            return parsePlotSpec(spec)
        }

        fun dataGeoDataFrame_NoMapJoin_GeomText(): MutableMap<String, Any> {
            val spec = """
                |{
                |   "ggtitle": {"text": "dataGeoDataFrame_GeomText"},
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

        fun mapGeoDataFrame_NoMapJoin_MixedShapes(geomName: String): MutableMap<String, Any> {
            val plotSpec = """
                |{
                |   "ggtitle": {"text": "mapGeoDataFrame_MixedShapes geom_$geomName"},
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
