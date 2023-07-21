/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.intern.spatial.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.findVariableOrFail
import org.jetbrains.letsPlot.core.spec.Option.Geom.Choropleth.GEO_POSITIONS
import org.jetbrains.letsPlot.core.spec.Option.Layer.MAP_JOIN
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toAes
import org.jetbrains.letsPlot.core.spec.Option.Meta.DATA_META
import org.jetbrains.letsPlot.core.spec.Option.Meta.GeoDataFrame.GDF
import org.jetbrains.letsPlot.core.spec.Option.Meta.GeoDataFrame.GEOMETRY
import org.jetbrains.letsPlot.core.spec.Option.Meta.GeoReference
import org.jetbrains.letsPlot.core.spec.Option.Meta.GeoReference.GEOREFERENCE
import org.jetbrains.letsPlot.core.spec.Option.Meta.MAP_DATA_META
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.config.ConfigUtil.createAesMapping
import org.jetbrains.letsPlot.core.spec.config.ConfigUtil.join
import org.jetbrains.letsPlot.core.spec.config.CoordinatesCollector.*
import org.jetbrains.letsPlot.core.spec.config.GeoConfig.Companion.GEO_ID
import org.jetbrains.letsPlot.core.spec.getList
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.has

class GeoConfig(
    geomKind: GeomKind,
    dataFrame: DataFrame,
    layerOptions: Map<*, *>,
    mappingOptions: Map<*, *>
) {
    val dataAndCoordinates: DataFrame
    val mappings: Map<Aes<*>, Variable>

    init {
        if (layerOptions.has(MAP_DATA_META, GDF) || layerOptions.has(DATA_META, GDF)) {
            GeoDataFrameProcessor(geomKind, dataFrame, layerOptions, mappingOptions).let {
                dataAndCoordinates = it.dataAndCoordinates
                mappings = it.mappings
            }
        } else if (layerOptions.has(MAP_DATA_META, GEOREFERENCE)) {
            GeoReferenceProcessor(dataFrame, layerOptions, mappingOptions).let {
                dataAndCoordinates = it.processedDataFrame
                mappings = it.processedMappings
            }
        } else {
            throw IllegalStateException()
        }
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

        fun isApplicable(layerOptions: Map<*, *>, combinedMappings: Map<*, *>, isMapPlot: Boolean): Boolean {
            if (!isMapPlot && combinedMappings.keys
                    .mapNotNull { it as? String }
                    .mapNotNull { runCatching { toAes(it) }.getOrNull() } // skip "group" or invalid names
                    .any(Aes.Companion::isPositional)
            ) {
                return false
            }

            return layerOptions.has(MAP_DATA_META, GDF, GEOMETRY) ||
                    layerOptions.has(DATA_META, GDF, GEOMETRY) ||
                    layerOptions.has(MAP_DATA_META, GEOREFERENCE) ||
                    layerOptions.has(DATA_META, GEOREFERENCE)
        }

        fun isGeoDataframe(layerOptions: Map<*, *>, gdfRole: String): Boolean {
            return layerOptions.has(toDataMetaKey(gdfRole), GDF, GEOMETRY)
        }

        fun getGeometryColumn(layerOptions: Map<*, *>, gdfRole: String): String {
            return layerOptions.getString(toDataMetaKey(gdfRole), GDF, GEOMETRY) ?: error("Geometry column not set")
        }

        private fun toDataMetaKey(gdfRole: String): String = when (gdfRole) {
            GEO_POSITIONS -> MAP_DATA_META
            DATA -> DATA_META
            else -> error("Unknown gdf role: '$gdfRole'. Expected: '$GEO_POSITIONS' or '$DATA'")
        }
    }
}

