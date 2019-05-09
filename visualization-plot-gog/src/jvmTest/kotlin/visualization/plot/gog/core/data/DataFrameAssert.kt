package jetbrains.datalore.visualization.plot.gog.core.data

import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataFrameAssert internal constructor(private val myData: DataFrame) {

    internal fun hasRowCount(expected: Int): DataFrameAssert {
        assertEquals(expected, myData.rowCount(), "Row count")
        return this
    }

    internal fun hasSerie(varName: String, serie: List<*>): DataFrameAssert {
        assertTrue(DataFrameUtil.hasVariable(myData, varName), "Var '$varName'")
        val `var` = DataFrameUtil.findVariableOrFail(myData, varName)
        val list = myData[`var`]
        val serie1 = serie
        assertEquals(list, serie1)
        return this
    }

    companion object {

        @JvmOverloads
        fun assertHasVars(df: DataFrame, vars: Iterable<DataFrame.Variable>, dataSize: Int = -1) {
            for (`var` in vars) {
                assertTrue(df.has(`var`), "Has var '" + `var`.name + "'")
                if (dataSize >= 0) {
                    assertEquals(dataSize, df[`var`].size, "Data siaze '" + `var`.name + "'")
                }
            }
        }

        fun assertNumericVectorEquals(df: DataFrame, vars: Iterable<DataFrame.Variable>, dataSize: Int) {

        }
    }
}
