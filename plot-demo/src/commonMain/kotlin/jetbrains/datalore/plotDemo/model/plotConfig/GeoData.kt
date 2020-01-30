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
            //emptyDataGdf(),
            //emptyMapGdf(),
            geomText()
            //mixedShapesGeom("polygon"),
            //mixedShapesGeom("point"),
            //mapRegionId()
        )
    }

    companion object {
        private const val pointA = """{\"type\": \"Point\", \"coordinates\": [12.0, 22.0]}"""
        private const val pointB = """{\"type\": \"Point\", \"coordinates\": [25.0, 11.0]}"""
        private const val multipolygon =
            """{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"""

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
                |        "map_join": "map_names",
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
                |            "id": ["MPolygon", "Point"], 
                |            "coord": ["$multipolygon", "$pointA"]
                |        }
                |    }]
                |}
                """.trimMargin()
            return parsePlotSpec(plotSpec)
        }
    }
}
