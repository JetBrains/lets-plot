package jetbrains.datalore.visualization.plot.gog.core.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.test.Test
import kotlin.test.assertEquals

class CoordsTest {

    private val coordinateSystem = Coords.create(X_RANGE, Y_RANGE)

    @Test
    fun simpleConversion() {
        val clientPos = DoubleVector(150.0, 240.0)
        val fromClient = coordinateSystem.fromClient(clientPos)
        val toClient = coordinateSystem.toClient(fromClient)

        assertEquals(clientPos, toClient)
    }

    @Test
    fun lessThanLowerRangeConversion() {
        val clientPos = DoubleVector(50.0, 40.0)
        val fromClient = coordinateSystem.fromClient(clientPos)
        val toClient = coordinateSystem.toClient(fromClient)

        assertEquals(clientPos, toClient)
    }

    @Test
    fun moreThanUpperRangeConversion() {
        val clientPos = DoubleVector(500.0, 4000.0)
        val fromClient = coordinateSystem.fromClient(clientPos)
        val toClient = coordinateSystem.toClient(fromClient)

        assertEquals(clientPos, toClient)
    }

    companion object {

        private const val X_LOWER = 100.0
        private const val X_UPPER = 400.0

        private const val Y_LOWER = 200.0
        private const val Y_UPPER = 600.0
        private val X_RANGE = ClosedRange.closed(X_LOWER, X_UPPER)
        private val Y_RANGE = ClosedRange.closed(Y_LOWER, Y_UPPER)
    }
}
