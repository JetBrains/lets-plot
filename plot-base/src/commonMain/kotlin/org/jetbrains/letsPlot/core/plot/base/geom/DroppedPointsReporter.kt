/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

interface DroppedPointsReporter {
    fun report(droppedIndices: Set<Int>)

    companion object {
        val NONE = object : DroppedPointsReporter {
            override fun report(droppedIndices: Set<Int>) {}
        }
    }
}
