/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.common.data.SeriesUtil

open class LineGeom : PathGeom() {

    override fun dataPoints(aesthetics: Aesthetics, coord: CoordinateSystem): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(super.dataPoints(aesthetics, coord)).filter { p ->
            val x = p.x()
            val y = p.y()
            SeriesUtil.allFinite(x, y) && coord.contains(DoubleVector(x!!,y!!))
        }
    }

    companion object {
//        val RENDERS = PathGeom.RENDERS

        const val HANDLES_GROUPS =
            PathGeom.HANDLES_GROUPS
    }
}
