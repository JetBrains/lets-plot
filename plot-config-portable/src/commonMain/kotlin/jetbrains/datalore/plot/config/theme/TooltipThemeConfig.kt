/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.guide.TooltipAnchor
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
            "top_right" -> TooltipAnchor.TOP_RIGHT
            "top_left" -> TooltipAnchor.TOP_LEFT
            "bottom_right" -> TooltipAnchor.BOTTOM_RIGHT
            "bottom_left" -> TooltipAnchor.BOTTOM_LEFT
            "none" -> null
            else -> throw IllegalArgumentException(
                "Illegal value $anchor, ${Option.Theme.TOOLTIP_ANCHOR}, expected values are: 'top_right'/'top_left'/'bottom_right'/'bottom_left'/'none'"
            )
        }
    }
}