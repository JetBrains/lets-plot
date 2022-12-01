/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.annotation.AnnotationLine
import jetbrains.datalore.plot.builder.annotation.AnnotationSpecification

class AnnotationConfig(
    opts: Map<String, Any>,
    constantsMap: Map<Aes<*>, Any>,
    groupingVarName: String?,
    varBindings: List<VarBinding>
) : LineSpecConfigParser(opts, constantsMap, groupingVarName, varBindings) {

    fun createAnnotations(): AnnotationSpecification {
        return create().run {
            AnnotationSpecification(
                valueSources = valueSources,
                linePatterns = linePatterns?.map(::AnnotationLine) ?: emptyList()
            )
        }
    }
}