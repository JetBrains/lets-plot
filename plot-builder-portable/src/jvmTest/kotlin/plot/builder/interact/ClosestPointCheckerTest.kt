/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker.COMPARISON_RESULT
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker.COMPARISON_RESULT.*
import jetbrains.datalore.plot.builder.interact.TestUtil.coord
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClosestPointCheckerTest {
    private var cp: ClosestPointChecker? = null
    private val dist10 = coord(10.0, 0.0)
    private val dist5 = coord(5.0, 0.0)
    private val dist15 = coord(15.0, 0.0)

    @BeforeTest
    fun setUp() {
        cp = ClosestPointChecker(0.0, 0.0)
    }

    @Test
    fun firstCheckAlwaysClosest() {
        assertCall(
                check(dist10).assertResult(true))
    }

    @Test
    fun whenCheckSameCoordTwice_ShouldReturnAlwaysTrue() {
        assertCall(
            check(dist10).assertResult(true),
            check(dist10).assertResult(true)
        )
    }

    @Test
    fun whenCheckCloserCoord_ShouldReturnTrue() {
        assertCall(
                check(dist10).assertResult(true),
                check(dist5).assertResult(true)
        )
    }

    @Test
    fun whenCheckNotCloserCoord_ShouldReturnFalse() {
        assertCall(
                check(dist10).assertResult(true),
                check(dist15).assertResult(false)
        )
    }

    @Test
    fun firstCompareAlwaysClosest() {
        assertCall(
                compare(dist10).assertResult(NEW_CLOSER))
    }

    @Test
    fun whenCompareSameCoordTwice_ShouldReturnTrueFirstTimeFalseSecondTime() {
        assertCall(
                compare(dist10).assertResult(NEW_CLOSER),
                compare(dist10).assertResult(EQUAL)
        )
    }

    @Test
    fun whenCompareCloserCoord_ShouldReturnTrue() {
        assertCall(
                compare(dist10).assertResult(NEW_CLOSER),
                compare(dist5).assertResult(NEW_CLOSER)
        )
    }

    @Test
    fun whenCompareNotCloserCoord_ShouldReturnFalse() {
        assertCall(
                compare(dist10).assertResult(NEW_CLOSER),
                compare(dist15).assertResult(NEW_FARTHER)
        )
    }

    private fun check(coord: DoubleVector): Expectation<Boolean> {
        return Expectation { cp!!.check(coord) }
    }

    private fun compare(coord: DoubleVector): Expectation<COMPARISON_RESULT> {
        return Expectation { cp!!.compare(coord) }
    }

    private fun assertCall(vararg assertions: Runnable) {
        for (assertion in assertions) {
            assertion.run()
        }
    }

    private class Expectation<TRes> internal constructor(private val myCall: () -> TRes) : Runnable {
        private var myExpectedResult: TRes? = null

        internal fun assertResult(expected: TRes): Expectation<TRes> {
            myExpectedResult = expected
            return this
        }

        override fun run() {
            val res = myCall()
            if (myExpectedResult != null) {
                assertEquals(myExpectedResult, res)
            }
        }
    }
}