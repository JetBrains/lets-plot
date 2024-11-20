/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util.sizing

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.util.sizing.SizingMode.*
import org.jetbrains.letsPlot.core.util.sizing.SizingOption.HEIGHT
import org.jetbrains.letsPlot.core.util.sizing.SizingOption.HEIGHT_MODE
import org.jetbrains.letsPlot.core.util.sizing.SizingOption.WIDTH
import org.jetbrains.letsPlot.core.util.sizing.SizingOption.WIDTH_MODE
import kotlin.math.max
import kotlin.math.min

private val LOG = PortableLogging.logger("Lets-Plot SizingPolicy")

/**
 * width and height are required for fit, min and fixed modes, and also when both modeas are 'scales'
 */
class SizingPolicy(
    val widthMode: SizingMode,
    val heightMode: SizingMode,
    val width: Double?,
    val height: Double?,
) {
    private fun normalize(v: Double): Double = max(1.0, v)

    fun isFixedDefined() = widthMode == FIXED && heightMode == FIXED && width != null && height != null

    fun getFixedDefined(): DoubleVector {
        check(isFixedDefined()) {
            "Undefined fixed size: $this"
        }

        return DoubleVector(
            x = normalize(width!!),
            y = normalize(height!!)
        )
    }

    fun resize(figureSize: DoubleVector): DoubleVector {
        // avoid division by zero
        @Suppress("NAME_SHADOWING")
        val figureSize = DoubleVector(
            normalize(figureSize.x),
            normalize(figureSize.y),
        )

        val definedWidth = width?.let { normalize(it) }
        val definedHeight = height?.let { normalize(it) }

        if (widthMode == SCALED && heightMode == SCALED) {
            require(definedWidth != null && definedHeight != null) {
                "Both 'width' and 'height' are required when both sides scaled: $this"
            }

            val containerSize = DoubleVector(
                x = normalize(definedWidth),
                y = normalize(definedHeight),
            )
            // Fit in container and preserve figure aspect ratio.
            return DoubleRectangle(DoubleVector.ZERO, containerSize)
                .shrinkToAspectRatio(figureSize)
                .dimension

        }

        val widthFixed = when (widthMode) {
            FIT -> definedWidth ?: throw IllegalArgumentException("Undefined `width`: $this")
            MIN -> definedWidth?.let { min(figureSize.x, definedWidth) } ?: figureSize.x
            SCALED -> null
            FIXED -> definedWidth ?: figureSize.x
        }

        val heightFixed = when (heightMode) {
            FIT -> definedHeight ?: throw IllegalArgumentException("Undefined `height`: $this")
            MIN -> definedHeight?.let { min(figureSize.y, definedHeight) } ?: figureSize.y
            SCALED -> null
            FIXED -> definedHeight ?: figureSize.y
        }

        return if (widthFixed != null && heightFixed != null) {
            DoubleVector(widthFixed, heightFixed)
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

    fun withFixedWidth(width: Double): SizingPolicy {
        return SizingPolicy(
            widthMode = FIXED,
            width = width,
            heightMode = this.heightMode,
            height = this.height,
        )
    }

    override fun toString(): String {
        return "SizingPolicy(widthMode=$widthMode, heightMode=$heightMode, width=$width, height=$height)"
    }

    companion object {
        // Notebook policy
        private val NOTEBOOK_WIDTH_MODE = MIN
        private val NOTEBOOK_HEIGHT_MODE = SCALED

        fun notebookCell(): SizingPolicy {
            return SizingPolicy(
                NOTEBOOK_WIDTH_MODE, NOTEBOOK_HEIGHT_MODE,
                width = null,
                height = null,
            )
        }

        fun create(
            options: Map<*, *>,
        ): SizingPolicy {
            val width = (options[WIDTH] as? Number)?.toDouble()
            val height = (options[HEIGHT] as? Number)?.toDouble()

            val defaultFixed = width != null && height != null
            val defaultWidthMode = if (defaultFixed) FIXED else NOTEBOOK_WIDTH_MODE
            val defaultHeightMode = if (defaultFixed) FIXED else NOTEBOOK_HEIGHT_MODE

            val widthMode = sizingMode(options, WIDTH_MODE) ?: defaultWidthMode
            val heightMode = sizingMode(options, HEIGHT_MODE) ?: defaultHeightMode

            return SizingPolicy(
                widthMode = widthMode,
                heightMode = heightMode,
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