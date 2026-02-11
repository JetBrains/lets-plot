/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.StatAnnotation
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class StatR2Geom : GeomBase() {
    var formatter: ((Any) -> String)? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null
    var labelX: List<Pair<Double?, LabelX>> = emptyList()
    var labelY: List<Pair<Double?, LabelY>> = emptyList()

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = BlankLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        ctx.annotation?.let { StatAnnotation.build(root, aesthetics.dataPoints(), labelX, labelY, coord, ctx) }
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        enum class LabelX {
            LEFT, CENTER, RIGHT
        }

        enum class LabelY {
            TOP, MIDDLE, BOTTOM
        }
    }
}
