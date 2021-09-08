/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom


import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromFill
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.render.SvgRoot

open class AreaGeom : GeomBase() {

    protected fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(aesthetics.dataPoints())
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val dataPoints = dataPoints(aesthetics)

        val helper = LinesHelper(pos, coord, ctx)
        val paths = helper.createBands(dataPoints, GeomUtil.TO_LOCATION_X_Y, GeomUtil.TO_LOCATION_X_ZERO)
        paths.reverse()
        appendNodes(paths, root)

        //if you want to retain the side edges of area: comment out the following codes,
        // and switch decorate method in LinesHelper.createbands
        helper.setAlphaEnabled(false)
        val lines = helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        appendNodes(lines, root)

        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
            aesthetics.dataPoints(),
            singlePointAppender { p -> toClient(geomHelper, p) },
            reducer(0.999, false)
        )

        val targetCollector = getGeomTargetCollector(ctx)
        for (multiPointData in multiPointDataList) {
            targetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                setupTooltipParams(multiPointData.aes)
            )
        }
    }

    protected open fun setupTooltipParams(aes: DataPointAesthetics): TooltipParams {
        return params().setColor(fromFill(aes))
    }

    private fun toClient(geomHelper: GeomHelper, p: DataPointAesthetics): DoubleVector? {
        val coord = GeomUtil.TO_LOCATION_X_Y(p)
        return if (coord != null) {
            geomHelper.toClient(coord, p)
        } else {
            null
        }
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.SIZE,
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA
//        )

        const val HANDLES_GROUPS = true
    }


}
