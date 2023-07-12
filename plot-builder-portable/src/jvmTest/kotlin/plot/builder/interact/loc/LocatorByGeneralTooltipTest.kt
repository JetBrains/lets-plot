/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.interact.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator.*
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock
import jetbrains.datalore.plot.builder.interact.TestUtil
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import kotlin.test.Test
import kotlin.test.assertEquals

class LocatorByGeneralTooltipTest {
    private val lookupSpec = LookupSpec(LookupSpace.XY, LookupStrategy.NEAREST)

    @Test
    fun `locator should take the object with general tooltip`() {
        val targetLocators = listOf(
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) } // it will be in the general tooltip
                ),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(MappedDataAccessMock()),
                targetPrototypes = listOf(SECOND_TARGET)
            )
        )
        val results = findTargets(targetLocators)
        assertLookupResults(results, FIRST_POINT_KEY)
    }

    @Test
    fun `between objects without tooltips, locator should choose the last`() {
        // Both objects don't have general tooltips => locator will choose the last added object
        val targets = listOf(
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(MappedDataAccessMock()),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(MappedDataAccessMock()),
                targetPrototypes = listOf(SECOND_TARGET)
            )
        )
        val results = findTargets(targets)
        assertLookupResults(results, SECOND_POINT_KEY)
    }

    @Test
    fun `between objects that have a general tooltip, locator should choose the last`() {
        // Both objects have general tooltips => locator will choose the last added object
        val targets = listOf(
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                ),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping
                    (MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                ),
                targetPrototypes = listOf(SECOND_TARGET)
            )
        )
        val results = findTargets(targets)
        assertLookupResults(results, SECOND_POINT_KEY)
    }

    @Test
    fun `one with a general tooltip and second with an axis tooltip - locator should choose both`() {
        val targets = listOf(
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                ),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X)) },
                    axisTooltips = true
                ),
                targetPrototypes = listOf(SECOND_TARGET)
            )
        )
        val results = findTargets(targets)
        assertLookupResults(results, FIRST_POINT_KEY, SECOND_POINT_KEY)
    }

    @Test
    fun `locator should choose vline and hline`() {
        run {
            val targets = listOf(
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping
                        (MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.XINTERCEPT)) }
                    ),
                    targetPrototypes = listOf(FIRST_TARGET),
                    geomKind = GeomKind.V_LINE
                ),
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping(
                        MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                    ),
                    targetPrototypes = listOf(SECOND_TARGET)
                )
            )
            val results = findTargets(targets)
            assertLookupResults(results, FIRST_POINT_KEY)
        }
        run {
            val targets = listOf(
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping(
                        MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                    ),
                    targetPrototypes = listOf(FIRST_TARGET)
                ),
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping
                        (MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.YINTERCEPT)) }
                    ),
                    targetPrototypes = listOf(SECOND_TARGET),
                    geomKind = GeomKind.H_LINE
                )
            )
            val results = findTargets(targets)
            assertLookupResults(results, SECOND_POINT_KEY)
        }
    }

    @Test
    fun `between objects with both tooltip types, locator should choose the closest`() {
        // Objects have general and axis tooltips => locator will choose the closest last added object
        val target1 = TestUtil.pointTarget(
            FIRST_POINT_KEY,
            COORD.add(DoubleVector(2.0, 0.0))
        )
        val target2 = TestUtil.pointTarget(
            SECOND_POINT_KEY,
            COORD.add(DoubleVector(2.0, 0.0))
        )
        val target3 = TestUtil.pointTarget(
            THIRD_PATH_KEY,
            COORD.add(DoubleVector(5.0, 0.0))
        )

        val targets = listOf(
            // closest with axis and general tooltips
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also {
                        it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X))
                        it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL))
                    },
                    axisTooltips = true
                ),
                targetPrototypes = listOf(target1)
            ),
            // closest with axis tooltip only
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X)) },
                    axisTooltips = true
                ),
                targetPrototypes = listOf(target2)
            ),
            // all types of tooltips but it's not the closest
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also {
                        it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X))
                        it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL))
                    },
                    axisTooltips = true
                ),
                targetPrototypes = listOf(target3)
            )
        )
        val results = findTargets(targets)
        assertLookupResults(results, FIRST_POINT_KEY)
    }

    @Test
    fun `between closest objects with different tooltip type, locator should choose both`() {
        val target1 = TestUtil.pointTarget(
            FIRST_POINT_KEY,
            COORD.add(DoubleVector(2.0, 0.0))
        )
        val target2 = TestUtil.pointTarget(
            SECOND_POINT_KEY,
            COORD.add(DoubleVector(2.0, 0.0))
        )
        val target3 = TestUtil.pointTarget(
            THIRD_PATH_KEY,
            COORD.add(DoubleVector(5.0, 0.0))
        )

        val targets = listOf(
            // closest with general tooltip only
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                ),
                targetPrototypes = listOf(target1)
            ),
            // closest with axis tooltip only
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X)) },
                    axisTooltips = true
                ),
                targetPrototypes = listOf(target2)
            ),
            // it's not the closest
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }
                ),
                targetPrototypes = listOf(target3)
            )
        )
        val results = findTargets(targets)
        assertLookupResults(results, FIRST_POINT_KEY, SECOND_POINT_KEY)
    }

    @Test
    fun `locator should choose closest, but hover_X shouldn't have priority`() {
        // Objects have same same tooltip types => will compare their distances to the cursor
        // But geoms like histogram, when mouse inside a rect or only X projection is used,
        // distance to cursor is zero => locator will be use fake distance to give a chance for tooltips from other layers.

        run {
            val target1 = TestUtil.rectTarget(
                FIRST_POINT_KEY,
                DoubleRectangle(COORD.add(DoubleVector(-2.0, 10.0)), DoubleVector(4.0, 2.0))
            )
            val target2 = TestUtil.pointTarget(
                SECOND_POINT_KEY,
                COORD.add(DoubleVector(2.0, 0.0))
            )

            val targets = listOf(
                createLocator(
                    lookupSpec = LookupSpec(LookupSpace.X, LookupStrategy.HOVER),
                    contextualMapping = createContextualMapping(MappedDataAccessMock().also { it.add(TestUtil.continuous(
                        org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }),
                    targetPrototypes = listOf(target1)
                ),
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping(MappedDataAccessMock().also { it.add(TestUtil.continuous(
                        org.jetbrains.letsPlot.core.plot.base.Aes.FILL)) }),
                    targetPrototypes = listOf(target2)
                )
            )
            val results = findTargets(targets)
            assertLookupResults(results, SECOND_POINT_KEY)
        }

        run {
            // The first should be chosen because it has general tooltip (in contrast to the second)

            val target1 = TestUtil.rectTarget(
                FIRST_POINT_KEY,
                DoubleRectangle(COORD.add(DoubleVector(-2.0, 10.0)), DoubleVector(4.0, 2.0))
            )
            val target2 = TestUtil.pointTarget(
                SECOND_POINT_KEY,
                COORD.add(DoubleVector(2.0, 0.0))
            )

            val targets = listOf(
                // with general and axis tooltips
                createLocator(
                    lookupSpec = LookupSpec(LookupSpace.X, LookupStrategy.HOVER),
                    contextualMapping = createContextualMapping(
                        MappedDataAccessMock().also {
                            it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X))
                            it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.FILL))
                        },
                        axisTooltips = true
                    ),
                    targetPrototypes = listOf(target1)
                ),
                // with axis tooltip only
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping(
                        MappedDataAccessMock().also {
                            it.add(TestUtil.continuous(org.jetbrains.letsPlot.core.plot.base.Aes.X))
                        },
                        axisTooltips = true
                    ),
                    targetPrototypes = listOf(target2)
                )
            )
            val results = findTargets(targets)
            assertLookupResults(results, FIRST_POINT_KEY)
        }
    }

    private fun createContextualMapping(
        mappedDataAccessMock: MappedDataAccessMock,
        axisTooltips: Boolean = false
    ): ContextualMapping {
        val contextualMappingProvider = GeomInteractionBuilder.DemoAndTest(
            supportedAes = org.jetbrains.letsPlot.core.plot.base.Aes.values(),
            axisAes = if (axisTooltips) listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X) else emptyList()
        )
            .bivariateFunction(true)
            .build()
        return contextualMappingProvider.createContextualMapping(
            mappedDataAccessMock.mappedDataAccess,
            DataFrame.Builder().build()
        )
    }

    private fun findTargets(
        targetLocators: List<GeomTargetLocator>
    ): List<LookupResult> {
        val targetsPicker = LocatedTargetsPicker(flippedAxis = false)
        targetLocators.forEach { locator ->
            locator.search(COORD)?.let(targetsPicker::addLookupResult)
        }
        return targetsPicker.picked
    }

    private fun assertLookupResults(results: List<LookupResult>, vararg expected: Int) {
        assertEquals(expected.size, results.size)
        results.forEachIndexed { index, lookupResult ->
            assertEquals(1, lookupResult.targets.size)
            val geomTarget = lookupResult.targets.single()
            assertEquals(expected[index], geomTarget.hitIndex)
        }
    }

    companion object {
        private val COORD = TestUtil.point(10.0, 10.0)
        private const val FIRST_POINT_KEY = 0
        private const val SECOND_POINT_KEY = 1
        private const val THIRD_PATH_KEY = 2
        private val FIRST_TARGET = TestUtil.pointTarget(
            FIRST_POINT_KEY,
            COORD
        )
        private val SECOND_TARGET = TestUtil.pointTarget(
            SECOND_POINT_KEY,
            COORD
        )
    }
}