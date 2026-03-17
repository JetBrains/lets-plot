/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

class DensityGeom : AreaGeom() {

    companion object {
        const val DEF_QUANTILE_LINES = AreaGeom.DEF_QUANTILE_LINES
        const val HANDLES_GROUPS = AreaGeom.HANDLES_GROUPS
    }
}
