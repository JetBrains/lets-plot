/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.base.stringFormat.StringFormat.FormatType.STRING_FORMAT
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.TooltipLineSpec
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class TooltipLine(
    private val label: String?,
    private val pattern: String,
    val fields: List<ValueSource>
) : TooltipLineSpec {
    constructor(other: TooltipLine) : this(other.label, other.pattern, other.fields.map(ValueSource::copy))

    //    private val myLineFormatter = StringFormat(pattern, STRING_FORMAT).also {
//        require(it.argsNumber == fields.size) { "Wrong number of arguments in pattern \'$pattern\' to format fields. Expected ${fields.size} arguments instead of ${it.argsNumber}" }
//    }
    private val myLineFormatter = StringFormat.forNArgs(pattern, STRING_FORMAT, fields.size, "fields")

    fun initDataContext(dataContext: DataContext) {
        fields.forEach { it.initDataContext(dataContext) }
    }

    override fun getDataPoint(index: Int): DataPoint? {
        val dataValues = fields.map { dataValue ->
            dataValue.getDataPoint(index) ?: return null
        }
        return if (dataValues.size == 1) {
            val dataValue = dataValues.single()
            DataPoint(
                label = chooseLabel(dataValue.label),
                value = myLineFormatter.format(dataValue.value),
                aes = dataValue.aes,
                isAxis = dataValue.isAxis,
                isOutlier = dataValue.isOutlier
            )
        } else {
            DataPoint(
                label = chooseLabel(dataValues.joinToString(", ") { it.label ?: "" }),
                value = myLineFormatter.format(dataValues.map { it.value }),
                aes = null,
                isAxis = false,
                isOutlier = false
            )
        }
    }

    private fun chooseLabel(dataLabel: String?): String? {
        return when (label) {
            DEFAULT_LABEL_SPECIFIER -> dataLabel    // use default label (from data)
            else -> label                     // use the given label (can be null)
        }
    }

    companion object {
        fun defaultLineForValueSource(valueSource: ValueSource): TooltipLine = TooltipLine(
            label = DEFAULT_LABEL_SPECIFIER,
            pattern = StringFormat.valueInLinePattern(),
            fields = listOf(valueSource)
        )

        private const val DEFAULT_LABEL_SPECIFIER = "@"
    }
}