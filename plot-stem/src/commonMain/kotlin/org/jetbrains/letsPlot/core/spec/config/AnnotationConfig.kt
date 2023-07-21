/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.annotation.AnnotationLine
import org.jetbrains.letsPlot.core.plot.builder.annotation.AnnotationSpecification
import org.jetbrains.letsPlot.core.spec.Option

class AnnotationConfig(
    opts: Map<String, Any>,
    varBindings: List<VarBinding>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarName: String?
) : org.jetbrains.letsPlot.core.spec.config.LineSpecConfigParser(opts, constantsMap, groupingVarName, varBindings) {

    fun createAnnotations(): AnnotationSpecification {
        return create().run {
            AnnotationSpecification(
                valueSources = valueSources,
                linePatterns = linePatterns?.map(::AnnotationLine) ?: emptyList(),
                textSize = getDouble(Option.Layer.ANNOTATION_SIZE)
            )
        }
    }
}