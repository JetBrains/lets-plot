/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.plotDemo

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class LiveMap : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            setZoom(),
            setLocation(),
            points(),
            pointsWithZoomAndLocation()
        )
    }


    companion object {

        fun basic(): Map<String, Any> {
            val spec = """
                {
                    "data": null, 
                    "mapping": null, 
                    "kind": "plot", 
                    "scales": [], 
                    "layers": [{
                        "geom": "livemap", 
                        "stat": null, 
                        "data": null, 
                        "mapping": null, 
                        "position": null, 
                        "show_legend": null, 
                        "sampling": null, 
                        display_mode": null, 
                        "level": null, 
                        "within": null, 
                        "interactive": null, 
                        "location": null, 
                        "zoom": null, 
                        "magnifier": null, 
                        "clustering": null, 
                        "scaled": null, 
                        "labels": null, 
                        "theme": null, 
                        "projection": null, 
                        "geodesic": null, 
                        "tiles": null}]
                }
            """.trimIndent()

            return parsePlotSpec(spec)
        }
        
        fun facetBars(): Map<String, Any> {
            val spec = """{"ggtitle": {"text": "Facet bars"}, "data": {"time": ["Lunch", "Lunch", "Dinner", "Dinner", "Dinner"]}, "mapping": null, "facet": {"name": "grid", "x": "time", "y": null}, "kind": "plot", "scales": [], "layers": [{"geom": "bar", "stat": null, "data": null, "mapping": {"x": "time", "y": null, "fill": "time"}, "position": null, "show_legend": null, "sampling": null}]}"""
            return parsePlotSpec(spec)
        }
        
        fun points(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Points on map"}, 
                "data": {"lon": [-100.420313, -91.016016], "lat": [34.835461, 38.843142], "clr": ["one", "two"]}, 
                "mapping": null, 
                "kind": "plot", 
                "scales": [], 
                "layers": [
                    {
                        "geom": "livemap", 
                        "stat": null, 
                        "data": null, 
                        "mapping": null, 
                        "position": null, 
                        "show_legend": null, 
                        "sampling": null, 
                        "display_mode": null, 
                        "level": null, 
                        "within": null, 
                        "interactive": null, 
                        "location": null, 
                        "zoom": null, 
                        "magnifier": null, 
                        "clustering": null, 
                        "scaled": null, 
                        "labels": null, 
                        "theme": null, 
                        "projection": null, 
                        "geodesic": null, 
                        "tiles": null
                    }, 
                    {
                        "geom": "point", 
                        "stat": null, 
                        "data": null, 
                        "mapping": {"x": "lon", "y": "lat", "color": "clr"}, 
                        "position": null, 
                        "show_legend": null, 
                        "sampling": null, 
                        "animation": null, 
                        "size": 20
                    }
                ]
            }""".trimMargin()

            return parsePlotSpec(spec)
        }
        
        fun bunch(): Map<String, Any> {
            val spec = """{"kind": "ggbunch", "items": [{"x": 0, "y": 0, "width": null, "height": null, "feature_spec": {"data": null, "mapping": null, "kind": "plot", "scales": [], "layers": [{"geom": "livemap", "stat": null, "data": null, "mapping": null, "position": null, "show_legend": null, "sampling": null, "display_mode": null, "level": null, "within": null, "interactive": null, "location": null, "zoom": null, "magnifier": null, "clustering": null, "scaled": null, "labels": null, "theme": null, "projection": null, "geodesic": null, "tiles": null}]}}, {"x": 0, "y": 400, "width": null, "height": null, "feature_spec": {"data": null, "mapping": null, "kind": "plot", "scales": [], "layers": [{"geom": "livemap", "stat": null, "data": null, "mapping": null, "position": null, "show_legend": null, "sampling": null, "display_mode": null, "level": null, "within": null, "interactive": null, "location": null, "zoom": null, "magnifier": null, "clustering": null, "scaled": null, "labels": null, "theme": null, "projection": null, "geodesic": null, "tiles": null}]}}]}""".trimIndent()

            return parsePlotSpec(spec)
            
        }

        fun facet(): Map<String, Any> {
            val spec = """{"data": {"lon": [-100.420313, -91.016016], "lat": [34.835461, 38.843142]}, "mapping": null, "facet": {"name": "grid", "x": "lon", "y": "lon"}, "ggtitle": {"text": "Two points"}, "kind": "plot", "scales": [], "layers": [{"geom": "livemap", "stat": null, "data": null, "mapping": null, "position": null, "show_legend": null, "sampling": null, "display_mode": null, "level": null, "within": null, "interactive": null, "location": null, "zoom": null, "magnifier": null, "clustering": null, "scaled": null, "labels": null, "theme": null, "projection": null, "geodesic": null, "tiles": null}, {"geom": "point", "stat": null, "data": null, "mapping": {"x": "lon", "y": "lat", "color": "lon"}, "position": null, "show_legend": null, "sampling": null, "animation": null, "size": 20}]}"""
            return parsePlotSpec(spec)
        }

        fun pointsWithZoomAndLocation(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Points with zoom and location"}, 
                "data": {"lon": [-100.420313, -91.016016], "lat": [34.835461, 38.843142], "clr": ["one", "two"]}, 
                "mapping": null, 
                "kind": "plot", 
                "scales": [], 
                "layers": [
                    {
                        "geom": "livemap", 
                        "stat": null, 
                        "data": null, 
                        "mapping": null, 
                        "position": null, 
                        "show_legend": null, 
                        "sampling": null, 
                        "display_mode": null, 
                        "level": null, 
                        "within": null, 
                        "interactive": null, 
                        "location": {
                            "type": "coordinates",
                            "data": [25.878516, 58.317548, 33.590918, 60.884144]
                        },
                        "zoom": 10, 
                        "magnifier": null, 
                        "clustering": null, 
                        "scaled": null, 
                        "labels": null, 
                        "theme": null, 
                        "projection": null, 
                        "geodesic": null, 
                        "tiles": null
                    }, 
                    {
                        "geom": "point", 
                        "stat": null, 
                        "data": null, 
                        "mapping": {"x": "lon", "y": "lat", "color": "clr"}, 
                        "position": null, 
                        "show_legend": null, 
                        "sampling": null, 
                        "animation": null, 
                        "size": 20
                    }
                ]
            }""".trimMargin()

            return parsePlotSpec(spec)
        }

        fun setLocation(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Set location"}, 
                "data": null,
                "mapping": null,
                "kind": "plot",
                "scales": [],
                "layers": [
                    {
                        "geom": "livemap",
                        "stat": null,
                        "data": null,
                        "mapping": null,
                        "position": null,
                        "show_legend": null,
                        "sampling": null,
                        "display_mode": null,
                        "level": null,
                        "within": null,
                        "interactive": null,
                        "location": {
                            "type": "coordinates",
                            "data": [25.878516, 58.317548, 33.590918, 60.884144]
                        },
                        "zoom": null,
                        "magnifier": null,
                        "clustering": null,
                        "scaled": null,
                        "labels": null,
                        "theme": null,
                        "projection": null,
                        "geodesic": null,
                        "tiles": null
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }

        fun setZoom(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Set zoom and default location"}, 
                "data": null,
                "mapping": null,
                "kind": "plot",
                "scales": [],
                "layers": [
                    {
                        "geom": "livemap",
                        "stat": null,
                        "data": null,
                        "mapping": null,
                        "position": null,
                        "show_legend": null,
                        "sampling": null,
                        "display_mode": null,
                        "level": null,
                        "within": null,
                        "interactive": null,
                        "location": null,
                        "zoom": 4,
                        "magnifier": null,
                        "clustering": null,
                        "scaled": null,
                        "labels": null,
                        "theme": null,
                        "projection": null,
                        "geodesic": null,
                        "tiles": null
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }
        
        fun wrongRasterTileUrl(): Map<String, Any> {
            val spec = """{
                "ggtitle": {"text": "Wrong tile url"}, 
                "data": null,
                "mapping": null,
                "kind": "plot",
                "scales": [],
                "layers": [
                    {
                        "geom": "livemap",
                        "stat": null,
                        "data": null,
                        "mapping": null,
                        "position": null,
                        "show_legend": null,
                        "sampling": null,
                        "display_mode": null,
                        "level": null,
                        "within": null,
                        "interactive": null,
                        "location": null,
                        "zoom": null,
                        "magnifier": null,
                        "clustering": null,
                        "scaled": null,
                        "labels": null,
                        "theme": null,
                        "projection": null,
                        "geodesic": null,
                        "tiles": {"raster": "http://c.tile.stamen.com/tonerd/{x}/{y}.png"}
                    }
                ]
            }"""
            return parsePlotSpec(spec)
        }
    }

    
}