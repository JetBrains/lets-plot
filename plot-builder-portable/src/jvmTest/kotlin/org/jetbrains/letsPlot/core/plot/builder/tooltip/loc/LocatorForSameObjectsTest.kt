/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil
import kotlin.test.Test
import kotlin.test.assertEquals


class LocatorForSameObjectsTest {

    @Test
    fun `locator should take the last closest object of the layer`() {
        // Two point objects with same coordinates in one layer:
        // if there are more than one closest object should take the last - this way the top object will be selected, not for the object lying beneath.
        val locator = TestUtil.createLocator(
            GeomTargetLocator.LookupStrategy.NEAREST,
            GeomTargetLocator.LookupSpace.XY,
            FIRST_TARGET, SECOND_TARGET
        )
        val results = findTargets(listOf(locator))
        assertLookupResult(results, SECOND_POINT_KEY)
    }

    @Test
    fun `tooltip should be taken for the object of the second layer`() {
        // Two point objects with same coordinates in different layers:
        // the object from the last layer will be selected
        val locators = listOf(
            TestUtil.createLocator(
                GeomTargetLocator.LookupStrategy.NEAREST,
                GeomTargetLocator.LookupSpace.XY,
                FIRST_TARGET
            ),
            TestUtil.createLocator(
                GeomTargetLocator.LookupStrategy.NEAREST,
                GeomTargetLocator.LookupSpace.XY,
                SECOND_TARGET
            )
        )
        val results = findTargets(locators)
        assertLookupResult(results, SECOND_POINT_KEY)
    }

    companion object {
        private val POINT_COORD = TestUtil.point(10.0, 10.0)
        private const val FIRST_POINT_KEY = 0
        private const val SECOND_POINT_KEY = 1
        private val FIRST_TARGET = TestUtil.pointTarget(FIRST_POINT_KEY, POINT_COORD)
        private val SECOND_TARGET = TestUtil.pointTarget(SECOND_POINT_KEY, POINT_COORD)

        private fun findTargets(targetLocators: List<GeomTargetLocator>): List<GeomTargetLocator.LookupResult> {
            val targetsPicker = LocatedTargetsPicker(flippedAxis = false)
            targetLocators.forEach { locator ->
                locator.search(POINT_COORD)?.let(targetsPicker::addLookupResult)
            }
            return targetsPicker.picked
        }

        private fun assertLookupResult(results: List<GeomTargetLocator.LookupResult>, expectedIndex: Int) {
            assertEquals(1, results.size)
            assertEquals(1, results.single().targets.size)
            val geomTarget = results.single().targets.single()

            assertEquals(expectedIndex, geomTarget.hitIndex)
        }
    }
}