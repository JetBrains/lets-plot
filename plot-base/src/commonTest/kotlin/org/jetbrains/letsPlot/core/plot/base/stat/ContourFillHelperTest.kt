/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.test.Test
import kotlin.test.assertEquals

class ContourFillHelperTest {

    private val myTest =
        ContourFillHelper(DoubleSpan(0.0, 1.0), DoubleSpan(0.0, 1.0))
    private val myLevels = listOf(0.5)
    private val myFillLevels = listOf(0.0, 1.0)
    private val myPathByLevels = HashMap<Double, List<List<DoubleVector>>>()
    private fun createFastDoubleVector(vararg d: Double): List<DoubleVector> {
        val result = ArrayList<DoubleVector>()
        var i = 0
        while (i < d.size / 2.0) {
            result.add(DoubleVector(d[2 * i], d[2 * i + 1]))
            i++
        }
        return result
    }

    @Test
    fun testCreatePolygonsNormal() {
        myPathByLevels[myLevels[0]] = listOf(createFastDoubleVector(.5, 0.0, .5, 1.0))

        val result = myTest.createPolygons(myPathByLevels, myLevels, myFillLevels)

        assertEquals(createFastDoubleVector(.5, 0.0, .5, 1.0, 0.0, 1.0, 0.0, 0.0, .5, 0.0), result[myFillLevels[0]])
        assertEquals(createFastDoubleVector(.5, 1.0, .5, 0.0, 1.0, 0.0, 1.0, 1.0, .5, 1.0), result[myFillLevels[1]])
    }

    @Test
    fun testCreatePolygonsEdge() {
        myPathByLevels[myLevels[0]] = listOf(createFastDoubleVector(0.0, 0.0, 1.0, 1.0))

        val result = myTest.createPolygons(myPathByLevels, myLevels, myFillLevels)

        assertEquals(createFastDoubleVector(0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0), result[myFillLevels[0]])
        assertEquals(createFastDoubleVector(1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0), result[myFillLevels[1]])
    }
}
