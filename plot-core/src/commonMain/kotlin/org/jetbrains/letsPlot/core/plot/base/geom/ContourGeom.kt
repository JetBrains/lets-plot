/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

open class ContourGeom : PathGeom() {
    companion object {
//        val RENDERS: List<Aes<*>> = PathGeom.RENDERS

        const val HANDLES_GROUPS =
            PathGeom.HANDLES_GROUPS
    }
}
