/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataFrameAssert internal constructor(private val myData: DataFrame) {

    internal fun hasRowCount(expected: Int): DataFrameAssert {
        assertEquals(expected, myData.rowCount(), "Row count")
        return this
    }

    internal fun hasSerie(varName: String, serie: List<*>): DataFrameAssert {
        assertTrue(DataFrameUtil.hasVariable(myData, varName), "Var '$varName'")
        val variable = DataFrameUtil.findVariableOrFail(myData, varName)
        val list = myData[variable]
        assertEquals(list, serie)
        return this
    }

    companion object {
        fun assertHasVars(df: DataFrame, vars: Iterable<DataFrame.Variable>, dataSize: Int = -1) {
            for (variable in vars) {
                assertTrue(df.has(variable), "Has var '${variable.name}'")
                if (dataSize >= 0) {
                    assertEquals(dataSize, df[variable].size, "Data size '${variable.name}'")
                }
            }
        }
    }
}
