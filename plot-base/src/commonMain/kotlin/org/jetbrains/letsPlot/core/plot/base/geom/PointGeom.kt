/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements

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

        for (i in 0 until count) {
            val p = aesthetics.dataPointAt(i)
            if (p.finiteOrNull(Aes.SIZE) == null) continue
            val point = p.doubleVectorOrNull(Aes.X, Aes.Y) ?: continue
            val location = helper.toClient(point, p) ?: continue
            val shape = p.shape()!!

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            // TODO: Need refactoring: It's better to use NamedShape.FILLED_CIRCLE.size(1.0)
            // but Shape.size() can't be used because it takes DataPointAesthetics as param
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, AesScaling.POINT_UNIT_SIZE)

            targetCollector.addPoint(
                i, location, (shape.size(p, sizeUnitRatio) + shape.strokeWidth(p)) / 2,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(p)
                )
            )
            val o = PointShapeSvg.create(shape, location, p, sizeUnitRatio)
            o.appendTo(slimGroup)
        }
        root.add(wrap(slimGroup))
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}

