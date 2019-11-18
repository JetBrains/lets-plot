/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.spatial.GeoUtils.FULL_LONGITUDE
import jetbrains.datalore.base.spatial.GeoUtils.MAX_LONGITUDE
import jetbrains.datalore.base.spatial.GeoUtils.MIN_LONGITUDE
import jetbrains.datalore.base.spatial.GeoUtils.limitLon


internal class LongitudeRange(lower: Double, upper: Double) {
    private val myLower: Double = limitLon(lower)
    private val myUpper: Double = limitLon(upper)

    val isEmpty: Boolean
        get() = myUpper == myLower

    fun lower(): Double {
        return myLower
    }

    fun upper(): Double {
        return myUpper
    }

    fun length(): Double {
        return myUpper - myLower + if (myUpper < myLower) FULL_LONGITUDE else 0.0
    }

    fun encloses(longitudeRange: LongitudeRange): Boolean {
        val externalRanges = splitByAntiMeridian()
        val internalRanges = longitudeRange.splitByAntiMeridian()

        for (internalRange in internalRanges) {
            if (!disjointRangesEncloseRange(externalRanges, internalRange)) {
                return false
            }
        }
        return true
    }

    fun invert(): LongitudeRange {
        return LongitudeRange(myUpper, myLower)
    }

    fun splitByAntiMeridian(): List<ClosedRange<Double>> {
        val result = ArrayList<ClosedRange<Double>>()
        splitRange(myLower, myUpper, MIN_LONGITUDE, MAX_LONGITUDE, result)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as LongitudeRange
        return that.myLower.equals(myLower) && that.myUpper.equals(myUpper)
    }

    override fun hashCode(): Int {
        return listOf(myLower, myUpper).hashCode()
    }

    companion object {
        fun splitRange(lower: Double, upper: Double, min: Double, max: Double, result: MutableCollection<ClosedRange<Double>>) {
            if (upper < lower) {
                result.add(ClosedRange.closed(lower, max))
                result.add(ClosedRange.closed(min, upper))
            } else {
                result.add(ClosedRange.closed(lower, upper))
            }
        }

        private fun disjointRangesEncloseRange(ranges: List<ClosedRange<Double>>, internalRange: ClosedRange<Double>): Boolean {
            for (range in ranges) {
                if (range.encloses(internalRange)) {
                    return true
                }
            }
            return false
        }
    }
}
