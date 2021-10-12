/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.FACET_STRIP_BGR_RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeValuesBase.PLOT_BACKGROUND

object ThemeValuesRClassic {
    val values: Map<String, Any> = ThemeValuesBase.values + mapOf(
        // Facet
        FACET_STRIP_BGR_RECT to mapOf(
            Elem.FILL to PLOT_BACKGROUND,
        ),
    )
}