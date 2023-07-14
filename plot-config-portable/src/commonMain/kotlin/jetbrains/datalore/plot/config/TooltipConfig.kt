/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipLine
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipSpecification

class TooltipConfig(
    opts: Map<String, Any>,
    constantsMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>,
    groupingVarName: String?,
    varBindings: List<VarBinding>
) : LineSpecConfigParser(opts, constantsMap, groupingVarName, varBindings) {

    fun createTooltips(): TooltipSpecification {
        return create().run {
            TooltipSpecification(
                valueSources,
                linePatterns?.map(::TooltipLine),
                TooltipSpecification.TooltipProperties(
                    anchor = readAnchor(),
                    minWidth = readMinWidth()
                ),
                titleLine?.let(::TooltipLine),
                disableSplitting = getBoolean(Option.Layer.DISABLE_SPLITTING, def = false)
            )
        }

    }

    private fun readAnchor(): TooltipAnchor? {
        if (!has(Option.Layer.TOOLTIP_ANCHOR)) {
            return null
        }

        return when (val anchor = getString(Option.Layer.TOOLTIP_ANCHOR)) {
            "top_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.LEFT)
            "top_center" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.CENTER)
            "top_right" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.RIGHT)
            "middle_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.MIDDLE, TooltipAnchor.HorizontalAnchor.LEFT)
            "middle_center" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.MIDDLE,
                TooltipAnchor.HorizontalAnchor.CENTER
            )

            "middle_right" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.MIDDLE,
                TooltipAnchor.HorizontalAnchor.RIGHT
            )

            "bottom_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.BOTTOM, TooltipAnchor.HorizontalAnchor.LEFT)
            "bottom_center" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.BOTTOM,
                TooltipAnchor.HorizontalAnchor.CENTER
            )

            "bottom_right" -> TooltipAnchor(
                TooltipAnchor.VerticalAnchor.BOTTOM,
                TooltipAnchor.HorizontalAnchor.RIGHT
            )

            else -> throw IllegalArgumentException(
                "Illegal value $anchor, ${Option.Layer.TOOLTIP_ANCHOR}, expected values are: " +
                        "'top_left'/'top_center'/'top_right'/" +
                        "'middle_left'/'middle_center'/'middle_right'/" +
                        "'bottom_left'/'bottom_center'/'bottom_right'"
            )
        }
    }

    private fun readMinWidth(): Double? {
        if (has(Option.Layer.TOOLTIP_MIN_WIDTH)) {
            return getDouble(Option.Layer.TOOLTIP_MIN_WIDTH)
        }
        return null
    }
}
