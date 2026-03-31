/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder
import demoAndTestShared.parsePlotSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.findVariableOrFail
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.LayerRendererUtil.createLayerRendererData
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.MAP_JOIN_REQUIRED_MESSAGE
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.POINT_X
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.POINT_Y
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.RECT_XMAX
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.RECT_XMIN
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.RECT_YMAX
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.RECT_YMIN
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil.createPlotGeomTiles
import org.jetbrains.letsPlot.core.spec.getList
import org.jetbrains.letsPlot.core.spec.has
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoConfigTest {

    private fun <T> polygonSequence(groupId: T) = (0..14).map { groupId }
    private fun <T> multiPolygonSequence(groupId: T) = (0..3).map { groupId }

    private val gdf = """
        |{
        |    "kind": ["Point", "MPoint", "Line", "MLine", "Polygon", "MPolygon"],
        |    "coord": [
        |        "{\"type\": \"Point\", \"coordinates\": [1.0, 2.0]}", 
        |        "{\"type\": \"MultiPoint\", \"coordinates\": [[3.0, 4.0], [5.0, 6.0]]}", 
        |        "{\"type\": \"LineString\", \"coordinates\": [[7.0, 8.0], [9.0, 10.0]]}", 
        |        "{\"type\": \"MultiLineString\", \"coordinates\": [[[11.0, 12.0], [13.0, 14.0]], [[15.0, 16.0], [17.0, 18.0]]]}", 
        |        "{ \"type\": \"Polygon\", \"coordinates\":[ [ [21.0, 21.0],  [21.0, 29.0],  [29.0, 29.0],  [29.0, 21.0],  [21.0, 21.0] ], [ [22.0, 22.0],  [23.0, 22.0],  [23.0, 23.0],  [22.0, 23.0],  [22.0, 22.0] ], [ [24.0, 24.0],  [26.0, 24.0],  [26.0, 26.0],  [24.0, 26.0],  [24.0, 24.0] ] ] }", 
        |        "{ \"type\": \"MultiPolygon\", \"coordinates\": [ [ [ [11.0, 12.0],  [13.0, 14.0],  [15.0, 13.0],  [11.0, 12.0] ] ] ] }"
        |    ],
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
            .assertAes(Aes.COLOR, polygonSequence(Color(228, 26, 28)) + multiPolygonSequence(Color(55, 126, 184)))
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
        val plotSpec = MonolithicCommon.processRawSpecs(parsePlotSpec(spec))
        val frontendConfig = PlotConfigFrontend.create(plotSpec) { _ -> }
        return createPlotGeomTiles(frontendConfig).coreLayersByTile().single().single()
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
        val spec = """
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

        val plotSpec = MonolithicCommon.processRawSpecs(parsePlotSpec(spec))
        val e = runCatching { PlotConfigFrontend.create(plotSpec) { } }.exceptionOrNull()

        assertThat(e).hasMessage(MAP_JOIN_REQUIRED_MESSAGE)
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
        val europe = Color(228, 26, 28)
        val asia = Color(55, 126, 184)
        singleGeomLayer(
            """
            |{
            |  "kind": "plot", 
            |  "layers": [{
            |    "geom": "rect",
            |    "data": {
            |      "continent": ["Europe", "Asia"],
            |      "temp": [8.6, 16.6]
            |    },
            |    "mapping": {"fill": "continent"},
            |    "map": {
            |      "country": ["Germany", "France", "China"],
            |      "cont": ["Europe", "Europe", "Asia"],
            |      "coord": [
            |        "{ \"type\": \"Polygon\", \"coordinates\": [ [ [1.0, 2.0], [3.0, 4.0], [5.0, 3.0], [1.0, 2.0] ] ] }", 
            |        "{ \"type\": \"Polygon\", \"coordinates\": [ [ [21.0, 22.0], [23.0, 24.0], [25.0, 23.0], [21.0, 22.0] ] ] }", 
            |        "{ \"type\": \"Polygon\", \"coordinates\": [ [ [31.0, 32.0], [33.0, 34.0], [35.0, 33.0], [31.0, 32.0] ] ] }"
            |      ]
            |    },
            |    "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |    "map_join": [["continent"], ["cont"]]
            |  }]
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
            .assertValues("lon", listOf(1.0, 3.0, 5.0))
            .assertValues("lat", listOf(2.0, 4.0, 6.0))
            .assertAes(Aes.COLOR, listOf(Color(228, 26, 28), Color(55, 126, 184), Color(77, 175, 74, 255)))
    }

    @Test
    fun `gdf in data without map_join should not override positional mapping with coord column`() {
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

    @Test
    fun `polygon should be merged on client side`() {
        val plotSpec = parsePlotSpec(
            """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "polygon",
            |      "data": {
            |        "key": [ "A", "B", "C", "A", "B", "C" ],
            |        "kind": [ "Point", "MPoint", "Line", "MLine", "Polygon", "MPolygon" ],
            |        "coord": [
            |          "{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}",
            |          "{\"type\": \"MultiPoint\", \"coordinates\": [[3.0, 15.0], [6.0, 13.0]]}",
            |          "{\"type\": \"LineString\", \"coordinates\": [[0.0, 0.0], [5.0, 5.0]]}",
            |          "{\"type\": \"MultiLineString\", \"coordinates\": [[[10.0, 0.0], [15.0, 5.0]], [[10.0, 5.0], [15.0, 0.0]]]}",
            |          "{\"type\": \"Polygon\", \"coordinates\": [[[1.0, 1.0], [1.0, 9.0], [9.0, 9.0], [9.0, 1.0], [1.0, 1.0]], [[2.0, 2.0], [3.0, 2.0], [3.0, 3.0], [2.0, 3.0], [2.0, 2.0]], [[4.0, 4.0], [6.0, 4.0], [6.0, 6.0], [4.0, 6.0], [4.0, 4.0]]]}",
            |          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [7.0, 4.0], [11.0, 12.0]]], [[[10.0, 2.0], [13.0, 10.0], [12.0, 3.0], [10.0, 2.0]]]]}"
            |        ]
            |      },
            |      "mapping": { "fill": "kind" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "str", "column": "key" },
            |          { "type": "str", "column": "kind" },
            |          { "type": "unknown(pandas:unknown-array)", "column": "coord" }
            |        ],
            |        "geodataframe": { "geometry": "coord" }
            |      }
            |    }
            |  ]
            |}
            |""".trimMargin()
        )
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec)

        // geo data was not replaced on a frontend
        assertThat(processedSpec.has("layers", 0, "data", POINT_X)).isFalse
        assertThat(processedSpec.has("layers", 0, "data", POINT_Y)).isFalse
        assertThat(processedSpec.getList("layers", 0, "data", "coord")).hasSize(6)

        val plotConfig = PlotConfigFrontend.create(processedSpec){}
        val layerData = plotConfig.layerConfigs.single().combinedData

        val x = findVariableOrFail(layerData, POINT_X)
        val y = findVariableOrFail(layerData, POINT_Y)

        assertThat(layerData[x]).hasSize(24) // total points in all geometries
        assertThat(layerData[y]).hasSize(24)
    }

    @Test
    fun `point should be merged on server side`() {
        val plotSpec = parsePlotSpec(
            """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "data": {
            |        "key": [ "A", "B", "C", "A", "B", "C" ],
            |        "kind": [ "Point", "MPoint", "Line", "MLine", "Polygon", "MPolygon" ],
            |        "coord": [
            |          "{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}",
            |          "{\"type\": \"MultiPoint\", \"coordinates\": [[3.0, 15.0], [6.0, 13.0]]}",
            |          "{\"type\": \"LineString\", \"coordinates\": [[0.0, 0.0], [5.0, 5.0]]}",
            |          "{\"type\": \"MultiLineString\", \"coordinates\": [[[10.0, 0.0], [15.0, 5.0]], [[10.0, 5.0], [15.0, 0.0]]]}",
            |          "{\"type\": \"Polygon\", \"coordinates\": [[[1.0, 1.0], [1.0, 9.0], [9.0, 9.0], [9.0, 1.0], [1.0, 1.0]], [[2.0, 2.0], [3.0, 2.0], [3.0, 3.0], [2.0, 3.0], [2.0, 2.0]], [[4.0, 4.0], [6.0, 4.0], [6.0, 6.0], [4.0, 6.0], [4.0, 4.0]]]}",
            |          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [7.0, 4.0], [11.0, 12.0]]], [[[10.0, 2.0], [13.0, 10.0], [12.0, 3.0], [10.0, 2.0]]]]}"
            |        ]
            |      },
            |      "mapping": { "fill": "kind" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "str", "column": "key" },
            |          { "type": "str", "column": "kind" },
            |          { "type": "unknown(pandas:unknown-array)", "column": "coord" }
            |        ],
            |        "geodataframe": { "geometry": "coord" }
            |      }
            |    }
            |  ]
            |}
            |""".trimMargin()
        )
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec)

        // geo data for point was replaced on a backend
        assertThat(processedSpec.has("layers", 0, "data", POINT_X)).isTrue
        assertThat(processedSpec.has("layers", 0, "data", POINT_Y)).isTrue
        assertThat(processedSpec.getList("layers", 0, "data", POINT_X)).containsExactly(-5.0, 3.0, 6.0)
        assertThat(processedSpec.getList("layers", 0, "data", POINT_Y)).containsExactly(17.0, 15.0, 13.0)

        val plotConfig = PlotConfigFrontend.create(processedSpec){}
        val layerData = plotConfig.layerConfigs.single().combinedData

        val x = findVariableOrFail(layerData, POINT_X)
        val y = findVariableOrFail(layerData, POINT_Y)

        assertThat(layerData[x]).containsExactly(-5.0, 3.0, 6.0)
        assertThat(layerData[y]).containsExactly(17.0, 15.0, 13.0)
    }

    // excluding LiveMap layer
    private fun getGeomLayer(spec: String): GeomLayer {
        val layers = TestingGeomLayersBuilder.createSingleTileGeomLayers(parsePlotSpec(spec))
        val geomLayers = layers.filterNot(GeomLayer::isLiveMap)
        assertTrue(geomLayers.size == 1, "No layers")
        return geomLayers.single()
    }

    @Test
    fun `for map plot - should trigger even if positional mapping exist`() {
        // add tooltips - just to keep these variables to check stat variables

        // without livemap => mapping
        val plotSpec = """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |         "geom": "pie",
            |         "data": {
            |             "fruit": ["Apple", "Apple", "Orange", "Orange"],
            |             "values": [4.0, 16.0, 6.0, 9.0],
            |             "nutrients": ["Fiber", "Carbs", "Fiber", "Carbs"]
            |         },
            |         "mapping": {
            |            "x" : "fruit",
            |            "weight" : "values",
            |            "fill": "nutrients",
            |            "group": ["fruit", "nutrients"]
            |         },
            |         "tooltips": { "variables": ["..count..", "..proppct..", "..sum.." ] },
            |         "map": {
            |             "name": ["Orange", "Apple"],
            |             "coord": [
            |                "{\"type\": \"Point\", \"coordinates\": [11.0, 22.0]}",
            |                "{\"type\": \"Point\", \"coordinates\": [33.0, 44.0]}"
            |            ]
            |         },
            |         "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |         "map_join": [["fruit"], ["name"]]                   
            |       }
            |    ]
            |}
            """.trimMargin()

        getGeomLayer(plotSpec)
            .assertValues("fruit", listOf("Apple", "Apple", "Orange", "Orange"))
            .assertValues("transform.X", listOf(33.0, 33.0, 11.0, 11.0))
            .assertValues("transform.Y", listOf(44.0, 44.0, 22.0, 22.0))
            .assertValues("..count..", listOf(4.0, 16.0, 6.0, 9.0))
            .assertValues("..proppct..", listOf(20.0, 80.0, 40.0, 60.0))
            .assertValues("..sum..", listOf(20.0, 20.0, 15.0, 15.0))

        // with livemap => geodata
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |          "geom": "livemap",
            |          "tiles": {
            |            "kind": "vector_lets_plot",
            |            "url": "wss://tiles.datalore.jetbrains.com",
            |            "url": "wss://tiles.datalore.jetbrains.com",
            |            "theme": null,
            |            "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
            |          }
            |       },
            |       {
            |         "geom": "pie",
            |         "data": {
            |             "fruit": ["Apple", "Apple", "Orange", "Orange"],
            |             "values": [4.0, 16.0, 6.0, 9.0],
            |             "nutrients": ["Fiber", "Carbs", "Fiber", "Carbs"]
            |         },
            |         "mapping": {
            |            "x" : "fruit",
            |            "weight" : "values",
            |            "fill": "nutrients"
            |         },
            |         "tooltips": { "variables": ["..count..", "..proppct..", "..sum.." ] },
            |         "map": {
            |             "name": ["Orange", "Apple"],
            |             "coord": [
            |                "{\"type\": \"Point\", \"coordinates\": [11.0, 22.0]}",
            |                "{\"type\": \"Point\", \"coordinates\": [33.0, 44.0]}"
            |            ]
            |         },
            |         "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |         "map_join": [["fruit"], ["name"]]
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("transform.X", listOf(33.0, 11.0, 33.0, 11.0))
            .assertValues("transform.Y", listOf(44.0, 22.0, 44.0, 22.0))
            .assertValues("..count..", listOf(4.0, 6.0, 16.0, 9.0))
            .assertValues("..proppct..", listOf(20.0, 40.0, 80.0, 60.0))
            .assertValues("..sum..", listOf(20.0, 15.0, 20.0, 15.0))
    }

    @Test
    // check point coordinates
    fun `should handle geometries with positional mapping for map plot`() {

        // just point layer
        getGeomLayer(
            """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "data": { "Name": [ "Boston" ] },
            |      "mapping": { "x" : "Name" },
            |      "map": {
            |        "city": ["Boston"],
            |        "geometry": [ "{\"type\": \"Point\", \"coordinates\": [ -71.0884755326693, 42.3110405355692]}" ]
            |      },
            |      "map_data_meta": { "geodataframe": { "geometry": "geometry" } },
            |      "map_join": [ ["Name"], ["city"] ]            
            |    }
            |  ]
            |}
            """.trimMargin()
        )
            .assertValues("lon", listOf(-71.0884755326693))
            .assertValues("lat", listOf(42.3110405355692))

        // add livemap
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |          "geom": "livemap",
            |           "tiles": {
            |            "kind": "vector_lets_plot",
            |            "url": "wss://tiles.datalore.jetbrains.com",
            |            "url": "wss://tiles.datalore.jetbrains.com",
            |            "theme": null,
            |            "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
            |          }
            |       },
            |       {
            |         "geom": "point",
            |         "data": { "Name": [ "Boston" ] },
            |         "mapping": { "x" : "Name" },
            |         "map": { "city": ["Boston"], "geometry": [ "{\"type\": \"Point\", \"coordinates\": [ -71.0884755326693, 42.3110405355692]}" ] },
            |         "map_data_meta": { "geodataframe": { "geometry": "geometry" } },
            |         "map_join": [ ["Name"], ["city"] ]
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("lon", listOf(-71.0884755326693))
            .assertValues("lat", listOf(42.3110405355692))
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
