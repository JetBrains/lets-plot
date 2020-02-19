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

class ColorBarComponentSpec(title: String,
                            internal val domain: ClosedRange<Double>,
                            internal val breaks: List<GuideBreak<Double>>,
                            internal val scale: Scale<Color>,
                            theme: LegendTheme,
                            override val layout: ColorBarComponentLayout
) : LegendBoxSpec(title, theme) {

    internal var binCount =
        DEF_NUM_BIN

    companion object {
        private const val DEF_BAR_THICKNESS = 1.0  // in 'key-size' multiples
        private const val DEF_BAR_LENGTH = 5.0   // in 'key-size' multiples

        private const val DEF_NUM_BIN = 20

        internal fun barAbsoluteSize(legendDirection: LegendDirection, theme: LegendTheme): DoubleVector {
            return if (legendDirection === LegendDirection.HORIZONTAL) {
                DoubleVector(
                        DEF_BAR_LENGTH * theme.keySize(),
                        DEF_BAR_THICKNESS * theme.keySize())
            } else DoubleVector(
                    DEF_BAR_THICKNESS * theme.keySize(),
                    DEF_BAR_LENGTH * theme.keySize())
        }
    }
}
