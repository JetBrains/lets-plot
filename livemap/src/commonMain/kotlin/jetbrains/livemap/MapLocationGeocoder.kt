/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.reinterpret
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
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
        val requestBuilder: GeoRequestBuilder.RequestBuilderBase<*>

        if (mapRegion.containsId()) {
            requestBuilder = GeoRequestBuilder.ExplicitRequestBuilder().setIds(mapRegion.idList)

        } else if (mapRegion.containsName()) {
            requestBuilder = GeoRequestBuilder.GeocodingRequestBuilder()
                .addQuery(
                    GeoRequestBuilder.RegionQueryBuilder()
                        .setQueryNames(mapRegion.name)
                        .build()
                )

        } else {
            throw IllegalArgumentException("Unknown map region kind")
        }

        return myGeocodingService.execute(
            requestBuilder
                .addFeature(GeoRequest.FeatureOption.CENTROID)
                .addFeature(GeoRequest.FeatureOption.POSITION)
                .build()
        ).map { features ->
            if (features.isEmpty()) {
                throw RuntimeException("There is no geocoded feature for location.")
            }

            val boundingBox: WorldRectangle
            if (features.size == 1) {
                val feature = features.get(0)
                boundingBox = calculateExtendedRectangleWithCenter(
                    myMapRuler,
                    calculateBBoxOfGeoRect(feature.position!!),
                    myMapProjection.project(feature.centroid!!.reinterpret<LonLat>())
                )
            } else {
                val positions = ArrayList<GeoRectangle>()
                features.forEach { feature -> positions.add(feature.position!!) }
                boundingBox = calculateBBoxOfGeoRects(positions)
            }
            boundingBox
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
