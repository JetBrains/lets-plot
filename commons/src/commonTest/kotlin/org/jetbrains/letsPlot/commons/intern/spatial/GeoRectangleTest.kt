/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial


import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangleTestHelper.rectangle
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoRectangleTest {

    @Test
    fun enclosesRectangleWithPositiveCoordinate() {
        assertTrue(
            FIRST_RECTANGLE.encloses(
                SECOND_RECTANGLE
            ))
    }

    @Test
    fun enclosesRectangleCrossedAntiMeridian() {
        assertTrue(
            FIRST_RECTANGLE.encloses(
                THIRD_RECTANGLE
            ))
    }

    @Test
    fun enclosesRectangleWithNegativeCoordinate() {
        assertTrue(
            FIRST_RECTANGLE.encloses(
                FOURTH_RECTANGLE
            ))
    }

    @Test
    fun enclosesRectangleWithWrongLongitude() {
        assertFalse(
            FIRST_RECTANGLE.encloses(
                FIFTH_RECTANGLE
            ))
    }

    @Test
    fun enclosesRectangleWithWrongLatitude() {
        assertFalse(
            FIRST_RECTANGLE.encloses(
                SIXTH_RECTANGLE
            ))
    }

    @Test
    fun getExceptionTryingCreateMixedLatitudeRectangle() {
        val minLat = TOP_LATITUDE
        val maxLat = BOTTOM_LATITUDE

        expectInvalidLatitudeException(minLat, maxLat) {
            rectangle(
                FIRST_LONGITUDE, minLat,
                SECOND_LONGITUDE, maxLat)
        }
    }

    private fun expectInvalidLatitudeException(minLat: Double, maxLat: Double, block: () -> Unit) {
        val expectMessage = "Invalid latitude range: [$minLat..$maxLat]"
        assertFailsWith(IllegalArgumentException::class, expectMessage, block)
    }

    companion object {
        private val FIRST_RECTANGLE = rectangle(160.0, 30.0, -140.0, 58.0)
        private val SECOND_RECTANGLE = rectangle(-175.0, 50.0, -150.0, 58.0)
        private val THIRD_RECTANGLE = rectangle(170.0, 30.0, -178.0, 48.0)
        private val FOURTH_RECTANGLE = rectangle(172.0, 40.0, 178.0, 50.0)
        private val FIFTH_RECTANGLE = rectangle(-20.0, 45.0, 40.0, 53.0)
        private val SIXTH_RECTANGLE = rectangle(172.0, 20.0, 178.0, 45.0)
        private val TOP_LATITUDE = 63.0
        private val BOTTOM_LATITUDE = 42.0
        private val FIRST_LONGITUDE = 15.0
        private val SECOND_LONGITUDE = 37.0
    }
}