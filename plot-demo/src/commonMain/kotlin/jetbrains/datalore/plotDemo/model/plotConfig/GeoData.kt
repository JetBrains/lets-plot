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
            mixedShapes("polygon"),
            mixedShapes("point"),
            mapRegionId()
        )
    }

    companion object {
        fun mapRegionId(): MutableMap<String, Any> {
            val pointA = """{\"type\": \"Point\", \"coordinates\": [12.0, 22.0]}"""
            val pointB = """{\"type\": \"Point\", \"coordinates\": [25.0, 11.0]}"""
            val spec =
"""
{
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
        "map_join": "map_names",
        "mapping": {
            "color": "values", 
            "map_id": "labels"
        }, 
        "data": {
            "labels": ["A", "B"], 
            "values": [12, 3]
        }, 
        "map": {
            "map_names": ["A", "B"], 
            "coord": [
                "$pointA", 
                "$pointB"
            ]
        }
    }]
}
"""
            return parsePlotSpec(spec)
        }

        fun mixedShapes(geomName: String): MutableMap<String, Any> {
            val multipolygon = """{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"""
            val point = """{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}"""
            val plotSpec =
"""
{
    "kind": "plot", 
    "layers": [{
        "geom": "$geomName", 
        "map_data_meta": {"geodataframe": {"geometry": "coord"}}, 
        "map": {
            "id": ["MPolygon", "Point"], 
            "coord": ["$multipolygon", "$point"]
        }
    }]
}
"""
            return parsePlotSpec(plotSpec)
        }
    }
}