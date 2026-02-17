/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.SmoothAnnotation
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.SmoothSummaryAnnotation
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

open class BlankGeom : GeomBase() {
    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = BlankLegendKeyElementFactory()

    public override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val smoothAnnotation = ctx.annotation as? SmoothAnnotation ?: return
        SmoothSummaryAnnotation.build(root, aesthetics.dataPoints(), smoothAnnotation.labelX, smoothAnnotation.labelY, coord, ctx)
    }

    companion object {
        const val HANDLES_GROUPS = false

        enum class LabelX {
            LEFT, CENTER, RIGHT
        }

        enum class LabelY {
            TOP, MIDDLE, BOTTOM
        }
    }
}

