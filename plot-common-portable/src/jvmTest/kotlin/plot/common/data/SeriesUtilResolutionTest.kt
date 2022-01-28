/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class SeriesUtilResolutionTest(
    private val serie: List<Double?>,
    private val naValue: Double,
    private val expected: Double?,
) {

    @Test
    fun test() {
        assertEquals(expected, SeriesUtil.resolution(serie, naValue))
    }


    companion object {
        private fun args(
            serie: List<Double?>,
            naValue: Double = 0.0,
            expected: Double?,
        ): Array<Any?> {
            return arrayOf(serie, naValue, expected)
        }

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Array<Array<Any?>> {
            return arrayOf(
                args(
                    serie = listOf(1.0, 2.0, 3.0),
                    expected = 1.0
                ),
                args(
                    serie = listOf(1.0, 3.0, 2.0),
                    expected = 1.0
                ),
            )
        }
    }
}
