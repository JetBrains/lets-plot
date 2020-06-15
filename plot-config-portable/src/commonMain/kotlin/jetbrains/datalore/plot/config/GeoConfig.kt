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
import jetbrains.datalore.plot.base.data.DataFrameUtil.findVariableOrFail
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.config.ConfigUtil.createAesMapping
import jetbrains.datalore.plot.config.ConfigUtil.createDataFrame
import jetbrains.datalore.plot.config.ConfigUtil.rightJoin
import jetbrains.datalore.plot.config.CoordinatesCollector.*
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

        val mapKeys: List<Any>
        val dataKeyColumn: String
        val mapKeyColumn: String
        val geoJson: List<String>
        val dataFrame: DataFrame
        val autoId = "__id__"

        when {
            // (aes(color='cyl'), data=data, map=gdf) - how to join without `map_join`?
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && !has(MAP_JOIN) && !data.isEmpty && mappingOptions.isNotEmpty() } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                error(MAP_JOIN_REQUIRED_MESSAGE)
            }

            // (data=data, map=gdf, map_join=('id', 'city'))
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                geoJson = getGeoJson(GEO_POSITIONS)

                val mapJoin = layerOptions.getList(MAP_JOIN) ?: error("require map_join parameter")
                dataKeyColumn = mapJoin[0] as String
                mapKeyColumn = mapJoin[1] as String
                mapKeys = layerOptions.getMap(GEO_POSITIONS)?.getList(mapKeyColumn)?.requireNoNulls() ?: error("'$mapKeyColumn' is not found in map")

                // All data keys should be present in map
                data.get(findVariableOrFail(data, dataKeyColumn))
                    .firstOrNull { dataKey -> dataKey !in mapKeys }
                    ?.let { error("'$it' not found in map") }

                dataFrame = data
            }

            // (map=gdf) - simple geometry
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                geoJson = getGeoJson(GEO_POSITIONS)

                dataKeyColumn = autoId
                mapKeyColumn = autoId
                mapKeys = geoJson.indices.map(Int::toString)
                dataFrame = DataFrame.Builder(data).put(DataFrame.Variable(dataKeyColumn), mapKeys).build()
            }

            // (data=gdf)
            with(layerOptions) { has(DATA_META, GDF, GEOMETRY) && !has(GEO_POSITIONS) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(DATA)) { "'data' parameter is mandatory with DATA_META" }
                geoJson = getGeoJson(DATA)

                dataKeyColumn = autoId
                mapKeyColumn = autoId
                mapKeys = geoJson.indices.map(Int::toString)
                dataFrame = DataFrame.Builder(data).put(DataFrame.Variable(dataKeyColumn), mapKeys).build()
            }

            else -> error("GeoDataFrame not found in data or map")
        }

        val coordinatesCollector = when(geomKind) {
            MAP, POLYGON -> BoundaryCoordinatesCollector()
            POINT, TEXT -> PointCoordinatesCollector()
            RECT -> BboxCoordinatesCollector()
            PATH -> PathCoordinatesCollector()
            else -> error("Unsupported geom: $geomKind")
        }

        coordinatesCollector
            .append(geoJson)
            .setIdColumn(columnName = mapKeyColumn, values = mapKeys)

        dataAndCoordinates = rightJoin(
            left = dataFrame,
            leftKey = dataKeyColumn,
            right = createDataFrame(coordinatesCollector.buildCoordinatesMap()),
            rightKey = mapKeyColumn
        )

        val coordinatesAutoMapping = coordinatesCollector.mappings
            .filterValues { coordName -> coordName in variables(dataAndCoordinates) }
            .map { (aes, coordName) -> aes to variables(dataAndCoordinates).getValue(coordName) }
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

const val POINT_X = "__x__"
const val POINT_Y = "__y__"
const val RECT_XMIN = "__xmin__"
const val RECT_YMIN = "__ymin__"
const val RECT_XMAX = "__xmax__"
const val RECT_YMAX = "__ymax__"

internal abstract class CoordinatesCollector(
    val mappings: Map<Aes<*>, String>
) {
    private lateinit var idColumnName: String
    private lateinit var ids: List<Any>
    private val groupLengths = mutableListOf<Int>()
    protected val coordinates: Map<String, MutableList<Any>> = mappings.values.associateBy({ it }) { mutableListOf<Any>() }
    protected abstract val geoJsonConsumer: SimpleFeature.Consumer<LonLat>
    protected abstract val supportedFeatures: List<String>

    fun append(geoJsons: List<String>): CoordinatesCollector {
        geoJsons.forEach {
            val oldRowCount = coordinates.rowCount
            GeoJson.parse(it, geoJsonConsumer)
            groupLengths += coordinates.rowCount - oldRowCount
        }

        if (coordinates.rowCount == 0) {
            error("Geometries are empty or no matching types. Expected: " + supportedFeatures)
        }

        return this
    }

    fun setIdColumn(columnName: String, values: List<Any>): CoordinatesCollector {
        idColumnName = columnName
        ids = values
        return this
    }

    fun buildCoordinatesMap(): Map<String, MutableList<Any>> {
        require(groupLengths.size == ids.size) { "Groups and ids should have same size" }

        // (['a', 'b'], [2, 3]) => ['a', 'a', 'b', 'b', 'b']
        fun <T> copies(values: Collection<T>, count: Collection<Int>) =
            values.asSequence().zip(count.asSequence())
                .fold(mutableListOf<T>()) { acc, (value, count) -> repeat(count) { acc += value }; acc }

        return coordinates + (idColumnName to copies(ids, groupLengths))
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
            Aes.X to POINT_X,
            Aes.Y to POINT_Y
        )

        val RECT_MAPPINGS = mapOf<Aes<*>, String>(
            Aes.XMIN to RECT_XMIN,
            Aes.YMIN to RECT_YMIN,
            Aes.XMAX to RECT_XMAX,
            Aes.YMAX to RECT_YMAX
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
}


fun Map<*, *>.dataJoinVariable() = getList(MAP_JOIN)?.get(0) as? String