/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorGradient2PaletteGeneratorTest {

    @Test
    fun `midpoint affects palette`() {
        val provider = ColorGradient2MapperProvider(
            low = Color.BLUE,
            mid = Color.WHITE,
            high = Color.RED,
            midpoint = 2.0,
            naValue = Color.GRAY
        )

        val palette = provider.generatePalette(5)

        // The middle color should be white (midpoint color)
        assertEquals(Color.WHITE.toHexColor(), palette[2], "Middle color should be white (midpoint)")
    }
}
