/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.plot.config.Option

class TooltipsOptions : Options() {
    var anchor: String? by map(Option.Layer.TOOLTIP_ANCHOR)
    var minWidth: String? by map(Option.Layer.TOOLTIP_MIN_WIDTH)
    var formats: List<Format>? by map(Option.Layer.TOOLTIP_FORMATS)
    var lines: List<String>? by map(Option.Layer.TOOLTIP_LINES)

    class Format : Options() {
        var field: String? by map(Option.TooltipFormat.FIELD)
        var format: String? by map(Option.TooltipFormat.FORMAT)
    }

    companion object {
        fun format(block: Format.() -> Unit) = Format().apply(block)
        fun variable(name: String) = "@$name"
    }
}

fun tooltips(block: TooltipsOptions.() -> Unit) = TooltipsOptions().apply(block)
