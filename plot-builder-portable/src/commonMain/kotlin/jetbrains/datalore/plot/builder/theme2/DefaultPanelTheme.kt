/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.PanelGridTheme
import jetbrains.datalore.plot.builder.theme.PanelTheme
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem

class DefaultPanelTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), PanelTheme {

    private val gridX = DefaultPanelGridTheme("x", options)
    private val gridY = DefaultPanelGridTheme("y", options)

    private val rectKey = listOf(
        ThemeOption.PANEL_RECT,
        ThemeOption.RECT
    )

    override fun showRect(): Boolean {
        return !isElemBlank(rectKey)
    }

    override fun rectColor(): Color {
        return getElemValue(rectKey)[Elem.COLOR] as Color
    }

    override fun rectFill(): Color {
        return getElemValue(rectKey)[Elem.FILL] as Color
    }

    override fun rectsize(): Double {
        return getNumber(getElemValue(rectKey), Elem.SIZE)
    }

    override fun gridX(): PanelGridTheme = gridX

    override fun gridY(): PanelGridTheme = gridY
}
