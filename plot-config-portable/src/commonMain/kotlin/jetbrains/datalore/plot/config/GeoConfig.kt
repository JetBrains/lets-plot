/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.spatial.BBOX_CALCULATOR
import jetbrains.datalore.base.spatial.GeoJson
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.SimpleFeature
import jetbrains.datalore.base.spatial.convertToGeoRectangle
import jetbrains.datalore.base.spatial.union
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.bottom
import jetbrains.datalore.base.typedGeometry.boundingBox
import jetbrains.datalore.base.typedGeometry.left
import jetbrains.datalore.base.typedGeometry.limit
import jetbrains.datalore.base.typedGeometry.right
import jetbrains.datalore.base.typedGeometry.top
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.findVariableOrFail
import jetbrains.datalore.plot.config.ConfigUtil.createAesMapping
import jetbrains.datalore.plot.config.ConfigUtil.join
import jetbrains.datalore.plot.config.CoordinatesCollector.*
import jetbrains.datalore.plot.config.GeoConfig.Companion.GEO_ID
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Layer.MAP_JOIN
import jetbrains.datalore.plot.config.Option.Mapping.toAes
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
        fun getGeoDataFrame(gdfLocation: String): DataFrame {
            val geoDataFrame: Map<String, Any> = when(gdfLocation) {
                GEO_POSITIONS -> layerOptions.getMap(GEO_POSITIONS) ?: error("require 'map' parameter")
                DATA -> layerOptions.getMap(DATA) ?: error("require 'data' parameter")
                else -> error("Unknown gdf location: $gdfLocation")
            }

            return DataFrameUtil.fromMap(geoDataFrame)
        }

        fun getGeometryColumn(gdfLocation: String): String = when(gdfLocation) {
            GEO_POSITIONS -> layerOptions.getString(MAP_DATA_META, GDF, GEOMETRY) ?: error("Geometry column not set")
            DATA -> layerOptions.getString(DATA_META, GDF, GEOMETRY) ?: error("Geometry column not set")
            else -> error("Unknown gdf location: $gdfLocation")
        }

        val dataFrame: DataFrame
        val geometries: Variable

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
                dataFrame = join(
                    left = data,
                    leftKeyVariableNames = (mapJoin[0] as List<*>),
                    right = getGeoDataFrame(gdfLocation = GEO_POSITIONS),
                    rightKeyVariableNames = (mapJoin[1] as List<*>)
                )

                geometries = findVariableOrFail(dataFrame, getGeometryColumn(GEO_POSITIONS))
            }

            // (map=gdf) - simple geometry
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                dataFrame = getGeoDataFrame(gdfLocation = GEO_POSITIONS)
                geometries = findVariableOrFail(dataFrame, getGeometryColumn(GEO_POSITIONS))
            }

            // (data=gdf)
            with(layerOptions) { has(DATA_META, GDF, GEOMETRY) && !has(GEO_POSITIONS) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(DATA)) { "'data' parameter is mandatory with DATA_META" }

                dataFrame = data
                geometries = findVariableOrFail(dataFrame, getGeometryColumn(DATA))
            }

            else -> error("GeoDataFrame not found in data or map")
        }

        val coordinatesCollector = when(geomKind) {
            MAP, POLYGON -> BoundaryCoordinatesCollector(dataFrame, geometries)
            LIVE_MAP, POINT, TEXT -> PointCoordinatesCollector(dataFrame, geometries)
            RECT -> BboxCoordinatesCollector(dataFrame, geometries)
            PATH -> PathCoordinatesCollector(dataFrame, geometries)
            else -> error("Unsupported geom: $geomKind")
        }

        dataAndCoordinates = coordinatesCollector.buildDataFrame()
        mappings = createAesMapping(dataAndCoordinates, mappingOptions + coordinatesCollector.mappings)
    }

    companion object {
        const val GEO_ID = "__geo_id__"
        const val POINT_X = "lon"
        const val POINT_Y = "lat"
        const val RECT_XMIN = "lonmin"
        const val RECT_YMIN = "latmin"
        const val RECT_XMAX = "lonmax"
        const val RECT_YMAX = "latmax"
        const val MAP_JOIN_REQUIRED_MESSAGE = "map_join is required when both data and map parameters used"

        fun isApplicable(layerOptions: Map<*, *>, combinedMappings: Map<*, *>): Boolean {
            if (combinedMappings.keys
                    .mapNotNull { it as? String }
                    .mapNotNull { runCatching { toAes(it) }.getOrNull()} // skip "group" or invalid names
                    .any(Aes.Companion::isPositional)
            ) {
                return false
            }

            return layerOptions.has(MAP_DATA_META, GDF, GEOMETRY) ||
                    layerOptions.has(DATA_META, GDF, GEOMETRY)
        }
    }
}

