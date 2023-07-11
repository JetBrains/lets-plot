/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.color

interface ColorScheme {
    val type: ColorPalette.Type

    val maxColors: Int

    val colorSet: Array<Array<String>>

    fun getColors(count: Int): Array<String>
}
