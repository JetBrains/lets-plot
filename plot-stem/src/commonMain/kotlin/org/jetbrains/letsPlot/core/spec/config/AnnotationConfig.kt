/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.annotation.AnnotationSpecification
import org.jetbrains.letsPlot.core.spec.Option.AnnotationSpec.ANNOTATION_SIZE
import org.jetbrains.letsPlot.core.spec.Option.AnnotationSpec.USE_LAYER_COLOR

class AnnotationConfig(
    opts: Map<String, Any>,
    varBindings: List<VarBinding>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarNames: List<String>?
) : LineSpecConfigParser(opts, constantsMap, groupingVarNames, varBindings) {

    fun createAnnotations(): AnnotationSpecification {
        return create().run {
            AnnotationSpecification(
                valueSources = valueSources,
                linePatterns = linePatterns ?: emptyList(),
                textSize = getDouble(ANNOTATION_SIZE),
                useLayerColor = getBoolean(USE_LAYER_COLOR, false)
            )
        }
    }
}