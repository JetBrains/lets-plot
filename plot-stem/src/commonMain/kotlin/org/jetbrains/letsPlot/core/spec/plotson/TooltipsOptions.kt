/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class TooltipsOptions private constructor(
    toSpecDelegate: (Options) -> Any,
) : Options(toSpecDelegate = toSpecDelegate) {
    constructor() : this(Options::properties)

    var anchor: String? by map(Option.Layer.TOOLTIP_ANCHOR)
    var minWidth: Double? by map(Option.Layer.TOOLTIP_MIN_WIDTH)
    var title: String? by map(Option.Layer.TOOLTIP_TITLE)
    var disableSplitting: Boolean? by map(Option.Layer.DISABLE_SPLITTING)
    var formats: List<Format>? by map(Option.LinesSpec.FORMATS)
    var lines: List<String>? by map(Option.LinesSpec.LINES)

    class Format : Options() {
        var field: String? by map(Option.LinesSpec.Format.FIELD)
        var format: String? by map(Option.LinesSpec.Format.FORMAT)
    }

    companion object {
        fun format(block: Format.() -> Unit) = Format().apply(block)
        fun variable(name: String) = "@$name"

        val NONE = TooltipsOptions { "none" }
    }
}

fun tooltips(block: TooltipsOptions.() -> Unit) = TooltipsOptions().apply(block)
