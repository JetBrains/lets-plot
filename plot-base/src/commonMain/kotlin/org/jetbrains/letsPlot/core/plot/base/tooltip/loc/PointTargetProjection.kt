/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.util.ClosestPointChecker
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.MathUtil

internal class PointTargetProjection(
    val point: DoubleVector,
    val radius: Double,
    val lookupSpace: LookupSpace
) : TargetProjection {
    val x = point.x
    val y = point.y
    val xy = point

    fun check(
        cursorCoord: DoubleVector,
        lookupStrategy: LookupStrategy,
        closestPointChecker: ClosestPointChecker
    ): Boolean {
        return when (lookupSpace) {
            LookupSpace.NONE -> false
            LookupSpace.X -> when (lookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(x, cursorCoord.x, radius + POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(DoubleVector(x, 0.0))
            }

            LookupSpace.Y -> when (lookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(y, cursorCoord.y, radius + POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(DoubleVector(0.0, y))
            }

            LookupSpace.XY -> when (lookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(xy, cursorCoord, radius + POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(xy, radius + POINT_AREA_EPSILON)
            }
        }
    }

    companion object {
        private const val POINT_AREA_EPSILON = 5.1
    }
}
