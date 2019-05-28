package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoBoundingBoxCalculator.Companion.BOTTOM_GETTER
import jetbrains.datalore.base.projectionGeometry.GeoBoundingBoxCalculator.Companion.LEFT_GETTER
import jetbrains.datalore.base.projectionGeometry.GeoBoundingBoxCalculator.Companion.RIGHT_GETTER
import jetbrains.datalore.base.projectionGeometry.GeoBoundingBoxCalculator.Companion.TOP_GETTER
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

    fun assertRectangleEquals(expected: DoubleRectangle, actual: DoubleRectangle) {
        assertDoubleParameterEquals(expected, actual, LEFT_GETTER)
        assertDoubleParameterEquals(expected, actual, TOP_GETTER)
        assertDoubleParameterEquals(expected, actual, RIGHT_GETTER)
        assertDoubleParameterEquals(expected, actual, BOTTOM_GETTER)
    }
}
