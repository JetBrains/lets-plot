/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class AnnotationOptions private constructor(
    toSpecDelegate: (Options) -> Any,
) : Options(toSpecDelegate = toSpecDelegate) {
    constructor() : this(Options::properties)

    var lines: List<String>? by map(Option.AnnotationSpec.LINES)
    var formats: List<Format>? by map(Option.AnnotationSpec.FORMATS)
    var size: Double? by map(Option.AnnotationSpec.ANNOTATION_SIZE)

    companion object {
        fun variable(name: String) = "@$name"

        val NONE = AnnotationOptions { "none" }
    }
}

fun annotation(block: AnnotationOptions.() -> Unit) = AnnotationOptions().apply(block)