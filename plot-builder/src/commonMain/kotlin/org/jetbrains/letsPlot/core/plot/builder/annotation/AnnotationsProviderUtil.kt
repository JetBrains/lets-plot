/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.builder.tooltip.LinePattern
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object AnnotationsProviderUtil {

    fun createAnnotations(
        spec: AnnotationSpecification,
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame,
        themeTextStyle: ThemeTextStyle?
    ): Annotations? {
        val mappedLines = LinePattern.prepareMappedLines(
            spec.linePatterns.map(::LinePattern),
            dataAccess, dataFrame
        )
        if (mappedLines.isEmpty()) {
            return null
        }

        return Annotations(
            mappedLines,
            textStyle = TextStyle(
                themeTextStyle?.family?.name ?: DEFAULT_STYLE.family.name,
                themeTextStyle?.face ?: DEFAULT_STYLE.face,
                spec.textSize ?: themeTextStyle?.size ?: DEFAULT_STYLE.size,
                themeTextStyle?.color ?: DEFAULT_STYLE.color
            )
        )
    }

    private val DEFAULT_STYLE = ThemeTextStyle(
        family = FontFamily.SERIF,
        face = FontFace.NORMAL,
        size = 10.0,
        color = Color.BLACK
    )
}