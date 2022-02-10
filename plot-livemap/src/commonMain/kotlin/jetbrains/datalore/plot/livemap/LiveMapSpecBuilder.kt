/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.livemap.LivemapConstants.Projection.*
import jetbrains.datalore.plot.base.livemap.LivemapConstants.ScaleObjects
import jetbrains.datalore.plot.builder.LayerRendererUtil.LayerRendererData
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.CLUSTERING
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.GEOCODING
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.GEODESIC
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.INTERACTIVE
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.LABELS
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.LOCATION
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.PROJECTION
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.SCALED
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.SCALE_OBJECTS
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.SCALE_ZOOMS
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.SHOW_COORD_PICK_TOOLS
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.TILES
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.KIND_CHESSBOARD
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.KIND_RASTER_ZXY
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.KIND_SOLID
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.KIND_VECTOR_LETS_PLOT
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.ZOOM
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayerBuilder
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.SortingMode
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.api.*
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import jetbrains.livemap.config.DevParams.Companion.DEBUG_TILES
import jetbrains.livemap.config.LiveMapSpec
import jetbrains.livemap.config.MAX_ZOOM
import jetbrains.livemap.config.MIN_ZOOM
import jetbrains.livemap.core.projections.Projections
import jetbrains.livemap.mapengine.basemap.BasemapTileSystemProvider
import jetbrains.livemap.mapengine.basemap.Tilesets
import jetbrains.livemap.ui.CursorService


internal class LiveMapSpecBuilder {
    private lateinit var myDisplayMode: DisplayMode
    private lateinit var myAesthetics: Aesthetics
    private lateinit var myMappedAes: Set<Aes<*>>
    private lateinit var myLetsPlotLayers: List<LayerRendererData>
    private lateinit var myLiveMapOptions: Map<*, *>
    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var mySize: DoubleVector
    private lateinit var myDevParams: DevParams
    private lateinit var myMapLocationConsumer: ((DoubleRectangle) -> Unit)
    private lateinit var myCursorService: CursorService
    private var minZoom: Int = MIN_ZOOM
    private var maxZoom: Int = MAX_ZOOM

    fun displayMode(displayMode: DisplayMode): LiveMapSpecBuilder {
        myDisplayMode = displayMode
        return this
    }

    fun aesthetics(aesthetics: Aesthetics): LiveMapSpecBuilder {
        myAesthetics = aesthetics
        return this
    }

    fun mappedAes(mappedAes: Set<Aes<*>>): LiveMapSpecBuilder {
        myMappedAes = mappedAes
        return this
    }

    fun layers(layers: List<LayerRendererData>): LiveMapSpecBuilder {
        myLetsPlotLayers = layers
        return this
    }

    fun liveMapOptions(liveMapOptions: Map<*, *>): LiveMapSpecBuilder {
        myLiveMapOptions = liveMapOptions
        myLiveMapOptions.getInt(TILES, Tile.MIN_ZOOM)?.let { minZoom = it }
        myLiveMapOptions.getInt(TILES, Tile.MAX_ZOOM)?.let { maxZoom = it }

        return this
    }

    fun dataAccess(dataAccess: MappedDataAccess): LiveMapSpecBuilder {
        myDataAccess = dataAccess
        return this
    }

    fun size(size: DoubleVector): LiveMapSpecBuilder {
        mySize = size
        return this
    }

    fun devParams(v: DevParams): LiveMapSpecBuilder {
        myDevParams = v
        return this
    }

    fun mapLocationConsumer(consumer: (DoubleRectangle) -> Unit): LiveMapSpecBuilder {
        myMapLocationConsumer = consumer
        return this
    }

    fun cursorService(cursorService: CursorService): LiveMapSpecBuilder {
        myCursorService = cursorService
        return this
    }

