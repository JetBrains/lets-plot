/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE_Y
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.ELEMENT_BLANK

object ThemeValuesLPLight {
    val values: Map<String, Any> = ThemeValuesBase.values + mapOf(
        // Axis
//        AXIS_LINE_Y to ELEMENT_BLANK,
        AXIS_LINE to mapOf(ThemeOption.Elem.COLOR to Color.GREEN),
    )
}