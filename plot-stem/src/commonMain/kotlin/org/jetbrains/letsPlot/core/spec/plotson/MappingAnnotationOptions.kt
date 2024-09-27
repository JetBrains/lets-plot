/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option.Meta

class MappingAnnotationOptions : Options() {
    var aes: Aes<*>? by map(Meta.MappingAnnotation.AES)
    var annotation: AnnotationType? by map(Meta.MappingAnnotation.ANNOTATION)
    var parameters: Parameters? by map(Meta.MappingAnnotation.PARAMETERS)

    fun parameters(block: Parameters.() -> Unit) {
        parameters = Parameters().apply(block)
    }

    class Parameters : Options() {
        var label: String? by map(Meta.MappingAnnotation.LABEL)
        var orderBy: String? by map(Meta.MappingAnnotation.ORDER_BY)
        var order: OrderType? by map(Meta.MappingAnnotation.ORDER)
    }

    enum class AnnotationType(val value: String) {
        AS_DISCRETE("as_discrete"),
    }

    enum class OrderType(val value: Int) {
        ASCENDING(1),
        DESCENDING(-1),
    }
}
