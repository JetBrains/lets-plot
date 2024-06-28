/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_ONTOP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_INSET
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT

internal class DefaultPanelTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), PanelTheme {

    private val gridX = DefaultPanelGridTheme("x", options, fontFamilyRegistry)
    private val gridY = DefaultPanelGridTheme("y", options, fontFamilyRegistry)

    internal val rectKey = listOf(PANEL_BKGR_RECT, RECT)
    internal val borderKey = listOf(PANEL_BORDER_RECT, RECT)
    private val borderOntopKey = listOf(PANEL_BORDER_ONTOP)
    private val insetKey = listOf(PANEL_INSET)

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

    override fun rectLineType() = getLineType(getElemValue(rectKey))

    override fun showBorder() = !isElemBlank(borderKey)

    override fun borderColor() = getColor(getElemValue(borderKey), Elem.COLOR)

    override fun borderWidth() = getNumber(getElemValue(borderKey), Elem.SIZE)

    override fun borderIsOntop(): Boolean = getBoolean(borderOntopKey)

    override fun borderLineType() = getLineType(getElemValue(borderKey))

    override fun verticalGrid(flipAxis: Boolean): PanelGridTheme = if (flipAxis) gridY else gridX

    override fun horizontalGrid(flipAxis: Boolean): PanelGridTheme = if (flipAxis) gridX else gridY

    override fun inset() = getPadding(getElemValue(insetKey))
}
