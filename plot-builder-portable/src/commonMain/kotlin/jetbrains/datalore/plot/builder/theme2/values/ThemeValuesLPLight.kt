/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE

object ThemeValuesLPLight {
    val values: Map<String, Any> = ThemeValuesBase.values + mapOf(
        // Axis
//        AXIS_LINE_Y to ELEMENT_BLANK,
        AXIS_LINE to mapOf(ThemeOption.Elem.COLOR to Color.GREEN),

        // Facet
        ThemeOption.FACET_STRIP to ThemeOption.RECT + mapOf(
            ThemeOption.Elem.FILL to Colors.lighter(Color.VERY_LIGHT_GRAY, 0.9),
            ThemeOption.Elem.SIZE to 0.0,
        ),
    )
}