internal abstract class CoordinatesCollector(
    private val dataFrame: DataFrame,
    private val geometries: Variable,
    val mappings: Map<String, String>
) {
    private val dupCounter = mutableListOf<Int>()
    protected val coordinates: Map<String, MutableList<Any>> = mappings.values.associateBy({ it }) { mutableListOf<Any>() }
    protected abstract val geoJsonConsumer: SimpleFeature.Consumer<LonLat>
    protected abstract val supportedFeatures: List<String>

    // (['a', 'b'], [2, 3]) => ['a', 'a', 'b', 'b', 'b']
    private fun <T> duplicate(values: List<T>, frequencies: Collection<Int>) =
        frequencies.mapIndexed { i, n -> MutableList(n) { values[i] } }.flatten()

    fun buildDataFrame(): DataFrame {
        for (geoJson in dataFrame.get(geometries)) {
            val oldRowCount = coordinates.rowCount
            GeoJson.parse(geoJson as String, geoJsonConsumer)
            dupCounter += coordinates.rowCount - oldRowCount
        }

        if (coordinates.rowCount == 0) {
            error("Geometries are empty or no matching types. Expected: " + supportedFeatures)
        }

        val builder = DataFrame.Builder()
        dataFrame.variables().forEach { variable -> builder.put(variable, duplicate(dataFrame.get(variable), dupCounter)) }
        coordinates.entries.forEach { (name, values) -> builder.put(Variable(name), values) }

        builder.put(Variable(GEO_ID), duplicate((0 until dataFrame.rowCount()).toList(), dupCounter))
        builder.remove(geometries)

        return builder.build()
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

    class PointCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) : CoordinatesCollector(dataFrame, geometries, POINT_COLUMNS) {
        override val supportedFeatures = listOf("Point, MultiPoint")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPoint = { p -> coordinates.append(p) }
            onMultiPoint = { it.forEach { p -> coordinates.append(p) } }
        }
    }

    class PathCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) : CoordinatesCollector(dataFrame, geometries, POINT_COLUMNS) {
        override val supportedFeatures = listOf("LineString, MultiLineString")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onLineString = { it.forEach { p -> coordinates.append(p) } }
            onMultiLineString = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BoundaryCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) : CoordinatesCollector(dataFrame, geometries, POINT_COLUMNS) {
        override val supportedFeatures = listOf("Polygon, MultiPolygon")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPolygon = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
            onMultiPolygon = { it.asSequence().flatten().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BboxCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) : CoordinatesCollector(dataFrame, geometries, RECT_MAPPINGS) {
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

        val POINT_COLUMNS = mapOf<String, String>(
            Aes.X.name to GeoConfig.POINT_X,
            Aes.Y.name to GeoConfig.POINT_Y
        )

        val RECT_MAPPINGS = mapOf<String, String>(
            Aes.XMIN.name to GeoConfig.RECT_XMIN,
            Aes.YMIN.name to GeoConfig.RECT_YMIN,
            Aes.XMAX.name to GeoConfig.RECT_XMAX,
            Aes.YMAX.name to GeoConfig.RECT_YMAX
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
