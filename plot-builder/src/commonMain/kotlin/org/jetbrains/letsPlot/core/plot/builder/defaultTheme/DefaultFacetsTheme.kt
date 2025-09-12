/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.FacetStripTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_PANEL_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_PANEL_SPACING_X
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_PANEL_SPACING_Y

internal class DefaultFacetsTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
): ThemeValuesAccess(options, fontFamilyRegistry), FacetsTheme {
    private val horizontalStrip = DefaultFacetStripTheme("x", options, fontFamilyRegistry)
    private val verticalStrip = DefaultFacetStripTheme("y", options, fontFamilyRegistry)

    override fun horizontalFacetStrip(): FacetStripTheme = horizontalStrip
    override fun verticalFacetStrip(): FacetStripTheme = verticalStrip

    override fun panelSpacing(): DoubleVector {
        val spacingX = getNumber(listOf(FACET_PANEL_SPACING_X, FACET_PANEL_SPACING))
        val spacingY = getNumber(listOf(FACET_PANEL_SPACING_Y, FACET_PANEL_SPACING))
        return DoubleVector(spacingX, spacingY)
    }
}