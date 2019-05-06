package jetbrains.datalore.visualization.plot.gog.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class DataFrameAssert internal constructor(private val myData: DataFrame) {

    internal fun hasRowCount(expected: Int): DataFrameAssert {
        assertEquals("Row count", expected.toLong(), myData.rowCount().toLong())
        return this
    }

    internal fun hasSerie(varName: String, serie: List<*>): DataFrameAssert {
        assertTrue("Var '$varName'", DataFrameUtil.hasVariable(myData, varName))
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
                assertTrue("Has var '" + `var`.name + "'", df.has(`var`))
                if (dataSize >= 0) {
                    assertEquals("Data siaze '" + `var`.name + "'", dataSize.toLong(), df[`var`].size.toLong())
                }
            }
        }

        fun assertNumericVectorEquals(df: DataFrame, vars: Iterable<DataFrame.Variable>, dataSize: Int) {

        }
    }
}
