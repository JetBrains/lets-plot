/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem

object ThemeValuesRGrey {
    val values: Map<String, Any> = ThemeValuesBase.values + mapOf(
        // Axis
        AXIS_LINE to (ThemeValuesBase.values.getValue(AXIS_LINE) as Map<*, *>) + (Elem.COLOR to Color.RED),
    )
}