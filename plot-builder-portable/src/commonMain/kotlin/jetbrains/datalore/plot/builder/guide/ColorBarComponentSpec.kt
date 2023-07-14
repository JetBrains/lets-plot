/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

class ColorBarComponentSpec(
    title: String,
    val domain: DoubleSpan,
    val breaks: ScaleBreaks,
    val scaleMapper: ScaleMapper<Color>,
    val binCount: Int,
    theme: LegendTheme,
    override val layout: ColorBarComponentLayout,
    reverse: Boolean
) : LegendBoxSpec(title, theme, reverse) {

    companion object {
        const val DEF_NUM_BIN = 20

        private const val DEF_BAR_THICKNESS = 1.0  // in 'key-size' multiples
        private const val DEF_BAR_LENGTH = 5.0   // in 'key-size' multiples

        internal fun barAbsoluteSize(horizontal: Boolean, theme: LegendTheme): DoubleVector {
            return when {
                horizontal -> DoubleVector(
                    DEF_BAR_LENGTH * theme.keySize(),
                    DEF_BAR_THICKNESS * theme.keySize()
                )
                else -> DoubleVector(
                    DEF_BAR_THICKNESS * theme.keySize(),
                    DEF_BAR_LENGTH * theme.keySize()
                )
            }
        }
    }
}
