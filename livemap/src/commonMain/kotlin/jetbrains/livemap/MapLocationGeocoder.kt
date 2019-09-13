package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.MapWidgetUtil.calculateExtendedRectangleWithCenter
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.ProjectionUtil.transformBBox
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

    fun calculateBBoxOfGeoRect(geoRectangle: GeoRectangle): Typed.Rectangle<World> {
        return myMapRuler.calculateBoundingBox(convertToXYRects(geoRectangle, myMapProjection))
    }

    private fun calculateBBoxOfGeoRects(geoRects: List<GeoRectangle>): Typed.Rectangle<World> {
        val xyRects = ArrayList<WorldRectangle>()
        geoRects.forEach { geoRect -> xyRects.addAll(convertToXYRects(geoRect, myMapProjection)) }
        return myMapRuler.calculateBoundingBox(xyRects)
    }

    companion object {

        fun convertToXYRects(geoRect: GeoRectangle, mapProjection: MapProjection): ArrayList<WorldRectangle> {
            val xyRects = ArrayList<WorldRectangle>()
            geoRect.splitByAntiMeridian()
                .map { LonLatRectangle(it.origin, it.dimension) }
                .forEach { rect ->
                    xyRects.add(transformBBox(rect) { mapProjection.project(it) })
                }
            return xyRects
        }
    }
}
