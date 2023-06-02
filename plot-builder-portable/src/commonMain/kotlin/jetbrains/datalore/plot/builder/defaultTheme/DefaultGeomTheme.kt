/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PLOT_BKGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.theme.GeomTheme

internal class DefaultGeomTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), GeomTheme {

    private val lineKey = listOf(AXIS_LINE + "_x", AXIS_LINE + "_y", AXIS_LINE, AXIS, LINE)

    private val backgroundKey = listOf(PLOT_BKGR_RECT, RECT)

    override fun lineColor(): Color {
        return getColor(getElemValue(lineKey), ThemeOption.Elem.COLOR)
    }

    override fun strokeColor(): Color {
        return getColor(getElemValue(backgroundKey), ThemeOption.Elem.FILL)
    }
}