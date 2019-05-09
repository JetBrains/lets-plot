package jetbrains.datalore.visualization.plot.gog.core.data.sampling

import jetbrains.datalore.visualization.plot.gog.core.data.TestUtil
import kotlin.test.*

class RandomSamplingTest {

    private val myData = TestUtil.generateData(N, listOf("x", "y", "c"))

    @Test
    fun noop() {
        assertTrue(RandomSampling(N - 1, null).isApplicable(myData))

        val failedSampling = RandomSampling(N, null)
        assertFalse(failedSampling.isApplicable(myData))

        assertFailsWith(RuntimeException::class) {
            failedSampling.apply(myData)
        }
    }

    @Test
    fun smallSample() {
        assertRowCount(N_SMALL_SAMPLE)
    }

    @Test
    fun largeSample() {
        assertRowCount(N_LARGE_SAMPLE)
    }

    private fun assertRowCount(sampleSize: Int) {
        val sampleData = RandomSampling(sampleSize, null).apply(myData)
        assertEquals(sampleSize, sampleData.rowCount(), "Row count")
    }

    companion object {
        private const val N = 10
        private const val N_SMALL_SAMPLE = 1
        private const val N_LARGE_SAMPLE = 9
    }
}