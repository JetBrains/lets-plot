/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package sizing

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.w3c.dom.HTMLElement
import sizing.SizingMode.*
import sizing.SizingOption.HEIGHT
import sizing.SizingOption.HEIGHT_MODE
import sizing.SizingOption.WIDTH
import sizing.SizingOption.WIDTH_MARGIN
import sizing.SizingOption.WIDTH_MODE
import kotlin.math.max
import kotlin.math.min

private val LOG = PortableLogging.logger("Lets-Plot SizingPolicy")

internal class SizingPolicy(
    val widthMode: SizingMode,
    val heightMode: SizingMode,
    val widthMargin: Double,
    val heightMargin: Double,
    val width: Double?,     // for fit | min | fixed modes
    val height: Double?,    // for fit | min | fixed modes
) {

    fun resize(figureSize: DoubleVector, container: HTMLElement): DoubleVector {
        // avoid division by zero
        @Suppress("NAME_SHADOWING")
        val figureSize = DoubleVector(
            if (figureSize.x == 0.0) 1.0 else figureSize.x,
            if (figureSize.y == 0.0) 1.0 else figureSize.y,
        )
        val containerSize = DoubleVector(
            x = max(1.0, container.clientWidth.toDouble() - widthMargin),
            y = max(1.0, container.clientHeight.toDouble() - heightMargin),
        )

        val widthFixed = when (widthMode) {
            FIT -> containerSize.x
            MIN -> min(figureSize.x, containerSize.x)
            SCALED -> null
            FIXED -> width ?: figureSize.x
        }
        val heightFixed = when (heightMode) {
            FIT -> containerSize.y
            MIN -> min(figureSize.y, containerSize.y)
            SCALED -> null
            FIXED -> height ?: figureSize.y
        }

//        LOG.info { "widthMode:$widthMode heightMode:$heightMode widthFixed: $widthFixed heightFixed: $heightFixed" }

        return if (widthFixed != null && heightFixed != null) {
            DoubleVector(widthFixed, heightFixed)
        } else if (widthFixed == null && heightFixed == null) {
            // both "scaled"
            DoubleRectangle(DoubleVector.ZERO, containerSize)
                .shrinkToAspectRatio(figureSize)
                .dimension
        } else if (widthFixed != null) {
            // scale height
            val height = widthFixed / figureSize.x * figureSize.y
            DoubleVector(widthFixed, height)
        } else if (heightFixed != null) {
            // scale width
            val width = heightFixed / figureSize.y * figureSize.x
            DoubleVector(width, heightFixed)
        } else {
            // Impossible!
            figureSize
        }
    }

    companion object {
        // Notebook policy
        private val NOTEBOOK_WIDTH_MODE = MIN
        private val NOTEBOOK_HEIGHT_MODE = SCALED

        fun notebookCell(cellWidth: Double?, cellHeight: Double?): SizingPolicy {
            return SizingPolicy(
                NOTEBOOK_WIDTH_MODE, NOTEBOOK_HEIGHT_MODE,
                widthMargin = 0.0, heightMargin = 0.0,
                width = cellWidth,
                height = cellHeight,
            )
        }

        fun fixedBoth(size: DoubleVector): SizingPolicy {
            return SizingPolicy(
                FIXED, FIXED,
                widthMargin = 0.0, heightMargin = 0.0,
                width = size.x,
                height = size.y,
            )
        }

        fun create(
            options: Map<*, *>,
        ): SizingPolicy {
            val widthMode = sizingMode(options, WIDTH_MODE) ?: NOTEBOOK_WIDTH_MODE
            val heightMode = sizingMode(options, HEIGHT_MODE) ?: NOTEBOOK_HEIGHT_MODE
            val widthMargin = (options[WIDTH_MARGIN] as? Number)?.toDouble() ?: 0.0
            val heightMargin = (options[HEIGHT_MODE] as? Number)?.toDouble() ?: 0.0

            val width = (options[WIDTH] as? Number)?.toDouble()
            val height = (options[HEIGHT] as? Number)?.toDouble()

            return SizingPolicy(
                widthMode = widthMode,
                heightMode = heightMode,
                widthMargin = widthMargin,
                heightMargin = heightMargin,
                width = width,
                height = height,
            )
        }

        private fun sizingMode(options: Map<*, *>, option: String): SizingMode? {
            return options[option]?.let { value ->
                SizingMode.byNameIgnoreCase(value as String) ?: let {
                    LOG.info { "Option $option: unexpected value '$value'" }
                    null
                }
            }
        }
    }
}