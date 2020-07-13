/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.data.DataFrameUtil.findVariableOrFail
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.config.ConfigUtil.createAesMapping
import jetbrains.datalore.plot.config.ConfigUtil.createDataFrame
import jetbrains.datalore.plot.config.ConfigUtil.rightJoin
import jetbrains.datalore.plot.config.CoordinatesCollector.*
import jetbrains.datalore.plot.config.GeoConfig.Companion.GEO_ID
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Layer.MAP_JOIN
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GDF
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.PlotBase.DATA

class GeoConfig(
    geomKind: GeomKind,
    data: DataFrame,
    layerOptions: Map<*, *>,
    mappingOptions: Map<*, *>
) {
    val dataAndCoordinates: DataFrame
    val mappings: Map<Aes<*>, Variable>

    init {
        fun getGeoData(gdfLocation: String, keys: List<Any>?): List<Pair<Any, String>> {
            val geoColumn: String
            val geoDataFrame: Map<String, Any>
            when(gdfLocation) {
                GEO_POSITIONS -> {
                    geoDataFrame = layerOptions.getMap(GEO_POSITIONS) ?: error("require 'map' parameter")
                    geoColumn = layerOptions.getString(MAP_DATA_META, GDF, GEOMETRY) ?: error("Geometry column not set")
                }
                DATA -> {
                    geoDataFrame = layerOptions.getMap(DATA) ?: error("require 'data' parameter")
                    geoColumn = layerOptions.getString(DATA_META, GDF, GEOMETRY) ?: error("Geometry column not set")
                }
                else -> error("Unknown gdf location: $gdfLocation")
            }

            // If no keys provided use indicies
            val ids = keys ?: geoDataFrame.indicies?.map(Int::toString) ?: emptyList<String>()
            val geoJsons = geoDataFrame.getList(geoColumn)?.map { it as String } ?: error("$geoColumn not found in $gdfLocation")

            return ids.zip(geoJsons)
        }

        fun appendGeoId(
            data: DataFrame,
            geoData: List<Pair<Any, String>>,
            dataKeyColumn: String
        ): DataFrame {
            return DataFrame.Builder(data).put(Variable(dataKeyColumn), geoData.map { (key, _) -> key }).build()
        }

        val dataKeyColumn: String
        val geoData: List<Pair<Any, String>>
        val dataFrame: DataFrame

        when {
            // (aes(color='cyl'), data=data, map=gdf) - how to join without `map_join`?
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && !has(MAP_JOIN) && !data.isEmpty && mappingOptions.isNotEmpty() } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                error(MAP_JOIN_REQUIRED_MESSAGE)
            }

            // (data=data, map=gdf, map_join=('id', 'city'))
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }

                val mapJoin = layerOptions.getList(MAP_JOIN) ?: error("require map_join parameter")
                val geoKeyColumn = mapJoin[1] as String
                val mapKeys = layerOptions
                    .getMap(GEO_POSITIONS)
                    ?.getList(geoKeyColumn)
                    ?.requireNoNulls()
                    ?: error("'$geoKeyColumn' is not found in map")
                geoData = getGeoData(gdfLocation = GEO_POSITIONS, keys = mapKeys)

                dataFrame = data
                dataKeyColumn = mapJoin[0] as String
            }

            // (map=gdf) - simple geometry
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                geoData = getGeoData(gdfLocation = GEO_POSITIONS, keys = null)

                dataKeyColumn = GEO_ID
                dataFrame = appendGeoId(data, geoData, dataKeyColumn)
            }

            // (data=gdf)
            with(layerOptions) { has(DATA_META, GDF, GEOMETRY) && !has(GEO_POSITIONS) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(DATA)) { "'data' parameter is mandatory with DATA_META" }
                geoData = getGeoData(gdfLocation = DATA, keys = null)

                dataKeyColumn = GEO_ID
                dataFrame = appendGeoId(data, geoData, dataKeyColumn)
            }

            else -> error("GeoDataFrame not found in data or map")
        }

        val coordinatesCollector = when(geomKind) {
            MAP, POLYGON -> BoundaryCoordinatesCollector()
            LIVE_MAP, POINT, TEXT -> PointCoordinatesCollector()
            RECT -> BboxCoordinatesCollector()
            PATH -> PathCoordinatesCollector()
            else -> error("Unsupported geom: $geomKind")
        }

        val geoFrame = coordinatesCollector
            .append(geoData)
            .buildCoordinatesMap()
            .let(::createDataFrame)

        dataAndCoordinates = rightJoin(
            left = dataFrame,
            leftKey = dataKeyColumn,
            right = geoFrame,
            rightKey = GEO_ID
        )

        val coordinatesAutoMapping = coordinatesCollector.mappings
            .filterValues { coordName -> coordName in variables(dataAndCoordinates) }
            .map { (aes, coordName) -> aes to variables(dataAndCoordinates).getValue(coordName) }
            .toMap()
        mappings = createAesMapping(dataAndCoordinates, mappingOptions) + coordinatesAutoMapping
    }

    companion object {
        const val GEO_ID = "__geo_id__"
        const val POINT_X = "__x__"
        const val POINT_Y = "__y__"
        const val RECT_XMIN = "__xmin__"
        const val RECT_YMIN = "__ymin__"
        const val RECT_XMAX = "__xmax__"
        const val RECT_YMAX = "__ymax__"
        const val MAP_JOIN_REQUIRED_MESSAGE = "map_join is required when both data and map parameters used"

        fun isApplicable(layerOptions: Map<*, *>): Boolean {
            return layerOptions.has(MAP_DATA_META, GDF, GEOMETRY) ||
                    layerOptions.has(DATA_META, GDF, GEOMETRY)
        }
    }
}

