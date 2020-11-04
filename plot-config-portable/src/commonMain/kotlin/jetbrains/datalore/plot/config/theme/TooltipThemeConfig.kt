/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.guide.TooltipAnchor
import jetbrains.datalore.plot.builder.guide.TooltipAnchor.*
import jetbrains.datalore.plot.builder.theme.TooltipTheme
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.OptionsAccessor

internal class TooltipThemeConfig(options: Map<*, *>, defOptions: Map<*, *>) : OptionsAccessor(options, defOptions), TooltipTheme {

    override fun isVisible(): Boolean {
        return ThemeConfig.DEF.tooltip().isVisible()
    }

    override fun anchor(): TooltipAnchor? {
        if (!has(Option.Theme.TOOLTIP_ANCHOR))
            return ThemeConfig.DEF.tooltip().anchor()

        return when (val anchor = getString(Option.Theme.TOOLTIP_ANCHOR)) {
            "top_left" -> TOP_LEFT
            "top_center" -> TOP_CENTER
            "top_right" -> TOP_RIGHT
            "middle_left" -> MIDDLE_LEFT
            "middle_center" -> MIDDLE_CENTER
            "middle_right" -> MIDDLE_RIGHT
            "bottom_left" -> BOTTOM_LEFT
            "bottom_center" -> BOTTOM_CENTER
            "bottom_right" -> BOTTOM_RIGHT
            else -> throw IllegalArgumentException(
                "Illegal value $anchor, ${Option.Theme.TOOLTIP_ANCHOR}, expected values are: " +
                        "'top_left'/'top_center'/'top_right'/" +
                        "'middle_left'/'middle_center'/'middle_right'/" +
                        "'bottom_left'/'bottom_center'/'bottom_right'/"
            )
        }
    }
}