package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle

interface GeoResponse {

    class SuccessGeoResponse internal constructor(
        val features: List<GeocodedFeature>,
        val featureLevel: FeatureLevel?
    ) : GeoResponse {

        class GeocodedFeature internal constructor(
            val request: String,
            val id: String,
            val name: String,
            val highlights: List<String>,
            val centroid: DoubleVector,
            val position: GeoRectangle,
            val limit: GeoRectangle,
            val boundary: Geometry,
            val tiles: List<GeoTile>
        )
    }

    class ErrorGeoResponse(val message: String) : GeoResponse

    class AmbiguousGeoResponse internal constructor(
        val features: List<AmbiguousFeature>,
        val featureLevel: FeatureLevel?
    ) : GeoResponse {

        class AmbiguousFeature internal constructor(
            val request: String,
            val namesakeCount: Int,
            val namesakes: List<Namesake>
        )

        class Namesake internal constructor(val name: String, val parents: List<NamesakeParent>)

        class NamesakeParent(val name: String, val level: FeatureLevel)
    }
}
