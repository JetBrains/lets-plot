package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

class GeoRectangle(minLongitude: Double, minLatitude: Double, maxLongitude: Double, maxLatitude: Double) {
    private val myLongitudeRange: LongitudeRange
    private val myLatitudeRange: ClosedRange<Double>

    val isEmpty: Boolean
        get() = myLongitudeRange.isEmpty && latitudeRangeIsEmpty(myLatitudeRange)

    private fun latitudeRangeIsEmpty(range: ClosedRange<Double>): Boolean {
        return range.upperEndpoint() == range.lowerEndpoint()
    }

    init {
        if (minLatitude > maxLatitude) {
            throw IllegalArgumentException("Invalid latitude range: [$minLatitude..$maxLatitude]")
        }

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

    fun splitByAntiMeridian(): List<DoubleRectangle> {
        val rects = ArrayList<DoubleRectangle>()

        val longitudeRanges = myLongitudeRange.splitByAntiMeridian()
        for (longitudeRange in longitudeRanges) {
            rects.add(DoubleRectangle.span(
                    DoubleVector(longitudeRange.lowerEndpoint(), myLatitudeRange.lowerEndpoint()),
                    DoubleVector(longitudeRange.upperEndpoint(), myLatitudeRange.upperEndpoint())))
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
