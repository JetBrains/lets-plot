package jetbrains.datalore.visualization.plot.gog.core.data.sampling

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.GroupAwareSampling
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

internal class GroupRandomSamplingTest : GroupSamplingTestBase() {

    @Before
    fun setUp() {
        setUp(N, N_GROUPS)
    }

    private fun assertThat(sampleSize: Int): SamplingAssert {
        return SamplingAssert(sampleSize)
    }

    @Test
    fun noop() {
        assertThat(2).isApplicable
        val napSampling = assertThat(3)
                .isNotApplicable
                .sampling

        assertFailsWith(RuntimeException::class) {
            napSampling.apply(data!!, groupMapper!!)
        }
    }

    @Test
    fun sample() {
        for (i in 1 until N_GROUPS) {
            assertThat(i)
                    .isApplicable
                    .hasGroupCount(i)
        }
    }


    private inner class SamplingAssert internal constructor(sampleSize: Int) {
        internal val sampling: GroupAwareSampling
        private val mySample: DataFrame?

        internal val isApplicable: SamplingAssert
            get() {
                assertTrue(sampling.isApplicable(data!!, groupMapper!!))
                return this
            }

        internal val isNotApplicable: SamplingAssert
            get() {
                assertFalse(sampling.isApplicable(data!!, groupMapper!!))
                return this
            }

        init {
            sampling = GroupRandomSampling(sampleSize, 1_111L)
            if (sampling.isApplicable(data!!, groupMapper!!)) {
                mySample = sampling.apply(data!!, groupMapper!!)
            } else {
                mySample = null
            }
        }

        internal fun hasGroupCount(expected: Int): SamplingAssert {
            val groupCount = mySample!![GroupSamplingTestBase.GROUP_SERIE_VAR].stream().distinct().count()
            assertEquals("Group count", expected.toLong(), groupCount)
            return this
        }
    }

    companion object {
        private const val N = 100
        private const val N_GROUPS = 3
    }

}