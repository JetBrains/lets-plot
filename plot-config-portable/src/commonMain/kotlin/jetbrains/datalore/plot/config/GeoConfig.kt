/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.config.ConfigUtil.createAesMapping
import jetbrains.datalore.plot.config.ConfigUtil.createDataFrame
import jetbrains.datalore.plot.config.ConfigUtil.rightJoin
import jetbrains.datalore.plot.config.CoordinatesBuilder.Companion.createCoordinateBuilder
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
    val mappings: Map<Aes<*>, DataFrame.Variable>

    init {
        fun getGeoJson(gdfLocation: String): List<String> {
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
            return geoDataFrame.getList(geoColumn)?.map { it as String } ?: error("$geoColumn not found in $gdfLocation")
        }

        val joinIds: List<Any>
        val dataJoinColumn: String
        val mapJoinColumn: String
        val geoJson: List<String>
        val dataFrame: DataFrame
        val autoId = "__gdf_id__"

        when {
            // (aes(color='cyl'), data=data, map=gdf) - how to join without `map_join`?
            with(layerOptions) { has(GEO_POSITIONS) && !has(MAP_JOIN) && !data.isEmpty && mappingOptions.isNotEmpty() } -> {
                error(MAP_JOIN_REQUIRED_MESSAGE)
            }
            
            // (map=gdf) - simple geometry
            with(layerOptions) { has(GEO_POSITIONS) && !has(MAP_JOIN) && has(MAP_DATA_META, GDF, GEOMETRY) } -> {
                geoJson = getGeoJson(GEO_POSITIONS)

                dataJoinColumn = autoId
                mapJoinColumn = autoId
                joinIds = geoJson.indices.map(Int::toString)
                dataFrame = DataFrame.Builder(data).put(DataFrame.Variable(dataJoinColumn), joinIds).build()
            }

            // (data=data, map=gdf, map_join=('id', 'city'))
            with(layerOptions) { has(GEO_POSITIONS) && has(MAP_DATA_META, GDF, GEOMETRY) && has(MAP_JOIN) } -> {
                geoJson = getGeoJson(GEO_POSITIONS)

                val mapJoin = layerOptions.getList(MAP_JOIN) ?: error("require map_join parameter")
                dataJoinColumn = mapJoin[0] as String
                mapJoinColumn = mapJoin[1] as String
                joinIds = layerOptions.getMap(GEO_POSITIONS)?.getList(mapJoinColumn)?.requireNoNulls() ?: error("MapJoinColumn '$mapJoinColumn' is not found")
                dataFrame = data
            }

            // (data=gdf)
            with(layerOptions) { !has(GEO_POSITIONS) && has(DATA_META, GDF, GEOMETRY) } -> {
                geoJson = getGeoJson(DATA)

                dataJoinColumn = autoId
                mapJoinColumn = autoId
                joinIds = geoJson.indices.map(Int::toString)
                dataFrame = DataFrame.Builder(data).put(DataFrame.Variable(dataJoinColumn), joinIds).build()
            }
            else -> error("GeoDataFrame not found in data or map")
        }

        val coordinatesBuilder = createCoordinateBuilder(geomKind)
            .append(geoJson)
            .setIdColumn(columnName = mapJoinColumn, values = joinIds)

        dataAndCoordinates = rightJoin(
            left = dataFrame,
            leftKey = dataJoinColumn,
            right = createDataFrame(coordinatesBuilder.build()),
            rightKey = mapJoinColumn
        )

        val coordinatesAutoMapping = coordinatesBuilder.columns
            .filterKeys { coordName -> coordName in variables(dataAndCoordinates) }
            .map { (coordName, aes) -> aes to variables(dataAndCoordinates).getValue(coordName) }
            .toMap()
        mappings = createAesMapping(dataAndCoordinates, mappingOptions) + coordinatesAutoMapping
    }

    companion object {
        const val MAP_JOIN_REQUIRED_MESSAGE = "map_join is required when both data and map parameters used"

        fun isApplicable(layerOptions: Map<*, *>): Boolean {
            return layerOptions.has(MAP_DATA_META, GDF, GEOMETRY) ||
                    layerOptions.has(DATA_META, GDF, GEOMETRY)
        }
    }
}

const val POINT_X = "__gdf_x__"
const val POINT_Y = "__gdf_y__"
const val RECT_XMIN = "__gdf_xmin__"
const val RECT_YMIN = "__gdf_ymin__"
const val RECT_XMAX = "__gdf_xmax__"
const val RECT_YMAX = "__gdf_ymax__"

