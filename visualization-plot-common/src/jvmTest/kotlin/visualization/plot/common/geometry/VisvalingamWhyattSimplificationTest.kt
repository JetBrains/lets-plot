package jetbrains.datalore.visualization.plot.common.geometry

import jetbrains.datalore.visualization.plot.common.geometry.TestUtil.COMPLEX_DATA
import jetbrains.datalore.visualization.plot.common.geometry.TestUtil.MEDIUM_DATA
import jetbrains.datalore.visualization.plot.common.geometry.TestUtil.SIMPLE_DATA
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class VisvalingamWhyattSimplificationTest {

    @Test
    fun simplificationByCountShouldNotBreakRing() {
        val indices = PolylineSimplifier.visvalingamWhyatt(SIMPLE_DATA).setCountLimit(4).indices

        assertThat(indices).has(TestUtil.ValidRingCondition(SIMPLE_DATA))
    }

    @Test
    fun simplificationByAreaShouldNotBreakRing() {
        val indices = PolylineSimplifier.visvalingamWhyatt(MEDIUM_DATA).setWeightLimit(0.001).indices
        assertThat(indices).has(TestUtil.ValidRingCondition(MEDIUM_DATA))
    }


    @Test
    fun tooManyPoints() {
        val indices = PolylineSimplifier.visvalingamWhyatt(COMPLEX_DATA).setCountLimit(13).indices
        assertThat(indices)
                .has(TestUtil.ValidRingCondition(COMPLEX_DATA))
                .containsExactly(0, 17, 28, 36, 45, 53, 65, 74, 86, 93, 102, 110, 122)
    }
}