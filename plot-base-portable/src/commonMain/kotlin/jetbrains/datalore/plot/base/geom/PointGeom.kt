/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromColorValue
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.render.point.PointShapeSvg
import jetbrains.datalore.plot.base.render.point.TinyPointShape
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.slim.SvgSlimElements

open class PointGeom : GeomBase() {

    var animation: Any? = null
    var sizeUnit: String? = null
    var sizeUnitScale: Double? = null

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

        val count = aesthetics.dataPointCount()
        val slimGroup = SvgSlimElements.g(count)
        for (i in 0 until count) {
            val p = aesthetics.dataPointAt(i)
            val x = p.x()
            val y = p.y()

            if (SeriesUtil.allFinite(x, y)) {
                val location = helper.toClient(DoubleVector(x!!, y!!), p)
                val shape = p.shape()!!
                val scale = getScaleBySizeUnit(ctx, p)

                targetCollector.addPoint(
                    i, location, scale * shape.size(p) / 2,
                    tooltipParams(p)
                )
                val o = PointShapeSvg.create(shape, location, p, scale)
                o.appendTo(slimGroup)
            }
        }
        root.add(wrap(slimGroup))
    }

    private fun getSizeUnitAes( sizeUnitName: String ) : Aes<Double> {
        Preconditions.checkArgument(
            sizeUnitName == X || sizeUnitName == Y,
            "size_unit value must be either '${X}' or '${Y}'"
        )

        if ( sizeUnitName == X) {
            return Aes.X
        } else if ( sizeUnitName == Y) {
            return Aes.Y
        }

        error("Wrong size_unit value")
    }

    private fun getScaleBySizeUnit(ctx: GeomContext, p: DataPointAesthetics): Double {
        sizeUnitScale?.let { return sizeUnitScale!! }
        sizeUnitScale = 1.0

        sizeUnit?.let {
            val aes = getSizeUnitAes(sizeUnit!!)
            val shape = p.shape()!!
            sizeUnitScale = (p.size()?.div(shape.size(p)) ?: 0.0) * ctx.getUnitResolution(aes)
        }

        return sizeUnitScale!!
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val X = "x"
        const val Y = "y"

        fun tooltipParams(p: DataPointAesthetics): TooltipParams {
            var color = Color.TRANSPARENT
            if (p.shape() == TinyPointShape) {
                color = p.color()!!
            } else if (p.shape() is NamedShape) {
                val shape = p.shape() as NamedShape
                color = AestheticsUtil.fill(shape.isFilled, shape.isSolid, p)
            }

            return params().setColor(fromColorValue(color, p.alpha()!!))
        }
    }
}

