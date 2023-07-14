/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style

fun main() {
    println(Style.generateCSS(Style.default(), plotId = null, decorationLayerId = null))
    println("======")
    println(
        Style.generateCSS(
            Style.fromTheme(DefaultTheme.minimal2(), flippedAxis = true),
            plotId = "p123",
            decorationLayerId = "d456"
        )
    )
}