package jetbrains.livemap.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.BBOX_CALCULATOR
import jetbrains.datalore.base.projectionGeometry.GeoUtils.convertToGeoRectangle
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.POINT_X
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.POINT_Y
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.RECT_XMAX
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.RECT_XMIN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.RECT_YMAX
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.RECT_YMIN
import jetbrains.datalore.plot.config.LiveMapOptions
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.geom.LiveMapLayerData
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.livemap.LivemapConstants
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.MapLocation
import jetbrains.livemap.api.internalTiles
import jetbrains.livemap.api.liveMapGeocoding
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionType
import jetbrains.livemap.projections.ProjectionUtil
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.projections.WorldRectangle


internal class LiveMapSpecBuilder {

    private lateinit var myAesthetics: Aesthetics
    private lateinit var myLayers: List<LiveMapLayerData>
    private lateinit var myLiveMapOptions: LiveMapOptions
    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var mySize: DoubleVector
    private lateinit var myDevParams: DevParams
    private lateinit var myMapLocationConsumer: ((DoubleRectangle) -> Unit)

    fun aesthetics(aesthetics: Aesthetics): LiveMapSpecBuilder {
        myAesthetics = aesthetics
        return this
    }

    fun layers(layers: List<LiveMapLayerData>): LiveMapSpecBuilder {
        myLayers = layers
        return this
    }

