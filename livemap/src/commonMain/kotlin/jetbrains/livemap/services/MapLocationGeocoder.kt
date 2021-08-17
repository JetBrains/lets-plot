/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.services

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder.ExplicitRequestBuilder
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.core.projections.ProjectionUtil
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.projection.World
import jetbrains.livemap.projection.WorldRectangle
import kotlin.math.min

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
                    val feature = features.single()
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

    private fun createRequestBuilder(mapRegion: MapRegion): ExplicitRequestBuilder {
        require(mapRegion.containsId()) { "location should contain geocode" }
        return ExplicitRequestBuilder().setIds(mapRegion.idList)
    }

    internal fun calculateBBoxOfGeoRect(geoRect: GeoRectangle): Rect<World> {
        return myMapRuler.calculateBoundingBox(geoRect.convertToWorldRects(myMapProjection))
    }

    private fun calculateBBoxOfGeoRects(geoRects: List<GeoRectangle>): Rect<World> {
        val xyRects = ArrayList<Rect<World>>()
        geoRects.forEach { geoRect -> xyRects.addAll(geoRect.convertToWorldRects(myMapProjection)) }
        return myMapRuler.calculateBoundingBox(xyRects)
    }

    private fun <TypeT> calculateExtendedRectangleWithCenter(
        mapRuler: MapRuler<TypeT>,
        rect: Rect<TypeT>,
        center: Vec<TypeT>
    ): Rect<TypeT> {
        val radiusX = calculateRadius(
            center.x,
            rect.left,
            rect.width,
            mapRuler::distanceX
        )
        val radiusY = calculateRadius(
            center.y,
            rect.top,
            rect.height,
            mapRuler::distanceY
        )

        return Rect(
            center.x - radiusX,
            center.y - radiusY,
            radiusX * 2,
            radiusY * 2
        )
    }

    private fun calculateRadius(
        center: Double,
        left: Double,
        width: Double,
        distance: (Double, Double) -> Double
    ): Double {
        val right = left + width
        val minEdgeDistance = min(
            distance(center, left),
            distance(center, right)
        )
        return when (center) {
            in left..right -> width - minEdgeDistance
            else -> width + minEdgeDistance
        }
    }

    companion object {
        fun GeoRectangle.convertToWorldRects(mapProjection: MapProjection): List<Rect<World>> {
            return splitByAntiMeridian()
                .map { rect ->
                    ProjectionUtil.transformBBox(rect) { mapProjection.project(it) }
                }
        }
    }
}
