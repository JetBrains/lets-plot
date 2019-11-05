/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.GeomUtil

open class LineGeom : PathGeom() {

    override fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(super.dataPoints(aesthetics))
    }

    companion object {
//        val RENDERS = PathGeom.RENDERS

        const val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
