/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Font

interface LabelSpec {
    val font: Font

    val isMonospaced: Boolean

    fun dimensions(labelText: String): DoubleVector

    fun width(labelText: String): Double

    fun widthByLength(labelLength: Int): Double

    fun height(): Double
}
