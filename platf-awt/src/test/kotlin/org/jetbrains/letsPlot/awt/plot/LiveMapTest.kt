package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class LiveMapTest : VisualPlotTestBase() {
    @Test
    fun `minard with default tiles`() {
        val spec = parsePlotSpec("""
            |{
            |    "data": {
            |        "long": [24.0, 24.5, 25.5, 26.0, 27.0, 28.0, 28.5, 29.0, 30.0, 30.3, 32.0, 33.2, 34.4, 35.5, 36.0, 37.6, 37.7, 37.5, 37.0, 36.8, 35.4, 34.3, 33.3, 32.0, 30.4, 29.2, 28.5, 28.3, 27.5, 26.8, 26.4, 25.0, 24.4, 24.2, 24.1, 24.0, 24.5, 25.5, 26.6, 27.4, 28.7, 28.7, 29.2, 28.5, 28.3, 24.0, 24.5, 24.6, 24.6, 24.2, 24.1 ], 
            |        "lat": [54.9, 55.0, 54.5, 54.7, 54.8, 54.9, 55.0, 55.1, 55.2, 55.3, 54.8, 54.9, 55.5, 55.4, 55.5, 55.8, 55.7, 55.7, 55.0, 55.0, 55.3, 55.2, 54.8, 54.6, 54.4, 54.3, 54.2, 54.3, 54.5, 54.3, 54.4, 54.4, 54.4, 54.4, 54.4, 55.1, 55.2, 54.7, 55.7, 55.6, 55.5, 55.5, 54.2, 54.1, 54.2, 55.2, 55.3, 55.8, 55.8, 54.4, 54.4 ], 
            |        "survivors": [340000.0, 340000.0, 340000.0, 320000.0, 300000.0, 280000.0, 240000.0, 210000.0, 180000.0, 175000.0, 145000.0, 140000.0, 127100.0, 100000.0, 100000.0, 100000.0, 100000.0, 98000.0, 97000.0, 96000.0, 87000.0, 55000.0, 37000.0, 24000.0, 20000.0, 20000.0, 20000.0, 20000.0, 20000.0, 12000.0, 14000.0, 8000.0, 4000.0, 4000.0, 4000.0, 60000.0, 60000.0, 60000.0, 40000.0, 33000.0, 33000.0, 33000.0, 30000.0, 30000.0, 28000.0, 22000.0, 22000.0, 6000.0, 6000.0, 6000.0, 6000.0 ], 
            |        "direction": ["A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "A", "A", "A", "A", "A", "A", "R", "R", "R", "R", "A", "A", "A", "R", "R", "R" ], 
            |        "group": [1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0 ]
            |    }, 
            |    "data_meta": {
            |        "series_annotations": [
            |            { "type": "float", "column": "long" }, 
            |            { "type": "float", "column": "lat" }, 
            |            { "type": "int", "column": "survivors" }, 
            |            { "type": "str", "column": "direction" }, 
            |            { "type": "int", "column": "group" }
            |        ]
            |    }, 
            |    "guides": { "color": "none" }, 
            |    "kind": "plot", 
            |    "scales": [
            |        { "aesthetic": "size", "range": [ 1.0, 20.0 ] }, 
            |        { "aesthetic": "color", "values": [ "#E1CBAE", "#232021" ] }
            |    ], 
            |    "layers": [
            |        {
            |            "geom": "livemap", 
            |            "tiles": { "kind": "vector_lets_plot", "url": "wss://tiles.datalore.jetbrains.com", "theme": "color", "attribution": "<a href=\"https://lets-plot.org\">© Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>." }, 
            |            "geocoding": { "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding" }
            |        }, 
            |        {
            |            "geom": "path", 
            |            "mapping": { "x": "long", "y": "lat", "size": "survivors", "group": "group", "color": "direction" }, 
            |            "data_meta": { }
            |        }
            |    ]
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("geom_livemap_minard.png", plotSpec, fontManager = fontManager)
    }
}