    fun build(): LiveMapSpec {
        val layerBuilders: MutableList<LayersBuilder.() -> Unit> = mutableListOf()

        val scaleObjects = myLiveMapOptions.getEnum(SCALE_OBJECTS) ?: ScaleObjects.STATIC
        val scaleZooms = myLiveMapOptions.getInt(SCALE_ZOOMS) ?: 2

        // livemap layer
        layerBuilders.add(run {
            val myLayerKind = when (myDisplayMode) {
                DisplayMode.POINT -> MapLayerKind.POINT
                DisplayMode.PIE -> MapLayerKind.PIE
                DisplayMode.BAR -> MapLayerKind.BAR
            }
            val sortingMode = when (myLayerKind) {
                MapLayerKind.PIE -> SortingMode.PIE_CHART
                MapLayerKind.BAR -> SortingMode.BAR
                else -> null
            }
            val dataPointLiveMapAesthetics = when (myLayerKind) {
                MapLayerKind.PIE, MapLayerKind.BAR ->
                    MultiDataPointHelper.getPoints(myAesthetics, sortingMode!!)
                        .map {
                            DataPointLiveMapAesthetics(it, 0, myLayerKind)
                                .setGeometryPoint(explicitVec(it.aes.x()!!, it.aes.y()!!))
                        }
                else ->
                    myAesthetics.dataPoints()
                        .map {
                            DataPointLiveMapAesthetics(it, 0, myLayerKind)
                                .setGeometryPoint(explicitVec(it.x()!!, it.y()!!))
                        }
            }

            createLayerBuilder(myLayerKind,
                dataPointLiveMapAesthetics,
                myMappedAes,
                scaleObjects,
                scaleZooms
            )
        })

        // other layers
        layerBuilders.addAll(
            myLetsPlotLayers.mapIndexed { layerIndex, layerData ->
                val dataPointsConverter = DataPointsConverter(
                    layerIndex = layerIndex + 1,
                    aesthetics = layerData.aesthetics,
                    geodesic = myLiveMapOptions.getBool(GEODESIC) ?: true
                )
                val (layerKind, dataPointLiveMapAesthetics) = when (layerData.geomKind) {
                    POINT -> MapLayerKind.POINT to dataPointsConverter.toPoint(layerData.geom)
                    H_LINE -> MapLayerKind.H_LINE to dataPointsConverter.toHorizontalLine()
                    V_LINE -> MapLayerKind.V_LINE to dataPointsConverter.toVerticalLine()
                    SEGMENT -> MapLayerKind.PATH to dataPointsConverter.toSegment(layerData.geom)
                    RECT -> MapLayerKind.POLYGON to dataPointsConverter.toRect()
                    TILE, BIN_2D -> MapLayerKind.POLYGON to dataPointsConverter.toTile()
                    DENSITY2D, CONTOUR, PATH -> MapLayerKind.PATH to dataPointsConverter.toPath(layerData.geom)
                    TEXT -> MapLayerKind.TEXT to dataPointsConverter.toText()
                    DENSITY2DF, CONTOURF, POLYGON, MAP -> MapLayerKind.POLYGON to dataPointsConverter.toPolygon()
                    else -> throw IllegalArgumentException("Layer '" + layerData.geomKind.name + "' is not supported on Live Map.")
                }
                createLayerBuilder(layerKind, dataPointLiveMapAesthetics, layerData.mappedAes, scaleObjects, scaleZooms)
            })

        // loopX <-> cylindrical
        val (geoProjection, loopX) = when (myLiveMapOptions.getEnum(PROJECTION) ?: EPSG3857) {
            EPSG3857 -> Projections.mercator() to true
            EPSG4326 -> Projections.geographic() to true
            AZIMUTHAL -> Projections.azimuthalEqualArea() to false
            CONIC -> Projections.conicEqualArea() to false
        }

        return LiveMapSpec(
            size = mySize,
            isScaled = myLiveMapOptions.getBool(SCALED) ?: false,
            isInteractive = myLiveMapOptions.getBool(INTERACTIVE) ?: true,
            isClustering = myLiveMapOptions.getBool(CLUSTERING) ?: false,
            isLabels = myLiveMapOptions.getBool(LABELS) ?: true,
            isTiles = DEFAULT_SHOW_TILES,
            isUseFrame = false, //liveMapProcessor.heatMapWithFrame(),
            geoProjection = geoProjection,
            location = createMapLocation(myLiveMapOptions.read(LOCATION)),
            zoom = checkZoom(myLiveMapOptions.getInt(ZOOM)),
            layers = layerBuilders,
            isLoopX = loopX,
            isLoopY = DEFAULT_LOOP_Y,
            mapLocationConsumer = myMapLocationConsumer,
            geocodingService = createGeocodingService(myLiveMapOptions.getMap(GEOCODING) ?: error("Geocoding service must be configured")),
            basemapTileSystemProvider = createTileSystemProvider(
                myLiveMapOptions.getMap(TILES) ?: error("Tiles must be condigured"),
                myDevParams.isSet(DEBUG_TILES),
                myDevParams.read(COMPUTATION_PROJECTION_QUANT)
            ),
            attribution = myLiveMapOptions.getString(TILES, Tile.ATTRIBUTION),
            showCoordPickTools = myLiveMapOptions.getBool(SHOW_COORD_PICK_TOOLS) ?: false,
            cursorService = myCursorService,
            minZoom = minZoom,
            maxZoom = maxZoom,
            devParams = myDevParams
        )
    }

