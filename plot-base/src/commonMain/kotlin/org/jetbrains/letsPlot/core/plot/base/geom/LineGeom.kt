/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil

open class LineGeom : PathGeom() {

    override fun filterDataPoints(dataPoints: Iterable<DataPointAesthetics>): Pair<Iterable<DataPointAesthetics>, Iterable<DataPointAesthetics>> {
        val (data, invalid) = GeomUtil.with_X(dataPoints)
        return GeomUtil.ordered_X(data) to invalid
    }

    companion object {
        const val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
