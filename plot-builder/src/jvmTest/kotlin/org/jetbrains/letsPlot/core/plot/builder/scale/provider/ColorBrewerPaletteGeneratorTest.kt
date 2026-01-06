/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorBrewerPaletteGeneratorTest {

    @Test
    fun `respects direction parameter`() {
        val provider = ColorBrewerMapperProvider(
            paletteTypeName = "seq",
            paletteNameOrIndex = 0,
            direction = 1.0,
            naValue = Color.GRAY
        )

        val providerReversed = ColorBrewerMapperProvider(
            paletteTypeName = "seq",
            paletteNameOrIndex = 0,
            direction = -1.0,
            naValue = Color.GRAY
        )

        val palette = provider.generatePalette(5)
        val paletteReversed = providerReversed.generatePalette(5)

        assertEquals(palette.reversed(), paletteReversed)
    }
}
