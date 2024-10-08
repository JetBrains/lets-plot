/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.nextUp

internal object DataBoundsFix {

    /**
     * Prevent a rect from being too small and collapsing
     * due to limited precision of the Double.
     */
    fun unImplode(rect: DoubleRectangle): DoubleRectangle {
        val right = shiftedUp(rect.left)
        val bottom = shiftedUp(rect.top)
        return rect.union(DoubleRectangle.span(rect.origin, DoubleVector(right, bottom)))
    }

    private fun shiftedUp(value: Double): Double {
        var result = value
        repeat(1000) {
            result = result.nextUp()
        }
        return result
    }
}