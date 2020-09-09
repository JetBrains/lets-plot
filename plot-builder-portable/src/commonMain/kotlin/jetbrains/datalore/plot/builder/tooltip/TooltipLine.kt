/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.TooltipLineSpec
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

open class TooltipLine(
    val label: String?,
    val linePattern: String,
    val data: List<ValueSource>
) : TooltipLineSpec {
    private val myLineFormatter =
        LinePatternFormatter(linePattern)

    fun setDataContext(dataContext: DataContext) {
        data.forEach { it.setDataContext(dataContext) }
    }

    override fun getDataPoint(index: Int): DataPoint? {
        val dataValues = data.map { dataValue ->
            dataValue.getDataPoint(index) ?: return null
        }
        return if (dataValues.size == 1) {
            val dataValue = dataValues.single()
            DataPoint(
                label = label ?: dataValue.label,
                value = myLineFormatter.format(dataValue.value),
                isContinuous = dataValue.isContinuous,
                aes = dataValue.aes,
                isAxis = dataValue.isAxis,
                isOutlier = dataValue.isOutlier
            )
        } else {
            DataPoint(
                label = label ?: dataValues.joinToString(", ") { it.label },
                value = myLineFormatter.format(dataValues.map { it.value }),
                isContinuous = false,
                aes = null,
                isAxis = false,
                isOutlier = false
            )
        }
    }

    companion object {
        fun defaultLineForValueSource(valueSource: ValueSource): TooltipLine = TooltipLine(
            label = null,
            linePattern = LinePatternFormatter.valueInLinePattern(),
            data = listOf(valueSource)
        )
    }
}