/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.FacetStripTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry

internal class DefaultFacetsTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
): FacetsTheme {
    private val horizontalStrip = DefaultFacetStripTheme("x", options, fontFamilyRegistry)
    private val verticalStrip = DefaultFacetStripTheme("y", options, fontFamilyRegistry)

    override fun horizontalFacetStrip(): FacetStripTheme = horizontalStrip
    override fun verticalFacetStrip(): FacetStripTheme = verticalStrip
}