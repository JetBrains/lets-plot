/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.presentation.ThemeStyleProperties

fun main() {
    println(Style.generateCSS(ThemeStyleProperties()))
    println("======")
    println(Style.generateCSS(ThemeStyleProperties().applyTheme(DefaultTheme.minimal2(), flippedAxis = true)))
}