/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.findVariableOrFail
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.LayerRendererUtil.createLayerRendererData
import jetbrains.datalore.plot.config.GeoConfig.Companion.MAP_JOIN_REQUIRED_MESSAGE
import jetbrains.datalore.plot.config.GeoConfig.Companion.POINT_X
import jetbrains.datalore.plot.config.GeoConfig.Companion.POINT_Y
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_XMAX
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_XMIN
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_YMAX
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_YMIN
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil.createPlotAssembler
import demoAndTestShared.parsePlotSpec
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoConfigTest {
    private val point = """{\"type\": \"Point\", \"coordinates\": [1.0, 2.0]}"""
    private val multiPoint = """{\"type\": \"MultiPoint\", \"coordinates\": [[3.0, 4.0], [5.0, 6.0]]}"""
    private val lineString = """{\"type\": \"LineString\", \"coordinates\": [[7.0, 8.0], [9.0, 10.0]]}"""
    private val multiLineString =
        """{\"type\": \"MultiLineString\", \"coordinates\": [[[11.0, 12.0], [13.0, 14.0]], [[15.0, 16.0], [17.0, 18.0]]]}"""
    private val polygon = """
|{
|   \"type\": \"Polygon\", 
|   \"coordinates\":[
|       [
|           [21.0, 21.0], 
|           [21.0, 29.0], 
|           [29.0, 29.0], 
|           [29.0, 21.0], 
|           [21.0, 21.0]
|       ], 
|       [
|           [22.0, 22.0], 
|           [23.0, 22.0], 
|           [23.0, 23.0], 
|           [22.0, 23.0], 
|           [22.0, 22.0]
|       ], 
|       [
|           [24.0, 24.0], 
|           [26.0, 24.0], 
|           [26.0, 26.0], 
|           [24.0, 26.0], 
|           [24.0, 24.0]
|       ]
|   ]
|}""".trimMargin()

    private val multiPolygon = """
|{
|    \"type\": \"MultiPolygon\", 
|    \"coordinates\": [
|       [
|           [
|               [11.0, 12.0], 
|               [13.0, 14.0], 
|               [15.0, 13.0], 
|               [11.0, 12.0]
|           ]
|       ]
|   ]
|}""".trimMargin()


    private fun <T> polygonSequence(groupId: T) = (0..14).map { groupId }
    private fun <T> multiPolygonSequence(groupId: T) = (0..3).map { groupId }

    private val gdf = """
        |{
        |    "kind": ["Point", "MPoint", "Line", "MLine", "Polygon", "MPolygon"],
        |    "coord": ["$point", "$multiPoint", "$lineString", "$multiLineString", "$polygon", "$multiPolygon"],
        |    "points": [1, 2, 2, 4, 15, 4]
        |}
        """.trimMargin()

    private val df = """
        |{
        |   "fig": ["Point", "MPoint", "Line", "MLine", "Polygon", "MPolygon"],
        |   "value": [1, 2, 3, 4, 5, 6]
        |}
        """.trimMargin()

    private fun `aes(color='kind'), data=gdf`(geom: String): GeomLayer {
        return singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "$geom", 
            |        "mapping": {"color": "kind"}, 
            |        "data": $gdf,
            |        "data_meta": {"geodataframe": {"geometry": "coord"}}
            |    }]
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `geom_point(aes(color='kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom = "point")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "kind")
    }

    @Test
    fun `geom_rect(aes(color='kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom = "rect")
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
            .assertBinding(Aes.COLOR, "kind")
    }

    @Test
    fun `geom_polygon(aes(color = 'kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom = "polygon")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "kind")
            .assertGroups(polygonSequence(0) + multiPolygonSequence(1))
            .assertAes(Aes.COLOR, polygonSequence(Color(102, 194, 165)) + multiPolygonSequence(Color(252, 141, 98)))
    }

    @Test
    fun `geom_path(aes(color = 'kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom = "path")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "kind")
    }

    private fun `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom: String): GeomLayer {
        return singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "$geom",
            |        "data": $df,
            |        "mapping": {"color": "value"},
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["fig"], ["kind"]]
            |    }]
            |}
        """.trimMargin()
        )
    }

    @Test
    fun `geom_point(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom = "point")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "value")
    }

    @Test
    fun `geom_polygon(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom = "polygon")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "value")
            .assertGroups(polygonSequence(0) + multiPolygonSequence(1))

    }

    @Test
    fun `geom_path(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom = "path")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "value")
    }

    @Test
    fun `geom_rect(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom = "rect")
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
            .assertBinding(Aes.COLOR, "value")
    }

    private fun `map=gdf`(geom: String): GeomLayer {
        return singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "$geom", 
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}
            |    }]
            |}
        """.trimMargin()
        )
    }

    private fun singleGeomLayer(spec: String): GeomLayer {
        val config = PlotConfigClientSide.create(parsePlotSpec(spec)) {}
        return createPlotAssembler(config).coreLayersByTile.single().single()
    }

    @Test
    fun `geom_point(map=gdf)`() {
        `map=gdf`(geom = "point")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
    }

    @Test
    fun `geom_polygon(map=gdf)`() {
        `map=gdf`(geom = "polygon")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
    }

    @Test
    fun `geom_path(map=gdf)`() {
        `map=gdf`(geom = "path")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
    }

    @Test
    fun `geom_rect(map=gdf)`() {
        `map=gdf`(geom = "rect")
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
    }

    @Test
    fun `aes(color'fig'), data=df, map=gdf without map_join should fail`() {
        // don't know how to join data with map without id columns.
        assertEquals(
            MAP_JOIN_REQUIRED_MESSAGE, failedTransformToClientPlotConfig(
                """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "polygon",
            |        "data": { "fig": ["a", "b", "C"] },
            |        "mapping": { "color": "fig" },
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}
            |    }]
            |}""".trimMargin()
            )
        )
    }


    @Test
    fun `should not throw error on data key missing in map`() {
        singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "polygon",
            |        "data": {
            |            "fig": ["Polygon", "MPolygon", "Missing_Key"],
            |            "value": [42, 23, 66]
            |        },
            |        "mapping": {"fill": "value"},
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["fig"], ["kind"]]
            |    }]
            |}
        """.trimMargin()
        )
    }

    @Test
    fun `should not fail if map has extra entries`() {
        singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "polygon",
            |        "data": {
            |            "fig": ["Polygon"],
            |            "value": [42]
            |        },
            |        "mapping": {"fill": "value"},
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["fig"], ["kind"]]
            |    }]
            |}
        """.trimMargin()
        ).assertGroups(polygonSequence(0) + multiPolygonSequence(1))
    }

    @Test
    fun `right join special case - should add rows from map to data`() {
        val foo1 = """
            |{
            |    \"type\": \"Polygon\", 
            |    \"coordinates\": [
            |       [
            |               [1.0, 2.0], 
            |               [3.0, 4.0], 
            |               [5.0, 3.0], 
            |               [1.0, 2.0]
            |       ]
            |   ]
            |}""".trimMargin()

        val foo2 = """
            |{
            |    \"type\": \"Polygon\", 
            |    \"coordinates\": [
            |       [
            |               [21.0, 22.0], 
            |               [23.0, 24.0], 
            |               [25.0, 23.0], 
            |               [21.0, 22.0]
            |       ]
            |   ]
            |}""".trimMargin()

        val bar1 = """
            |{
            |    \"type\": \"Polygon\", 
            |    \"coordinates\": [
            |       [
            |               [31.0, 32.0], 
            |               [33.0, 34.0], 
            |               [35.0, 33.0], 
            |               [31.0, 32.0]
            |       ]
            |   ]
            |}""".trimMargin()


        val europe = Color(102, 194, 165)
        val asia = Color(252, 141, 98)
        singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "rect",
            |        "data": {
            |            "continent": ["Europe", "Asia"],
            |            "temp": [8.6, 16.6]
            |        },
            |        "mapping": {"fill": "continent"},
            |        "map": {
            |            "country": ["Germany", "France", "China"],
            |            "cont": ["Europe", "Europe", "Asia"],
            |            "coord": ["$foo1", "$foo2", "$bar1"]
            |        },
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["continent"], ["cont"]]
            |    }]
            |}
        """.trimMargin()
        )
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
            .assertAes(Aes.FILL, listOf(europe, europe, asia))
    }

    @Ignore
    @Test
    fun `geom_livemap(symbol='bar', map_join=( ))`() {
        val orangeCoord = """{\"type\": \"Point\", \"coordinates\": [1.0, 2.0]}"""
        val appleCoord = """{\"type\": \"Point\", \"coordinates\": [3.0, 4.0]}"""

        singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "livemap",
            |        "data": {
            |            "fruit": ["Apple", "Apple", "Orange", "Orange"],
            |            "nutrients": ["Fiber", "Carbs", "Fiber", "Carbs"],
            |            "values": [4.0, 25.0, 2.4, 12.0]
            |        },
            |        "symbol": "bar",
            |        "mapping": {
            |           "sym_x": "fruit",
            |           "sym_y": "values",
            |           "fill": "nutrients"
            |        },
            |        "map": {
            |            "name": ["Orange", "Apple"],
            |            "coord": ["$orangeCoord", "$appleCoord"]
            |        },
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": ["fruit", "name"]
            |    }]
            |}
        """.trimMargin()
        )
            .assertValues("fruit", listOf("Apple", "Apple", "Orange", "Orange"))
            .assertValues("nutrients", listOf("Fiber", "Carbs", "Fiber", "Carbs"))
            .assertValues("values", listOf(4.0, 25.0, 2.4, 12.0))
            .assertValues("__x__", listOf(3.0, 3.0, 1.0, 1.0))
            .assertValues("__y__", listOf(4.0, 4.0, 2.0, 2.0))
    }

    @Test
    fun `color mapping to __geo_id__ with multikey and map_join to make colors unique`() {
        val fooQux = """{\"type\": \"Point\", \"coordinates\": [1.0, 2.0]}"""
        val barQux = """{\"type\": \"Point\", \"coordinates\": [3.0, 4.0]}"""
        val bazQux = """{\"type\": \"Point\", \"coordinates\": [5.0, 6.0]}"""

        // county is not unique so to get unique color use special variable __geo_id__

        singleGeomLayer(
            """
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "point",
            |        "data": {
            |            "State": ["foo", "bar", "baz"],
            |            "County": ["qux", "qux", "qux"],
            |            "values": [100.0, 500.0, 42.42]
            |        },
            |        "mapping": {
            |           "color": "__geo_id__"
            |        },
            |        "map": {
            |            "state": ["foo", "bar", "baz"],
            |            "county": ["qux", "qux", "qux"],
            |            "name": ["Qux", "Qux", "Qux"],
            |            "coord": ["$fooQux", "$barQux", "$bazQux"]
            |        },
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["County", "State"], ["county", "state"]]
            |    }]
            |}
        """.trimMargin()
        )
            .assertValues("County", listOf("qux", "qux", "qux"))
            .assertValues("State", listOf("foo", "bar", "baz"))
            .assertValues("name", listOf("Qux", "Qux", "Qux"))
            .assertValues("county", listOf("qux", "qux", "qux"))
            .assertValues("state", listOf("foo", "bar", "baz"))
            .assertValues("values", listOf(100.0, 500.0, 42.42))
            .assertValues("lon", listOf(1.0, 3.0, 5.0))
            .assertValues("lat", listOf(2.0, 4.0, 6.0))
            .assertAes(Aes.COLOR, listOf(Color(102, 194, 165), Color(252, 141, 98), Color(141, 160, 203)))
    }

    @Test
    fun `should not trigger when positional mapping exist and it is not the map plot`() {
        singleGeomLayer(
            """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "histogram",
            |      "data": {
            |        "..count..": [1, 2, 3],
            |        "id": ["A", "B", "C"],
            |        "price": [123, 22, 44],
            |        "coord": [
            |          "{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}",
            |          "{\"type\": \"Polygon\", \"coordinates\": [[[1.0, 1.0], [1.0, 9.0], [9.0, 9.0], [9.0, 1.0], [1.0, 1.0]], [[2.0, 2.0], [3.0, 2.0], [3.0, 3.0], [2.0, 3.0], [2.0, 2.0]], [[4.0, 4.0], [6.0, 4.0], [6.0, 6.0], [4.0, 6.0], [4.0, 4.0]]]}",
            |          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"
            |        ]
            |      },
            |      "mapping": { "x": "price" },
            |      "data_meta": {
            |        "geodataframe": {
            |          "geometry": "coord"
            |        }
            |      }
            |    }
            |  ]
            |}
            |""".trimMargin()
        )
            .assertBinding(Aes.X, "price") // was not rebind to gdf
    }

    // excluding LiveMap layer
    private fun getGeomLayer(spec: String): GeomLayer {
        val layers = TestUtil.createSingleTileGeomLayers(parsePlotSpec(spec))
        val geomLayers = layers.filterNot(GeomLayer::isLiveMap)
        assertTrue(geomLayers.size == 1, "No layers")
        return geomLayers.single()
    }

    @Test
    fun `for map plot - should trigger even if positional mapping exist`() {
        val orangeCoord = """{\"type\": \"Point\", \"coordinates\": [1.0, 2.0]}"""
        val appleCoord = """{\"type\": \"Point\", \"coordinates\": [3.0, 4.0]}"""

        // add tooltips - just to keep these variable to check stat variables
        val pieLayer = """
            |        "geom": "pie",
            |        "data": {
            |            "fruit": ["Apple", "Apple", "Orange", "Orange"],
            |            "values": [4.0, 16.0, 6.0, 9.0],
            |            "nutrients": ["Fiber", "Carbs", "Fiber", "Carbs"]
            |        },
            |        "mapping": {
            |           "x" : "fruit",
            |           "weight" : "values", 
            |           "fill": "nutrients"
            |        },
            |        "tooltips": { "variables": ["..count..", "..proppct..", "..sum.." ] },
            |        "map": {
            |            "name": ["Orange", "Apple"],
            |            "coord": ["$orangeCoord", "$appleCoord"]
            |        },
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["fruit"], ["name"]]            
        """.trimMargin()

        // without livemap => mapping
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |           $pieLayer
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("fruit", listOf("Apple", "Orange", "Apple", "Orange"))
            .assertValues("transform.X", listOf(0.0, 1.0, 0.0, 1.0))
            .assertValues("transform.Y", listOf(0.0, 0.0, 0.0, 0.0))
            .assertValues("..count..", listOf(4.0, 6.0, 16.0, 9.0))
            .assertValues("..proppct..", listOf(20.0, 40.0, 80.0, 60.0))
            .assertValues("..sum..", listOf(20.0, 15.0, 20.0, 15.0))

        // with livemap => geodata
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |          "geom": "livemap"
            |       },
            |       {
            |           $pieLayer
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("fruit", listOf("Apple", "Orange", "Apple", "Orange"))
            .assertValues("transform.X", listOf(3.0, 1.0, 3.0, 1.0))
            .assertValues("transform.Y", listOf(4.0, 2.0, 4.0, 2.0))
            .assertValues("..count..", listOf(4.0, 6.0, 16.0, 9.0))
            .assertValues("..proppct..", listOf(20.0, 40.0, 80.0, 60.0))
            .assertValues("..sum..", listOf(20.0, 15.0, 20.0, 15.0))
    }

    @Test
    // check point coordinates
    fun `should handle geometries with positional mapping for map plot`() {
        val BOSTON_LON = -71.0884755326693
        val BOSTON_LAT = 42.3110405355692

        val pointLayer = """
            |        "geom": "point",
            |        "data": { 
            |           "Name": [ "Boston" ] 
            |         },
            |        "mapping": {
            |           "x" : "Name"
            |        },
            |        "map": {
            |            "city": ["Boston"],
            |            "geometry": [
            |               "{\"type\": \"Point\", \"coordinates\": [ $BOSTON_LON, $BOSTON_LAT]}"
            |            ]
            |        },
            |        "map_data_meta": { "geodataframe": { "geometry": "geometry" } },
            |        "map_join": [ ["Name"], ["city"] ]            
        """.trimMargin()

        // just point layer
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |           $pointLayer
            |       }
            |    ]
            |}
            """.trimMargin()
        ).let {
            assertFalse(DataFrameUtil.hasVariable(it.dataFrame, "lon"))
            assertFalse(DataFrameUtil.hasVariable(it.dataFrame, "lat"))
        }

        // add livemap
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |          "geom": "livemap"
            |       },
            |       {
            |           $pointLayer
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("lon", listOf(BOSTON_LON))
            .assertValues("lat", listOf(BOSTON_LAT))
    }

    @Test
    fun `georeference with limit and oposition`() {
        singleGeomLayer(
            """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "polygon",
            |      "map": {
            |        "id": ["148838", "1428125"],
            |        "country": ["usa", "canada"],
            |        "found name": ["United States", "Canada"],
            |        "centroid": [[-99.7426055742426, 37.2502586245537], [-110.450525298983, 56.8387750536203]],
            |        "position": [
            |          [-124.733375608921, 25.1162923872471, -66.9498561322689, 49.3844716250896],
            |          [-141.002660393715, 41.6765552759171, -55.6205673515797, 72.0015004277229]
            |        ],
            |        "limit": [
            |          [144.618412256241, -14.3740922212601, -64.564847946167, 71.3878083229065],
            |          [-141.002660393715, 41.6765552759171, -52.6194141805172, 83.1445701420307]
            |        ]
            |      },
            |      "fill": "blue",
            |      "map_data_meta": {
            |        "georeference": {}
            |      }
            |    }
            |  ]
            |}
            |""".trimMargin()
        )
        //.assertBinding(Aes.X, "price") // was not rebind to gdf
    }

    private fun GeomLayer.assertBinding(aes: Aes<*>, variable: String): GeomLayer {
        assertTrue(hasBinding(aes), "Binding for aes $aes was not found")
        assertEquals(variable, scaleMap.getValue(aes).name)
        return this
    }

    private fun GeomLayer.assertValues(variable: String, values: List<*>): GeomLayer {
        assertEquals(values, dataFrame.get(findVariableOrFail(dataFrame, variable)))
        return this
    }

    private fun GeomLayer.assertGroups(expected: Collection<*>): GeomLayer {
        val actualGroups = createLayerRendererData(this/*, Mappers.IDENTITY, Mappers.IDENTITY*/)
            .aesthetics.dataPoints().map(DataPointAesthetics::group)
        assertEquals(expected, actualGroups, "Aes values didn't match")
        return this
    }

    private fun GeomLayer.assertAes(aes: Aes<*>, expected: Collection<*>): GeomLayer {
        val actualGroups = createLayerRendererData(this/*, Mappers.IDENTITY, Mappers.IDENTITY*/)
            .aesthetics.dataPoints().map { it.get(aes) }
        assertEquals(expected, actualGroups, "Aes values didn't match")
        return this
    }
}
