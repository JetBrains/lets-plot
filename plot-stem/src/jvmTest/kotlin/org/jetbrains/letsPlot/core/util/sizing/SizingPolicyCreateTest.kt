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
}