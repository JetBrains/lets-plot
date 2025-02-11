/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util.sizing

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.junit.Test
import kotlin.test.assertEquals

class SizingPolicyCreateTest {

    private val defaultFigureSize = DoubleVector(100.0, 50.0)
    private val squareContainer = DoubleVector(300.0, 300.0)

    @Test
    fun `test create with valid width and height`() {
        val policy = SizingPolicy.create(
            mapOf(
                "width" to 300,
                "height" to 150
            )
        )

        val result = policy.resize(defaultFigureSize, null)
        assertEquals(300.0, result.x)
        assertEquals(150.0, result.y)
    }

    @Test
    fun `test create with missing values`() {
        val policy = SizingPolicy.create(emptyMap<String, Any>())

        val result = policy.resize(defaultFigureSize, null)
        assertEquals(100.0, result.x)
        assertEquals(50.0, result.y)
    }

    @Test
    fun `test create with null values`() {
        val policy = SizingPolicy.create(
            mapOf(
                "width" to null,
                "height" to null
            )
        )

        val result = policy.resize(defaultFigureSize, null)
        assertEquals(100.0, result.x)
        assertEquals(50.0, result.y)
    }

    @Test
    fun `test create with zero values`() {
        val policy = SizingPolicy.create(
            mapOf(
                "width" to 0.0,
                "height" to 0.0
            )
        )

        val result = policy.resize(defaultFigureSize, null)
        assertEquals(0.0, result.x)  // based on normalize() implementation
        assertEquals(0.0, result.y)  // based on normalize() implementation
    }

    @Test
    fun `test create with negative values`() {
        val policy = SizingPolicy.create(
            mapOf(
                "width" to -300.0,
                "height" to -150.0
            )
        )

        val result = policy.resize(defaultFigureSize, null)
        assertEquals(-300.0, result.x)
        assertEquals(-150.0, result.y)
    }

    @Test
    fun `test create with mixed presence of values`() {
        val policy = SizingPolicy.create(
            mapOf(
                "width" to 300.0
                // height missing
            )
        )

        println(policy)
        val result = policy.resize(defaultFigureSize, null)
        assertEquals(300.0, result.x)
        assertEquals(150.0, result.y)  // scailed figure height
    }

    @Test
    fun `test create with invalid value types`() {
        val policy = SizingPolicy.create(
            mapOf(
                "width" to "not a number",
                "height" to listOf(1, 2, 3)
            )
        )

        val result = policy.resize(defaultFigureSize, null)
        assertEquals(100.0, result.x)
        assertEquals(50.0, result.y)
    }

    @Test
    fun `test create with different case in mode names`() {
        // Reference policy with standard casing
        val reference = SizingPolicy.create(
            mapOf(
                "width_mode" to "fixed",
                "width" to 150.0
            )
        )

        // Test different casings
        val variations = listOf(
            mapOf("width_mode" to "fixed", "height_mode" to "scaled", "width" to 150.0),
            mapOf("width_mode" to "Fixed", "height_mode" to "Scaled", "width" to 150.0),
            mapOf("width_mode" to "FIXED", "height_mode" to "scaled", "width" to 150.0),
            mapOf("width_mode" to "fixed", "height_mode" to "SCALED", "width" to 150.0)
        )

        val referenceResult = reference.resize(defaultFigureSize, squareContainer)

        variations.forEachIndexed { index, options ->
            val policy = SizingPolicy.create(options)
            val result = policy.resize(defaultFigureSize, squareContainer)

            assertEquals(
                referenceResult.x, result.x,
                "Width mismatch for variation $index: ${options["width_mode"]}/${options["height_mode"]}"
            )
            assertEquals(
                referenceResult.y, result.y,
                "Height mismatch for variation $index: ${options["width_mode"]}/${options["height_mode"]}"
            )
        }
    }

    @Test
    fun `test create with SizingMode enum values`() {
        // All possible combinations of sizing modes
        val modes = SizingMode.entries.toTypedArray()

        modes.forEach { widthMode ->
            modes.forEach { heightMode ->
                // Create policy using enum toString()
                val withEnumStrings = SizingPolicy.create(
                    mapOf(
                        "width_mode" to "$widthMode",
                        "height_mode" to "$heightMode",
                        "width" to 150.0
                    )
                )

                // Create reference policy using direct string values
                val reference = SizingPolicy.create(
                    mapOf(
                        "width_mode" to widthMode.name,
                        "height_mode" to heightMode.name,
                        "width" to 150.0
                    )
                )

                val result = withEnumStrings.resize(defaultFigureSize, squareContainer)
                val referenceResult = reference.resize(defaultFigureSize, squareContainer)

                assertEquals(
                    referenceResult.x, result.x,
                    "Width mismatch for modes: $widthMode/$heightMode"
                )
                assertEquals(
                    referenceResult.y, result.y,
                    "Height mismatch for modes: $widthMode/$heightMode"
                )
            }
        }
    }

    @Test
    fun `test create with missing mode values defaults to notebook policy`() {
        // Test with no modes specified
        val noModes = SizingPolicy.create(
            mapOf(
                "width" to 150.0
            )
        )

        // Test with null modes
        val nullModes = SizingPolicy.create(
            mapOf(
                "width_mode" to null,
                "height_mode" to null,
                "width" to 150.0
            )
        )

        // Test with invalid mode strings
        val invalidModes = SizingPolicy.create(
            mapOf(
                "width_mode" to "INVALID_MODE",
                "height_mode" to "ANOTHER_INVALID",
                "width" to 150.0        // --> fixed
            )
        )

        // Reference notebook policy (FIXED, SCALED)
        val reference = SizingPolicy.create(
            mapOf(
                "width_mode" to "fixed",
                "height_mode" to "scaled",
                "width" to 150.0
            )
        )

        val referenceResult = reference.resize(defaultFigureSize, squareContainer)

        listOf(noModes, nullModes, invalidModes).forEach { policy ->
            val result = policy.resize(defaultFigureSize, squareContainer)
            assertEquals(
                referenceResult.x, result.x,
                "Width should match notebook policy"
            )
            assertEquals(
                referenceResult.y, result.y,
                "Height should match notebook policy"
            )
        }
    }

}