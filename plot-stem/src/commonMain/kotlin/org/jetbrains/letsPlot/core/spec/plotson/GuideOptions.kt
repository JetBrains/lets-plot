/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class GuideOptions private constructor(
    toSpecDelegate: (Options) -> Any,
) : Options(toSpecDelegate = toSpecDelegate) {
    var legendOptions: LegendOptions? by map(Option.Guide.LEGEND)
    var colorBarOptions: ColorBarOptions? by map(Option.Guide.COLOR_BAR)
    var title: String? by map(Option.Guide.TITLE)

    class LegendOptions : Options() {
        var rowCount: Int? by map(Option.Guide.Legend.ROW_COUNT)
        var colCount: Int? by map(Option.Guide.Legend.COL_COUNT)
        var byRow: Boolean? by map(Option.Guide.Legend.BY_ROW)
        var overrideAes: Map<String, Any>? by map(Option.Guide.Legend.OVERRIDE_AES)
    }

    class ColorBarOptions : Options() {
        var width: Number? by map(Option.Guide.ColorBar.WIDTH)
        var height: Number? by map(Option.Guide.ColorBar.HEIGHT)
        var binCount: Int? by map(Option.Guide.ColorBar.BIN_COUNT)
    }

    companion object {
        fun none() = GuideOptions { Option.Guide.NONE }
        fun guide(block: GuideOptions.() -> Unit) = GuideOptions(Options::properties).apply(block)
    }
}
