/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.ValueSource

class AnnotationSpecification(
    val valueSources: List<ValueSource>,
    val linePatterns: List<AnnotationLine>,
    // other settings
    val textSize: Double?
) {
    companion object {
        val NONE = AnnotationSpecification(
            valueSources = emptyList(),
            linePatterns = emptyList(),
            textSize = null
        )
    }
}