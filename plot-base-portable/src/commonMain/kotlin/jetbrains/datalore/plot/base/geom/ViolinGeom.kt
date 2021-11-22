/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil

class ViolinGeom : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        buildLines(root, aesthetics, pos, coord, ctx)
    }

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = LinesHelper(pos, coord, ctx)
        val groupedDataPoints = aesthetics.dataPoints().groupBy {  it.x()!! }
        for ((_, nonOrderedDataPoints) in groupedDataPoints) {
            val dataPoints = GeomUtil.ordered_Y(nonOrderedDataPoints, false)
            val toLocationBound = fun (sign: Double): (p: DataPointAesthetics) -> DoubleVector? {
                return fun (p: DataPointAesthetics): DoubleVector? {
                    val x = p.x()!! + 30 * sign * p.weight()!! // TODO: Remove magic constant
                    return if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(p.y())) {
                        DoubleVector(x, p.y()!!)
                    } else null
                }
            }
            val paths = helper.createBands(dataPoints, toLocationBound(-1.0), toLocationBound(1.0))
            paths.reverse()
            appendNodes(paths, root)
        }
        // TODO: Build hints
    }

    companion object {
        const val HANDLES_GROUPS = false
    }

}