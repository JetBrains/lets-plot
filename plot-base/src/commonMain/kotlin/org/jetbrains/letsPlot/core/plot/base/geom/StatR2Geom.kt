/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.StatAnnotation
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label

class StatR2Geom : GeomBase() {
    var formatter: ((Any) -> String)? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null
    var labelX: String = "left"
    var labelY: String = "top"

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        ctx.annotation?.let { StatAnnotation.build(root, aesthetics.dataPoints(), positionX(labelX), positionY(labelY), coord, ctx) }
    }

    fun toString(label: Any?, geomContext: GeomContext): String {
        if (label == null) return ""

        val formatter = geomContext.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        fun positionX(x: String): LabelX {
            return when (x) {
                "left" -> LabelX.LEFT
                "center" -> LabelX.CENTER
                "right" -> LabelX.RIGHT
                else -> LabelX.LEFT
            }
        }

        fun positionY(y: String): LabelY {
            return when (y) {
                "top" -> LabelY.TOP
                "middle" -> LabelY.MIDDLE
                "bottom" -> LabelY.BOTTOM
                else -> LabelY.TOP
            }
        }

        enum class LabelX {
            LEFT, CENTER, RIGHT
        }

        enum class LabelY {
            TOP, MIDDLE, BOTTOM
        }
    }
}
