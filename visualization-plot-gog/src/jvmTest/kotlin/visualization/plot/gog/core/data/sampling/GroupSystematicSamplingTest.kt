package jetbrains.datalore.visualization.plot.gog.core.data.sampling

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.GroupAwareSampling
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.stream.Collectors.toList
import kotlin.test.assertFailsWith

internal class GroupSystematicSamplingTest : GroupSamplingTestBase() {

    @Before
    fun setUp() {
        setUp(N_POINTS, N_GROUPS)
    }

    private fun assertThat(sampleSize: Int): SamplingAssert {
        return SamplingAssert(sampleSize)
    }

    @Test
    fun noop() {
        // not applicable with steps < 1.5
        assertThat(N_GROUPS + 1).isNotApplicable
        assertThat(N_GROUPS).isNotApplicable
        assertThat(N_GROUPS - 1).isNotApplicable
        assertThat(sampleSize(1.1)).isNotApplicable

        // applicable with steps >= 1.5
        assertThat(N_GROUPS / 2).isApplicable

        val napSampling = assertThat(N_GROUPS)
                .isNotApplicable
                .sampling

        assertFailsWith(RuntimeException::class) {
            napSampling.apply(data!!, groupMapper!!)
        }
    }

    @Test
    fun exactStep() {
        // step 3 is a perfect fit when last data index in 9, 99... (i.e. that size of data is 10, 100...)
        assertThat(sampleSize(3.0))
                .hasGroupCount(sampleSize(3.0))
                .hasGroup(0)
                .hasGroup(N_GROUPS - 1)
    }

    @Test
    fun roundDownStep() {
        // rounded to closest lesser int
        val targetStep = 3.1
        val expectedStep = 3
        assertThat(sampleSize(targetStep))
                .hasGroupCount(sampleSize(expectedStep.toDouble()))
                .hasGroup(0)
                .hasGroup(N_GROUPS - 1)
    }

    @Test
    fun roundUpStep() {
        // rounded to closest greater int
        val targetStep = 2.9
        val expectedStep = 3
        assertThat(sampleSize(targetStep))
                .hasGroupCount(sampleSize(expectedStep.toDouble()))
                .hasGroup(0)
                .hasGroup(N_GROUPS - 1)
    }

    @Test
    fun unfitStep() {
        assertThat(sampleSize(2.0))
                .hasGroupCount(sampleSize(2.0))
                .hasGroup(0)
                .hasNoGroup(N_GROUPS - 1)
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
            sampling = GroupSystematicSampling(sampleSize)
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

        internal fun hasGroup(distinctGroupIndex: Int): SamplingAssert {
            val b = checkGroup(distinctGroupIndex)
            assertTrue("Group [$distinctGroupIndex]", b)
            return this
        }

        internal fun hasNoGroup(distinctGroupIndex: Int): SamplingAssert {
            val b = checkGroup(distinctGroupIndex)
            assertFalse("Group [$distinctGroupIndex]", b)
            return this
        }

        private fun checkGroup(distinctGroupIndex: Int): Boolean {
            val originalDistinctGroups = data!![GroupSamplingTestBase.GROUP_SERIE_VAR].stream().distinct().collect(toList())
            val v = originalDistinctGroups[distinctGroupIndex]

            return mySample!![GroupSamplingTestBase.GROUP_SERIE_VAR].stream().anyMatch { v == it }
        }
    }

    companion object {
        private const val N_GROUPS = 100
        private const val POINTS_PER_GROUP = 100
        private const val N_POINTS = N_GROUPS * POINTS_PER_GROUP

        private fun sampleSize(step: Double): Int {
            return SystematicSamplingTest.sampleSize(step, N_GROUPS)
        }
    }
}