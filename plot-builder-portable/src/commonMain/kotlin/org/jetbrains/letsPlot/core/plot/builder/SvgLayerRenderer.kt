/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent

class SvgLayerRenderer(
    private val aesthetics: Aesthetics,
    private val geom: Geom,
    private val pos: PositionAdjustment,
    private val coord: CoordinateSystem,
    private val geomContext: GeomContext
) : SvgComponent(), SvgRoot {

    override fun buildComponent() {
        buildLayer()
    }

    private fun buildLayer() {
        geom.build(this, aesthetics, pos, coord, geomContext)
    }
}
