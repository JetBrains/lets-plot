package jetbrains.datalore.visualization.plot.gog.core.data.sampling

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.TestUtil
import kotlin.test.*

class SystematicSamplingTest {

    private val myData = TestUtil.generateData(N, listOf("x", "y", "c"))

    private fun assertThat(sampleSize: Int): SamplingAssert {
        return SamplingAssert(sampleSize)
    }

    @Test
    fun noop() {
        // not applicable with steps < 1.5
        assertThat(N + 1).isNotApplicable
        assertThat(N).isNotApplicable
        assertThat(N - 1).isNotApplicable
        assertThat(sampleSize(1.1)).isNotApplicable

        // applicable with steps >= 1.5
        assertThat(N / 2).isApplicable

        val failedSampling = RandomSampling(N, null)
        assertFalse(failedSampling.isApplicable(myData))

        assertFailsWith(RuntimeException::class) {
            failedSampling.apply(myData)
        }
    }

    @Test
    fun exactStep() {
        // step 3 is a perfect fit when last data index in 9, 99... (i.e. that size of data is 10, 100...)
        assertThat(sampleSize(3.0))
                .hasRowCount(sampleSize(3.0))
    }

    @Test
    fun roundDownStep() {
        // rounded to closest lesser int
        val targetStep = 3.1
        val expectedStep = 3
        assertThat(sampleSize(targetStep))
                .hasRowCount(sampleSize(expectedStep.toDouble()))
    }

    @Test
    fun roundUpStep() {
        // rounded to closest greater int
        val targetStep = 2.9
        val expectedStep = 3
        assertThat(sampleSize(targetStep))
                .hasRowCount(sampleSize(expectedStep.toDouble()))
    }

    @Test
    fun unfitStep() {
        assertThat(sampleSize(2.0))
                .hasRowCount(sampleSize(2.0))
    }


    private inner class SamplingAssert internal constructor(sampleSize: Int) {
        private val mySampling: SystematicSampling = SystematicSampling(sampleSize)
        private val mySample: DataFrame?

        internal val isApplicable: SamplingAssert
            get() {
                assertTrue(mySampling.isApplicable(myData))
                return this
            }

        internal val isNotApplicable: SamplingAssert
            get() {
                assertFalse(mySampling.isApplicable(myData))
                return this
            }

        init {
            if (mySampling.isApplicable(myData)) {
                mySample = mySampling.apply(myData)
            } else {
                mySample = null
            }
        }

        internal fun hasRowCount(expected: Int): SamplingAssert {
            assertEquals(expected, mySample!!.rowCount(), "Group count")
            return this
        }
    }

    companion object {
        private const val N = 100

        private fun sampleSize(step: Double): Int {
            return sampleSize(step, N)
        }

        internal fun sampleSize(step: Double, popSize: Int): Int {
            return Math.floor((popSize - 1) / step).toInt() + 1
        }
    }
}