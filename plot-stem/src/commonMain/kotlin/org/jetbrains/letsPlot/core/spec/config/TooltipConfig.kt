/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.StatKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehavior
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehaviorFactory
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinesContentSpecification
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.spec.Option.Layer.Tooltips

class TooltipConfig(
    private val geomKind: GeomKind,
    opts: Map<String, Any>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarNames: List<String>?,
    varBindings: List<VarBinding>
) : OptionsAccessor(opts) {
    private val lineSpecParser = LineSpecParser(
        opts = opts,
        constantsMap = constantsMap,
        groupingVarNames = groupingVarNames,
        varBindings = varBindings
    )

    val contentSpecification: LinesContentSpecification by lazy {
        lineSpecParser.create()
    }
    val anchor: TooltipAnchor? = readAnchor()
    val isCrosshairEnabled = isCrosshairEnabled(anchor)
    val minWidth = getDouble(Tooltips.TOOLTIP_MIN_WIDTH)
    val disableSplitting = getBoolean(Tooltips.DISABLE_SPLITTING, def = false)
    val tooltipGroup: String? = getString(Tooltips.TOOLTIP_GROUP) ?: "__auto_line_group__".takeIf { geomKind in LINE_LIKE_GEOMS }

    val valueSources
        get() = contentSpecification.valueSources

    private fun readAnchor(): TooltipAnchor? {
        if (!has(Tooltips.TOOLTIP_ANCHOR)) {
            return null
        }

        return when (val anchor = getString(Tooltips.TOOLTIP_ANCHOR)) {
            "top_left" -> TooltipAnchor(VerticalAnchor.TOP, HorizontalAnchor.LEFT)
            "top_center" -> TooltipAnchor(VerticalAnchor.TOP, HorizontalAnchor.CENTER)
            "top_right" -> TooltipAnchor(VerticalAnchor.TOP, HorizontalAnchor.RIGHT)
            "middle_left" -> TooltipAnchor(VerticalAnchor.MIDDLE, HorizontalAnchor.LEFT)
            "middle_center" -> TooltipAnchor(VerticalAnchor.MIDDLE, HorizontalAnchor.CENTER)
            "middle_right" -> TooltipAnchor(VerticalAnchor.MIDDLE, HorizontalAnchor.RIGHT)
            "bottom_left" -> TooltipAnchor(VerticalAnchor.BOTTOM, HorizontalAnchor.LEFT)
            "bottom_center" -> TooltipAnchor(VerticalAnchor.BOTTOM, HorizontalAnchor.CENTER)
            "bottom_right" -> TooltipAnchor(VerticalAnchor.BOTTOM, HorizontalAnchor.RIGHT)

            else -> throw IllegalArgumentException(
                "Illegal value $anchor, ${Tooltips.TOOLTIP_ANCHOR}, expected values are: " +
                        "'top_left'/'top_center'/'top_right'/" +
                        "'middle_left'/'middle_center'/'middle_right'/" +
                        "'bottom_left'/'bottom_center'/'bottom_right'"
            )
        }
    }

    companion object {
        fun defaultTooltip(
            geomKind: GeomKind,
            statKind: StatKind,
        ): TooltipBehavior {
            return TooltipBehaviorFactory.defaultTooltip(geomKind, statKind)
        }

        private fun isCrosshairEnabled(anchor: TooltipAnchor?): Boolean {
            return anchor != null
        }

        private val LINE_LIKE_GEOMS = setOf(
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.SMOOTH,
            GeomKind.STEP,
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.RIBBON,
            GeomKind.SEGMENT,
            GeomKind.SPOKE
        )
    }
}
