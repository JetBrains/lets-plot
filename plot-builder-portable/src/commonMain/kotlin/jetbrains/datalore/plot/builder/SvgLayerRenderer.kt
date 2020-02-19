/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.SvgComponent

class SvgLayerRenderer(
    private val myAesthetics: Aesthetics,
    private val myGeom: Geom,
    private val myPos: PositionAdjustment,
    private val myCoord: CoordinateSystem,
    private val myGeomContext: GeomContext
) : SvgComponent(), SvgRoot {

    override fun buildComponent() {
        buildLayer()
    }

    private fun buildLayer() {
        myGeom.build(this, myAesthetics, myPos, myCoord, myGeomContext)
    }
}
