/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class TitleOptions : Options() {
    var titleText: String? by map(Option.Plot.TITLE_TEXT)
    var subtitleText: String? by map(Option.Plot.SUBTITLE_TEXT)
}

fun title(block: TitleOptions.() -> Unit) = TitleOptions().apply(block)
