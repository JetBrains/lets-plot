/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.test.Test
import kotlin.test.assertEquals

class CoordsTest {

    private val coordinateSystem = Coords.create(
        X_RANGE,
        Y_RANGE
    )

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
        private val X_RANGE = DoubleSpan(
            X_LOWER,
            X_UPPER
        )
        private val Y_RANGE = DoubleSpan(
            Y_LOWER,
            Y_UPPER
        )
    }
}
