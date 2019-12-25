/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.*
import kotlin.test.assertEquals

internal object GeoRectangleTestHelper {
//    private const val DOUBLE_INACCURACY = 0.01

    fun point(lon: Double, lat: Double): DoubleVector {
        return DoubleVector(lon, lat)
    }

    fun rectangle(minLon: Double, minLat: Double, maxLon: Double, maxLat: Double): GeoRectangle {
        return GeoRectangle(minLon, minLat, maxLon, maxLat)
    }

    fun assertDoubleEquals(expected: Double, actual: Double) {
//        assertEquals(expected, actual, DOUBLE_INACCURACY)
        assertEquals(expected, actual)
    }

    private fun <T> assertDoubleParameterEquals(expected: T, actual: T, getter: (T) -> Double) {
        assertDoubleEquals(getter(expected), getter(actual))
    }

    fun assertRectangleEquals(expected: Rect<*>, actual: Rect<*>) {
        assertDoubleParameterEquals(expected, actual, Rect<*>::left)
        assertDoubleParameterEquals(expected, actual, Rect<*>::top)
        assertDoubleParameterEquals(expected, actual, Rect<*>::right)
        assertDoubleParameterEquals(expected, actual, Rect<*>::bottom)
    }
}