internal abstract class CoordinatesCollector(
    val mappings: Map<Aes<*>, String>
) {
    private val groupKeys = mutableListOf<Any>()
    private val groupLengths = mutableListOf<Int>()
    protected val coordinates: Map<String, MutableList<Any>> = mappings.values.associateBy({ it }) { mutableListOf<Any>() }
    protected abstract val geoJsonConsumer: SimpleFeature.Consumer<LonLat>
    protected abstract val supportedFeatures: List<String>

    fun append(geoData: List<Pair<Any, String>>): CoordinatesCollector {
        geoData.forEach { (key, geoJson) ->
            val oldRowCount = coordinates.rowCount
            GeoJson.parse(geoJson, geoJsonConsumer)
            groupLengths += coordinates.rowCount - oldRowCount
            groupKeys += key
        }

        if (coordinates.rowCount == 0) {
            error("Geometries are empty or no matching types. Expected: " + supportedFeatures)
        }

        return this
    }

    fun buildCoordinatesMap(): Map<String, MutableList<Any>> {
        require(groupLengths.size == groupKeys.size) { "Groups and ids should have same size" }

        // (['a', 'b'], [2, 3]) => ['a', 'a', 'b', 'b', 'b']
        fun <T> copies(values: Collection<T>, count: Collection<Int>) =
            values.asSequence().zip(count.asSequence())
                .fold(mutableListOf<T>()) { acc, (value, count) -> repeat(count) { acc += value }; acc }

        return coordinates + (GEO_ID to copies(groupKeys, groupLengths))
    }

    internal fun defaultConsumer(config: SimpleFeature.Consumer<LonLat>.() -> Unit) =
        SimpleFeature.Consumer<LonLat>(
            onPoint = {},
            onMultiPoint = {},
            onLineString = {},
            onMultiLineString = {},
            onPolygon = {},
            onMultiPolygon = {}
        ).apply(config)

    private val <K, V : List<Any>> Map<K, V>.rowCount get() = values.firstOrNull()?.size ?: 0

    class PointCoordinatesCollector : CoordinatesCollector(POINT_COLUMNS) {
        override val supportedFeatures = listOf("Point, MultiPoint")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPoint = { p -> coordinates.append(p) }
            onMultiPoint = { it.forEach { p -> coordinates.append(p) } }
        }
    }

    class PathCoordinatesCollector : CoordinatesCollector(POINT_COLUMNS) {
        override val supportedFeatures = listOf("LineString, MultiLineString")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onLineString = { it.forEach { p -> coordinates.append(p) } }
            onMultiLineString = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BoundaryCoordinatesCollector : CoordinatesCollector(POINT_COLUMNS) {
        override val supportedFeatures = listOf("Polygon, MultiPolygon")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPolygon = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
            onMultiPolygon = { it.asSequence().flatten().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BboxCoordinatesCollector : CoordinatesCollector(RECT_MAPPINGS) {
        override val supportedFeatures = listOf("MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            fun insert(bboxes: List<Rect<LonLat>>) =
                bboxes
                    .run(BBOX_CALCULATOR::union)
                    .run(::convertToGeoRectangle)
                    .run(GeoRectangle::splitByAntiMeridian)
                    .forEach{ r -> coordinates.append(r) }

            fun insert(bbox: Rect<LonLat>) = insert(listOf(bbox))

            onMultiPoint = { insert(it.boundingBox()) }
            onLineString = { insert(it.boundingBox()) }
            onMultiLineString = { insert(it.flatten().boundingBox()) }
            onPolygon = { insert(it.limit()) }
            onMultiPolygon = { insert(it.limit()) }
        }
    }

    companion object {

        val POINT_COLUMNS = mapOf<Aes<*>, String>(
            Aes.X to GeoConfig.POINT_X,
            Aes.Y to GeoConfig.POINT_Y
        )

        val RECT_MAPPINGS = mapOf<Aes<*>, String>(
            Aes.XMIN to GeoConfig.RECT_XMIN,
            Aes.YMIN to GeoConfig.RECT_YMIN,
            Aes.XMAX to GeoConfig.RECT_XMAX,
            Aes.YMAX to GeoConfig.RECT_YMAX
        )

        internal fun Map<String, MutableList<Any>>.append(p: Vec<LonLat>) {
            append(GeoConfig.POINT_X, p.x)
            append(GeoConfig.POINT_Y, p.y)
        }

        internal fun Map<String, MutableList<Any>>.append(rect: Rect<LonLat>) {
            append(GeoConfig.RECT_XMIN, rect.left)
            append(GeoConfig.RECT_XMAX, rect.right)
            append(GeoConfig.RECT_YMIN, rect.top)
            append(GeoConfig.RECT_YMAX, rect.bottom)
        }

        private fun Map<String, MutableList<Any>>.append(key: String, value: Double) {
            get(key)?.add(value) ?: error("$key is not found")
        }
    }
}


fun Map<*, *>.dataJoinVariable() = getList(MAP_JOIN)?.get(0) as? String
private fun DataFrame.getOrFail(varName: String) = this.get(findVariableOrFail(this, varName))
private val <K, V> Map<K, V>.indicies get() = (values.firstOrNull() as? List<*>)?.indices
