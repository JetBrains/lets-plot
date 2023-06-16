/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.GeomKind

class DensityGeom : AreaGeom() {

    override fun tooltipsGeomKind() = GeomKind.DENSITY

    companion object {
        const val DEF_QUANTILE_LINES = AreaGeom.DEF_QUANTILE_LINES
        const val HANDLES_GROUPS = AreaGeom.HANDLES_GROUPS
    }
}
