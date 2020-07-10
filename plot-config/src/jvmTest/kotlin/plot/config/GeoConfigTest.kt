/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.getOrFail
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.LayerRendererUtil.createLayerRendererData
import jetbrains.datalore.plot.config.GeoConfig.Companion.MAP_JOIN_REQUIRED_MESSAGE
import jetbrains.datalore.plot.config.GeoConfig.Companion.POINT_X
import jetbrains.datalore.plot.config.GeoConfig.Companion.POINT_Y
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_XMAX
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_XMIN
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_YMAX
import jetbrains.datalore.plot.config.GeoConfig.Companion.RECT_YMIN
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil.createPlotAssembler
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class GeoConfigTest {
    private val point = """{\"type\": \"Point\", \"coordinates\": [1.0, 2.0]}"""
    private val multiPoint = """{\"type\": \"MultiPoint\", \"coordinates\": [[3.0, 4.0], [5.0, 6.0]]}"""
    private val lineString = """{\"type\": \"LineString\", \"coordinates\": [[7.0, 8.0], [9.0, 10.0]]}"""
    private val multiLineString = """{\"type\": \"MultiLineString\", \"coordinates\": [[[11.0, 12.0], [13.0, 14.0]], [[15.0, 16.0], [17.0, 18.0]]]}"""
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


    private fun polygonGroup(groupId: Int) = (0..14).map { groupId }
    private fun multiPolygonGroup(groupId: Int) = (0..3).map { groupId }

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
        return singleGeomLayer("""
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
        `aes(color='kind'), data=gdf`(geom="point")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "kind")
    }

    @Test
    fun `geom_rect(aes(color='kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom="rect")
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
            .assertBinding(Aes.COLOR, "kind")
    }

    @Test
    fun `geom_polygon(aes(color = 'kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom="polygon")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "kind")
            .assertGroups(polygonGroup(0) + multiPolygonGroup(1))
    }

    @Test
    fun `geom_path(aes(color = 'kind'), gdf)`() {
        `aes(color='kind'), data=gdf`(geom="path")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "kind")
    }

    private fun `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom: String): GeomLayer {
        return singleGeomLayer("""
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "$geom",
            |        "data": $df,
            |        "mapping": {"color": "value"},
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": ["fig", "kind"]
            |    }]
            |}
        """.trimMargin()
        )
    }

    @Test
    fun `geom_point(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom="point")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "value")
    }

    @Test
    fun `geom_polygon(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom="polygon")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "value")
            .assertGroups(polygonGroup(0) + multiPolygonGroup(1))

    }

    @Test
    fun `geom_path(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom="path")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
            .assertBinding(Aes.COLOR, "value")
    }

    @Test
    fun `geom_rect(aes(color = 'value'), df, map = gdf, map_join=('fig', 'kind'))`() {
        `aes(color='value'), data=df, map=gdf, map_join=('fig', 'kind')`(geom="rect")
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
            .assertBinding(Aes.COLOR, "value")
    }

    private fun `map=gdf`(geom: String): GeomLayer {
        return singleGeomLayer("""
            |{
            |    "kind": "plot", 
            |    "layers": [{
            |        "geom": "$geom", 
            |        "map": $gdf,
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}}
            |    }]
            |}
        """.trimMargin())
    }

    private fun singleGeomLayer(spec: String) =
        createPlotAssembler(parsePlotSpec(spec)).layersByTile.single().single()

    @Test
    fun `geom_point(map=gdf)`() {
        `map=gdf`(geom="point")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
    }

    @Test
    fun `geom_polygon(map=gdf)`() {
        `map=gdf`(geom="polygon")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
    }

    @Test
    fun `geom_path(map=gdf)`() {
        `map=gdf`(geom="path")
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)
    }

    @Test
    fun `geom_rect(map=gdf)`() {
        `map=gdf`(geom="rect")
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
    }

    @Test
    fun `aes(color'fig'), data=df, map=gdf without map_join should fail`() {
        // don't know how to join data with map without id columns.
        assertEquals(MAP_JOIN_REQUIRED_MESSAGE, failedTransformToClientPlotConfig("""
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
    fun `should show data key missing in map`() {
        val spec = """
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
            |        "map_join": ["fig", "kind"]
            |    }]
            |}
        """.trimMargin()

        try {
            createPlotAssembler(parsePlotSpec(spec))
            fail("'Missing_Key' is missing in map - should throw exception")
        } catch (e: Exception) {
            assertEquals("'Missing_Key' not found in map", e.localizedMessage)
        }
    }

    @Test
    fun `should not fail if map has extra entries`() {
        singleGeomLayer("""
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
            |        "map_join": ["fig", "kind"]
            |    }]
            |}
        """.trimMargin()
        ).assertGroups(polygonGroup(0) + multiPolygonGroup(1))
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


        singleGeomLayer("""
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
            |        "map_join": ["continent", "cont"]
            |    }]
            |}
        """.trimMargin()
        )
            .assertBinding(Aes.XMIN, RECT_XMIN)
            .assertBinding(Aes.XMAX, RECT_XMAX)
            .assertBinding(Aes.YMIN, RECT_YMIN)
            .assertBinding(Aes.YMAX, RECT_YMAX)
            .assertGroups(listOf(0, 0, 1)) // RECTs of Germany, France, China

    }

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

    private fun GeomLayer.assertBinding(aes: Aes<*>, variable: String): GeomLayer {
        assertTrue(hasBinding(aes), "Binding for aes $aes was not found")
        assertEquals(variable, getBinding(aes).scale!!.name)
        return this
    }


    private fun GeomLayer.assertValues(variable: String, values: List<*>): GeomLayer {
        assertEquals(values, dataFrame.getOrFail(variable))
        return this
    }


    private fun GeomLayer.assertGroups(expected: Collection<*>) {
        val actualGroups = createLayerRendererData(this, emptyMap(), emptyMap())
            .aesthetics.dataPoints().map(DataPointAesthetics::group)
        assertEquals(expected, actualGroups,"Aes valeus didn't match")
    }
}
