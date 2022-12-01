/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.annotation

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.annotations.AnnotationLineSpec
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.builder.tooltip.LinesContentSpecification.Companion.LineSpec
import jetbrains.datalore.plot.builder.tooltip.MappingValue
import jetbrains.datalore.plot.builder.tooltip.ValueSource
import jetbrains.datalore.vis.TextStyle

class AnnotationLine(
    pattern: String,
    fields: List<ValueSource>
): LineSpec(label = null, pattern, fields), AnnotationLineSpec {

    constructor(other: LineSpec) : this(other.pattern, other.fields.map(ValueSource::copy))

    private val myLineFormatter = StringFormat.forNArgs(pattern, fields.size, "fields")

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        fields.forEach { it.initDataContext(data, mappedDataAccess) }
    }

    override fun getDataPointAnnotation(index: Int): String? {
        val dataValues = fields.map { dataValue ->
            dataValue.getDataPointFormattedText(index) ?: return null
        }
        return myLineFormatter.format(dataValues.map { it })
    }

    companion object {
        fun createAnnotations(
            spec: AnnotationSpecification,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame
        ): Annotations? {
            if (spec.linePatterns.isEmpty()) {
                return null
            }
            val mappedLines = spec.linePatterns.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingValue>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedLines.forEach { it.initDataContext(dataFrame, dataAccess) }
            return Annotations(
                mappedLines,
                textStyle = TextStyle(
                    spec.textStyle.family.name,
                    spec.textStyle.face,
                    spec.textStyle.size,
                    spec.textStyle.color
                )
            )
        }
    }
}