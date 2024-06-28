/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import sizing.SizingMode
import sizing.SizingPolicy
import kotlin.math.max

/**
 * This is not how it is supposed to be - get rid of this adapter later.
 */
internal class SizingPolicyAdapter(
    private val sizingPolicy: SizingPolicy,
) {
    fun toMonolithicSizingParameters(): SizeAndMaxWidth {

        // 'Monolithic Sizing' requires the 'width'.
        if (sizingPolicy.width == null) {
            return SizeAndMaxWidth(
                plotSize = null,
                plotMaxWidth = null
            )
        }

        val cellWidth: Double = sizingPolicy.width
        val cellHeight: Double? = sizingPolicy.height

        // Currently only some of all possible sizing policies are supported.
        return when (sizingPolicy.widthMode) {
            SizingMode.SCALED, // Note: 'width: SCALED' mode is not implemented
            SizingMode.MIN -> {
                // Note: only 'hight: SCALED' mode is currently implemented
                SizeAndMaxWidth(
                    plotSize = null,
                    plotMaxWidth = max(1.0, cellWidth - sizingPolicy.widthMargin)
                )
            }

            SizingMode.FIT -> {
                // Note: only 'hight: FIT' mode is currently implemented
                check(cellHeight != null) { "Height-FIT sizing mode: the cell 'height' not provided." }

                SizeAndMaxWidth(
                    plotSize = DoubleVector(
                        x = max(1.0, cellWidth - sizingPolicy.widthMargin),
                        y = max(1.0, cellHeight - sizingPolicy.heightMargin),
                    ),
                    plotMaxWidth = null
                )
            }

            SizingMode.FIXED -> {

                // Currently, if the wigth is FIXED than the height must be fixed as well.
                check(sizingPolicy.heightMode == SizingMode.FIXED) { "Not supported: width-FIXED + height-${sizingPolicy.heightMode} sizing policy. Try both FIXED." }
                check(cellHeight != null) { "Height-FIXED sizing mode: the cell 'height' not provided." }
                SizeAndMaxWidth(
                    plotSize = DoubleVector(cellWidth, cellHeight),
                    plotMaxWidth = null
                )
            }
        }
    }

    internal data class SizeAndMaxWidth(
        val plotSize: DoubleVector?,
        val plotMaxWidth: Double?,
    )
}