    private fun checkZoom(zoom: Int?): Int? {
        if (zoom == null || zoom in IntRange(minZoom, maxZoom)) {
            return zoom
        }

        error("Zoom must be in range [$minZoom, $maxZoom], but was $zoom")
    }

    companion object {
        private const val REGION_TYPE = "type"
        private const val REGION_DATA = "data"
        private const val REGION_TYPE_NAME = "region_name"
        private const val REGION_TYPE_IDS = "region_ids"
        private const val REGION_TYPE_COORDINATES = "coordinates"
        private const val REGION_TYPE_DATAFRAME = "data_frame"

        private const val POINT_X = "lon"
        private const val POINT_Y = "lat"

        private const val RECT_XMIN = "lonmin"
        private const val RECT_XMAX = "lonmax"
        private const val RECT_YMIN = "latmin"
        private const val RECT_YMAX = "latmax"

        object Geocoding {
            const val URL = "url"
        }

        private const val DEFAULT_SHOW_TILES = true
        private const val DEFAULT_LOOP_Y = false

        private fun <T> List<T>.toDoubleList(): List<Double> {
            if (isEmpty()) {
                return emptyList()
            }

            if (all { it is Double }) {
                @Suppress("UNCHECKED_CAST")
                return this as List<Double>
            }

            error("Can't cast to collection of numbers")
        }

        private fun getWithIdList(data: Any): MapRegion {
            @Suppress("UNCHECKED_CAST")
            val list = data as List<String>
            return MapRegion.withIdList(list)
        }

        private fun calculateGeoRectangle(lonLatList: List<*>): GeoRectangle {
            require(!(lonLatList.isNotEmpty() && lonLatList.size % 2 != 0)) {
                ("Expected: location"
                        + " = [double lon1, double lat1, double lon2, double lat2, ... , double lonN, double latN]")
            }
            return convertToGeoRectangle(BboxUtil.calculateBoundingBox(lonLatList.toDoubleList()))
        }

        private fun calculateGeoRectangle(lonLatDataMap: Map<*, *>): GeoRectangle {

            if (lonLatDataMap.containsKey(POINT_X) && lonLatDataMap.containsKey(POINT_Y)) {
                return convertToGeoRectangle(
                    BboxUtil.calculateBoundingBox(
                        (lonLatDataMap[POINT_X] as List<*>).toDoubleList(),
                        (lonLatDataMap[POINT_Y] as List<*>).toDoubleList()
                    )
                )
            }

            if ((lonLatDataMap.containsKey(RECT_XMIN) && lonLatDataMap.containsKey(RECT_YMIN) &&
                        lonLatDataMap.containsKey(RECT_XMAX) && lonLatDataMap.containsKey(RECT_YMAX))
            ) {
                return convertToGeoRectangle(
                    BboxUtil.calculateBoundingBox(
                        (lonLatDataMap[RECT_XMIN] as List<*>).toDoubleList(),
                        (lonLatDataMap[RECT_YMIN] as List<*>).toDoubleList(),
                        (lonLatDataMap[RECT_XMAX] as List<*>).toDoubleList(),
                        (lonLatDataMap[RECT_YMAX] as List<*>).toDoubleList()
                    )
                )
            }

            throw IllegalArgumentException(
                "Expected: location = DataFrame with " +
                        "['$POINT_X', '$POINT_Y'] or " +
                        "['$RECT_XMIN', '$RECT_YMIN', '$RECT_XMAX', '$RECT_YMAX'] " +
                        "columns"
            )
        }

        private fun createMapRegion(region: Any?): MapRegion? {
            return when (region) {
                null -> null
                is Map<*, *> -> {
                    val handlerMap = HashMap<String, (Any) -> MapRegion>()
                    handlerMap[REGION_TYPE_NAME] = { data -> MapRegion.withName(data as String) }
                    handlerMap[REGION_TYPE_IDS] = { getWithIdList(it) }
                    handleRegionObject(region, handlerMap)
                }
                else -> throw IllegalArgumentException("Expected: parent" + " = [String]")
            }
        }

        private fun createMapLocation(location: Any?): MapLocation? {
            return when (location) {
                null -> null
                is Map<*, *> -> {
                    val handlerMap = HashMap<String, (Any) -> MapLocation>()
                    handlerMap[REGION_TYPE_NAME] = { data -> MapLocation.create(MapRegion.withName(data as String)) }
                    handlerMap[REGION_TYPE_IDS] = { data -> MapLocation.create(getWithIdList(data)) }
                    handlerMap[REGION_TYPE_COORDINATES] =
                        { data -> MapLocation.create(calculateGeoRectangle(data as List<*>)) }
                    handlerMap[REGION_TYPE_DATAFRAME] =
                        { data -> MapLocation.create(calculateGeoRectangle(data as Map<*, *>)) }
                    handleRegionObject(location, handlerMap)
                }
                else -> throw IllegalArgumentException("Expected: location" + " = [String|Array|DataFrame]")
            }
        }

        private fun <T> handleRegionObject(region: Map<*, *>, handlerMap: Map<String, (Any) -> T>): T {
            val regionType = region[REGION_TYPE] ?: throw IllegalArgumentException("Invalid map region object")
            val regionData = region[REGION_DATA] ?: throw IllegalArgumentException("Invalid map region object")

            for ((key, handler) in handlerMap) {
                if (regionType == key) {
                    return handler(regionData)
                }
            }

            throw IllegalArgumentException("Invalid map region type: $regionType")
        }

        fun createTileSystemProvider(options: Map<*, *>, debugTiles: Boolean, quant: Int): BasemapTileSystemProvider {
            if (debugTiles) {
                return Tilesets.chessboard()
            }

            return when (options[Tile.KIND]) {
                KIND_CHESSBOARD -> Tilesets.chessboard()
                KIND_SOLID -> Tilesets.solid(Color.parseHex(options.getString(Tile.FILL_COLOR)!!))
                KIND_RASTER_ZXY -> options.getString(Tile.URL)!!.let(::splitSubdomains).let(Tilesets::raster)
                KIND_VECTOR_LETS_PLOT -> Tilesets.letsPlot(
                    quantumIterations = quant,
                    tileService = liveMapVectorTiles {
                        options.getString(Tile.URL)?.let { url = it }
                        options.getString(Tile.THEME)?.let { theme = TileService.Theme.valueOf(it.uppercase()) }
                    }
                )
                else -> throw IllegalArgumentException("Tile provider is not set.")
            }
        }

        private fun splitSubdomains(url: String): List<String> {
            val openBracketIndex = url.indexOfFirst { it == '[' }
            val closeBracketIndex = url.indexOfLast { it == ']' }

            if (openBracketIndex < 0 || closeBracketIndex < 0) {
                // single domain
                return listOf(url)
            }

            if (openBracketIndex > closeBracketIndex) {
                throw IllegalArgumentException("Error parsing subdomains: wrong brackets order")
            }

            val subdomains = url.substring(openBracketIndex + 1, closeBracketIndex)
            if (subdomains.isEmpty()) {
                throw IllegalArgumentException("Empty subdomains list")
            }
            if (subdomains.any { it.lowercaseChar() !in 'a'..'z' }) {
                throw IllegalArgumentException("subdomain list contains non-letter symbols")
            }

            val urlStart = url.substring(0, openBracketIndex)
            val urlEnd = url.substring(closeBracketIndex + 1, url.length)
            return subdomains.map { urlStart + it + urlEnd }
        }

        private fun createGeocodingService(options: Map<*, *>): GeocodingService {
            return options[Geocoding.URL]
                ?.let { liveMapGeocoding { url = it as String } }
                ?: Services.bogusGeocodingService()
        }
    }

