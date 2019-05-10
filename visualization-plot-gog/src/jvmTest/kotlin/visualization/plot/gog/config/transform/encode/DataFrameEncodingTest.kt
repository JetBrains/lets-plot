package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.data.TransformVar
import jetbrains.datalore.visualization.plot.gog.core.data.stat.Stats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DataFrameEncodingTest {

    @Test
    fun encodeEmpty() {
        val df = DataFrame.Builder.emptyFrame()
        // --------------------------
        val encoded = DataFrameEncoding.encode(df)
        assertTrue(DataFrameEncoding.isEncodedDataFrame(encoded))
        // --------------------------
        val decoded = DataFrameEncoding.decode(encoded)
        assertEquals(0, decoded.variables().size.toLong())
    }

    @Test
    fun encodeNumeric() {
        val vars = listOf(
                Variable("a", Variable.Source.ORIGIN, "a-lab"),
                TransformVar.X,
                Stats.COUNT
        )

        val vectors = listOf(
                DOUBLES_ZOO,
                //Collections.emptyList(),
                DOUBLES_ZOO,
                DOUBLES_ZOO
        )

        val b = DataFrame.Builder()
        for (i in vars.indices) {
            b.putNumeric(vars[i], vectors[i])
        }

        val df = b.build()

        // --------------------------
        val encoded = DataFrameEncoding.encode(df)
        assertTrue(DataFrameEncoding.isEncodedDataFrame(encoded))

        // --------------------------
        val decoded = DataFrameEncoding.decode(encoded)

        for (i in vars.indices) {
            val variable = vars[i]
            val vect = vectors[i]

            val decodedVar = DataFrameUtil.findVariableOrFail(decoded, variable.name)
            assertEqualVars(variable, decodedVar)
            assertTrue(decoded.isNumeric(decodedVar))
            val decodedVector = decoded.getNumeric(decodedVar)

            //System.out.println("DataFrameEncodingTest.encodeNumeric expected: " + vect);
            //System.out.println("DataFrameEncodingTest.encodeNumeric actual  : " + decodedVector);
            assertEquals(
                    toExpected(vect),
                    decodedVector
            )
        }
    }

    @Test
    fun encodeNotNumeric() {
        val vars = listOf(
                Variable("a", Variable.Source.ORIGIN, "a-lab"),
                Variable("b", Variable.Source.ORIGIN, "b-lab"),
                Variable("c", Variable.Source.ORIGIN, "c-lab")
        )

        val vectors = listOf(
                listOf("1", null, "", Double.NaN, 2.0),
                //Collections.emptyList(),
                listOf(100, "test", 200, 300, "+"),
                listOf("100", "test", 200, null, "-")
        )

        val b = DataFrame.Builder()
        for (i in vars.indices) {
            b.put(vars[i], vectors[i])
        }

        val df = b.build()

        // --------------------------
        val encoded = DataFrameEncoding.encode(df)
        assertTrue(DataFrameEncoding.isEncodedDataFrame(encoded))

        // --------------------------
        val decoded = DataFrameEncoding.decode(encoded)

        for (i in vars.indices) {
            val variable = vars[i]
            val vect = vectors[i]

            val decodedVar = DataFrameUtil.findVariableOrFail(decoded, variable.name)
            assertEqualVars(variable, decodedVar)
            assertFalse(decoded.isNumeric(decodedVar))
            val decodedVector = decoded[decodedVar]

            assertEquals(
                    vect,
                    decodedVector
            )
        }
    }

    companion object {
        private val DOUBLES_ZOO = listOf(
                777.77,
                -777.77,
                0.0,
                Double.NaN, null,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.MAX_VALUE,
                Double.MIN_VALUE
        )

        private fun toExpected(l: List<Double?>): List<Double> {
            val result = ArrayList<Double>()
            for (d in l) {
                if (d == null) {
                    result.add(Double.NaN)
                } else {
                    result.add(d)
                }
            }
            return result
        }

        private fun assertEqualVars(v0: Variable, v1: Variable) {
            assertEquals(v0.name, v1.name)
            assertEquals(v0.label, v1.label)
            assertEquals(v0.source, v1.source)
        }
    }
}