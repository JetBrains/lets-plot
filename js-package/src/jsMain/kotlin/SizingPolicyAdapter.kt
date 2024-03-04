/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.w3c.dom.HTMLElement
import sizing.SizingMode
import sizing.SizingPolicy
import kotlin.math.max

/**
 * This is not how it is supposed to be - get rid of this adapter later.
 */
internal class SizingPolicyAdapter(
    private val sizingPolicy: SizingPolicy,
) {
    fun monolithicSizingParameters(parentElement: HTMLElement): SizeAndMaxWidth {

        val parentWidth = when (val w = parentElement.clientWidth) {
            0 -> null  // parent element wasn't yet layouted
            else -> w
        }?.toDouble()

        if (parentWidth == null) {
            return SizeAndMaxWidth(
                plotSize = null,
                plotMaxWidth = null
            )
        }

        // currently: only some of all possible policies
        return when (sizingPolicy.widthMode) {
            SizingMode.SCALED, // Note: 'width: SCALED' mode is not implemented
            SizingMode.MIN -> {
                // Note: only 'hight: SCALED' mode is currently implemented
                SizeAndMaxWidth(
                    plotSize = null,
                    plotMaxWidth = max(1.0, parentWidth - sizingPolicy.widthMargin)
                )
            }

            SizingMode.FIT -> {
                // Note: only 'hight: FIT' mode is currently implemented
                val parentHeight = parentElement.clientHeight.toDouble()

                SizeAndMaxWidth(
                    plotSize = DoubleVector(
                        x = max(1.0, parentWidth - sizingPolicy.widthMargin),
                        y = max(1.0, parentHeight - sizingPolicy.heightMargin),
                    ),
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