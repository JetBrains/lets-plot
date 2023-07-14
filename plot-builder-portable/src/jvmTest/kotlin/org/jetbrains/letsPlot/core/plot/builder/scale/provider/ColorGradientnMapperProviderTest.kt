/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.assertj.core.api.Assertions
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.junit.Test
import kotlin.test.assertEquals

class ColorGradientnMapperProviderTest {

    @Test
    fun `2 colors`() {
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLUE, Color.RED), Color.GRAY)
                .createContinuousMapper(DoubleSpan(0.0, 1.0), Transforms.IDENTITY)

        Assertions.assertThat(mapper.invoke(0.0)).isEqualTo(Color.BLUE)
        Assertions.assertThat(mapper.invoke(1.0)).isEqualTo(Color.RED)
    }

    @Test
    fun simple() {
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLUE, Color.YELLOW, Color.ORANGE, Color.RED), Color.GRAY)
                .createContinuousMapper(DoubleSpan(0.0, 3.0), Transforms.IDENTITY)

        Assertions.assertThat(mapper.invoke(0.0)).isEqualTo(Color.BLUE)
        Assertions.assertThat(mapper.invoke(1.0)).isEqualTo(Color.YELLOW)
        Assertions.assertThat(mapper.invoke(2.0)).isEqualTo(Color.ORANGE)
        Assertions.assertThat(mapper.invoke(3.0)).isEqualTo(Color.RED)
    }

    @Test
    fun `simple interpolation`() {
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLACK, Color.WHITE), Color.RED)
                .createContinuousMapper(DoubleSpan(0.0, 1.0), Transforms.IDENTITY)

        Assertions.assertThat(mapper.invoke(0.5)).isEqualTo(Color(128, 128, 128))
    }

    @Test
    fun `interpolation between same color`() {
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLACK, Color.BLACK), Color.RED)
                .createContinuousMapper(DoubleSpan(0.0, 1.0), Transforms.IDENTITY)

        Assertions.assertThat(mapper.invoke(0.5)).isEqualTo(Color.BLACK)
    }

    @Test
    fun `NA color`() {
        val naColor = Color.BLACK
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLUE, Color.RED), naColor)
                .createContinuousMapper(DoubleSpan(0.0, 1.0), Transforms.IDENTITY)

        fun check(v: Double?) {
            val actual = try {
                mapper.invoke(v)
            } catch (t: Throwable) {
                throw AssertionError("was value: $v", t)
            }
            assertEquals(naColor, actual)
        }

        check(-0.01)
        check(1.01)
        check(null)
        check(Double.NaN)
        check(Double.NEGATIVE_INFINITY)
        check(Double.POSITIVE_INFINITY)
    }
}