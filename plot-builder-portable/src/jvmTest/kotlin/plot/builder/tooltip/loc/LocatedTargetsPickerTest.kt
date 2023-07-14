/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupResult
import jetbrains.datalore.plot.builder.tooltip.loc.LocatedTargetsPicker.Companion.CUTOFF_DISTANCE
import jetbrains.datalore.plot.builder.tooltip.loc.LocatedTargetsPicker.Companion.FAKE_DISTANCE
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.BeforeTest
import kotlin.test.Test

class LocatedTargetsPickerTest {
    private lateinit var firstLookupResultConfig: LookupResultConfig
    private var secondLookupResultConfig: LookupResultConfig? = null

    @BeforeTest
    fun setUp() {
        firstLookupResultConfig = LookupResultConfig()
            .geomKind(GeomKind.HISTOGRAM)
        secondLookupResultConfig = LookupResultConfig()
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
    fun whenBothTargetsHaveZeroDistance_ShouldSelectSecond() {
        firstLookupResultConfig.distanceToTarget(0.0)
        secondLookupResultConfig!!.distanceToTarget(0.0)

        assertTargetFrom(secondLookupResultConfig)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ShouldSelectBoth() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.HISTOGRAM)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.HISTOGRAM)

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ButWithTwoVars_ShouldSelectSecond() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.POINT)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.POINT)

        assertTargetFrom(secondLookupResultConfig)
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
    fun shouldIgnoreTextTooltipsIfOtherTooltipsArePresent() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.POINT)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.TEXT)

        assertTargetFrom(firstLookupResultConfig)
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

    private fun assertTargetFrom(vararg expected: LookupResultConfig?) {

        val targetsPicker = LocatedTargetsPicker(flippedAxis = false)
        listOfNotNull(lookupResult(firstLookupResultConfig), lookupResult(secondLookupResultConfig))
                .forEach(targetsPicker::addLookupResult)

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

    private fun lookupResult(lookupResultConfig: LookupResultConfig?): LookupResult? {
        return lookupResultConfig?.build()
    }

    internal class LookupResultConfig {
        internal var myResult: LookupResult? = mock(LookupResult::class.java)
        private var myGeomKind: GeomKind? = null
        private var myDistance: Double = 0.toDouble()
        private val myContextualMapping = mock(ContextualMapping::class.java)

        fun distanceToTarget(v: Double): LookupResultConfig {
            myDistance = v
            return this
        }

        fun geomKind(v: GeomKind): LookupResultConfig {
            myGeomKind = v
            return this
        }

        fun withoutTarget(): LookupResultConfig {
            myResult = null
            return this
        }

        fun build(): LookupResult? {
            if (myResult != null) {
                `when`<GeomKind>(myResult!!.geomKind)
                    .thenReturn(myGeomKind)
                `when`<Double>(myResult!!.distance)
                    .thenReturn(myDistance)
                `when`(myResult!!.contextualMapping)
                    .thenReturn(myContextualMapping)
            }
            return myResult
        }
    }
}
