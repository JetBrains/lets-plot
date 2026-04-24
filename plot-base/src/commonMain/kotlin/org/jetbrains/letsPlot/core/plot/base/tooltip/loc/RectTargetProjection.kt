/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.util.ClosestPointChecker
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy

internal class RectTargetProjection(
    val rect: DoubleRectangle,
    val lookupSpace: LookupSpace
) : TargetProjection {
    val x by lazy { rect.xRange() }
    val y by lazy { rect.yRange() }
    val xy = rect

    fun check(
        cursorCoord: DoubleVector,
        lookupStrategy: LookupStrategy,
        closestPointChecker: ClosestPointChecker
    ): Boolean {
        return when (lookupSpace) {
            LookupSpace.NONE -> false
            LookupSpace.X -> rangeBasedLookup(cursorCoord, lookupStrategy, closestPointChecker, x, byX = true)
            LookupSpace.Y -> rangeBasedLookup(cursorCoord, lookupStrategy, closestPointChecker, y, byX = false)
            LookupSpace.XY -> when (lookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> cursorCoord in xy
                LookupStrategy.NEAREST -> if (cursorCoord in xy) {
                    closestPointChecker.check(cursorCoord)
                } else {
                    var x = if (cursorCoord.x < xy.left) xy.left else xy.right
                    var y = if (cursorCoord.y < xy.top) xy.top else xy.bottom

                    x = if (xy.xRange().contains(cursorCoord.x)) cursorCoord.x else x
                    y = if (xy.yRange().contains(cursorCoord.y)) cursorCoord.y else y

                    closestPointChecker.check(DoubleVector(x, y))
                }
            }
        }
    }

    private fun rangeBasedLookup(
        cursor: DoubleVector,
        lookupStrategy: LookupStrategy,
        closestPointChecker: ClosestPointChecker,
        range: DoubleSpan,
        byX: Boolean
    ): Boolean {
        val p = if (byX) cursor.x else cursor.y

        return when (lookupStrategy) {
            LookupStrategy.NONE -> false
            LookupStrategy.HOVER -> {
                if (p in range) {
                    updatePointChecker(closestPointChecker)
                    true
                } else {
                    false
                }
            }

            LookupStrategy.NEAREST -> {
                val pp = if (byX) {
                    DoubleVector(range.lowerEnd, cursor.y)
                } else {
                    DoubleVector(cursor.x, range.lowerEnd)
                }
                //if (range.contains(p - RECT_X_NEAREST_EPSILON) || range.contains(p + RECT_X_NEAREST_EPSILON)) {
                    closestPointChecker.check(pp)
                //} else {
                //    false
                //}
            }
        }
    }

    private fun updatePointChecker(pointChecker: ClosestPointChecker) {
        if (rect.contains(pointChecker.target)) {
            pointChecker.check(pointChecker.target)
        } else {
            pointChecker.check(DoubleVector(rect.left, rect.top))
            pointChecker.check(DoubleVector(rect.right, rect.top))
            pointChecker.check(DoubleVector(rect.left, rect.bottom))
            pointChecker.check(DoubleVector(rect.right, rect.bottom))
        }
    }

    companion object {
        private const val RECT_X_NEAREST_EPSILON = 2.0
    }
}
