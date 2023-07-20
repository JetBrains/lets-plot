/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf.bistro.util

import jetbrains.datalore.plot.config.Option

class TooltipsOptions : Options() {
    var anchor: String? by map(Option.Layer.TOOLTIP_ANCHOR)
    var minWidth: String? by map(Option.Layer.TOOLTIP_MIN_WIDTH)
    var formats: List<Format>? by map(Option.LinesSpec.FORMATS)
    var lines: List<String>? by map(Option.LinesSpec.LINES)

    class Format : Options() {
        var field: String? by map(Option.LinesSpec.Format.FIELD)
        var format: String? by map(Option.LinesSpec.Format.FORMAT)
    }

    companion object {
        fun format(block: Format.() -> Unit) = Format().apply(block)
        fun variable(name: String) = "@$name"
    }
}

fun tooltips(block: TooltipsOptions.() -> Unit) = TooltipsOptions().apply(block)
