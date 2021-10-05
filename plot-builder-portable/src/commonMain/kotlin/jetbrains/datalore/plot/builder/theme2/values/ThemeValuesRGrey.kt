/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_RECT

object ThemeValuesRGrey {
    val values: Map<String, Any> = ThemeValuesBase.values + mapOf(
        // Panel (geom background)
        PANEL_RECT to mapOf(
            Elem.FILL to Color.parseHex("#EBEBEB"),
            Elem.SIZE to 0.0,
        ),

        // Axis
        AXIS_LINE to ELEMENT_BLANK,

        // Facets
        ThemeOption.FACET_STRIP to mapOf(
            Elem.FILL to Color.parseHex("#D9D9D9"),
            Elem.SIZE to 0.0,
        ),
    )
}