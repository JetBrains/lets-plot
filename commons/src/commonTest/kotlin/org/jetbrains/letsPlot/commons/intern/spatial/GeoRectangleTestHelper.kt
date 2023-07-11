/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import kotlin.test.assertEquals

internal object GeoRectangleTestHelper {
//    private const val DOUBLE_INACCURACY = 0.01

    fun point(lon: Double, lat: Double): DoubleVector {
        return DoubleVector(lon, lat)
    }

    fun rectangle(minLon: Double, minLat: Double, maxLon: Double, maxLat: Double): GeoRectangle {
        return GeoRectangle(minLon, minLat, maxLon, maxLat)
    }

    private fun <T> assertDoubleParameterEquals(expected: T, actual: T, getter: (T) -> Double) {
        assertEquals(
            getter(expected),
            getter(actual)
        )
    }

    fun assertRectangleEquals(expected: Rect<*>, actual: Rect<*>) {
        assertDoubleParameterEquals(expected, actual, Rect<*>::left)
        assertDoubleParameterEquals(expected, actual, Rect<*>::top)
        assertDoubleParameterEquals(expected, actual, Rect<*>::right)
        assertDoubleParameterEquals(expected, actual, Rect<*>::bottom)
    }
}
