package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.visualtesting.plot.runStubRasterTileServer
import org.jetbrains.letsPlot.visualtesting.plot.runStubVectorTileServer
import kotlin.test.Ignore
import kotlin.test.Test

class LiveMapTest : VisualPlotTestBase() {
    //@Ignore("For debugging purposes only")
    @Test
    fun `geom_livemap prod vector tiles`() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "livemap",
            |      "zoom": 1.0,
            |      "tiles": {
            |        "kind": "vector_lets_plot",
            |        "url": "wss://tiles.datalore.jetbrains.com",
            |        "theme": "color",
            |        "attribution": "<a href=\"https://lets-plot.org\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
            |      },
            |      "geocoding": { "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding" }
            |    },
            |    { "geom": "point", "x": 0.0, "y": 0.0 }
            |  ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()
        assertPlot("geom_livemap_prod_vector_tiles.png", plotSpec, fontManager = fontManager)
    }

    @Test
    @Ignore("Need local tile server")
    fun `geom_livemap prod minard`() {
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
            |            "geocoding": { "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding" },
            |            "dev_params": { "microtask_executor": "ui_thread", "computation_projection_quant": 100000, "computation_frame_time": 1000 }
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

        assertPlot("geom_livemap_prod_minard.png", plotSpec, fontManager = fontManager)
    }

    @Test
    //@Ignore("Need local tile server")
    fun `geom_livemap nasa tiles`() {
        val spec = parsePlotSpec("""
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "livemap",
            |      "tiles": {
            |        "kind": "raster_zxy",
            |        "url": "https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/VIIRS_CityLights_2012/default//GoogleMapsCompatible_Level8/{z}/{y}/{x}.jpg",
            |        "attribution": "<a href=\"https://lets-plot.org\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs\">\u00a9 NASA Global Imagery Browse Services (GIBS)</a>",
            |        "min_zoom": 1.0,
            |        "max_zoom": 8.0
            |      },
            |      "geocoding": { "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding" }
            |    }
            |  ]
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()
        assertPlot("geom_livemap_nasa_tiles.png", plotSpec, fontManager = fontManager)
    }

    @Test
    fun `geom_livemap test png tiles`() {
        runStubRasterTileServer("png") { url ->
            val spec = parsePlotSpec("""
                |{
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "livemap",
                |      "zoom": 1,
                |      "tiles": { "kind": "raster_zxy", "url": "$url", "attribution": "Lets-Plot" }
                |    },
                |    { "geom": "point", "x": 0, "y": 0 }
                |  ]
                |}
            """.trimMargin()
            )

            val plotSpec = spec.themeTextNotoSans()
            assertPlot("geom_livemap_test_png_tiles.png", plotSpec, fontManager = fontManager)
        }
    }

    @Test
    fun `geom_livemap test vector tiles`() {
        runStubVectorTileServer { url ->
            val spec = parsePlotSpec("""
                |{
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "livemap",
                |      "zoom": 1,
                |      "tiles": { "kind": "vector_lets_plot", "url": "$url", "attribution": "Lets-Plot" }
                |    },
                |    { "geom": "point", "x": 0, "y": 0 }
                |  ]
                |}
            """.trimMargin()
            )

            val plotSpec = spec.themeTextNotoSans()
            assertPlot("geom_livemap_test_vector_tiles.png", plotSpec, fontManager = fontManager)
        }
    }
}
