/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataFrameUtilTest {
    @Test
    fun originalVariablesPreserved() {
        val x0 = DataFrame.Variable("x")
        val df0 = DataFrame.Builder()
            .putNumeric(x0, listOf(0.0, 1.0))
            .build()

        val df1 = DataFrame.Builder()
            .putNumeric(DataFrame.Variable("x"), listOf(2.0, 3.0, 4.0))
            .build()

        val result = DataFrameUtil.appendReplace(df0, df1)
        assertTrue(result.has(x0))
        assertEquals(listOf(2.0, 3.0, 4.0), result.getNumeric(x0))
    }

}