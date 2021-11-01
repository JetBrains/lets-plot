/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.theme.PanelGridTheme
import jetbrains.datalore.plot.builder.theme.PanelTheme

internal class DefaultPanelTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), PanelTheme {

    private val gridX = DefaultPanelGridTheme("x", options)
    private val gridY = DefaultPanelGridTheme("y", options)

    internal val rectKey = listOf(PANEL_BKGR_RECT, RECT)

    override fun showRect(): Boolean {
        return !isElemBlank(rectKey)
    }

    override fun rectColor(): Color {
        return getColor(getElemValue(rectKey), Elem.COLOR)
    }

    override fun rectFill(): Color {
        return getColor(getElemValue(rectKey), Elem.FILL)
    }

    override fun rectStrokeWidth(): Double {
        return getNumber(getElemValue(rectKey), Elem.SIZE)
    }

    override fun gridX(flipAxis: Boolean): PanelGridTheme = if (flipAxis) gridY else gridX

    override fun gridY(flipAxis: Boolean): PanelGridTheme = if (flipAxis) gridX else gridY
}
