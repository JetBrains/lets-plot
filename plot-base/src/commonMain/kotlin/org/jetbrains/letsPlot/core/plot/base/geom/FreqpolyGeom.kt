/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

class FreqpolyGeom : LineGeom() {
    companion object {
//        val RENDERS: List<Aes<*>> = LineGeom.RENDERS

        const val HANDLES_GROUPS =
            LineGeom.HANDLES_GROUPS
    }
}
