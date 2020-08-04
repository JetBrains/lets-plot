/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.numberFormat.NumberFormat
import jetbrains.datalore.plot.base.interact.ValueSource

internal class LineFormatter(
    val formatPattern: String?
) {
    // composite string with a single value source
    fun format(value: String, isContinuous: Boolean): String {
        return if (formatPattern != null) {
            RE_PATTERN.replace(formatPattern) { match ->
                val pattern = match.groupValues[MATCHED_INDEX]
                when {
                    isContinuous -> NumberFormat(pattern).apply(value.toFloat())
                    else -> value
                }
            }
        } else {
            value
        }
    }

    // composite string with a multiple sources
    fun format(valuePoints: List<ValueSource.DataPoint>): String {
        if (formatPattern == null) {
            return valuePoints.joinToString(transform = ValueSource.DataPoint::line)
        }

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
    }
}