/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.color

interface ColorScheme {
    val type: ColorPalette.Type

    val maxColors: Int

    val colorSet: Array<Array<String>>

    fun getColors(count: Int): Array<String>
}
