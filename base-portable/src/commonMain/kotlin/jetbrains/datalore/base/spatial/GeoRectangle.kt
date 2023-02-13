/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.typedGeometry.Rect

class GeoRectangle(startLongitude: Double, minLatitude: Double, endLongitude: Double, maxLatitude: Double) {
    private val myLongitudeSegment: LongitudeSegment
    private val myLatitudeRange: DoubleSpan

    val isEmpty: Boolean
        get() = myLongitudeSegment.isEmpty && latitudeRangeIsEmpty(myLatitudeRange)

    private fun latitudeRangeIsEmpty(range: DoubleSpan): Boolean {
        return range.upperEnd == range.lowerEnd
    }

    init {
        require(minLatitude <= maxLatitude) { "Invalid latitude range: [$minLatitude..$maxLatitude]" }

        myLongitudeSegment = LongitudeSegment(startLongitude, endLongitude)
        myLatitudeRange = DoubleSpan(minLatitude, maxLatitude)
    }

    fun startLongitude(): Double = myLongitudeSegment.start()
    fun endLongitude(): Double = myLongitudeSegment.end()

    fun minLatitude(): Double = myLatitudeRange.lowerEnd
    fun maxLatitude(): Double = myLatitudeRange.upperEnd

    fun encloses(rect: GeoRectangle): Boolean {
        return myLongitudeSegment.encloses(rect.myLongitudeSegment) && myLatitudeRange.encloses(rect.myLatitudeRange)
    }

    fun splitByAntiMeridian(): List<Rect<LonLat>> {
        val rects = ArrayList<Rect<LonLat>>()

        val longitudeRanges = myLongitudeSegment.splitByAntiMeridian()
        for (longitudeRange in longitudeRanges) {
            rects.add(
                Rect.LTRB(
                    longitudeRange.lowerEnd, myLatitudeRange.lowerEnd,
                    longitudeRange.upperEnd, myLatitudeRange.upperEnd
                )
            )
        }
        return rects
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as GeoRectangle?
        return myLongitudeSegment == that!!.myLongitudeSegment && myLatitudeRange == that.myLatitudeRange
    }

    override fun hashCode(): Int {
        return listOf(myLongitudeSegment, myLatitudeRange).hashCode()
    }
}