    fun livemapOptions(livemapOptions: LiveMapOptions): LiveMapSpecBuilder {
        myLiveMapOptions = livemapOptions
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

    fun build(): LiveMapSpec {
        val projectionType = convertProjectionType(myLiveMapOptions.projection)
        val mapRect = WorldRectangle(0.0, 0.0, ProjectionUtil.TILE_PIXEL_SIZE, ProjectionUtil.TILE_PIXEL_SIZE)
        val mapProjection = createMapProjection(projectionType, mapRect)

        //val liveMapProcessor = LiveMapDataPointAestheticsProcessor(myAesthetics, myDataAccess, myLiveMapOptions, mapProjection)

        val mapLayers = ArrayList<MapLayer>()
        //mapLayers.add(liveMapProcessor.createMapLayer())
        mapLayers.addAll(createMapLayers(mapProjection))
        mapLayers.removeAll { layer -> layer.mapObjects.isEmpty() }

        return LiveMapSpec(
            liveMapGeocoding {
                host = "geo.datalore.io"
                port = null
            },
            internalTiles {
                theme = TileService.Theme.COLOR
                host = "tiles.datalore.io"
                port = null
            },
            mySize,
            myLiveMapOptions.scaled,
            myLiveMapOptions.interactive,
            myLiveMapOptions.magnifier,
            myLiveMapOptions.clustering,
            myLiveMapOptions.labels,
            DEFAULT_SHOW_TILES,
            false, //liveMapProcessor.heatMapWithFrame(),
            projectionType,
            createMapLocation(myLiveMapOptions.location),
            myLiveMapOptions.zoom,
            getFeatureLevel(myLiveMapOptions.featureLevel),
            createMapRegion(myLiveMapOptions.parent),
            mapLayers,
            CYLINDRICAL_PROJECTIONS.contains(projectionType),
            DEFAULT_LOOP_Y,
            myMapLocationConsumer,
            myDevParams
        )
    }

    private fun createMapLayers(mapProjection: MapProjection): List<MapLayer> {
        val mapLayers = ArrayList<MapLayer>()
//        val layerProcessor = LayerDataPointAestheticsProcessor(mapProjection, myLiveMapOptions.geodesic)
//
//        for (layerData in myLayers!!) {
//            val mapLayer = layerProcessor.createMapLayer(layerData)
//            if (mapLayer != null) {
//                mapLayers.add(mapLayer)
//            }
//        }
        return mapLayers
    }

    companion object {
        private val REGION_TYPE = "type"
        private val REGION_DATA = "data"
        private val REGION_TYPE_NAME = "region_name"
        private val REGION_TYPE_IDS = "region_ids"
        private val REGION_TYPE_COORDINATES = "coordinates"
        private val REGION_TYPE_DATAFRAME = "data_frame"
        private val DEFAULT_SHOW_TILES = true
        private val DEFAULT_LOOP_Y = false
        private val CYLINDRICAL_PROJECTIONS = setOf(
            ProjectionType.GEOGRAPHIC,
            ProjectionType.MERCATOR
        )

        private fun <T> List<T>.toDoubleList() : List<Double> {
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
            val list = data as List<String>
            return MapRegion.withIdList(list)
        }

        private fun calculateGeoRectangle(lonLatList: List<*>): GeoRectangle {
            if (lonLatList.isNotEmpty() && lonLatList.size % 2 != 0) {
                throw IllegalArgumentException(
                    "Expected: " + Option.Geom.LiveMap.LOCATION
                            + " = [double lon1, double lat1, double lon2, double lat2, ... , double lonN, double latN]"
                )
            }
            return convertToGeoRectangle(BBOX_CALCULATOR.calculateBoundingBox(lonLatList.toDoubleList()))
        }

        private fun calculateGeoRectangle(lonLatDataMap: Map<*, *>): GeoRectangle {
            if (lonLatDataMap.containsKey(POINT_X) && lonLatDataMap.containsKey(POINT_Y)) {
                return convertToGeoRectangle(
                    BBOX_CALCULATOR.calculateBoundingBox(
                        (lonLatDataMap[POINT_X] as List<*>).toDoubleList(),
                        (lonLatDataMap[POINT_Y] as List<*>).toDoubleList()
                    )
                )
            }

            if ((lonLatDataMap.containsKey(RECT_XMIN) && lonLatDataMap.containsKey(RECT_YMIN) &&
                        lonLatDataMap.containsKey(RECT_XMAX) && lonLatDataMap.containsKey(RECT_YMAX))
            ) {
                return convertToGeoRectangle(
                    BBOX_CALCULATOR.calculateBoundingBox(
                        (lonLatDataMap[RECT_XMIN] as List<*>).toDoubleList(),
                        (lonLatDataMap[RECT_YMIN] as List<*>).toDoubleList(),
                        (lonLatDataMap[RECT_XMAX] as List<*>).toDoubleList(),
                        (lonLatDataMap[RECT_YMAX] as List<*>).toDoubleList()
                    )
                )
            }

            throw IllegalArgumentException(
                ("Expected: " + Option.Geom.LiveMap.LOCATION + " = DataFrame with "
                        + "['" + POINT_X + "', '" + POINT_Y + "'] or "
                        + "['" + RECT_XMIN + "', '" + RECT_YMIN + "', '" + RECT_XMAX + "', '" + RECT_YMAX + "'] columns")
            )
        }

        private fun convertProjectionType(projection: LivemapConstants.Projection): ProjectionType {
            return when (projection) {
                LivemapConstants.Projection.EPSG3857 -> ProjectionType.MERCATOR
                LivemapConstants.Projection.EPSG4326 -> ProjectionType.GEOGRAPHIC
                LivemapConstants.Projection.AZIMUTHAL -> ProjectionType.AZIMUTHAL_EQUAL_AREA
                LivemapConstants.Projection.CONIC -> ProjectionType.CONIC_EQUAL_AREA
                else -> throw IllegalArgumentException("Unknown projection value: $projection")
            }
        }

        private fun getFeatureLevel(level: String?): FeatureLevel? {
            if (level == null) {
                return null
            }
            try {
                return FeatureLevel.valueOf(level.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException(Option.Geom.LiveMap.FEATURE_LEVEL + FeatureLevel.values())
            }

        }

        private fun createMapRegion(region: Any?): MapRegion? {
            if (region == null) {
                return null
            } else if (region is Map<*, *>) {
                val handlerMap = HashMap<String, (Any) -> MapRegion>()
                handlerMap[REGION_TYPE_NAME] = { data -> MapRegion.withName(data as String) }
                handlerMap[REGION_TYPE_IDS] = { getWithIdList(it) }
                return handleRegionObject((region as Map<*, *>?)!!, handlerMap)
            } else {
                throw IllegalArgumentException("Expected: " + Option.Geom.LiveMap.PARENT + " = [String]")
            }
        }

        private fun createMapLocation(location: Any?): MapLocation? {
            if (location == null) {
                return null
            } else if (location is Map<*, *>) {
                val handlerMap = HashMap<String, (Any) -> MapLocation>()
                handlerMap[REGION_TYPE_NAME] = { data -> MapLocation.create(MapRegion.withName(data as String)) }
                handlerMap[REGION_TYPE_IDS] = { data -> MapLocation.create(getWithIdList(data)) }
                handlerMap[REGION_TYPE_COORDINATES] =
                    { data -> MapLocation.create(calculateGeoRectangle(data as List<*>)) }
                handlerMap[REGION_TYPE_DATAFRAME] =
                    { data -> MapLocation.create(calculateGeoRectangle(data as Map<*, *>)) }
                return handleRegionObject((location as Map<*, *>?)!!, handlerMap)
            } else {
                throw IllegalArgumentException("Expected: " + Option.Geom.LiveMap.LOCATION + " = [String|Array|DataFrame]")
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
    }
}