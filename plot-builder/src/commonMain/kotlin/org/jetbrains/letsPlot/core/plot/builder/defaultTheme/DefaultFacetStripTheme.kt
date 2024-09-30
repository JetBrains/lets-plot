/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.FacetStripTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT

internal class DefaultFacetStripTheme(
    direction: String,
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), FacetStripTheme {

    private val suffix = "_$direction"
    internal val rectKey = listOf(FACET_STRIP_BGR_RECT + suffix, FACET_STRIP_BGR_RECT, RECT)
    internal val textKey = listOf(FACET_STRIP_TEXT + suffix, FACET_STRIP_TEXT, TEXT)

    override fun showStrip() = !isElemBlank(textKey)

    override fun showStripBackground() = showStrip() && !isElemBlank(rectKey)

    override fun stripFill() = getColor(getElemValue(rectKey), Elem.FILL)

    override fun stripColor() = getColor(getElemValue(rectKey), Elem.COLOR)

    override fun stripStrokeWidth() = getNumber(getElemValue(rectKey), Elem.SIZE)

    override fun stripLineType() = getLineType(getElemValue(rectKey))

    override fun stripTextStyle() = getTextStyle(getElemValue(textKey))

    override fun stripMargins() = getMargins(getElemValue(textKey))

    override fun stripTextJustification() = getTextJustification(getElemValue(textKey))
}