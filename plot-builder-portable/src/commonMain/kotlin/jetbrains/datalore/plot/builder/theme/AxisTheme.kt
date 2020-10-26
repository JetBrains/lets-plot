/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

interface AxisTheme {
    fun showLine(): Boolean

    fun showTickMarks(): Boolean

    fun showTickLabels(): Boolean

    fun showTitle(): Boolean

    fun showTooltip(): Boolean

    fun lineWidth(): Double

    fun tickMarkWidth(): Double

    fun tickMarkLength(): Double

    fun tickMarkPadding(): Double

    fun tickLabelDistance(): Double {
        var result = tickMarkPadding()  // little space always
        if (showTickMarks()) {
            result += tickMarkLength()
        }
        return result
    }
}
