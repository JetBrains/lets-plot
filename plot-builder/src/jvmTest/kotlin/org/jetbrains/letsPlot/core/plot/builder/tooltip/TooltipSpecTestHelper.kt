/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.NullPlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MappedDataAccessMock.Mapping
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.axisTheme
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.coord
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteraction
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.ValueSource
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpecFactory
import kotlin.test.assertEquals

open class TooltipSpecTestHelper {
    private lateinit var mappedDataAccessMock: MappedDataAccessMock
    private lateinit var myTooltipSpecs: List<TooltipSpec>
    internal lateinit var geomTargetBuilder: TestingGeomTargetBuilder
        private set
    private var axisTooltipEnabled: Boolean = false
    private lateinit var nonTooltipAes: List<Aes<*>>
    private lateinit var axisAes: List<Aes<*>>

    private val plotContext = NullPlotContext

    internal fun init() {
        geomTargetBuilder =
            TestingGeomTargetBuilder(TARGET_HIT_COORD)
        mappedDataAccessMock = MappedDataAccessMock()

        setAxisTooltipEnabled(false)

        nonTooltipAes =
            ArrayList<Aes<*>>(listOf(Aes.X))
        axisAes =
            ArrayList<Aes<*>>(listOf(Aes.X))
    }

    internal fun <T> addMappedData(mapping: Mapping<T>): Mapping<T> {
        mappedDataAccessMock.add(mapping)
        return mapping
    }

    internal fun assertHint(expectedHintKind: Kind, expectedHintCoord: DoubleVector, expectedObjectRadius: Double) {
        assertHint(0, expectedHintKind, expectedHintCoord, expectedObjectRadius)
    }

    internal fun assertFill(expected: Color) {
        assertEquals(expected, myTooltipSpecs[0].fill)
    }

    private fun assertHint(
        index: Int,
        expectedHintKind: Kind,
        expectedHintCoord: DoubleVector,
        expectedObjectRadius: Double
    ) {
        val tooltipSpec = myTooltipSpecs[index]
        assertEquals(expectedHintKind, tooltipSpec.layoutHint.kind)
        assertEquals(expectedHintCoord, tooltipSpec.layoutHint.coord)
        assertEquals(expectedObjectRadius, tooltipSpec.layoutHint.objectRadius, 0.001)
    }

    internal fun assertLines(index: Int, vararg expectedLines: String) {
        assertLines(index, listOf(*expectedLines))
    }

    private fun assertLines(index: Int, expectedLines: List<String>) {
        val tooltipSpec = myTooltipSpecs[index]
        assertEquals(expectedLines, tooltipSpec.lines.map(TooltipSpec.Line::toString))
    }

    fun assertLines(expectedLines: List<String>, isSide: Boolean) {
        val actualLines =
            myTooltipSpecs.filter { it.isSide == isSide }.flatMap { it.lines.map(TooltipSpec.Line::toString) }
        assertEquals(expectedLines.size, actualLines.size)
        assertEquals(expectedLines, actualLines)
    }

    internal fun assertTooltipsCount(expectedCount: Int) {
        assertEquals(expectedCount, myTooltipSpecs.size)
    }

    internal fun createTooltipSpecs(geomTarget: GeomTarget) {
        val tipAes = ArrayList<Aes<*>>()
        for (aes in mappedDataAccessMock.getMappedAes()) {
            if (nonTooltipAes.contains(aes)) {
                continue
            }
            tipAes.add(aes)
        }

        myTooltipSpecs = TooltipSpecFactory(
            GeomInteraction.createTestContextualMapping(
                tipAes,
                if (axisTooltipEnabled) axisAes else emptyList(),
                sideTooltipAes = geomTarget.aesTipLayoutHints.map { it.key },
                mappedDataAccessMock.mappedDataAccess,
                DataFrame.Builder().build()
            ),
            DoubleVector.ZERO,
            flippedAxis = false,
            axisTheme,
            axisTheme,
            plotContext
        ).create(geomTarget)
    }

    internal fun createTooltipSpecWithValueSources(
        geomTarget: GeomTarget,
        valueSources: List<ValueSource>
    ) {
        myTooltipSpecs = TooltipSpecFactory(
            GeomInteraction.createTestContextualMapping(
                emptyList(),
                if (axisTooltipEnabled) axisAes else emptyList(),
                geomTarget.aesTipLayoutHints.map { it.key },
                mappedDataAccessMock.mappedDataAccess,
                DataFrame.Builder().build(),
                valueSources
            ),
            DoubleVector.ZERO,
            flippedAxis = false,
            axisTheme,
            axisTheme,
            plotContext
        ).create(geomTarget)
    }

    internal fun buildTooltipSpecs() {
        createTooltipSpecs(geomTargetBuilder.withPathHitShape().build())
    }

    internal fun setAxisTooltipEnabled(axisTooltipEnabled: Boolean) {
        this.axisTooltipEnabled = axisTooltipEnabled
    }

    companion object {
        internal val TARGET_HIT_COORD = coord(100.0, 100.0)
        internal val TARGET_X_AXIS_COORD = coord(TARGET_HIT_COORD.x, 0.0)
        internal val CURSOR_COORD = DoubleVector(1.0, 2.0)
        internal const val OBJECT_RADIUS = 6.0
        internal const val DEFAULT_OBJECT_RADIUS = 0.0
        internal val AES_WIDTH = Aes.WIDTH
    }
}
