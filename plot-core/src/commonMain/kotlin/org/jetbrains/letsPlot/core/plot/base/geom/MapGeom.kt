/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

class MapGeom : PolygonGeom() {
    companion object {
//        val RENDERS = listOf(
//
//                // auto-wired to 'x' or 'long' and to 'y' or 'lat'
//                Aes.X,
//                Aes.Y,
//
//                Aes.SIZE, // path width
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA,
//        )

        const val HANDLES_GROUPS = true
    }
}
