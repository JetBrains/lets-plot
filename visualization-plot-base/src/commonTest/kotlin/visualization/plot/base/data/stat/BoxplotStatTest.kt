package jetbrains.datalore.visualization.plot.base.data.stat

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.SimpleStatContext
import jetbrains.datalore.visualization.plot.base.data.StatContext
import jetbrains.datalore.visualization.plot.base.data.TransformVar
import jetbrains.datalore.visualization.plot.base.render.Aes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoxplotStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    private fun df(m: Map<DataFrame.Variable, List<Double>>): DataFrame {
        val b = DataFrame.Builder()
        for (key in m.keys) {
            b.putNumeric(key, m.getValue(key))
        }
        return b.build()
    }

    @Test
    fun emptyDataFrame() {
        val df = df(emptyMap())
        val stat = BoxplotStat()
        val statDf = stat.apply(df, statContext(df))

        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                val variable = stat.getDefaultMapping(aes)
                assertTrue(statDf.has(variable), "Has var " + variable.name)
                assertEquals(0, statDf[variable].size, "Get var " + variable.name)
            }
        }
    }

    @Test
    fun oneElementDataFrame() {
        val df = df(mapOf(
                TransformVar.X to listOf(3.3),
                TransformVar.Y to listOf(4.4)
        ))
        val stat = BoxplotStat()
        val statDf = stat.apply(df, statContext(df))

        // no 'width'
        assertTrue(!statDf.has(Stats.WIDTH))

        val checkVar: (DataFrame.Variable, Double) -> Unit = { variable: DataFrame.Variable, expected: Double ->
            assertTrue(statDf.has(variable), "Has var " + variable.name)
            assertEquals(1, statDf[variable].size, "Size var " + variable.name)
            assertEquals(expected, statDf[variable][0], "Get var " + variable.name)
        }

        checkVar(Stats.X, 3.3)

        val varsY = arrayOf(Stats.MIDDLE, Stats.LOWER, Stats.UPPER, Stats.Y_MIN, Stats.Y_MAX)
        for (`var` in varsY) {
            checkVar(`var`, 4.4)
        }
    }

    @Test
    fun varWidth() {
        val df = df(mapOf(
                TransformVar.X to listOf(3.3),
                TransformVar.Y to listOf(4.4)
        ))

        val stat = BoxplotStat()
        stat.setComputeWidth(true)                              // varWidth = True
        val statDf = stat.apply(df, statContext(df))

        // no 'width'
        assertTrue(statDf.has(Stats.WIDTH))
        assertEquals(1, statDf[Stats.WIDTH].size)
        assertEquals(1.0, statDf[Stats.WIDTH][0])
    }
}