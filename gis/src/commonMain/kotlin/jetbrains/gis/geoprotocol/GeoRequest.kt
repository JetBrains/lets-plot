package jetbrains.gis.geoprotocol


import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.QuadKey


interface GeoRequest {

    val features: List<FeatureOption>
    val tiles: Map<String, List<QuadKey>>?
    val levelOfDetails: LevelOfDetails?

    enum class FeatureOption private constructor(private val myValue: String) {
        HIGHLIGHTS("highlights"),
        POSITION("position"),
        CENTROID("centroid"),
        LIMIT("limit"),
        BOUNDARY("boundary"),
        TILES("tiles");

        override fun toString(): String {
            return myValue
        }
    }


    interface ExplicitSearchRequest : GeoRequest {
        val ids: List<String>
    }


    interface GeocodingSearchRequest : GeoRequest {

        val queries: List<RegionQuery>
        val level: FeatureLevel?
        val namesakeExampleLimit: Int

        class AmbiguityResolver private constructor(
            val ignoringStrategy: IgnoringStrategy?,
            val closestCoord: DoubleVector?,
            val box: DoubleRectangle?
        ) {

            val isEmpty: Boolean
                get() = !closestCoord.isPresent() && !ignoringStrategy.isPresent() && !box.isPresent()

            enum class IgnoringStrategy {
                SKIP_ALL,
                SKIP_MISSING,
                SKIP_NAMESAKES,
                TAKE_NAMESAKES
            }

            companion object {

                fun ignoring(strategy: IgnoringStrategy): AmbiguityResolver {
                    return AmbiguityResolver(null, null, null)
                }

                fun closestTo(closestCoord: DoubleVector): AmbiguityResolver {
                    return AmbiguityResolver(null, closestCoord, null)
                }

                fun within(box: DoubleRectangle): AmbiguityResolver {
                    return AmbiguityResolver(null, null, box)
                }

                fun empty(): AmbiguityResolver {
                    return AmbiguityResolver(null, null, null)
                }
            }
        }

        class RegionQuery internal constructor(
            val names: List<String>,
            val parent: MapRegion?,
            val ambiguityResolver: AmbiguityResolver
        )
    }

    interface ReverseGeocodingSearchRequest : GeoRequest {
        val coordinates: List<DoubleVector>
        val level: FeatureLevel
        val parent: MapRegion?
    }

}

fun <T : Any?> T?.isPresent(): Boolean {
    return this != null
}
