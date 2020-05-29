/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.numberFormat.NumberFormat
import jetbrains.datalore.plot.base.interact.ValueSource

class LineFormatter(
    private val formatPattern: String
) {
    fun format(value: String, isContinuous: Boolean): String {
        return RE_PATTERN.replace(formatPattern) { match ->
            val pattern = match.groupValues[MATCHED_INDEX]
            when {
                isContinuous -> NumberFormat(pattern).apply(value.toFloat())
                else -> value
            }
        }
    }

    fun format(valuePoints: List<ValueSource.DataPoint>): String {
        val myFormatList = RE_PATTERN.findAll(formatPattern).map { it.groupValues[MATCHED_INDEX] }.toList()
        if (myFormatList.size != valuePoints.size) {
            return ""
        }
        var index = 0
        return RE_PATTERN.replace(formatPattern) { match ->
            val pattern = match.groupValues[MATCHED_INDEX]
            val value = valuePoints[index++]
            when {
                value.isContinuous -> NumberFormat(pattern).apply(value.value.toFloat())
                else -> value.value
            }
        }
    }

    companion object {
        val RE_PATTERN = """\{([^{}]*)}""".toRegex()
        private const val MATCHED_INDEX = 1

        private const val DEFAULT_LABEL = "{}"
        fun chooseLabel(dataLabel: String, userLabel: String): String {
            return if (userLabel == DEFAULT_LABEL) {
                dataLabel
            } else {
                userLabel
            }
        }
    }
}