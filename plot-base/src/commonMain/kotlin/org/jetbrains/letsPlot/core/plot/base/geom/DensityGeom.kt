/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.GeomKind

class DensityGeom : AreaGeom() {

    override fun tooltipsGeomKind() = GeomKind.DENSITY

    companion object {
        const val DEF_QUANTILE_LINES = AreaGeom.DEF_QUANTILE_LINES
        const val HANDLES_GROUPS = AreaGeom.HANDLES_GROUPS
    }
}
