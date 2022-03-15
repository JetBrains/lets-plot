/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.tooltip
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.PointShapeSvg
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.slim.SvgSlimElements

open class PointGeom : GeomBase() {

    var animation: Any? = null
    var sizeUnit: String? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PointLegendKeyElementFactory()

    public override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.POINT, ctx)

        val count = aesthetics.dataPointCount()
        val slimGroup = SvgSlimElements.g(count)
        val sizeUnitRatio = getSizeUnitRatio(ctx)

        for (i in 0 until count) {
            val p = aesthetics.dataPointAt(i)
            val x = p.x()
            val y = p.y()

            if (SeriesUtil.allFinite(x, y)) {
                val location = helper.toClient(DoubleVector(x!!, y!!), p)

                val shape = p.shape()!!

                targetCollector.addPoint(
                    i, location, sizeUnitRatio * shape.size(p) / 2,
                    tooltip {
                        markerColors = colorsByDataPoint(p)
                    }
                )
                val o = PointShapeSvg.create(shape, location, p, sizeUnitRatio)
                o.appendTo(slimGroup)
            }
        }
        root.add(wrap(slimGroup))
    }

    private fun getSizeUnitRatio(ctx: GeomContext): Double {
        return if (sizeUnit != null) {
            val unitRes = ctx.getUnitResolution(GeomHelper.getSizeUnitAes(sizeUnit!!))
            // TODO: Need refactoring: It's better to use NamedShape.FILLED_CIRCLE.size(1.0)
            // but Shape.size() can't be used because it takes DataPointAesthetics as param
            unitRes / AesScaling.UNIT_SHAPE_SIZE
        } else {
            1.0
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

    }
}

