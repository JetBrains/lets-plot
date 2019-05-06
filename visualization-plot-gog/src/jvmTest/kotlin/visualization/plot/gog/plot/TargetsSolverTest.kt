package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LocatedTargets
import jetbrains.datalore.visualization.plot.gog.plot.TargetsSolver.Companion.CUTOFF_DISTANCE
import jetbrains.datalore.visualization.plot.gog.plot.TargetsSolver.Companion.FAKE_DISTANCE
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import java.util.stream.Stream

class TargetsSolverTest {
    private var firstLocatedTargetConfig: LocatedTargetsConfig? = null
    private var secondLocatedTargetConfig: LocatedTargetsConfig? = null

    @Before
    fun setUp() {
        firstLocatedTargetConfig = LocatedTargetsConfig().geomKind(GeomKind.HISTOGRAM)
        secondLocatedTargetConfig = LocatedTargetsConfig().geomKind(GeomKind.BAR)
    }

    @Test
    fun closestTargetShouldBeSelected() {
        firstLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.7)
        secondLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.5)

        assertTargetFrom(secondLocatedTargetConfig!!)
    }

    @Test
    fun closestTargetShouldBeSelected2() {
        firstLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.1)
        secondLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.9)

        assertTargetFrom(firstLocatedTargetConfig!!)
    }

    @Test
    fun whenOutOfRangeNothingShouldBeSelected() {
        firstLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 1.2)
        secondLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 1.3)

        assertTargetFrom(none()!!)
    }

    @Test
    fun whenZeroDistanceShouldBeExtendedWithExtraDistance() {
        firstLocatedTargetConfig!!.distanceToTarget(0.0)
        secondLocatedTargetConfig!!.distanceToTarget(FAKE_DISTANCE * 0.7)

        assertTargetFrom(secondLocatedTargetConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_ShouldSelectFirst() {
        firstLocatedTargetConfig!!.distanceToTarget(0.0)
        secondLocatedTargetConfig!!.distanceToTarget(0.0)

        assertTargetFrom(firstLocatedTargetConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ShouldSelectBoth() {
        firstLocatedTargetConfig!!.distanceToTarget(0.0).geomKind(GeomKind.HISTOGRAM)
        secondLocatedTargetConfig!!.distanceToTarget(0.0).geomKind(GeomKind.HISTOGRAM)

        assertTargetFrom(firstLocatedTargetConfig!!, secondLocatedTargetConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ButWithTwoVars_ShouldSelectFirst() {
        firstLocatedTargetConfig!!.distanceToTarget(0.0).geomKind(GeomKind.POINT)
        secondLocatedTargetConfig!!.distanceToTarget(0.0).geomKind(GeomKind.POINT)

        assertTargetFrom(firstLocatedTargetConfig!!)
    }

    @Test
    fun whenSecondLayerHaveNoTargets_ShouldSelectFirst() {
        firstLocatedTargetConfig!!.distanceToTarget(0.0)
        secondLocatedTargetConfig!!.withoutTarget()

        assertTargetFrom(firstLocatedTargetConfig!!)
    }

    @Test
    fun whenBothLayersHaveNoTargets_ShouldSelectNothing() {
        firstLocatedTargetConfig!!.withoutTarget()
        secondLocatedTargetConfig!!.withoutTarget()

        assertTargetFrom(none()!!)
    }

    @Test
    fun withOneLayer_WhenOutOfDistance_ShouldSelectNone() {
        firstLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 1.5)
        secondLocatedTargetConfig = null

        assertTargetFrom(none()!!)
    }

    @Test
    fun withOneLayer_WithinMaxDistance_ShouldSelectFirst() {
        firstLocatedTargetConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.5)
        secondLocatedTargetConfig = null

        assertTargetFrom(firstLocatedTargetConfig!!)
    }

    private fun none(): LocatedTargetsConfig? {
        return null
    }

    private fun assertTargetFrom(vararg expected: LocatedTargetsConfig) {

        val targetsSolver = TargetsSolver()
        Stream
                .of<LocatedTargets>(locatedTargets(firstLocatedTargetConfig), locatedTargets(secondLocatedTargetConfig))
                .filter { Objects.nonNull(it) }
                .forEach { targetsSolver.addLocatedTargets(it) }

        val locatedTargets = targetsSolver.solve()

        if (expected.isEmpty() || Arrays.stream(expected).allMatch { layerConfig -> layerConfig === none() }) {
            assertThat<LocatedTargets>(locatedTargets).isEmpty()
        } else {
            assertThat<LocatedTargets>(locatedTargets).hasSameSizeAs(expected)
            locatedTargets.zip(expected).forEach { pair -> assertThat(pair.first).isEqualTo(pair.second.myLocatedTargets) }
        }
    }

    private fun locatedTargets(locatedTargetsConfig: LocatedTargetsConfig?): LocatedTargets? {
        return if (locatedTargetsConfig === none()) {
            null
        } else locatedTargetsConfig!!.build()

    }

    internal class LocatedTargetsConfig {
        internal var myLocatedTargets: LocatedTargets? = mock(LocatedTargets::class.java)
        private var myGeomKind: GeomKind? = null
        private var myDistance: Double = 0.toDouble()

        fun distanceToTarget(v: Double): LocatedTargetsConfig {
            myDistance = v
            return this
        }

        fun geomKind(v: GeomKind): LocatedTargetsConfig {
            myGeomKind = v
            return this
        }

        fun withoutTarget(): LocatedTargetsConfig {
            myLocatedTargets = null
            return this
        }

        fun build(): LocatedTargets? {
            if (myLocatedTargets != null) {
                `when`<GeomKind>(myLocatedTargets!!.geomKind)
                        .thenReturn(myGeomKind)
                `when`<Double>(myLocatedTargets!!.distance)
                        .thenReturn(myDistance)
            }

            return myLocatedTargets
        }
    }
}
