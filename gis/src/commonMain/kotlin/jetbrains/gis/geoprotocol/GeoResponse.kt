package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.Point

interface GeoResponse {

    data class SuccessGeoResponse internal constructor(
        val features: List<GeocodedFeature>,
        val featureLevel: FeatureLevel?
    ) : GeoResponse {

        data class GeocodedFeature internal constructor(
            val request: String,
            val id: String,
            val name: String,
            val centroid: Point?,
            val position: GeoRectangle?,
            val limit: GeoRectangle?,
            val boundary: Geometry?,
            val highlights: List<String>?,
            val tiles: List<GeoTile>?
        )
    }

    data class ErrorGeoResponse(val message: String) : GeoResponse

    data class AmbiguousGeoResponse internal constructor(
        val features: List<AmbiguousFeature>,
        val featureLevel: FeatureLevel?
    ) : GeoResponse {

        data class AmbiguousFeature internal constructor(
            val request: String,
            val namesakeCount: Int,
            val namesakes: List<Namesake>
        )

        data class Namesake internal constructor(val name: String, val parents: List<NamesakeParent>)

        data class NamesakeParent(val name: String, val level: FeatureLevel)
    }
}
