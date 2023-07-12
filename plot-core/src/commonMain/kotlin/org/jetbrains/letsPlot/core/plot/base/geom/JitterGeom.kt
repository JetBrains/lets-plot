/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

class JitterGeom : PointGeom() {
    companion object {
//        val RENDERS: List<Aes<*>> = PointGeom.RENDERS

        const val HANDLES_GROUPS =
            PointGeom.HANDLES_GROUPS
    }
}