    object BboxUtil {

        fun calculateBoundingBox(xyCoords: List<Double>): Rect<LonLat> {
            return BBOX_CALCULATOR.pointsBBox(xyCoords)
        }

        fun calculateBoundingBox(xCoords: List<Double>, yCoords: List<Double>): Rect<LonLat> {
            require(xCoords.size == yCoords.size) { "Longitude list count is not equal Latitude list count." }

            return BBOX_CALCULATOR.calculateBoundingBox(
                makeSegments(
                    xCoords::get,
                    xCoords::get,
                    xCoords.size
                ),
                makeSegments(
                    yCoords::get,
                    yCoords::get,
                    xCoords.size
                )
            )
        }

        fun calculateBoundingBox(
            minXCoords: List<Double>,
            minYCoords: List<Double>,
            maxXCoords: List<Double>,
            maxYCoords: List<Double>,
        ): Rect<LonLat> {
            val count = minXCoords.size
            require(minYCoords.size == count && maxXCoords.size == count && maxYCoords.size == count)
            { "Counts of 'minLongitudes', 'minLatitudes', 'maxLongitudes', 'maxLatitudes' lists are not equal." }

            return BBOX_CALCULATOR.calculateBoundingBox(
                makeSegments(
                    minXCoords::get,
                    maxXCoords::get,
                    count
                ),
                makeSegments(minYCoords::get, maxYCoords::get, count)
            )
        }
    }
}
