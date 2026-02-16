/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.geom.util.LabelOptions
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

open class LabelGeom : TextGeom() {
    val labelOptions = LabelOptions()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val targetCollector = getGeomTargetCollector(ctx)

        val textHelper = TextHelper(aesthetics, pos, coord, ctx, labelOptions = labelOptions, formatter, naValue, sizeUnit, checkOverlap, flipAngle = false, ::coordOrNull)
        textHelper.createSvgComponents().forEach { svgElement ->
            root.add(svgElement)
        }
        textHelper.buildHints(targetCollector)
    }
}