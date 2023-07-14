/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.annotation

import jetbrains.datalore.plot.builder.tooltip.data.ValueSource

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