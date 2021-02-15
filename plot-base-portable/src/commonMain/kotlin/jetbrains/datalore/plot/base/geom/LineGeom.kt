/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.GeomUtil

open class LineGeom : PathGeom() {

    override fun dataPoints(aesthetics: Aesthetics, coordinateSystem: CoordinateSystem): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(super.dataPoints(aesthetics, coordinateSystem)).filter { p ->
            val coord = GeomUtil.TO_LOCATION_X_Y(p)
            coord != null && coordinateSystem.contains(coord)
        }
    }

    companion object {
//        val RENDERS = PathGeom.RENDERS

        const val HANDLES_GROUPS =
            PathGeom.HANDLES_GROUPS
    }
}
