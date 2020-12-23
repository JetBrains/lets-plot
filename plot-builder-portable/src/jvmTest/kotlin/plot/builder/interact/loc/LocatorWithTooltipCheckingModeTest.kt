/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.*
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock
import jetbrains.datalore.plot.builder.interact.TestUtil
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import kotlin.test.Test
import kotlin.test.assertEquals

class LocatorWithTooltipCheckingModeTest {
    private val lookupSpec = LookupSpec(LookupSpace.XY, LookupStrategy.NEAREST)

    @Test
    fun `locator with disabled tooltip checking mode - should take the last object`() {
        val targetLocators = listOf(
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(
                    MappedDataAccessMock().also { it.add(TestUtil.continuous(Aes.FILL)) }
                ),
                targetPrototypes = listOf(FIRST_TARGET)
            ),
            createLocator(
                lookupSpec = lookupSpec,
                contextualMapping = createContextualMapping(MappedDataAccessMock()),
                targetPrototypes = listOf(SECOND_TARGET)
            )
        )
        val results = findTargets(targetLocators, withGeneralTooltip = false)
        assertLookupResult(results, SECOND_POINT_KEY)
    }

    @Test
    fun `locator with tooltip checking mode - should take the object with general tooltip`() {
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
        val results = findTargets(targetLocators, withGeneralTooltip = true)
        assertLookupResult(results, FIRST_POINT_KEY)
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
        val results = findTargets(targets, withGeneralTooltip = true)
        assertLookupResult(results, SECOND_POINT_KEY)
    }

    private fun createContextualMapping(mappedDataAccessMock: MappedDataAccessMock): ContextualMapping {
        val contextualMappingProvider = GeomInteractionBuilder(Aes.values()).bivariateFunction(true).build()
        return contextualMappingProvider.createContextualMapping(
            mappedDataAccessMock.mappedDataAccess,
            DataFrame.Builder().build(),
            tooltipAnchor = null,
            tooltipMinWidth = null
        )
    }

    private fun findTargets(
        targetLocators: List<GeomTargetLocator>,
        withGeneralTooltip: Boolean
    ): List<LookupResult> {
        val targetsPicker = LocatedTargetsPicker()
        targetLocators.forEach { locator ->
            val lookupResult = locator.search(COORD)
            lookupResult?.let { targetsPicker.addLookupResult(it) }
        }
        return targetsPicker.picked(withGeneralTooltip)
    }

    private fun assertLookupResult(results: List<LookupResult>, expectedIndex: Int) {
        assertEquals(1, results.size)
        assertEquals(1, results.single().targets.size)
        val geomTarget = results.single().targets.single()

        assertEquals(expectedIndex, geomTarget.hitIndex)
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