class GeoReferenceProcessor(
    dataFrame: DataFrame,
    layerOptions: Map<*, *>,
    mappingOptions: Map<*, *>
) {
    val processedDataFrame: DataFrame
    val processedMappings: Map<Aes<*>, Variable>

    init {
        val data: DataFrame

        when {
            // (aes(color='cyl'), data=data, map=geocodes) - how to join without `map_join`?
            with(layerOptions) {
                has(
                    MAP_DATA_META,
                    GEOREFERENCE
                ) && !has(MAP_JOIN) && !dataFrame.isEmpty && mappingOptions.isNotEmpty()
            } -> {
                error(GeoConfig.MAP_JOIN_REQUIRED_MESSAGE)
            }

            // (data=data, map=, map_join=('City_Name', 'city'))
            with(layerOptions) { has(MAP_DATA_META, GEOREFERENCE) && has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }

                val mapJoin = layerOptions.getList(MAP_JOIN) ?: error("require map_join parameter")
                data = join(
                    left = dataFrame,
                    leftKeyVariableNames = (mapJoin[0] as List<*>),
                    right = DataFrameUtil.fromMap(layerOptions.getMap(GEO_POSITIONS)!!),
                    rightKeyVariableNames = (mapJoin[1] as List<*>)
                )
            }

            // (map=geocodes) - simple geometry
            with(layerOptions) { has(MAP_DATA_META, GEOREFERENCE) && !has(MAP_JOIN) && dataFrame.isEmpty } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                data = DataFrameUtil.fromMap(layerOptions.getMap(GEO_POSITIONS)!!)
            }

            // (data=geocodes)
            with(layerOptions) { has(DATA_META, GEOREFERENCE) && !has(GEO_POSITIONS) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(DATA)) { "'data' parameter is mandatory with DATA_META" }
                data = dataFrame
            }

            else -> throw IllegalStateException("Unknown state")
        }


        val idVar = findVariableOrFail(data, GeoReference.Columns.ID)
        val id = data[idVar]
        val pos = data[findVariableOrFail(data, GeoReference.Columns.POSITION)]
        val lim = data[findVariableOrFail(data, GeoReference.Columns.LIMIT)]
        val cen = data[findVariableOrFail(data, GeoReference.Columns.CENTROID)]
        val mapids = IntRange(0, id.lastIndex).map { i ->
            JsonSupport.formatJson(
                mapOf(
                    "id" to id[i],
                    "pos" to pos.get(i),
                    "lim" to lim.get(i),
                    "cen" to cen.get(i)
                )
            )
        }

        processedDataFrame = data.builder()
            .remove(idVar)
            .put(idVar, mapids)
            .build()

        processedMappings =
            createAesMapping(processedDataFrame, mappingOptions + mapOf(Aes.MAP_ID.name to GeoReference.Columns.ID))
    }
}

class GeoDataFrameProcessor(
    geomKind: GeomKind,
    data: DataFrame,
    layerOptions: Map<*, *>,
    mappingOptions: Map<*, *>
) {
    val dataAndCoordinates: DataFrame
    val mappings: Map<Aes<*>, Variable>

    init {

        fun getGeoDataFrame(gdfLocation: String): DataFrame {
            val geoDataFrame: Map<String, Any> = when (gdfLocation) {
                GEO_POSITIONS -> layerOptions.getMap(GEO_POSITIONS) ?: error("require 'map' parameter")
                DATA -> layerOptions.getMap(DATA) ?: error("require 'data' parameter")
                else -> error("Unknown gdf location: $gdfLocation")
            }

            return DataFrameUtil.fromMap(geoDataFrame)
        }

        val dataFrame: DataFrame
        val geometries: Variable

        when {
            // (aes(color='cyl'), data=data, map=gdf) - how to join without `map_join`?
            with(layerOptions) {
                has(
                    MAP_DATA_META,
                    GDF,
                    GEOMETRY
                ) && !has(MAP_JOIN) && !data.isEmpty && mappingOptions.isNotEmpty()
            } -> {
                error(GeoConfig.MAP_JOIN_REQUIRED_MESSAGE)
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

                geometries = findVariableOrFail(dataFrame, GeoConfig.getGeometryColumn(layerOptions, GEO_POSITIONS))
            }

            // (map=gdf) - simple geometry
            with(layerOptions) { has(MAP_DATA_META, GDF, GEOMETRY) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(GEO_POSITIONS)) { "'map' parameter is mandatory with MAP_DATA_META" }
                dataFrame = getGeoDataFrame(gdfLocation = GEO_POSITIONS)
                geometries = findVariableOrFail(dataFrame, GeoConfig.getGeometryColumn(layerOptions, GEO_POSITIONS))
            }

            // (data=gdf)
            with(layerOptions) { has(DATA_META, GDF, GEOMETRY) && !has(GEO_POSITIONS) && !has(MAP_JOIN) } -> {
                require(layerOptions.has(DATA)) { "'data' parameter is mandatory with DATA_META" }

                dataFrame = data
                geometries = findVariableOrFail(dataFrame, GeoConfig.getGeometryColumn(layerOptions, DATA))
            }

            else -> error("GeoDataFrame not found in data or map")
        }

        val coordinatesCollector = when (geomKind) {
            MAP, POLYGON -> BoundaryCoordinatesCollector(dataFrame, geometries)
            LIVE_MAP, POINT, TEXT, LABEL, PIE -> PointCoordinatesCollector(dataFrame, geometries)
            RECT -> BboxCoordinatesCollector(dataFrame, geometries)
            PATH -> PathCoordinatesCollector(dataFrame, geometries)
            else -> error("Unsupported geom: $geomKind")
        }

        dataAndCoordinates = coordinatesCollector.buildDataFrame()
        mappings = createAesMapping(dataAndCoordinates, mappingOptions + coordinatesCollector.mappings)
    }

}

