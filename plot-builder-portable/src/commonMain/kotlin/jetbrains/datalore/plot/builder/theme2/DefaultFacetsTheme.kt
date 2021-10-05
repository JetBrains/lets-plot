/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption

class DefaultFacetsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), FacetsTheme {
    private val rectKey = listOf(
        ThemeOption.FACET_STRIP,
        ThemeOption.RECT
    )

    override fun stripFill(): Color {
        return getElemValue(rectKey)[ThemeOption.Elem.FILL] as Color
    }

    override fun stripColor(): Color {
        return getElemValue(rectKey)[ThemeOption.Elem.COLOR] as Color
    }

    override fun stripSize(): Double {
        return getNumber(getElemValue(rectKey), ThemeOption.Elem.SIZE)
    }
}