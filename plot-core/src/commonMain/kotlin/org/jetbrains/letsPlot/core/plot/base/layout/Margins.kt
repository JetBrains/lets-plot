/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.layout

class Margins(
    val top: Double = 0.0,
    val right: Double = 0.0,
    val bottom: Double = 0.0,
    val left: Double = 0.0
) {
    fun width() = left + right

    fun height() = top + bottom

    override fun toString(): String {
        return "Margins(top=$top, right=$right, bottom=$bottom, left=$left)"
    }
}