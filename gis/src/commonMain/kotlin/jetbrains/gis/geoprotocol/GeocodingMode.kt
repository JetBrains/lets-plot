package jetbrains.gis.geoprotocol

enum class GeocodingMode private constructor(private val myValue: String) {
    BY_ID("by_id"),
    BY_NAME("by_geocoding"),
    REVERSE("reverse");

    override fun toString(): String {
        return myValue
    }
}
