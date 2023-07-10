/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.colormap

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import kotlin.math.*

/**
 * For reference see
 * - https://cran.r-project.org/web/packages/viridis/vignettes/intro-to-viridis.html
 * - https://sjmgarnier.github.io/viridisLite/reference/viridis.html
 */
object ColorMaps {
    const val VIRIDIS = "viridis"

    fun getColors(
        cmName: String,
        alpha: Double,
        hueRange: DoubleSpan,
        n: Int? = null
    ): List<Color> {
        val colors = colorData(cmName)
        val colorsN = resample(colors, hueRange, n)
        return colorsN.map {
            Color(
                red = (it.r * 255).roundToInt(),
                green = (it.g * 255).roundToInt(),
                blue = (it.b * 255).roundToInt(),
                alpha = (alpha * 255).roundToInt(),
            )
        }
    }

    private fun colorData(cmName: String): List<C> {
        return when (cmName.lowercase().trim()) {
            "magma", "a" -> CmMagma.colors
            "inferno", "b" -> CmInferno.colors
            "plasma", "c" -> CmPlasma.colors
            VIRIDIS, "d" -> CmViridis.colors
            "cividis", "e" -> CmCividis.colors
            "turbo" -> CmTurbo.colors
            "twilight" -> CmTwilight.colors
            else -> {
                throw IllegalArgumentException(
                    "Unknown colormap \"$cmName\". " +
                            "Use: " +
                            "\"magma\" (or \"A\"), " +
                            "\"inferno\" (or \"B\"), " +
                            "\"plasma\" (or \"C\"), " +
                            "\"viridis\" (or \"D\"), " +
                            "\"cividis\" (or \"E\"), " +
                            "\"turbo\" or \"twilight\"."
                )
            }
        }
    }

    private fun resample(
        colors: List<C>,
        hueRange: DoubleSpan,
        n: Int? = null
    ): List<C> {
        val maxIndex = colors.size - 1
        val fromIndex = max(0, min(maxIndex, floor(hueRange.lowerEnd * maxIndex).roundToInt()))
        val toIndex = max(0, min(maxIndex, ceil((hueRange.upperEnd * maxIndex)).roundToInt()))

        return if (n == null || n <= 0) {
            colors.subList(fromIndex, toIndex + 1)
        } else if (n == 1) {
            val mid = ((toIndex - fromIndex) / 2.0 + fromIndex).roundToInt()
            listOf(colors[mid])
        } else {
            val numColors = toIndex - fromIndex + 1
            val inc = (numColors - 1.0) / (n - 1)
            List<C>(n) { i -> colors[(i * inc).roundToInt() + fromIndex] }
        }
    }
}