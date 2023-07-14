/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.HitIndex
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.PathPoint
import kotlin.test.Test

class LayerTargetLocatorPathNearestXTest : TargetLocatorPathXTestBase() {

    override val strategy: LookupStrategy
        get() = LookupStrategy.NEAREST

    @Test
    fun nearestX_WhenCloserToLeft() {
        assertThat(
            findTargets(
                rightFrom(
                    p1,
                    THIS_POINT_DISTANCE
                )
            )
        ).first().has(HitIndex.equalTo(p1.hitIndex))
    }


    @Test
    fun nearestX_WhenCloserToRight() {
        assertThat(
            findTargets(
                rightFrom(
                    p1,
                    NEXT_POINT_DISTANCE
                )
            )
        ).first().has(HitIndex.equalTo(p2.hitIndex))
    }

    @Test
    fun nearestX_WhenInTheMiddle_ShouldSelectSecondPoint() {
        assertThat(
            findTargets(
                rightFrom(
                    p1,
                    MIDDLE_POINTS_DISTANCE
                )
            )
        ).first().has(HitIndex.equalTo(p1.hitIndex))
    }


    @Test
    fun nearestX_WhenOutOfPath_ShouldFindNothing() {
        assertThat(
            findTargets(
                leftFrom(
                    p0,
                    NEXT_POINT_DISTANCE
                )
            )
        ).isEmpty()
    }


    private fun leftFrom(p: PathPoint, distance: Double): DoubleVector {
        return DoubleVector(p.x - distance, p.y)
    }

    private fun rightFrom(p: PathPoint, distance: Double): DoubleVector {
        return DoubleVector(p.x + distance, p.y)
    }


    private fun findTargets(p: DoubleVector): List<GeomTarget> {
        return org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.findTargets(locator, p)
    }
}
