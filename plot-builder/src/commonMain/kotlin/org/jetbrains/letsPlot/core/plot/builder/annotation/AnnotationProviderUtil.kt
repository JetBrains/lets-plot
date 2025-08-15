/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.Annotation
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.builder.tooltip.LinePattern
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object AnnotationProviderUtil {

    fun createAnnotation(
        spec: AnnotationSpecification,
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame,
        themeTextStyle: ThemeTextStyle,
        useCustomColor: Boolean
    ): Annotation? {
        val mappedLines = LinePattern.prepareMappedLines(
            spec.linePatterns.map(::LinePattern),
            dataAccess, dataFrame
        )
        if (mappedLines.isEmpty()) {
            return null
        }

        return Annotation(
            mappedLines,
            textStyle = TextStyle(
                themeTextStyle.family.name,
                themeTextStyle.face,
                spec.textSize ?: themeTextStyle.size,
                themeTextStyle.color
            ),
            useCustomColor,
            useLayerColor = spec.useLayerColor
        )
    }
}