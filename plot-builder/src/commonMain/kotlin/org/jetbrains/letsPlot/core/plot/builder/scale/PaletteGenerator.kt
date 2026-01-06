/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper

interface PaletteGenerator {
    fun createPaletteGeneratorScaleMapper(domain: DoubleSpan): ScaleMapper<Color>

    fun generatePalette(colorCount: Int): List<String> {
        if (colorCount <= 0) {
            return emptyList()
        }

        val domain = DoubleSpan(0.0, (colorCount - 1).toDouble())
        val scaleMapper = createPaletteGeneratorScaleMapper(domain)
        return (0 until colorCount).map { i ->
            scaleMapper(i.toDouble())?.toHexColor()
                ?: throw IllegalStateException("Can't generate a palette color for index: $i")
        }
    }
}