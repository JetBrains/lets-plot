/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.LinePathConstructor
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot

open class PathGeom : GeomBase() {

    var animation: Any? = null
    var flat: Boolean = false
    var geodesic: Boolean = false

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    protected open fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {

        val dataPoints = dataPoints(aesthetics)
        val targetCollector = getGeomTargetCollector(ctx)
        val linesHelper = LinesHelper(pos, coord, ctx)

        val geomConstructor = LinePathConstructor(
            targetCollector,
            dataPoints,
            linesHelper,
            myClosePath = false,
            HintColorUtil.createColorMarkerMapper(GeomKind.PATH, ctx),
            ctx.flipped
        )

        appendNodes(
            geomConstructor.construct(),
            root
        )
    }

    companion object {
        const val HANDLES_GROUPS = true
    }

}
