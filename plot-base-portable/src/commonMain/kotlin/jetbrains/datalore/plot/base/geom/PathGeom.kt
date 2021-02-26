/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.LinePathConstructor
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.base.geometry.DoubleVector

open class PathGeom : GeomBase() {

    var animation: Any? = null

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
            false
        )
        appendNodes(
            geomConstructor.construct(withHints = false),
            root
        )
        buildHints(aesthetics, coord, linesHelper, targetCollector)
    }

    private fun buildHints(
        aesthetics: Aesthetics,
        coord: CoordinateSystem,
        linesHelper: LinesHelper,
        targetCollector: GeomTargetCollector
    ) {
        val dataPoints = dataPoints(aesthetics).filter { p ->
            val x = p.x()
            val y = p.y()
            SeriesUtil.allFinite(x, y) && coord.isPointInLimits(DoubleVector(x!!,y!!), isClient = false)
        }

        LinePathConstructor(
            targetCollector,
            dataPoints,
            linesHelper,
            false
        ).buildHints()
    }

    companion object {
        const val HANDLES_GROUPS = true
    }

}
