/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.scale.transform.Transforms
import org.assertj.core.api.Assertions
import org.junit.Test

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
        Assertions.assertThat(mapper.invoke(1.99)).isEqualTo(Color.ORANGE)
        Assertions.assertThat(mapper.invoke(3.0)).isEqualTo(Color.RED)
    }

    @Test
    fun `simple interpolation`() {
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLACK, Color.WHITE), Color.RED)
                .createContinuousMapper(DoubleSpan(0.0, 1.0), Transforms.IDENTITY)

        Assertions.assertThat(mapper.invoke(0.5)).isEqualTo(Color(127, 127, 127))
    }

    @Test
    fun `interpolation between same color`() {
        val mapper =
            ColorGradientnMapperProvider(listOf(Color.BLACK, Color.BLACK), Color.RED)
                .createContinuousMapper(DoubleSpan(0.0, 1.0), Transforms.IDENTITY)

        Assertions.assertThat(mapper.invoke(0.5)).isEqualTo(Color.BLACK)
    }
}