/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.annotation

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.base.annotations.AnnotationLineSpec
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.builder.theme.ThemeTextStyle
import jetbrains.datalore.plot.builder.tooltip.LinesContentSpecification.Companion.LineSpec
import jetbrains.datalore.plot.builder.tooltip.MappingField
import jetbrains.datalore.plot.builder.tooltip.ValueSource
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class AnnotationLine(
    pattern: String,
    fields: List<ValueSource>
): LineSpec(label = null, pattern, fields), AnnotationLineSpec {

    constructor(other: LineSpec) : this(other.pattern, other.fields.map(ValueSource::copy))

    private val myLineFormatter = StringFormat.forNArgs(pattern, fields.size, "fields")

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        fields.forEach { it.initDataContext(data, mappedDataAccess) }
    }

    override fun getAnnotationText(index: Int): String? {
        val dataValues = fields.map { dataValue ->
            dataValue.getAnnotationText(index) ?: return null
        }
        return myLineFormatter.format(dataValues.map { it })
    }

    companion object {
        private val DEFAULT_STYLE = ThemeTextStyle(
            family = FontFamily.SERIF,
            face = FontFace.NORMAL,
            size = 10.0,
            color = Color.BLACK
        )

        fun createAnnotations(
            spec: AnnotationSpecification,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            themeTextStyle: ThemeTextStyle?
        ): Annotations? {
            if (spec.linePatterns.isEmpty()) {
                return null
            }
            val mappedLines = spec.linePatterns.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingField>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedLines.forEach { it.initDataContext(dataFrame, dataAccess) }
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
    }
}