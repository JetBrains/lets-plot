/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
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
            val x = p.x()
            val y = p.y()
            val size = p.size()

            if (SeriesUtil.allFinite(x, y, size)) {
                val point = DoubleVector(x!!, y!!)
                val location = helper.toClient(point, p)
                if (location == null) continue

                val shape = p.shape()!!

                // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
                val sizeUnitRatio = when (sizeUnit) {
                    null -> 1.0
                    else -> getSizeUnitRatio(point, coord, sizeUnit!!)
                }

                targetCollector.addPoint(
                    i, location, (sizeUnitRatio * shape.size(p) + shape.strokeWidth(p)) / 2,
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    )
                )
                val o = PointShapeSvg.create(shape, location, p, sizeUnitRatio)
                o.appendTo(slimGroup)
            }
        }
        root.add(wrap(slimGroup))
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun getSizeUnitRatio(
            p: DoubleVector,
            coord: CoordinateSystem,
            axis: String
        ): Double {
            val unitSquareSize = coord.unitSize(p)
            val unitSize = when (axis.lowercase()) {
                "x" -> unitSquareSize.x
                "y" -> unitSquareSize.y
                else -> error("Size unit value must be either 'x' or 'y', but was $axis.")
            }

            // TODO: Need refactoring: It's better to use NamedShape.FILLED_CIRCLE.size(1.0)
            // but Shape.size() can't be used because it takes DataPointAesthetics as param
            return unitSize / AesScaling.UNIT_SHAPE_SIZE
        }
    }
}

