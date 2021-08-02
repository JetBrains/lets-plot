/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.scale.GuideBreak
import jetbrains.datalore.plot.builder.theme.LegendTheme

class ColorBarComponentSpec(
    title: String,
    val domain: ClosedRange<Double>,
    val breaks: List<GuideBreak<Double>>,
    val scale: Scale<Color>,
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
