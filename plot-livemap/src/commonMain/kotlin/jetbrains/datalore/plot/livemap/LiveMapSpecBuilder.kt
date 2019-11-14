/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.BBOX_CALCULATOR
import jetbrains.datalore.base.projectionGeometry.GeoUtils.convertToGeoRectangle
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMIN
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMIN
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.DevParams
import jetbrains.livemap.LayerProvider.LayerProviderImpl
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.MapLocation
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.api.liveMapGeocoding
import jetbrains.livemap.projections.ProjectionType


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

    fun liveMapOptions(liveMapOptions: LiveMapOptions): LiveMapSpecBuilder {
        myLiveMapOptions = liveMapOptions
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


        val liveMapLayerProcessor = LiveMapDataPointAestheticsProcessor(myAesthetics, myLiveMapOptions)
        val geomLayersProcessor = LayerDataPointAestheticsProcessor(myLiveMapOptions.geodesic)

        val layersConfigurators: ArrayList<LayersBuilder.() -> Unit> = ArrayList()
        layersConfigurators.add(liveMapLayerProcessor.createConfigurator())
        layersConfigurators.addAll(myLayers.map(geomLayersProcessor::createConfigurator))

        return LiveMapSpec(
            geocodingService = liveMapGeocoding {
                host = "geo.datalore.io"
                port = null
            },
            size = mySize,
            isScaled = myLiveMapOptions.scaled,
            isInteractive = myLiveMapOptions.interactive,
            isEnableMagnifier = myLiveMapOptions.magnifier,
            isClustering = myLiveMapOptions.clustering,
            isLabels = myLiveMapOptions.labels,
            isTiles = DEFAULT_SHOW_TILES,
            isUseFrame = false, //liveMapProcessor.heatMapWithFrame(),
            projectionType = projectionType,
            location = createMapLocation(myLiveMapOptions.location),
            zoom = myLiveMapOptions.zoom,
            level = getFeatureLevel(myLiveMapOptions.featureLevel),
            parent = createMapRegion(myLiveMapOptions.parent),
            layerProvider = LayerProviderImpl(myDevParams) { layersConfigurators.forEach { it() } },
            isLoopX = CYLINDRICAL_PROJECTIONS.contains(projectionType),
            isLoopY = DEFAULT_LOOP_Y,
            mapLocationConsumer = myMapLocationConsumer,
            devParams = myDevParams
        )
    }

    companion object {
        private const val REGION_TYPE = "type"
        private const val REGION_DATA = "data"
        private const val REGION_TYPE_NAME = "region_name"
        private const val REGION_TYPE_IDS = "region_ids"
        private const val REGION_TYPE_COORDINATES = "coordinates"
        private const val REGION_TYPE_DATAFRAME = "data_frame"
        private const val DEFAULT_SHOW_TILES = true
        private const val DEFAULT_LOOP_Y = false
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
            @Suppress("UNCHECKED_CAST")
            val list = data as List<String>
            return MapRegion.withIdList(list)
        }

        private fun calculateGeoRectangle(lonLatList: List<*>): GeoRectangle {
            require(!(lonLatList.isNotEmpty() && lonLatList.size % 2 != 0)) { ("Expected: location"
            + " = [double lon1, double lat1, double lon2, double lat2, ... , double lonN, double latN]") }
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
                ("Expected: location" + " = DataFrame with "
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
            }
        }

        private fun getFeatureLevel(level: String?): FeatureLevel? {
            if (level == null) {
                return null
            }
            try {
                return FeatureLevel.valueOf(level.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException("FeatureLevel: " + FeatureLevel.values())
            }

        }

        private fun createMapRegion(region: Any?): MapRegion? {
            return when (region) {
                null -> null
                is Map<*, *> -> {
                    val handlerMap = HashMap<String, (Any) -> MapRegion>()
                    handlerMap[REGION_TYPE_NAME] = { data -> MapRegion.withName(data as String) }
                    handlerMap[REGION_TYPE_IDS] = { getWithIdList(it) }
                    handleRegionObject((region as Map<*, *>?)!!, handlerMap)
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
                    handlerMap[REGION_TYPE_COORDINATES] = { data -> MapLocation.create(calculateGeoRectangle(data as List<*>)) }
                    handlerMap[REGION_TYPE_DATAFRAME] = { data -> MapLocation.create(calculateGeoRectangle(data as Map<*, *>)) }
                    handleRegionObject((location as Map<*, *>?)!!, handlerMap)
                }
                else -> throw IllegalArgumentException("Expected: locatiobn" + " = [String|Array|DataFrame]")
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