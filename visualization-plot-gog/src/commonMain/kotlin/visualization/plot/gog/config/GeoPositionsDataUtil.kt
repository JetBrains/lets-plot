package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.render.Aes

object GeoPositionsDataUtil {
    // Fixed columns in dataframe supplied by 'geo-coding'
    const val OBJECT_OSM_ID = "id"

    // Key used for join from MAP
    const val DATA_COLUMN_JOIN_KEY = "__key__"
    // Key used for join into DATA
    const val MAP_COLUMN_JOIN_KEY = "key"

    const val MAP_COLUMN_REQUEST = "request"
    const val MAP_COLUMN_REGION = "region"
    const val MAP_COLUMN_OSM_ID = "__geoid__"
    const val MAP_COLUMN_GEOJSON = "__geometry__"

    // additional fixed colums in 'boundaries' of 'centroids' dataframes
    val POINT_X = "lon"
    val POINT_Y = "lat"

    // additional fixed colums in 'limits'
    val RECT_XMIN = "lonmin"
    val RECT_XMAX = "lonmax"
    val RECT_YMIN = "latmin"
    val RECT_YMAX = "latmax"

    // Columns can be used as request
    val GEO_POSITIONS_KEYS = listOf(MAP_COLUMN_REGION, MAP_COLUMN_JOIN_KEY)

    private val GEOMS_SUPPORT = mapOf(
            GeomKind.MAP to GeoDataSupport.boundary(),
            GeomKind.POLYGON to GeoDataSupport.boundary(),
            GeomKind.POINT to GeoDataSupport.centroid(),
            GeomKind.RECT to GeoDataSupport.limit(),
            GeomKind.PATH to GeoDataSupport.centroid()
    )

    fun isGeomSupported(geomKind: GeomKind): Boolean {
        return GEOMS_SUPPORT.containsKey(geomKind)
    }

    fun getGeoDataKind(geomKind: GeomKind): GeoDataKind {
        return GEOMS_SUPPORT[geomKind]!!.geoDataKind
    }

    internal fun hasGeoPositionsData(layerConfig: LayerConfig): Boolean {
        return layerConfig.has(Option.Geom.Choropleth.GEO_POSITIONS)
    }

    internal fun getGeoPositionsData(layerConfig: LayerConfig): DataFrame {
        val geoPositionsRaw = layerConfig.getMap(Option.Geom.Choropleth.GEO_POSITIONS)
        return ConfigUtil.createDataFrame(geoPositionsRaw)
    }

    internal fun initDataAndMappingForGeoPositions(
            geomKind: GeomKind, layerData: DataFrame, geoPositions: DataFrame, mappingOptions: Map<*, *>): Pair<DataFrame, Map<Aes<*>, Variable>> {
        var layerData = layerData

        val leftMapId = mappingOptions[Option.Mapping.MAP_ID]
        checkState(leftMapId != null || mappingOptions.isEmpty(), "'map_id' aesthetic is required to show data on map")

        if (leftMapId != null) {
            val rightMapId = getGeoPositionsIdVar(geoPositions).name
            layerData = ConfigUtil.rightJoin(layerData, leftMapId.toString(), geoPositions, rightMapId)

            val aesMapping = HashMap(ConfigUtil.createAesMapping(layerData, mappingOptions))
            aesMapping.putAll(generateMappings(geomKind, layerData))
            return Pair(layerData, aesMapping)

        } else {
            // just show a blank map
            return Pair(geoPositions, generateMappings(geomKind, geoPositions))
        }
    }

    private fun generateMappings(geomKind: GeomKind, layerData: DataFrame): Map<Aes<*>, Variable> {
        return if (isGeomSupported(geomKind)) {
            GEOMS_SUPPORT[geomKind]!!.generateMapping(layerData)
        } else {
            emptyMap()
        }
    }

    private fun getGeoPositionsIdVar(regionBoundaries: DataFrame): Variable {
        val `var` = findFirstVariable(regionBoundaries, GEO_POSITIONS_KEYS)
        if (`var` != null) {
            return `var`
        }

        throw IllegalArgumentException(geoPositionsColumnNotFoundError("region id", GEO_POSITIONS_KEYS))
    }

    private fun findMapping(aes: Aes<*>, names: List<String>, dataFrame: DataFrame): Map<Aes<*>, Variable> {
        val variable = findFirstVariable(dataFrame, names)
                ?: throw IllegalArgumentException(geoPositionsColumnNotFoundError(aes.name + "-column", names))
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
        CENTROID,
        LIMIT,
        BOUNDARY
    }

    internal class GeoDataSupport(val geoDataKind: GeoDataKind, private val mappingsGenerator: (DataFrame) -> Map<Aes<*>, Variable>) {

        fun generateMapping(df: DataFrame): Map<Aes<*>, Variable> {
            return mappingsGenerator(df)
        }

        companion object {
            fun boundary(): GeoDataSupport {
                return GeoDataSupport(GeoDataKind.BOUNDARY, { createPointMapping(it) })
            }

            fun centroid(): GeoDataSupport {
                return GeoDataSupport(GeoDataKind.CENTROID, { createPointMapping(it) })
            }

            fun limit(): GeoDataSupport {
                return GeoDataSupport(GeoDataKind.LIMIT, { createRectMapping(it) })
            }

            private fun createRectMapping(dataFrame: DataFrame): Map<Aes<*>, Variable> {
                val mapping = HashMap<Aes<*>, Variable>()
                mapping.putAll(findMapping(Aes.XMIN, listOf(RECT_XMIN), dataFrame))
                mapping.putAll(findMapping(Aes.XMAX, listOf(RECT_XMAX), dataFrame))
                mapping.putAll(findMapping(Aes.YMIN, listOf(RECT_YMIN), dataFrame))
                mapping.putAll(findMapping(Aes.YMAX, listOf(RECT_YMAX), dataFrame))
                return mapping
            }

            private fun createPointMapping(dataFrame: DataFrame): Map<Aes<*>, Variable> {
                val mapping = HashMap<Aes<*>, Variable>()
                mapping.putAll(findMapping(Aes.X, listOf(POINT_X, "x", "long"), dataFrame))
                mapping.putAll(findMapping(Aes.Y, listOf(POINT_Y, "y"), dataFrame))

                return mapping
            }
        }
    }
}
