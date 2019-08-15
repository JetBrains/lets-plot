package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.MapWidgetUtil.calculateExtendedRectangleWithCenter
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.ProjectionUtil.transformBBox

class MapLocationGeocoder(
    private val myGeocodingService: GeocodingService,
    private val myMapRuler: MapRuler,
    private val myMapProjection: MapProjection
) {

    fun geocodeMapRegion(mapRegion: MapRegion): Async<DoubleRectangle> {
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

            val boundingBox: DoubleRectangle
            if (features.size == 1) {
                val feature = features.get(0)
                boundingBox = calculateExtendedRectangleWithCenter(
                    myMapRuler,
                    calculateBBoxOfGeoRect(feature.position!!),
                    myMapProjection.project(feature.centroid!!)
                )
            } else {
                val positions = ArrayList<GeoRectangle>()
                features.forEach { feature -> positions.add(feature.position!!) }
                boundingBox = calculateBBoxOfGeoRects(positions)
            }
            boundingBox
        }
    }

    fun calculateBBoxOfGeoRect(geoRectangle: GeoRectangle): DoubleRectangle {
        return myMapRuler.calculateBoundingBox(convertToXYRects(geoRectangle, myMapProjection))
    }

    private fun calculateBBoxOfGeoRects(geoRects: List<GeoRectangle>): DoubleRectangle {
        val xyRects = ArrayList<DoubleRectangle>()
        geoRects.forEach { geoRect -> xyRects.addAll(convertToXYRects(geoRect, myMapProjection)) }
        return myMapRuler.calculateBoundingBox(xyRects)
    }

    companion object {

        fun convertToXYRects(geoRect: GeoRectangle, mapProjection: MapProjection): List<DoubleRectangle> {
            val xyRects = ArrayList<DoubleRectangle>()
            geoRect.splitByAntiMeridian().forEach { lonLatRect ->
                xyRects.add(
                    transformBBox(lonLatRect, mapProjection::project)
                )
            }
            return xyRects
        }
    }
}