internal abstract class CoordinatesBuilder(
    val columns: Map<String, Aes<*>>
) {
    companion object {

        fun createCoordinateBuilder(geomKind: GeomKind): CoordinatesBuilder {
            return when(geomKind) {
                MAP, POLYGON -> BoundaryCoordinatesBuilder()
                POINT, TEXT -> PointCoordinatesBuilder()
                RECT -> BboxCoordinatesBuilder()
                PATH -> PathCoordinatesBuilder()
                else -> error("Unsupported geom: $geomKind")
            }
        }

        val POINT_COLUMNS = mapOf(
            POINT_X to Aes.X,
            POINT_Y to Aes.Y
        )

        val RECT_COLUMNS = mapOf(
            RECT_XMIN to Aes.XMIN,
            RECT_YMIN to Aes.YMIN,
            RECT_XMAX to Aes.XMAX,
            RECT_YMAX to Aes.YMAX
        )

        internal fun Map<String, MutableList<Any>>.append(p: Vec<LonLat>) {
            append(POINT_X, p.x)
            append(POINT_Y, p.y)
        }

        internal fun Map<String, MutableList<Any>>.append(rect: Rect<LonLat>) {
            append(RECT_XMIN, rect.left)
            append(RECT_XMAX, rect.right)
            append(RECT_YMIN, rect.top)
            append(RECT_YMAX, rect.bottom)
        }

        private fun Map<String, MutableList<Any>>.append(key: String, value: Double) {
            get(key)?.add(value) ?: error("$key is not found")
        }
    }

    private var idColumnName: String? = null
    private var ids: List<Any>? = null
    private val groupLengths = mutableListOf<Int>()
    protected val coordinates: Map<String, MutableList<Any>> = columns.keys.associateBy({ it }) { mutableListOf<Any>() }
    protected abstract val geoJsonConsumer: SimpleFeature.Consumer<LonLat>
    protected abstract val supportedFeatures: List<String>

    fun append(geoJsons: List<String>): CoordinatesBuilder {
        geoJsons.forEach {
            val oldRowCount = coordinates.rowCount
            GeoJson.parse(it, geoJsonConsumer)
            groupLengths += coordinates.rowCount - oldRowCount
        }
        return this
    }

    fun setIdColumn(columnName: String, values: List<Any>): CoordinatesBuilder {
        idColumnName = columnName
        ids = values
        return this
    }

    fun build(): Map<String, MutableList<Any>> {
        if (coordinates.rowCount == 0) {
            error("Geometries are empty or no matching types. Expected: " + supportedFeatures)
        }

        if (idColumnName == null && ids == null) {
            return coordinates
        }

        if (idColumnName != null && ids != null) {
            require(groupLengths.size == ids!!.size) { "Groups and ids should have same size" }

            // (['a', 'b'], [2, 3]) => ['a', 'a', 'b', 'b', 'b']
            fun <T> copies(values: Collection<T>, count: Collection<Int>) =
                values.asSequence().zip(count.asSequence())
                    .fold(mutableListOf<T>()) { acc, (value, count) -> repeat(count) { acc += value }; acc }

            return coordinates + (idColumnName!! to copies(ids!!, groupLengths))
        }

        error("idColumnName and idValues should be both null or not null")
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

    class PointCoordinatesBuilder : CoordinatesBuilder(POINT_COLUMNS) {
        override val supportedFeatures = listOf("Point, MultiPoint")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPoint = { p -> coordinates.append(p) }
            onMultiPoint = { it.forEach { p -> coordinates.append(p) } }
        }
    }

    class PathCoordinatesBuilder : CoordinatesBuilder(POINT_COLUMNS) {
        override val supportedFeatures = listOf("LineString, MultiLineString")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onLineString = { it.forEach { p -> coordinates.append(p) } }
            onMultiLineString = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BoundaryCoordinatesBuilder : CoordinatesBuilder(POINT_COLUMNS) {
        override val supportedFeatures = listOf("Polygon, MultiPolygon")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPolygon = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
            onMultiPolygon = { it.asSequence().flatten().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BboxCoordinatesBuilder : CoordinatesBuilder(RECT_COLUMNS) {
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
}


fun Map<*, *>.dataJoinVariable() = getList(MAP_JOIN)?.get(0) as? String