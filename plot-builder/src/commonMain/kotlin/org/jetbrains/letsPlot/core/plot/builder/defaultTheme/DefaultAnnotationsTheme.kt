/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.AnnotationsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption

internal class DefaultAnnotationsTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), AnnotationsTheme {

    internal val annotationTextKey = listOf(ThemeOption.ANNOTATION_TEXT, ThemeOption.TEXT)

    override fun textStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(annotationTextKey))
    }
}