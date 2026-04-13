/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

interface DroppedPointsReporter {
    fun report(droppedPoints: Iterable<DataPointAesthetics>)

    companion object {
        val NONE = object : DroppedPointsReporter {
            override fun report(droppedPoints: Iterable<DataPointAesthetics>) {}
        }
    }
}
