/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package org.jetbrains.letsPlot.core.util.sizing

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class SizingPolicyResizeTest(
    private val testName: String,
    private val widthMode: SizingMode,
    private val heightMode: SizingMode,
    private val width: Double?,
    private val height: Double?,
    private val containerSize: DoubleVector?,
    private val expectedWidth: Double,
    private val expectedHeight: Double
) {
    // Figure aspect ratio 2:1
    private val defaultFigureSize = DoubleVector(100.0, 50.0)

    @Test
    fun testResize() {
        val policy = SizingPolicy(
            widthMode = widthMode,
            heightMode = heightMode,
            width = width,
            height = height
        )

        val result = policy.resize(defaultFigureSize, containerSize)

        assertEquals(expectedWidth, result.x,
            "Width mismatch for test: $testName")
        assertEquals(expectedHeight, result.y,
            "Height mismatch for test: $testName")
    }

    companion object {
        private val NO_CONTAINER: DoubleVector? = null
        private val SQUARE_CONTAINER = DoubleVector(300.0, 300.0)  // 1:1 ratio
        private val SHORT_CONTAINER = DoubleVector(300.0, 60.0)  // 5:1 ratio
        private val TALL_CONTAINER = DoubleVector(60.0, 300.0)  // 1:5 ratio

        @JvmStatic
        @Parameters(name = "{0}")
        fun testData() = listOf(
            // FIXED,FIXED cases
            arrayOf(
                "FIXED,FIXED: explicit dimensions (no container)",
                SizingMode.FIXED, SizingMode.FIXED,
                150.0, 75.0, NO_CONTAINER,
                // expected:
                150.0, 75.0
            ),
            arrayOf(
                "FIXED,FIXED: null dimensions (no container)",
                SizingMode.FIXED, SizingMode.FIXED,
                null, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "FIXED,FIXED: explicit dimensions (with container)",
                SizingMode.FIXED, SizingMode.FIXED,
                150.0, 75.0, SQUARE_CONTAINER,
                // expected:
                150.0, 75.0
            ),

            // FIXED,SCALED cases
            arrayOf(
                "FIXED,SCALED: explicit width (no container)",
                SizingMode.FIXED, SizingMode.SCALED,
                150.0, null, NO_CONTAINER,
                // expected:
                150.0, 75.0  // maintains figure's 2:1 ratio
            ),
            arrayOf(
                "FIXED,SCALED: null dimensions (no container)",
                SizingMode.FIXED, SizingMode.SCALED,
                null, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "FIXED,SCALED: explicit width (with container)",
                SizingMode.FIXED, SizingMode.SCALED,
                150.0, null, SQUARE_CONTAINER,
                // expected:
                150.0, 75.0  // maintains figure's 2:1 ratio despite container
            ),

            // MIN,SCALED cases
            arrayOf(
                "MIN,SCALED: width > figure (no container)",
                SizingMode.MIN, SizingMode.SCALED,
                150.0, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "MIN,SCALED: width < figure (no container)",
                SizingMode.MIN, SizingMode.SCALED,
                80.0, null, NO_CONTAINER,
                // expected:
                80.0, 40.0
            ),
            arrayOf(
                "MIN,SCALED: null dimensions (no container)",
                SizingMode.MIN, SizingMode.SCALED,
                null, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "MIN,SCALED: null dimensions (with container)",
                SizingMode.MIN, SizingMode.SCALED,
                null, null, SQUARE_CONTAINER,
                // expected:
                100.0, 50.0  // maintains figure's 2:1 ratio, container doesn't affect scaling
            ),

            // FIT,SCALED cases
            arrayOf(
                "FIT,SCALED: explicit width (no container)",
                SizingMode.FIT, SizingMode.SCALED,
                150.0, null, NO_CONTAINER,
                // expected:
                150.0, 75.0  // maintains figure's 2:1 ratio
            ),
            arrayOf(
                "FIT,SCALED: null dimensions (no container)",
                SizingMode.FIT, SizingMode.SCALED,
                null, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "FIT,SCALED: null dimensions (with container)",
                SizingMode.FIT, SizingMode.SCALED,
                null, null, SQUARE_CONTAINER,
                // expected:
                300.0, 150.0  // container width with maintained 2:1 ratio
            ),

            // FIT,FIT cases
            arrayOf(
                "FIT,FIT: explicit dimensions (no container)",
                SizingMode.FIT, SizingMode.FIT,
                150.0, 75.0, NO_CONTAINER,
                // expected:
                150.0, 75.0
            ),
            arrayOf(
                "FIT,FIT: null dimensions (no container)",
                SizingMode.FIT, SizingMode.FIT,
                null, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "FIT,FIT: null dimensions (with container)",
                SizingMode.FIT, SizingMode.FIT,
                null, null, SQUARE_CONTAINER,
                // expected:
                300.0, 300.0  // takes container dimensions directly
            ),

            // SCALED,SCALED cases
            arrayOf(
                "SCALED,SCALED: null dimensions (no container)",
                SizingMode.SCALED, SizingMode.SCALED,
                null, null, NO_CONTAINER,
                // expected:
                100.0, 50.0
            ),
            arrayOf(
                "SCALED,SCALED: explicit dimensions (no container)",
                SizingMode.SCALED, SizingMode.SCALED,
                150.0, 75.0, NO_CONTAINER,
                // expected:
                150.0, 75.0
            ),
            arrayOf(
                "SCALED,SCALED: null dimensions (with square container)",
                SizingMode.SCALED, SizingMode.SCALED,
                null, null, SQUARE_CONTAINER,
                // expected:
                300.0, 150.0  // scales to container width, maintaining 2:1 ratio
            ),
            arrayOf(
                "SCALED,SCALED: null dimensions (with short container)",
                SizingMode.SCALED, SizingMode.SCALED,
                null, null, SHORT_CONTAINER,
                // expected:
                120.0, 60.0  // container height limits the scale, width follows 2:1 ratio
            ),
            arrayOf(
                "SCALED,SCALED: null dimensions (with tall container)",
                SizingMode.SCALED, SizingMode.SCALED,
                null, null, TALL_CONTAINER,
                // expected:
                60.0, 30.0  // container width limits the scale, height follows 2:1 ratio
            )
        )
    }
}