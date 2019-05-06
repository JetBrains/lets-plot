package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.SimpleStatContext
import jetbrains.datalore.visualization.plot.gog.core.data.StatContext
import jetbrains.datalore.visualization.plot.gog.core.data.TransformVar
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BoxplotStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    private fun df(m: Map<DataFrame.Variable, List<Double>>): DataFrame {
        val b = DataFrame.Builder()
        for (key in m.keys) {
            b.putNumeric(key, m[key]!!)
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
                val `var` = stat.getDefaultMapping(aes)
                assertTrue("Has var " + `var`.name, statDf.has(`var`))
                assertEquals("Get var " + `var`.name, 0, statDf[`var`].size.toLong())
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
            assertTrue("Has var " + variable.name, statDf.has(variable))
            assertEquals("Size var " + variable.name, 1, statDf[variable].size.toLong())
            assertEquals("Get var " + variable.name, expected, statDf[variable][0])
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
        assertEquals(1, statDf[Stats.WIDTH].size.toLong())
        assertEquals(1.0, statDf[Stats.WIDTH][0])
    }
}