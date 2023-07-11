/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat

data class LinesContentSpecification(
    val valueSources: List<ValueSource>,
    val linePatterns: List<LineSpec>?,
    val titleLine: LineSpec?
) {
    companion object {
        open class LineSpec(
            val label: String?,
            val pattern: String,
            val fields: List<ValueSource>
        ) {
            companion object {
                fun defaultLineForValueSource(valueSource: ValueSource): LineSpec = LineSpec(
                    label = DEFAULT_LABEL_SPECIFIER,
                    pattern = StringFormat.valueInLinePattern(),
                    fields = listOf(valueSource)
                )
                const val DEFAULT_LABEL_SPECIFIER = "@"
            }
        }
    }
}