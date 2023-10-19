/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.*
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MappedDataAccessMock
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.createLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteractionBuilder
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.LocatedTargetsPicker
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.TargetPrototype
import kotlin.test.Test
import kotlin.test.assertEquals

class LocatedTargetsPickerFilterTargetsTest {

    @Test
    fun `line plot - choose targets closest to cursor by x`() {
        val pathKey1 = 1
        val pathKey2 = 2
        val pathKey3 = 3

        val targetPrototypes = listOf(
            TestUtil.pathTarget(listOf(DoubleVector(0.0, 0.0), DoubleVector(3.0, 0.0)), indexMapper = { pathKey1 }),
            TestUtil.pathTarget(listOf(DoubleVector(1.0, 1.0), DoubleVector(3.0, 1.0)), indexMapper = { pathKey2 }),
            TestUtil.pathTarget(listOf(DoubleVector(0.0, 2.0), DoubleVector(2.0, 2.0)), indexMapper = { pathKey3 }),
        )
        val locator = createLocator(GeomKind.LINE, targetPrototypes)

        assertTargets(
            findTargets(locator, cursor = DoubleVector(-0.5, 0.0))
            // no targets
        )
        assertTargets(
            findTargets(locator, cursor = DoubleVector(0.5, 0.0)),
            pathKey1, pathKey3
        )
        assertTargets(
            findTargets(locator, cursor = DoubleVector(1.5, 0.0)),
            pathKey2
        )
        assertTargets(
            findTargets(locator, cursor = DoubleVector(2.0, 0.0)),
            pathKey3
        )
        assertTargets(
            findTargets(locator, cursor = DoubleVector(2.5, 0.0)),
            pathKey1, pathKey2
        )
    }

    @Test
    fun `bar plot - check restriction on visible tooltips`() {

        val targetPrototypes = run {
            val startTargetRect = DoubleRectangle(DoubleVector.ZERO, DoubleVector(1.0, 10.0))
            (0..10)
                .toList()
                .map { startTargetRect.add(DoubleVector(0.0, it.toDouble())) }
                .mapIndexed { index, rect -> TestUtil.rectTarget(index, rect) }
        }

        //  restriction for bar tooltips = 5:
        //   - if more - choose the closest one
        //   - else - get all targets

        run {
            val locator = createLocator(GeomKind.BAR, targetPrototypes)
            assertTargets(
                findTargets(locator, cursor = DoubleVector(0.0, 0.0)),
                0
            )
            assertTargets(
                findTargets(locator, cursor = DoubleVector(0.0, 6.0)),
                6
            )
            assertTargets(
                findTargets(locator, cursor = DoubleVector(0.0, 10.0)),
                10
            )
        }
        run {
            // targets no more than the restriction value => use all targets
            val locator = createLocator(GeomKind.BAR, targetPrototypes.take(5))
            assertTargets(
                findTargets(locator, cursor = DoubleVector(0.0, 10.0)),
                0, 1, 2, 3, 4
            )
        }
    }

    private fun createLocator(geomKind: GeomKind, targetPrototypes: List<TargetPrototype>): GeomTargetLocator {
        val contextualMapping = GeomInteractionBuilder.DemoAndTest(supportedAes = Aes.values())
            .xUnivariateFunction(LookupStrategy.HOVER)
            .build()
            .createContextualMapping(
                MappedDataAccessMock().mappedDataAccess,
                DataFrame.Builder().build()
            )
        return createLocator(
            lookupSpec = LookupSpec(LookupSpace.X, LookupStrategy.HOVER),
            contextualMapping = contextualMapping,
            targetPrototypes = targetPrototypes,
            geomKind = geomKind
        )
    }

    private fun findTargets(
        locator: GeomTargetLocator,
        cursor: DoubleVector
    ): List<GeomTarget> {
        return LocatedTargetsPicker(flippedAxis = false, cursor)
            .apply { locator.search(cursor)?.let(::addLookupResult) }
            .picked
            .singleOrNull()
            ?.targets
            ?: emptyList()
    }

    private fun assertTargets(targets: List<GeomTarget>, vararg expected: Int) {
        assertEquals(expected.size, targets.size)
        val actual = targets.map(GeomTarget::hitIndex)
        assertEquals(expected.toList(), actual)
    }
}