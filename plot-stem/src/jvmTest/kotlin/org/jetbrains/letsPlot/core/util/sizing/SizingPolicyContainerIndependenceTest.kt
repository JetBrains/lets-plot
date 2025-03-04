/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util.sizing

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.junit.Test
import kotlin.test.assertEquals

class SizingPolicyContainerIndependenceTest {
    // Figure aspect ratio 2:1
    private val defaultFigureSize = DoubleVector(100.0, 50.0)

    // Various container sizes to verify independence
    private val containerSizes = listOf(
        null,
        DoubleVector(300.0, 300.0),  // square
        DoubleVector(300.0, 60.0),   // wide
        DoubleVector(60.0, 300.0)    // tall
    )

    @Test
    fun `test FIXED,FIXED container independence`() {
        val policy = SizingPolicy(
            widthMode = SizingMode.FIXED,
            heightMode = SizingMode.FIXED,
            width = 150.0,
            height = 75.0
        )

        val reference = policy.resize(defaultFigureSize, null)

        containerSizes.forEach { containerSize ->
            val result = policy.resize(defaultFigureSize, containerSize)
            assertEquals(
                reference.x, result.x,
                "Width should not depend on container: ${containerSize?.x}x${containerSize?.y}"
            )
            assertEquals(
                reference.y, result.y,
                "Height should not depend on container: ${containerSize?.x}x${containerSize?.y}"
            )
        }
    }

    @Test
    fun `test FIXED,SCALED container independence`() {
        val policy = SizingPolicy(
            widthMode = SizingMode.FIXED,
            heightMode = SizingMode.SCALED,
            width = 150.0,
            height = null
        )

        val reference = policy.resize(defaultFigureSize, null)

        containerSizes.forEach { containerSize ->
            val result = policy.resize(defaultFigureSize, containerSize)
            assertEquals(
                reference.x, result.x,
                "Width should not depend on container: ${containerSize?.x}x${containerSize?.y}"
            )
            assertEquals(
                reference.y, result.y,
                "Height should not depend on container: ${containerSize?.x}x${containerSize?.y}"
            )
        }
    }
}