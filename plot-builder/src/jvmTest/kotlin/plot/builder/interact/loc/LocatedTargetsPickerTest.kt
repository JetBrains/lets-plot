package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker.Companion.CUTOFF_DISTANCE
import jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker.Companion.FAKE_DISTANCE
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupResult
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.BeforeTest
import kotlin.test.Test

class LocatedTargetsPickerTest {
    private lateinit var firstLookupResultConfig: jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig
    private var secondLookupResultConfig: jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig? = null

    @BeforeTest
    fun setUp() {
        firstLookupResultConfig = jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig()
            .geomKind(GeomKind.HISTOGRAM)
        secondLookupResultConfig = jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig()
            .geomKind(GeomKind.BAR)
    }

    @Test
    fun closestTargetShouldBeSelected() {
        firstLookupResultConfig.distanceToTarget(CUTOFF_DISTANCE * 0.7)
        secondLookupResultConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.5)

        assertTargetFrom(secondLookupResultConfig!!)
    }

    @Test
    fun closestTargetShouldBeSelected2() {
        firstLookupResultConfig.distanceToTarget(CUTOFF_DISTANCE * 0.1)
        secondLookupResultConfig!!.distanceToTarget(CUTOFF_DISTANCE * 0.9)

        assertTargetFrom(firstLookupResultConfig)
    }

    @Test
    fun whenOutOfRangeNothingShouldBeSelected() {
        firstLookupResultConfig.distanceToTarget(CUTOFF_DISTANCE * 1.2)
        secondLookupResultConfig!!.distanceToTarget(CUTOFF_DISTANCE * 1.3)

        assertTargetFrom(null)
    }

    @Test
    fun whenZeroDistanceShouldBeExtendedWithExtraDistance() {
        firstLookupResultConfig.distanceToTarget(0.0)
        secondLookupResultConfig!!.distanceToTarget(FAKE_DISTANCE * 0.7)

        assertTargetFrom(secondLookupResultConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_ShouldSelectFirst() {
        firstLookupResultConfig.distanceToTarget(0.0)
        secondLookupResultConfig!!.distanceToTarget(0.0)

        assertTargetFrom(firstLookupResultConfig)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ShouldSelectBoth() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.HISTOGRAM)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.HISTOGRAM)

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ButWithTwoVars_ShouldSelectFirst() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.POINT)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.POINT)

        assertTargetFrom(firstLookupResultConfig)
    }

    @Test
    fun whenSecondLayerHaveNoTargets_ShouldSelectFirst() {
        firstLookupResultConfig.distanceToTarget(0.0)
        secondLookupResultConfig!!.withoutTarget()

        assertTargetFrom(firstLookupResultConfig)
    }

    @Test
    fun whenBothLayersHaveNoTargets_ShouldSelectNothing() {
        firstLookupResultConfig.withoutTarget()
        secondLookupResultConfig!!.withoutTarget()

        assertTargetFrom(null)
    }

    @Test
    fun withOneLayer_WhenOutOfDistance_ShouldSelectNone() {
        firstLookupResultConfig.distanceToTarget(CUTOFF_DISTANCE * 1.5)
        secondLookupResultConfig = null

        assertTargetFrom(null)
    }

    @Test
    fun withOneLayer_WithinMaxDistance_ShouldSelectFirst() {
        firstLookupResultConfig.distanceToTarget(CUTOFF_DISTANCE * 0.5)
        secondLookupResultConfig = null

        assertTargetFrom(firstLookupResultConfig)
    }

    private fun assertTargetFrom(vararg expected: jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig?) {

        val targetsPicker = jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker()
        listOfNotNull(lookupResult(firstLookupResultConfig), lookupResult(secondLookupResultConfig))
                .forEach { targetsPicker.addLookupResult(it) }

        val lookupResults = targetsPicker.picked

        if (expected.isEmpty() || expected.all { layerConfig -> layerConfig == null }) {
            assertThat<LookupResult>(lookupResults).isEmpty()
        } else {
            assertThat<LookupResult>(lookupResults).hasSameSizeAs(expected)
            lookupResults.zip(expected).forEach { pair ->
                assertThat(pair.first).isEqualTo(pair.second!!.myResult)
            }
        }
    }

    private fun lookupResult(lookupResultConfig: jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig?): LookupResult? {
        return lookupResultConfig?.build()
    }

    internal class LookupResultConfig {
        internal var myResult: LookupResult? = mock(LookupResult::class.java)
        private var myGeomKind: GeomKind? = null
        private var myDistance: Double = 0.toDouble()

        fun distanceToTarget(v: Double): jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig {
            myDistance = v
            return this
        }

        fun geomKind(v: GeomKind): jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig {
            myGeomKind = v
            return this
        }

        fun withoutTarget(): jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPickerTest.LookupResultConfig {
            myResult = null
            return this
        }

        fun build(): LookupResult? {
            if (myResult != null) {
                `when`<GeomKind>(myResult!!.geomKind)
                        .thenReturn(myGeomKind)
                `when`<Double>(myResult!!.distance)
                        .thenReturn(myDistance)
            }

            return myResult
        }
    }
}
