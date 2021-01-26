/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
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
    fun `get all targets with the given X`() {
        val targets = createTargetLocators()
        run {
            val results = findTargets(TestUtil.point(0.0, 0.0), targets)
            assertLookupResults(results, listOf(FIRST_POINT_KEY, SECOND_POINT_KEY))
        }
        run {
            val results = findTargets(TestUtil.point(1.0, 0.0), targets)
            assertLookupResults(results, listOf(SECOND_POINT_KEY))
        }
    }

    companion object {
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

        private fun createTargetLocators(): List<GeomTargetLocator> {
            val contextualMappingProvider = GeomInteractionBuilder(Aes.values())
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
            cursor: DoubleVector,
            targetLocators: List<GeomTargetLocator>
        ): List<GeomTargetLocator.LookupResult> {
            val targetsPicker = LocatedTargetsPicker()
            targetLocators.forEach { locator ->
                val lookupResult = locator.search(cursor)
                lookupResult?.let { targetsPicker.addLookupResult(it, cursor) }
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