internal abstract class CoordinatesCollector(
    private val dataFrame: DataFrame,
    private val geometries: Variable,
    val mappings: Map<String, String>
) {
    private val dupCounter = mutableListOf<Int>()
    protected val coordinates: Map<String, MutableList<Any>> = mappings.values.associateBy({ it }) { mutableListOf() }
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
        dataFrame.variables()
            .forEach { variable -> builder.put(variable, duplicate(dataFrame.get(variable), dupCounter)) }
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

    class PointCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) :
        CoordinatesCollector(dataFrame, geometries, POINT_COLUMNS) {
        override val supportedFeatures = listOf("Point, MultiPoint")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPoint = { p -> coordinates.append(p) }
            onMultiPoint = { it.forEach { p -> coordinates.append(p) } }
        }
    }

    class PathCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) :
        CoordinatesCollector(dataFrame, geometries, POINT_COLUMNS) {
        override val supportedFeatures = listOf("LineString, MultiLineString")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onLineString = { it.forEach { p -> coordinates.append(p) } }
            onMultiLineString = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BoundaryCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) :
        CoordinatesCollector(dataFrame, geometries, POINT_COLUMNS) {
        override val supportedFeatures = listOf("Polygon, MultiPolygon")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            onPolygon = { it.asSequence().flatten().forEach { p -> coordinates.append(p) } }
            onMultiPolygon = { it.asSequence().flatten().flatten().forEach { p -> coordinates.append(p) } }
        }
    }

    class BboxCoordinatesCollector(dataFrame: DataFrame, geometries: Variable) :
        CoordinatesCollector(dataFrame, geometries, RECT_MAPPINGS) {
        override val supportedFeatures = listOf("MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon")
        override val geoJsonConsumer: SimpleFeature.Consumer<LonLat> = defaultConsumer {
            fun insert(bboxes: List<Rect<LonLat>>) =
                bboxes
                    .run(BBOX_CALCULATOR::union)
                    .run(::convertToGeoRectangle)
                    .run(GeoRectangle::splitByAntiMeridian)
                    .forEach { r -> coordinates.append(r) }

            fun insert(bbox: Rect<LonLat>) = insert(listOf(bbox))

            onMultiPoint = { it.bbox?.let(::insert) }
            onLineString = { it.bbox?.let(::insert) }
            onMultiLineString = { it.bbox?.let(::insert) }
            onPolygon = { it.bbox?.let(::insert) }
            onMultiPolygon = { insert(it.mapNotNull(Polygon<LonLat>::bbox)) }
        }
    }

    companion object {

        val POINT_COLUMNS = mapOf(
            Aes.X.name to GeoConfig.POINT_X,
            Aes.Y.name to GeoConfig.POINT_Y
        )

        val RECT_MAPPINGS = mapOf(
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
