/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.annotation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.builder.theme.ThemeTextStyle
import jetbrains.datalore.plot.builder.tooltip.ValueSource

class AnnotationSpecification(
    val valueSources: List<ValueSource>,
    val linePatterns: List<AnnotationLine>,
    // other settings
    val textSize: Double?
) {
    var textStyle: ThemeTextStyle = DEFAULT_STYLE

    companion object {
        val NONE = AnnotationSpecification(
            valueSources = emptyList(),
            linePatterns = emptyList(),
            textSize = null
        )
        private val DEFAULT_STYLE = ThemeTextStyle(
            family = FontFamily.SERIF,
            face = FontFace.NORMAL,
            size = 5.0,
            color = Color.BLACK
        )
    }
}