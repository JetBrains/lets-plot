package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange

class GeoRectangle(minLongitude: Double, minLatitude: Double, maxLongitude: Double, maxLatitude: Double) {
    private val myLongitudeRange: LongitudeRange
    private val myLatitudeRange: ClosedRange<Double>

    val isEmpty: Boolean
        get() = myLongitudeRange.isEmpty && latitudeRangeIsEmpty(myLatitudeRange)

    private fun latitudeRangeIsEmpty(range: ClosedRange<Double>): Boolean {
        return range.upperEndpoint() == range.lowerEndpoint()
    }

    init {
        require(minLatitude <= maxLatitude) { "Invalid latitude range: [$minLatitude..$maxLatitude]" }

        myLongitudeRange = LongitudeRange(minLongitude, maxLongitude)
        myLatitudeRange = ClosedRange.closed(minLatitude, maxLatitude)
    }

    fun minLongitude(): Double {
        return myLongitudeRange.lower()
    }

    fun minLatitude(): Double {
        return myLatitudeRange.lowerEndpoint()
    }

    fun maxLongitude(): Double {
        return myLongitudeRange.upper()
    }

    fun maxLatitude(): Double {
        return myLatitudeRange.upperEndpoint()
    }

    fun encloses(rect: GeoRectangle): Boolean {
        return myLongitudeRange.encloses(rect.myLongitudeRange) && myLatitudeRange.encloses(rect.myLatitudeRange)
    }

    fun splitByAntiMeridian(): List<Rect<LonLat>> {
        val rects = ArrayList<Rect<LonLat>>()

        val longitudeRanges = myLongitudeRange.splitByAntiMeridian()
        for (longitudeRange in longitudeRanges) {
            rects.add(
                newSpanRectangle(
                    Vec(longitudeRange.lowerEndpoint(), myLatitudeRange.lowerEndpoint()),
                    Vec(longitudeRange.upperEndpoint(), myLatitudeRange.upperEndpoint())
                )
            )
        }
        return rects
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as GeoRectangle?
        return myLongitudeRange == that!!.myLongitudeRange && myLatitudeRange == that.myLatitudeRange
    }

    override fun hashCode(): Int {
        return listOf(myLongitudeRange, myLatitudeRange).hashCode()
    }
}
