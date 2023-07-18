/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

import demoAndTestShared.assertEquals
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegularMeshDetectorTest {
    private fun genRows(numRows: Int, rowSize: Int, step: Double, maxError: Double, ascending: Boolean): List<Double> {
        val r = Random(31)
        val sign = if (ascending) 1 else -1
        val values = ArrayList<Double>()
        for (i in 0 until rowSize * numRows) {
            val colIndex = i % rowSize
            val errorSign = if (r.nextBoolean()) 1 else -1
            val `val` = colIndex * step + r.nextDouble() * maxError * errorSign.toDouble()
            values.add(`val` * sign)
        }
        return values
    }

    private fun genColumns(numRows: Int, rowSize: Int, step: Double, maxError: Double, ascending: Boolean): List<Double> {
        val r = Random(31)
        val sign = if (ascending) 1 else -1
        val values = ArrayList<Double>()
        for (i in 0 until rowSize * numRows) {
            val rowIndex = i / rowSize
            val errorSign = if (r.nextBoolean()) 1 else -1
            val `val` = rowIndex * step + r.nextDouble() * maxError * errorSign.toDouble()
            values.add(`val` * sign)
        }
        return values
    }

    private fun assertByRowGridDetected(rowSize: Int, step: Double, error: Double, values: Iterable<Double>) {
        run {
            val detector = RegularMeshDetector.tryRow(
                rowSize,
                error * 3,
                values
            )
            assertTrue(detector.isMesh)
            assertEquals(step, detector.resolution, error * 3)
        }
        run {
            val detector = RegularMeshDetector.tryRow(
                rowSize - 1,
                error * 3,
                values
            )
            assertTrue(detector.isMesh)
            assertEquals(step, detector.resolution, error * 3)
        }
        run {
            val detector = RegularMeshDetector.tryRow(
                rowSize + 1,
                error * 3,
                values
            )
            assertFalse(detector.isMesh)
        }

        val detector = RegularMeshDetector.tryColumn(
            rowSize,
            error * 3,
            values
        )
        assertFalse(detector.isMesh)
    }

    private fun assertByColGridDetected(rowSize: Int, step: Double, error: Double, values: Iterable<Double>) {
        run {
            val detector = RegularMeshDetector.tryColumn(
                rowSize,
                error * 3,
                values
            )
            assertTrue(detector.isMesh)
            assertEquals(step, detector.resolution, error * 3)
        }
        run {
            val detector = RegularMeshDetector.tryColumn(
                rowSize - 1,
                error * 3,
                values
            )
            assertTrue(detector.isMesh)
            assertEquals(step, detector.resolution, error * 3)
        }
        run {
            val detector = RegularMeshDetector.tryColumn(
                rowSize + 1,
                error * 3,
                values
            )
            assertFalse(detector.isMesh)
        }

        val detector = RegularMeshDetector.tryRow(
            rowSize,
            error * 3,
            values
        )
        assertFalse(detector.isMesh)
    }

    @Test
    fun rowAscending() {
        val step = 1.0
        val error = 0.01
        val rowSize = 10
        val values = genRows(2, rowSize, step, error, true)
        assertByRowGridDetected(rowSize, step, error, values)
    }

    @Test
    fun rowDescending() {
        val step = 1.0
        val error = 0.01
        val rowSize = 10
        val values = genRows(2, rowSize, step, error, false)
        assertByRowGridDetected(rowSize, step, error, values)
    }

    @Test
    fun rowAscendingAuto() {
        val rawValues = ArrayList<Double>()
        val values = ArrayList<Double>()
        val rowSize = 60
        val start = 101.0
        val inc = 1.03
        for (i in 0 until rowSize) {
            rawValues.add(start + inc * i)
        }

        val mapper = 0.17
        for (i in 0 until rowSize) {
            values.add(rawValues[i] * mapper)
        }

        val detector = RegularMeshDetector.tryRow(values)
        assertTrue(detector.isMesh)
        //      Assert.assertEquals(step, detector.getResolution(), SeriesUtil.TINY);
    }


    @Test
    fun columnAscending() {
        val step = 1.0
        val error = 0.01
        val rowSize = 10
        val values = genColumns(2, rowSize, step, error, true)
        assertByColGridDetected(rowSize, step, error, values)
    }

    @Test
    fun columnDescending() {
        val step = 1.0
        val error = 0.01
        val rowSize = 10
        val values = genColumns(2, rowSize, step, error, false)
        assertByColGridDetected(rowSize, step, error, values)
    }
}