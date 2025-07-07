/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class Format : Options() {
    var field: String? by map(Option.LinesSpec.Format.FIELD)
    var format: String? by map(Option.LinesSpec.Format.FORMAT)
}

fun format(block: Format.() -> Unit) = Format().apply(block)