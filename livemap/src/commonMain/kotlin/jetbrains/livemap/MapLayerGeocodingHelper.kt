/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.reinterpret
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.*
import jetbrains.gis.geoprotocol.GeoRequestBuilder.ExplicitRequestBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.GeocodingRequestBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.RegionQueryBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.MapWidgetUtil.convertToWorldRects
import jetbrains.livemap.mapobjects.*
import jetbrains.livemap.mapobjects.MapLayerKind.H_LINE
import jetbrains.livemap.mapobjects.MapLayerKind.V_LINE
import jetbrains.livemap.mapobjects.Utils.calculateBBoxes
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil
import jetbrains.livemap.projections.World

internal class MapLayerGeocodingHelper(
    private val myMapLayer: MapLayer,
    private val myGeocodingService: GeocodingService,
    private val myMapProjection: MapProjection,
    needLocations: Boolean,
    private val myNeedBBoxes: Boolean,
    private var myFeatureLevel: FeatureLevel?,
    private var myParent: MapRegion?
) {
    private val myNeedLocation: Boolean
    private val myMapIdSpecified: Boolean
    private val myOsmIdInsideMapIdSpecified: Boolean
    private val myPointSpecified: Boolean
    private val myGeometrySpecified: Boolean
    private val myOsmIdLocationMap = HashMap<String, GeoRectangle>()
    private val myOsmIdBBoxMap = HashMap<String, GeoRectangle>()

    val osmIdBBoxMap: Map<String, GeoRectangle>
        get() = myOsmIdBBoxMap

    private fun isMapIdSet(mapObject: MapObject): Boolean {
        return mapObject.mapId != null
    }

    private fun <T> getGeocodingDataMap(
        features: List<GeocodedFeature>,
        getData: (GeocodedFeature) -> T
    ): MutableMap<String, T> {
        val dataMap = HashMap<String, T>(features.size)
        for (feature in features) {
            dataMap[feature.request] = getData(feature)
        }
        return dataMap
    }

    private fun <T> acceptIfMapContains(map: MutableMap<String, T?>, key: String, consumer: Consumer<T>) {
        map[key]?.let(consumer)
    }

    private fun isMapLayerKindEnableLocation(kind: MapLayerKind): Boolean {
        return kind !== H_LINE && kind !== V_LINE
    }

    private fun isMapPointObject(mapObject: MapObject): Boolean {
        return mapObject is MapPoint
    }

    init {
        require(myMapLayer.mapObjects.isNotEmpty())

        with(myMapLayer) {
            myNeedLocation = needLocations && isMapLayerKindEnableLocation(kind)

            myMapIdSpecified = mapObjects.all(::isMapIdSet)
            myOsmIdInsideMapIdSpecified = mapObjects.all { isMapIdWithOsmId(it.mapId) }
            myPointSpecified = mapObjects.all(::isMapPointObject)
            myGeometrySpecified = mapObjects.all(::isMapObjectContainsGeometry)
        }
    }

    fun geocodeLayerData(): Async<List<Rect<World>>> {
        return geocodeLayerRegionIds()
            .flatMap { regionsAreGeocoded ->
                if (regionsAreGeocoded) geocodeLayerGeometries()
                else Asyncs.voidAsync() }
            .map { calculateLayerBBoxes() }
    }

    private fun isMapObjectContainsGeometry(mapObject: MapObject): Boolean {
        return when (mapObject) {
            is MapPointGeometry -> true
            is MapGeometry -> mapObject.geometry != null
            else -> throw IllegalStateException("Unexpected MapObject type: " + mapObject::class.simpleName)
        }
    }

    private fun geocodeLayerRegionIds(): Async<Boolean> {
        if (myGeometrySpecified) {
            return Asyncs.constant(false)
        }

        if (!myMapIdSpecified) {
            return Asyncs.constant(false)
        }

        if (myOsmIdInsideMapIdSpecified) {
            // MapId already contains osmid and do not require any geocoding.
            // To minimize changes store it into regionsIdMap
            myMapLayer.mapObjects.forEach { it.regionId = it.mapId }
            return Asyncs.constant(true)
        }

        val names = getUniqueMapLayerField(MapObject::mapId)

        val request = GeocodingRequestBuilder()
            .setLevel(myFeatureLevel)
            .addQuery(
                RegionQueryBuilder()
                    .setQueryNames(names)
                    .setParent(myParent)
                    .build()
            )
            .build()

        return myGeocodingService
            .execute(request)
            .map(::setMapObjectRegionIdFromGeocodedFeatures)
    }

    private fun setMapObjectRegionIdFromGeocodedFeatures(features: List<GeocodedFeature>): Boolean {
        getGeocodingDataMap(features, GeocodedFeature::id).let { idsMap ->
            myMapLayer.mapObjects.forEach { mapObject ->
                mapObject.regionId = idsMap[mapObject.mapId]
            }
        }
        return true
    }

    private fun getUniqueMapLayerField(getter: (MapObject) -> String?): List<String> {
        return myMapLayer.mapObjects
            .asSequence()
            .mapNotNull { getter(it) }
            .toSet()
            .toList()
    }

    private fun geocodeLayerGeometries(): Async<Unit> {
        val featureOptions = ArrayList<FeatureOption>()

        if (myPointSpecified) {
            featureOptions.add(CENTROID)
        }

        if (myNeedLocation) {
            featureOptions.add(POSITION)
        }

        if (myNeedBBoxes) {
            featureOptions.add(LIMIT)
        }

        if (featureOptions.isEmpty()) {
            return Asyncs.voidAsync()
        }

        val osmIds = getUniqueMapLayerField(MapObject::regionId)
        val request = ExplicitRequestBuilder()
            .setIds(osmIds)
            .setFeatures(featureOptions)
            .build()

        return myGeocodingService
            .execute(request)
            .map { features ->
                if (featureOptions.contains(CENTROID)) {
                    parseCentroidMap(features)
                }

                if (featureOptions.contains(POSITION)) {
                    parseLocationMap(features)
                }

                if (featureOptions.contains(LIMIT)) {
                    parseBBoxMap(features)
                }
                return@map
            }
    }

    private fun parseCentroidMap(features: List<GeocodedFeature>) {
        val centroidsById = getGeocodingDataMap(features, GeocodedFeature::centroid)

        myMapLayer.mapObjects.forEach { mapObject ->
            if (mapObject is MapPoint) {
                mapObject.regionId?.let { regionId ->
                    acceptIfMapContains(centroidsById, regionId) { coord ->
                        mapObject.point = coord.reinterpret()
                    }
                }
            }
        }
    }

    private fun parseLocationMap(features: List<GeocodedFeature>) {
        getGeocodingDataMap(features, GeocodedFeature::position)
            .forEach { (requestString, geoRect) ->
                geoRect?.let {
                    myOsmIdLocationMap[requestString] = geoRect
                }
            }
    }

    private fun parseBBoxMap(features: List<GeocodedFeature>) {
        getGeocodingDataMap(features, GeocodedFeature::limit)
            .forEach { (requestString, limit) ->
                limit?.let {
                    myOsmIdBBoxMap[requestString] = it
                }
            }
    }

    private fun calculateLayerBBoxes(): List<Rect<World>> {
        if (!myNeedLocation) {
            return emptyList()
        }

        return myMapLayer.mapObjects.flatMap { calculateMapObjectBBoxes(it) }
    }

    private fun calculateMapObjectBBoxes(mapObject: MapObject): List<Rect<World>> {
        return myOsmIdLocationMap[mapObject.regionId]
            ?.convertToWorldRects(myMapProjection)
            ?: calculateBBoxes(mapObject).map { rect ->
                ProjectionUtil.transformBBox(rect) { myMapProjection.project(it) }
            }
    }

    private fun allMapObjectMatch(matcher: (MapObject) -> Boolean): Boolean {
        if (myMapLayer.mapObjects.isEmpty()) {
            return false
        }

        for (mapObject in myMapLayer.mapObjects) {
            if (!matcher(mapObject)) {
                return false
            }
        }
        return true
    }

    companion object {
        fun isMapIdWithOsmId(mapId: String?): Boolean {
            return mapId?.toIntOrNull() != null
        }
    }
}