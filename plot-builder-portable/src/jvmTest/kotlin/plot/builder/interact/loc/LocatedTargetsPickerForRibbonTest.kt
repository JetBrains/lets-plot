/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock
import jetbrains.datalore.plot.builder.interact.TestUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class LocatedTargetsPickerForRibbonTest {

    @Test
    fun `get the closest target`() {
        val targets = createTargetLocators(isCrosshairEnabled = false)
        val results = findTargets(targets)
        assertLookupResults(results, listOf(FIRST_POINT_KEY))
    }

    @Test
    fun `when anchor is used - get all targets with the given X`() {
        val targets = createTargetLocators(isCrosshairEnabled = true)
        val results = findTargets(targets)
        assertLookupResults(results, listOf(FIRST_POINT_KEY, SECOND_POINT_KEY))
    }

    companion object {
        private val COORD = TestUtil.point(0.0, 0.0)

        private const val FIRST_POINT_KEY = 0
        private const val SECOND_POINT_KEY = 1
        private val FIRST_TARGET = TestUtil.pathTarget(
            FIRST_POINT_KEY,
            listOf(TestUtil.point(0.0, 0.0), TestUtil.point(2.0, 2.0))
        )
        private val SECOND_TARGET = TestUtil.pathTarget(
            SECOND_POINT_KEY,
            listOf(TestUtil.point(0.0, 2.0), TestUtil.point(1.0, 3.0))
        )

        private fun createTargetLocators(isCrosshairEnabled: Boolean): List<GeomTargetLocator> {
            val contextualMappingProvider = GeomInteractionBuilder(Aes.values())
                .setIsCrosshairEnabled(isCrosshairEnabled)
                .univariateFunction(GeomTargetLocator.LookupStrategy.HOVER)
                .build()
            val contextualMapping = contextualMappingProvider.createContextualMapping(
                MappedDataAccessMock().mappedDataAccess,
                DataFrame.Builder().build()
            )
            return listOf(
                TestUtil.createLocator(
                    lookupSpec = GeomTargetLocator.LookupSpec(
                        GeomTargetLocator.LookupSpace.X,
                        GeomTargetLocator.LookupStrategy.HOVER
                    ),
                    contextualMapping = contextualMapping,
                    targetPrototypes = listOf(FIRST_TARGET, SECOND_TARGET),
                    geomKind = GeomKind.RIBBON
                )
            )
        }

        private fun findTargets(
            targetLocators: List<GeomTargetLocator>
        ): List<GeomTargetLocator.LookupResult> {
            val targetsPicker = LocatedTargetsPicker()
            targetLocators.forEach { locator ->
                val lookupResult = locator.search(COORD)
                lookupResult?.let { targetsPicker.addLookupResult(it, COORD) }
            }
            return targetsPicker.picked
        }

        private fun assertLookupResults(
            results: List<GeomTargetLocator.LookupResult>,
            expected: List<Int>
        ) {
            assertEquals(1, results.size)
            assertEquals(expected.size, results.single().targets.size)
            val lookupResult = results.single()
            lookupResult.targets.forEachIndexed { index, target ->
                TestUtil.HitIndex(expected[index]).matches(target)
            }
        }
    }
}