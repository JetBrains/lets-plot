/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.*
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
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) } // it will be in the general tooltip
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
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) }
                ),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping
                    (MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) }
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
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) }
                ),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping
                    (MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.X)) },
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
                        (MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.XINTERCEPT)) }
                    ),
                    targetPrototypes = listOf(FIRST_TARGET),
                    geomKind = GeomKind.V_LINE
                ),
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping(
                        MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) }
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
                        MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) }
                    ),
                    targetPrototypes = listOf(FIRST_TARGET)
                ),
                createLocator(
                    lookupSpec = lookupSpec,
                    contextualMapping = createContextualMapping
                        (MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.YINTERCEPT)) }
                    ),
                    targetPrototypes = listOf(SECOND_TARGET),
                    geomKind = GeomKind.H_LINE
                )
            )
            val results = findTargets(targets)
            assertLookupResults(results, SECOND_POINT_KEY)
        }
    }

    private fun createContextualMapping(mappedDataAccessMock: MappedDataAccessMock, axisTooltips: Boolean = false): ContextualMapping {
        val contextualMappingProvider = GeomInteractionBuilder(Aes.values())
            .bivariateFunction(true)
            .axisAes(if (axisTooltips) listOf(Aes.X) else emptyList())
            .build()
        return contextualMappingProvider.createContextualMapping(
            mappedDataAccessMock.mappedDataAccess,
            DataFrame.Builder().build()
        )
    }

    private fun findTargets(
        targetLocators: List<GeomTargetLocator>
    ): List<LookupResult> {
        val targetsPicker = LocatedTargetsPicker()
        targetLocators.forEach { locator ->
            val lookupResult = locator.search(COORD)
            lookupResult?.let { targetsPicker.addLookupResult(it) }
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