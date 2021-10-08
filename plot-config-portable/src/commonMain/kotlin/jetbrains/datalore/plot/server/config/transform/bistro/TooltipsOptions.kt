/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.config.Option

class TooltipsOptions : Options<ThemeOptions>() {
    var anchor: String? by map(Option.Layer.TOOLTIP_ANCHOR)
    var minWidth: String? by map(Option.Layer.TOOLTIP_MIN_WIDTH)
    var color: String? by map(Option.Layer.TOOLTIP_COLOR)
    var formats: List<Format>? by map(Option.Layer.TOOLTIP_FORMATS)
    var lines: List<String>? by map(Option.Layer.TOOLTIP_LINES)

    class Format : Options<Format>() {
        var field: String? by map(Option.TooltipFormat.FIELD)
        var format: String? by map(Option.TooltipFormat.FORMAT)
    }

    companion object {
        fun variable(name: String) = "@$name"
    }
}
