/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.newSpanRectangle

class GeoRectangle(startLongitude: Double, minLatitude: Double, endLongitude: Double, maxLatitude: Double) {
    private val myLongitudeSegment: LongitudeSegment
    private val myLatitudeRange: ClosedRange<Double>

    val isEmpty: Boolean
        get() = myLongitudeSegment.isEmpty && latitudeRangeIsEmpty(myLatitudeRange)

    private fun latitudeRangeIsEmpty(range: ClosedRange<Double>): Boolean {
        return range.upperEndpoint() == range.lowerEndpoint()
    }

    init {
        require(minLatitude <= maxLatitude) { "Invalid latitude range: [$minLatitude..$maxLatitude]" }

        myLongitudeSegment = LongitudeSegment(startLongitude, endLongitude)
        myLatitudeRange = ClosedRange.closed(minLatitude, maxLatitude)
    }

    fun startLongitude(): Double = myLongitudeSegment.start()
    fun endLongitude(): Double = myLongitudeSegment.end()

    fun minLatitude(): Double = myLatitudeRange.lowerEndpoint()
    fun maxLatitude(): Double = myLatitudeRange.upperEndpoint()

    fun encloses(rect: GeoRectangle): Boolean {
        return myLongitudeSegment.encloses(rect.myLongitudeSegment) && myLatitudeRange.encloses(rect.myLatitudeRange)
    }

    fun splitByAntiMeridian(): List<Rect<LonLat>> {
        val rects = ArrayList<Rect<LonLat>>()

        val longitudeRanges = myLongitudeSegment.splitByAntiMeridian()
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
        return myLongitudeSegment == that!!.myLongitudeSegment && myLatitudeRange == that.myLatitudeRange
    }

    override fun hashCode(): Int {
        return listOf(myLongitudeSegment, myLatitudeRange).hashCode()
    }
}
