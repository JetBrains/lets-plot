/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
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
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(ctx)

        val source = aesthetics.dataPoints()
        val dataPoints = GeomUtil.withDefined(source, Aes.X, Aes.Y, Aes.SIZE)

        val filteredPointsIds = source.excludedIndicesComparedTo(dataPoints)
        val slimGroup = SvgSlimElements.g(dataPoints.count())
        val droppedPointsIds = LinkedHashSet<Int>()

        for (p in dataPoints) {
            val point = p.finiteVectorOrNull(Aes.X, Aes.Y)!!
            val location = helper.toClient(point, p)

            if (location == null) {
                droppedPointsIds.add(p.index())
                continue
            }

            val shape = p.shape()!!

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            // TODO: Need refactoring: It's better to use NamedShape.FILLED_CIRCLE.size(1.0)
            // but Shape.size() can't be used because it takes DataPointAesthetics as param

            val scaleFactor = if (sizeUnit.isNullOrBlank()) {
                ctx.getScaleFactor()
            } else {
                AesScaling.sizeUnitRatio(point, coord, sizeUnit, AesScaling.POINT_UNIT_SIZE)
            }

            targetCollector.addPoint(
                p.index(),
                location,
                (shape.size(p, scaleFactor) + shape.strokeWidth(p)) / 2,
                GeomTargetCollector.TooltipParams(markerColors = colorsByDataPoint(p))
            )
            PointShapeSvg.create(shape, location, p, scaleFactor)
                .appendTo(slimGroup)
        }
        ctx.droppedPointsReporter().report(filteredPointsIds + droppedPointsIds)
        root.add(wrap(slimGroup))
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
