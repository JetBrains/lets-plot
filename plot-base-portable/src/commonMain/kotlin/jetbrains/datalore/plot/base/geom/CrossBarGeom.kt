/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.BarTooltipHelper
import jetbrains.datalore.plot.base.geom.util.CrossBarHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot

class CrossBarGeom : GeomBase() {
    var fattenMidline: Double = 2.5

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coordinateSystem: CoordinateSystem,
        ctx: GeomContext
    ) {
        CrossBarHelper.buildBoxes(
            root, aesthetics, pos, coordinateSystem, ctx,
            rectangleByDataPoint(ctx, coordinateSystem, isHintRect = false)
        )
        CrossBarHelper.buildMidlines(root, aesthetics, pos, coordinateSystem, ctx, fattenMidline)
        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.YMIN),
            aesthetics, pos, coordinateSystem, ctx,
            rectangleByDataPoint(ctx, coordinateSystem, isHintRect = true),
            { HintColorUtil.fromColor(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = CrossBarHelper.legendFactory(false)

        private fun rectangleByDataPoint(
            ctx: GeomContext,
            coordinateSystem: CoordinateSystem,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                var result: DoubleRectangle? = null
                if (!isHintRect &&
                    p.defined(Aes.X) &&
                    p.defined(Aes.YMIN) &&
                    p.defined(Aes.YMAX) &&
                    p.defined(Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val ymin = p.ymin()!!
                    val ymax = p.ymax()!!
                    val width = GeomUtil.widthPx(p, ctx, 2.0)

                    val rect = DoubleRectangle(
                        origin = DoubleVector(x - width / 2, ymin),
                        dimension = DoubleVector(width, ymax - ymin)
                    )
                    if (coordinateSystem.contains(rect)) {
                        result = rect
                    }
                } else if (isHintRect &&
                    p.defined(Aes.X) &&
                    p.defined(Aes.MIDDLE)
                ) {
                    val x = p.x()!!
                    val middle = p.middle()!!
                    val width = GeomUtil.widthPx(p, ctx, 2.0)

                    val rect = DoubleRectangle(
                        origin = DoubleVector(x - width / 2, middle),
                        dimension = DoubleVector(width, 0.0)
                    )
                    if (coordinateSystem.contains(rect)) {
                        result = rect
                    }
                }
                result
            }
        }
    }
}
