/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class IsBeyondPrecisionTest(
    private val baseValue: Double,
    private val deltaValue: Double,
    private val expected: Boolean,
) {

    @Test
    fun test() {
        assertEquals(expected, SeriesUtil.isBeyondPrecision(baseValue, deltaValue))
    }


    companion object {
        private fun args(
            base: Double,
            delta: Double,
            expected: Boolean
        ): Array<Any?> {
            return arrayOf(base, delta, expected)
        }

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Array<Array<Any?>> {
            return arrayOf(
                args(1.0, 1E+20, false),
                args(1.0, 1.0, false),
                args(1.0, 1E-5, false),
                args(1.0, 1E-10, false),
                args(1.0, 1E-12, false),
                args(1.0, 1E-13, true),

                args(-1.0, 1E-12, false),
                args(1E-15, 1.0, false),
                args(1E-15, 1E-13, false),
                args(1E-15, 1E-27, false),
                args(1E-15, 1E-28, true),
            )
        }
    }
}
