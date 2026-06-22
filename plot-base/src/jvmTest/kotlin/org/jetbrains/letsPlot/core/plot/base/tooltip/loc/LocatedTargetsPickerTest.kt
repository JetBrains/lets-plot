/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.NullPlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor.HorizontalAnchor.RIGHT
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor.VerticalAnchor.TOP
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehavior
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.LocatedTargetsPicker.Companion.CUTOFF_DISTANCE
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.LocatedTargetsPicker.Companion.FAKE_DISTANCE
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ConstantField
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.MappingField
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource
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
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomSmooth_ShouldSelectBoth() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.SMOOTH)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.SMOOTH)

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun whenBothTargetsHaveZeroDistance_AndHaveSameGeomKind_ButWithTwoVars_ShouldSelectBoth() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.POINT)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.POINT)

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun `tooltipGroup for line and point`() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.LINE).tooltipGroup("sameGroup")
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.POINT).tooltipGroup("sameGroup")

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun `tooltipGroup for line and point with distance`() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.LINE).tooltipGroup("sameGroup")
        secondLookupResultConfig!!.distanceToTarget(5.0).geomKind(GeomKind.POINT).tooltipGroup("sameGroup")

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun `tooltipGroup for line and smooth`() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.LINE).tooltipGroup("sameGroup")
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.SMOOTH).tooltipGroup("sameGroup")

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun `tooltipGroup for line and smooth with distance`() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.LINE).tooltipGroup("sameGroup")
        secondLookupResultConfig!!.distanceToTarget(5.0).geomKind(GeomKind.SMOOTH).tooltipGroup("sameGroup")

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
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
    fun shouldIgnoreLabelTooltipsIfOtherTooltipsArePresent() {
        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.POINT)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.LABEL)

        assertTargetFrom(firstLookupResultConfig)
    }

    @Test
    fun whenThreeStackableLayersHaveSameDistance_ShouldSelectAll() {
        val thirdLookupResultConfig = LookupResultConfig()
            .distanceToTarget(0.0)
            .geomKind(GeomKind.LINE)

        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.LINE)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.LINE)

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!, thirdLookupResultConfig) {
            addLookupResult(firstLookupResultConfig.build()!!)
            addLookupResult(secondLookupResultConfig!!.build()!!)
            addLookupResult(thirdLookupResultConfig.build()!!)
        }
    }

    @Test
    fun whenTextIsBetweenStackableLayers_ShouldIgnoreTextAndKeepStacking() {
        val thirdLookupResultConfig = LookupResultConfig()
            .distanceToTarget(0.0)
            .geomKind(GeomKind.LINE)

        firstLookupResultConfig.distanceToTarget(0.0).geomKind(GeomKind.LINE)
        secondLookupResultConfig!!.distanceToTarget(0.0).geomKind(GeomKind.TEXT)

        assertTargetFrom(firstLookupResultConfig, thirdLookupResultConfig) {
            addLookupResult(firstLookupResultConfig.build()!!)
            addLookupResult(secondLookupResultConfig!!.build()!!)
            addLookupResult(thirdLookupResultConfig.build()!!)
        }
    }

    @Test
    fun whenClosestHasGeneralAndAxisTooltips_ShouldNotFallbackToOtherLayers() {
        firstLookupResultConfig
            .distanceToTarget(0.0)
            .generalTooltip(true)
            .axisTooltip(true)
        secondLookupResultConfig!!
            .distanceToTarget(FAKE_DISTANCE)
            .generalTooltip(true)

        assertTargetFrom(firstLookupResultConfig)
    }

    @Test
    fun whenClosestLayersSplitGeneralAndAxisTooltips_ShouldSelectBoth() {
        firstLookupResultConfig
            .distanceToTarget(0.0)
            .generalTooltip(true)
        secondLookupResultConfig!!
            .distanceToTarget(0.0)
            .generalTooltip(false)
            .axisTooltip(true)

        assertTargetFrom(firstLookupResultConfig, secondLookupResultConfig!!)
    }

    @Test
    fun whenFartherLayerHasGeneralAndAxisTooltips_ShouldReplaceClosestSplitTooltips() {
        firstLookupResultConfig
            .distanceToTarget(0.0)
            .generalTooltip(true)
        secondLookupResultConfig!!
            .distanceToTarget(0.0)
            .axisTooltip(true)
        val thirdLookupResultConfig = LookupResultConfig()
            .distanceToTarget(FAKE_DISTANCE)
            .geomKind(GeomKind.POINT)
            .generalTooltip(true)
            .axisTooltip(true)

        assertTargetFrom(thirdLookupResultConfig) {
            addLookupResult(firstLookupResultConfig.build()!!)
            addLookupResult(secondLookupResultConfig!!.build()!!)
            addLookupResult(thirdLookupResultConfig.build()!!)
        }
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

    @Test
    fun `general tooltips from multiple targets are merged into one model`() {
        val targetsPicker = LocatedTargetsPicker(
            flippedAxis = false,
            cursorCoord = DoubleVector.ZERO,
            axisOrigin = DoubleVector.ZERO,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = NullPlotContext,
            mergeTooltips = true,
            tooltipMaxCount = 10
        )
        val contextualMapping = ContextualMapping(
            tooltipBehavior = TooltipBehavior.DEFAULT,
            tooltipLines = listOf(
                LinePattern("value", "{}", listOf(ConstantField(Aes.Y, "18.37", label = null)))
            ),
            tooltipTitle = LinePattern(null, "x = {}", listOf(ConstantField(Aes.X, "12", label = null)))
        )

        val tooltipModels = targetsPicker.chooseTooltipModels(
            listOf(
                geomTarget(marker = TooltipMarker.create(majorColor = Color.DARK_GREEN)),
                geomTarget(marker = TooltipMarker.create(majorColor = Color.LIGHT_PINK))
            ),
            contextualMapping
        )

        assertThat(tooltipModels).hasSize(1)
        val mergedTooltip = tooltipModels.single()
        // every target carries its own title; the renderer hoists a unanimous title into the box header
        assertThat(mergedTooltip.targets.map { it.title }).containsExactly("x = 12", "x = 12")
        assertThat(mergedTooltip.targets).hasSize(2)
        assertThat(mergedTooltip.targets.map { it.marker.majorColor }).containsExactly(Color.DARK_GREEN, Color.LIGHT_PINK)
        assertThat(mergedTooltip.targets.flatMap { it.lines }.map(TooltipModel.Line::toString))
            .containsExactly("value: 18.37", "value: 18.37")
    }

    @Test
    fun `general tooltips from multiple targets are separate by default`() {
        val targetsPicker = LocatedTargetsPicker(
            flippedAxis = false,
            cursorCoord = DoubleVector.ZERO,
            axisOrigin = DoubleVector.ZERO,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = NullPlotContext,
            mergeTooltips = false,
            tooltipMaxCount = 10
        )
        val contextualMapping = ContextualMapping(
            tooltipBehavior = TooltipBehavior.DEFAULT,
            tooltipLines = listOf(
                LinePattern("value", "{}", listOf(ConstantField(Aes.Y, "18.37", label = null)))
            ),
            tooltipTitle = null
        )

        val tooltipModels = targetsPicker.chooseTooltipModels(
            listOf(
                geomTarget(marker = TooltipMarker.create(majorColor = Color.DARK_GREEN)),
                geomTarget(marker = TooltipMarker.create(majorColor = Color.LIGHT_PINK))
            ),
            contextualMapping
        )

        assertThat(tooltipModels).hasSize(2)
        assertThat(tooltipModels).allMatch { it.targets.size == 1 }
    }

    @Test
    fun `default tooltip max count keeps only closest target after limit`() {
        val targetsPicker = createTargetsPicker(cursorCoord = DoubleVector(3.2, 0.0))

        targetsPicker.addLookupResult(lookupResultWithTargets(11))

        val lookupResult = targetsPicker.chooseBestLookupResults().single()
        assertThat(lookupResult.targets).hasSize(1)
        assertThat(lookupResult.targets.single().hitIndex).isEqualTo(3)
    }

    @Test
    fun `custom tooltip max count keeps all targets within limit`() {
        val targetsPicker = createTargetsPicker(
            cursorCoord = DoubleVector(3.2, 0.0),
            tooltipMaxCount = 11
        )

        targetsPicker.addLookupResult(lookupResultWithTargets(11))

        val lookupResult = targetsPicker.chooseBestLookupResults().single()
        assertThat(lookupResult.targets).hasSize(11)
    }

    @Test
    fun `zero tooltip max count disables target limit`() {
        val targetsPicker = createTargetsPicker(
            cursorCoord = DoubleVector(3.2, 0.0),
            tooltipMaxCount = 0
        )

        targetsPicker.addLookupResult(lookupResultWithTargets(11))

        val lookupResult = targetsPicker.chooseBestLookupResults().single()
        assertThat(lookupResult.targets).hasSize(11)
    }

    @Test
    fun `zero tooltip max count disables bar target limit`() {
        val targetsPicker = createTargetsPicker(
            cursorCoord = DoubleVector(1.2, 0.0),
            tooltipMaxCount = 0
        )

        targetsPicker.addLookupResult(lookupResultWithTargets(2, GeomKind.BAR))

        val lookupResult = targetsPicker.chooseBestLookupResults().single()
        assertThat(lookupResult.targets).hasSize(2)
    }

    private fun assertTargetFrom(vararg expected: LookupResultConfig?) {
        assertTargetFrom(*expected) {
            listOfNotNull(lookupResult(firstLookupResultConfig), lookupResult(secondLookupResultConfig))
                .forEach(::addLookupResult)
        }
    }

    private fun assertTargetFrom(vararg expected: LookupResultConfig?, addResults: LocatedTargetsPicker.() -> Unit) {
        val targetsPicker = LocatedTargetsPicker(
            flippedAxis = false,
            cursorCoord = DoubleVector.ZERO,
            axisOrigin = DoubleVector.ZERO,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = NullPlotContext,
            mergeTooltips = false,
            tooltipMaxCount = 10
        )
        targetsPicker.addResults()

        val lookupResults = targetsPicker.chooseBestLookupResults()

        if (expected.isEmpty() || expected.all { layerConfig -> layerConfig == null }) {
            assertThat<LookupResult>(lookupResults).isEmpty()
        } else {
            assertThat<LookupResult>(lookupResults).hasSameSizeAs(expected)
            lookupResults.zip(expected).forEach { pair ->
                assertLookupResult(pair.first, pair.second!!)
            }
        }
    }

    private fun createTargetsPicker(
        cursorCoord: DoubleVector = DoubleVector.ZERO,
        tooltipMaxCount: Int = 10
    ): LocatedTargetsPicker {
        return LocatedTargetsPicker(
            flippedAxis = false,
            cursorCoord = cursorCoord,
            axisOrigin = DoubleVector.ZERO,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = NullPlotContext,
            mergeTooltips = false,
            tooltipMaxCount = tooltipMaxCount
        )
    }

    private fun assertLookupResult(actual: LookupResult, expected: LookupResultConfig) {
        val expectedResult = expected.build()!!
        assertThat(actual.geomKind).isEqualTo(expectedResult.geomKind)
        assertThat(actual.lookupDistance).isEqualTo(expectedResult.lookupDistance)
        assertThat(actual.ownerDistance).isEqualTo(expectedResult.ownerDistance)
        assertThat(actual.hasGeneralTooltip).isEqualTo(expectedResult.hasGeneralTooltip)
        assertThat(actual.hasAxisTooltip).isEqualTo(expectedResult.hasAxisTooltip)
        assertThat(actual.isCrosshairEnabled).isEqualTo(expectedResult.isCrosshairEnabled)
        assertThat(actual.hitShapeKind).isEqualTo(expectedResult.hitShapeKind)
    }

    private fun lookupResult(lookupResultConfig: LookupResultConfig?): LookupResult? {
        return lookupResultConfig?.build()
    }

    private fun geomTarget(marker: TooltipMarker): GeomTarget {
        return GeomTarget(
            hitIndex = 0,
            tooltipHint = TooltipHint.verticalTooltip(
                coord = DoubleVector.ZERO,
                objectRadius = 0.0,
                marker = marker
            ),
            sideTooltipHints = emptyMap()
        )
    }

    private fun lookupResultWithTargets(
        count: Int,
        geomKind: GeomKind = GeomKind.POINT
    ): LookupResult {
        val contextualMapping = ContextualMapping(
            tooltipBehavior = TooltipBehavior.DEFAULT,
            tooltipLines = listOf(
                LinePattern(null, "", listOf(MappingField(Aes.X, isSide = true, isAxis = false)))
            ),
            tooltipTitle = null
        )

        return LookupResult(
            targets = (0 until count).map { index ->
                GeomTarget(
                    hitIndex = index,
                    tooltipHint = TooltipHint.verticalTooltip(
                        coord = DoubleVector(index.toDouble(), 0.0),
                        objectRadius = 0.0
                    ),
                    sideTooltipHints = emptyMap()
                )
            },
            lookupDistance = 0.0,
            ownerDistance = 0.0,
            geomKind = geomKind,
            contextualMapping = contextualMapping,
            hitShapeKind = HitShape.Kind.RECT
        )
    }

    internal class LookupResultConfig {
        internal var myResult: LookupResult? = null
        private var myGeomKind: GeomKind? = null
        private var myDistance: Double = 0.toDouble()
        private var myOwnerDistance: Double? = null
        private var myLookupSpec: GeomTargetLocator.LookupSpec = GeomTargetLocator.LookupSpec.NONE
        private var myHasTarget: Boolean = true
        private var myHasGeneralTooltip: Boolean = false
        private var myHasAxisTooltip: Boolean = false
        private var myIsCrosshairEnabled: Boolean = false
        private var myHitShapeKind: HitShape.Kind = HitShape.Kind.RECT
        private var myTooltipGroup: String? = null

        fun distanceToTarget(v: Double): LookupResultConfig {
            myDistance = v
            return this
        }

        fun geomKind(v: GeomKind): LookupResultConfig {
            myGeomKind = v
            return this
        }

        fun ownerDistance(v: Double): LookupResultConfig {
            myOwnerDistance = v
            return this
        }

        fun lookupSpec(v: GeomTargetLocator.LookupSpec): LookupResultConfig {
            myLookupSpec = v
            return this
        }

        fun withoutTarget(): LookupResultConfig {
            myHasTarget = false
            myResult = null
            return this
        }

        fun generalTooltip(v: Boolean): LookupResultConfig {
            myHasGeneralTooltip = v
            return this
        }

        fun axisTooltip(v: Boolean): LookupResultConfig {
            myHasAxisTooltip = v
            return this
        }

        fun crosshair(v: Boolean): LookupResultConfig {
            myIsCrosshairEnabled = v
            return this
        }

        fun hitShapeKind(v: HitShape.Kind): LookupResultConfig {
            myHitShapeKind = v
            return this
        }

        fun tooltipGroup(v: String?): LookupResultConfig {
            myTooltipGroup = v
            return this
        }

        fun build(): LookupResult? {
            if (!myHasTarget) {
                return null
            }
            if (myResult == null) {
                val fields = mutableListOf<ValueSource>()

                fields += if (myHasGeneralTooltip) {
                    MappingField(Aes.X, isSide = false, isAxis = false)
                } else {
                    MappingField(Aes.X, isSide = true, isAxis = false)
                }

                if (myHasAxisTooltip) {
                    fields += MappingField(Aes.Y, isSide = false, isAxis = true)
                }

                val contextualMapping = ContextualMapping(
                    tooltipBehavior = TooltipBehavior(
                        lookupSpec = myLookupSpec,
                        tooltipGroup = myTooltipGroup ?: defaultTooltipGroup(requireNotNull(myGeomKind)),
                        anchor = if (myIsCrosshairEnabled) TooltipAnchor(TOP, RIGHT) else null,
                        minWidth = null,
                        ignoreInvisibleTargets = false,
                        disableSplitting = false,
                        valueSources = emptyList(),
                        tooltipLinePatterns = null,
                        tooltipTitle = null,
                    ),
                    tooltipLines = listOf(LinePattern(null, "", fields)),
                    tooltipTitle = null
                )
                myResult = LookupResult(
                    targets = emptyList(),
                    lookupDistance = myDistance,
                    ownerDistance = myOwnerDistance ?: myDistance,
                    geomKind = requireNotNull(myGeomKind),
                    contextualMapping = contextualMapping,
                    hitShapeKind = myHitShapeKind
                )
            }
            return myResult
        }

        private fun defaultTooltipGroup(geomKind: GeomKind): String {
            return when (geomKind) {
                in LINE_LIKE_GEOMS -> "line-like"
                else -> "geom:${geomKind.name.lowercase()}"
            }
        }

        companion object {
            private val LINE_LIKE_GEOMS = setOf(
                GeomKind.LINE,
                GeomKind.AREA,
                GeomKind.SMOOTH,
                GeomKind.STEP,
                GeomKind.DENSITY,
                GeomKind.FREQPOLY,
                GeomKind.RIBBON,
                GeomKind.SEGMENT,
                GeomKind.SPOKE
            )
        }
    }
}
