/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.interval.DoubleSpan


internal class LongitudeSegment(start: Double, end: Double) {
    private val myStart: Double = limitLon(start)
    private val myEnd: Double = limitLon(end)

    val isEmpty: Boolean = myEnd == myStart
    fun start(): Double = myStart
    fun end(): Double = myEnd
    fun length(): Double = myEnd - myStart + if (myEnd < myStart) FULL_LONGITUDE else 0.0
    fun invert(): LongitudeSegment = LongitudeSegment(myEnd, myStart)

    fun encloses(longitudeSegment: LongitudeSegment): Boolean {
        val externalRanges = splitByAntiMeridian()
        val internalRanges = longitudeSegment.splitByAntiMeridian()

        for (internalRange in internalRanges) {
            if (!disjointRangesEncloseRange(
                    externalRanges,
                    internalRange
                )
            ) {
                return false
            }
        }
        return true
    }


    fun splitByAntiMeridian(): List<DoubleSpan> = splitSegment(myStart, myEnd, MIN_LONGITUDE, MAX_LONGITUDE)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as LongitudeSegment
        return that.myStart.equals(myStart) && that.myEnd.equals(myEnd)
    }

    override fun hashCode(): Int {
        return listOf(myStart, myEnd).hashCode()
    }

    companion object {
        fun splitSegment(
            start: Double,
            end: Double,
            min: Double,
            max: Double
        ) : List<DoubleSpan> {
            return if (start <= end) {
                listOf(DoubleSpan(start, end))
            } else {
                listOf(
                    DoubleSpan(start, max),
                    DoubleSpan(min, end)
                )
            }
        }

        private fun disjointRangesEncloseRange(
            ranges: List<DoubleSpan>,
            internalRange: DoubleSpan
        ): Boolean {
            for (range in ranges) {
                if (range.encloses(internalRange)) {
                    return true
                }
            }
            return false
        }
    }
}
