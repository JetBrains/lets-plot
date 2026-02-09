package org.jetbrains.letsPlot.visualtesting.plot

internal class LiveMapTest : PlotTestBase() {
    init {

    }

//    fun `geom_livemap stub png tile test`() {
//        runStubRasterTileServer("png") { url ->
//            val spec = JsonSupport.parseJson("""
//                |{
//                |  "kind": "plot",
//                |  "layers": [
//                |    {
//                |      "geom": "livemap",
//                |      "zoom": 1,
//                |      "tiles": { "kind": "raster_zxy", "url": "$url", "attribution": "Lets-Plot" }
//                |    },
//                |    { "geom": "point", "x": 0, "y": 0 }
//                |  ]
//                |}
//            """.trimMargin()
//            )
//
//            val plotSpec = spec.themeTextNotoSans()
//            assertPlot("geom_livemap_test_png_tiles.png", plotSpec, fontManager = fontManager)
//        }
//
//    }
}