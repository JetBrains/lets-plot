/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.guide.TooltipAnchor
import jetbrains.datalore.plot.builder.guide.TooltipAnchor.HorizontalAnchor.*
import jetbrains.datalore.plot.builder.guide.TooltipAnchor.VerticalAnchor.*
import jetbrains.datalore.plot.builder.theme.TooltipTheme
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.OptionsAccessor

internal class TooltipThemeConfig(
    options: Map<String, Any>,
    defOptions: Map<String, Any>
) : OptionsAccessor(options, defOptions),
    TooltipTheme {

    override fun isVisible(): Boolean {
        return ThemeConfig.DEF.tooltip().isVisible()
    }

    override fun anchor(): TooltipAnchor? {
        if (!has(Option.Theme.TOOLTIP_ANCHOR))
            return ThemeConfig.DEF.tooltip().anchor()

        return when (val anchor = getString(Option.Theme.TOOLTIP_ANCHOR)) {
            "top_left" -> TooltipAnchor(TOP, LEFT)
            "top_center" -> TooltipAnchor(TOP, CENTER)
            "top_right" -> TooltipAnchor(TOP, RIGHT)
            "middle_left" -> TooltipAnchor(MIDDLE, LEFT)
            "middle_center" -> TooltipAnchor(MIDDLE, CENTER)
            "middle_right" -> TooltipAnchor(MIDDLE, RIGHT)
            "bottom_left" -> TooltipAnchor(BOTTOM, LEFT)
            "bottom_center" -> TooltipAnchor(BOTTOM, CENTER)
            "bottom_right" -> TooltipAnchor(BOTTOM, RIGHT)
            else -> throw IllegalArgumentException(
                "Illegal value $anchor, ${Option.Theme.TOOLTIP_ANCHOR}, expected values are: " +
                        "'top_left'/'top_center'/'top_right'/" +
                        "'middle_left'/'middle_center'/'middle_right'/" +
                        "'bottom_left'/'bottom_center'/'bottom_right'"
            )
        }
    }

    override fun minWidth(): Double? {
        if (has(Option.Theme.TOOLTIP_WIDTH)) {
            return getDouble(Option.Theme.TOOLTIP_WIDTH)
        }
        return ThemeConfig.DEF.tooltip().minWidth()
    }
}