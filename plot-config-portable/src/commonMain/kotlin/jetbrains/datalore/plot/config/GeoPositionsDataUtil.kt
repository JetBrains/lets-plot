/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.map.GeoPositionField
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GeoDataKind.*
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Meta.MapJoin

object GeoPositionsDataUtil {
    // Provided by regions object
    const val MAP_REGION_COLUMN = "region"

    const val MAP_OSM_ID_COLUMN = "__geoid__"

    val GEOMS_SUPPORT = mapOf(
            GeomKind.MAP to GeoDataSupport(BOUNDARY, ::createPointMapping),
            GeomKind.POLYGON to GeoDataSupport(BOUNDARY, ::createPointMapping),
            GeomKind.POINT to GeoDataSupport(POINT, ::createPointMapping),
            GeomKind.RECT to GeoDataSupport(BBOX, ::createRectMapping),
            GeomKind.PATH to GeoDataSupport(PATH, ::createPointMapping),
            GeomKind.TEXT to GeoDataSupport(POINT, ::createPointMapping)
    )

    fun isGeomSupported(geomKind: GeomKind): Boolean {
        return geomKind in GEOMS_SUPPORT
    }

    fun getGeoDataKind(geomKind: GeomKind): GeoDataKind {
        return GEOMS_SUPPORT[geomKind]!!.geoDataKind
    }

    internal fun getGeoPositionsData(layerConfig: LayerConfig): DataFrame {
        return ConfigUtil.createDataFrame(layerConfig.getMap(GEO_POSITIONS))
    }

    internal fun initDataAndMappingForGeoPositions(
        geomKind: GeomKind,
        layerData: DataFrame,
        mapOptions: DataFrame,
        mappingOptions: Map<*, *>,
        leftMapId: String,
        rightMapId: String
    ): Pair<DataFrame, Map<Aes<*>, Variable>> {
        @Suppress("NAME_SHADOWING")
        var layerData = layerData

        //val rightMapId = getGeoPositionsIdVar(mapOptions).name
        layerData = ConfigUtil.rightJoin(layerData, leftMapId, mapOptions, rightMapId)

        val aesMapping = HashMap(
            ConfigUtil.createAesMapping(
                layerData,
                mappingOptions
            )
        )
        aesMapping.putAll(
            generateMappings(
                geomKind,
                layerData
            )
        )
        return Pair(layerData, aesMapping)
    }

    private fun generateMappings(geomKind: GeomKind, layerData: DataFrame): Map<Aes<*>, Variable> {
        return if (isGeomSupported(geomKind)) {
            GEOMS_SUPPORT[geomKind]!!.generateMapping(layerData)
        } else {
            emptyMap()
        }
    }

    private fun getGeoPositionsIdVar(mapOptions: DataFrame): Variable {
        val variable = findFirstVariable(mapOptions, listOf(MapJoin.MAP_ID, MAP_REGION_COLUMN))
        if (variable != null) {
            return variable
        }

        throw IllegalArgumentException(
            geoPositionsColumnNotFoundError(
                "region id",
                listOf(MapJoin.MAP_ID, MAP_REGION_COLUMN)
            )
        )
    }

    private fun findMapping(aes: Aes<*>, names: List<String>, dataFrame: DataFrame): Map<Aes<*>, Variable> {
        val variable = findFirstVariable(dataFrame, names)
                ?: throw IllegalArgumentException(
                    geoPositionsColumnNotFoundError(
                        aes.name + "-column",
                        names
                    )
                )
        return mapOf(aes to variable)
    }

    private fun findFirstVariable(data: DataFrame, names: Iterable<String>): Variable? {
        val variableMap = DataFrameUtil.variables(data)
        for (name in names) {
            if (variableMap.containsKey(name)) {
                return variableMap[name]
            }
        }
        return null
    }

    private fun geoPositionsColumnNotFoundError(what: String, names: List<String>): String {
        return "Can't draw map: " + what + " not found. Geo position data must contain column " +
                names.joinToString(" or ") { s -> "'$s'" }
    }

    enum class GeoDataKind {
        POINT,
        PATH,
        BBOX,
        BOUNDARY
    }

    private fun createRectMapping(dataFrame: DataFrame): Map<Aes<*>, Variable> {
        val mapping = HashMap<Aes<*>, Variable>()
        mapping.putAll(
            findMapping(
                Aes.XMIN,
                listOf(GeoPositionField.RECT_XMIN),
                dataFrame
            )
        )
        mapping.putAll(
            findMapping(
                Aes.XMAX,
                listOf(GeoPositionField.RECT_XMAX),
                dataFrame
            )
        )
        mapping.putAll(
            findMapping(
                Aes.YMIN,
                listOf(GeoPositionField.RECT_YMIN),
                dataFrame
            )
        )
        mapping.putAll(
            findMapping(
                Aes.YMAX,
                listOf(GeoPositionField.RECT_YMAX),
                dataFrame
            )
        )
        return mapping
    }

    private fun createPointMapping(dataFrame: DataFrame): Map<Aes<*>, Variable> {
        val mapping = HashMap<Aes<*>, Variable>()
        mapping.putAll(
            findMapping(
                Aes.X,
                listOf(
                    GeoPositionField.POINT_X,
                    "x",
                    GeoPositionField.POINT_X2
                ),
                dataFrame
            )
        )
        mapping.putAll(
            findMapping(
                Aes.Y,
                listOf(GeoPositionField.POINT_Y, "y"),
                dataFrame
            )
        )

        return mapping
    }

    class GeoDataSupport(
        val geoDataKind: GeoDataKind,
        private val mappingsGenerator: (DataFrame) -> Map<Aes<*>, Variable>
    ) {

        fun generateMapping(df: DataFrame): Map<Aes<*>, Variable> = mappingsGenerator(df)

    }
}
