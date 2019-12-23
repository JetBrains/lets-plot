/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.reinterpret
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.MapWidgetUtil.calculateExtendedRectangleWithCenter
import jetbrains.livemap.MapWidgetUtil.convertToWorldRects
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.World
import jetbrains.livemap.projections.WorldRectangle

class MapLocationGeocoder(
    private val myGeocodingService: GeocodingService,
    private val myMapRuler: MapRuler<World>,
    private val myMapProjection: MapProjection
) {
    fun geocodeMapRegion(mapRegion: MapRegion): Async<WorldRectangle> {

        return createRequestBuilder(mapRegion)
            .addFeature(GeoRequest.FeatureOption.CENTROID)
            .addFeature(GeoRequest.FeatureOption.POSITION)
            .build()
            .run(myGeocodingService::execute)
            .map { features ->
                if (features.isEmpty()) {
                    throw RuntimeException("There is no geocoded feature for location.")
                }

                if (features.size == 1) {
                    val feature = features[0]
                    calculateExtendedRectangleWithCenter(
                        myMapRuler,
                        calculateBBoxOfGeoRect(feature.position!!),
                        myMapProjection.project(feature.centroid!!.reinterpret())
                    )
                } else {
                    features
                        .map { it.position!! }
                        .run(::calculateBBoxOfGeoRects)
                }
            }
    }

    private fun createRequestBuilder(mapRegion: MapRegion) =
        when {
            mapRegion.containsId() -> {
                GeoRequestBuilder.ExplicitRequestBuilder().setIds(mapRegion.idList)
            }
            mapRegion.containsName() -> {
                GeoRequestBuilder.GeocodingRequestBuilder()
                    .addQuery(
                        GeoRequestBuilder.RegionQueryBuilder()
                            .setQueryNames(mapRegion.name)
                            .build()
                    )
            }
            else -> {
                throw IllegalArgumentException("Unknown map region kind")
            }
        }

    fun calculateBBoxOfGeoRect(geoRect: GeoRectangle): Rect<World> {
        return myMapRuler.calculateBoundingBox(geoRect.convertToWorldRects(myMapProjection))
    }

    private fun calculateBBoxOfGeoRects(geoRects: List<GeoRectangle>): Rect<World> {
        val xyRects = ArrayList<Rect<World>>()
        geoRects.forEach { geoRect -> xyRects.addAll(geoRect.convertToWorldRects(myMapProjection)) }
        return myMapRuler.calculateBoundingBox(xyRects)
    }
}
