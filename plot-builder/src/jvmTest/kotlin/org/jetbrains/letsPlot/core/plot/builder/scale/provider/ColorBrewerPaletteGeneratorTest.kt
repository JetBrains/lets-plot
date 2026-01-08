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

    @Test
    fun `generates correct Paired palette colors`() {
        val provider = ColorBrewerMapperProvider(
            paletteTypeName = "qual",
            paletteNameOrIndex = "Paired",
            direction = 1.0,
            naValue = Color.GRAY
        )

        val palette = provider.generatePalette(7)

        assertEquals(7, palette.size)
        assertEquals("#a6cee3", palette[0])
        assertEquals("#1f78b4", palette[1])
        assertEquals("#b2df8a", palette[2])
        assertEquals("#33a02c", palette[3])
        assertEquals("#fb9a99", palette[4])
        assertEquals("#e31a1c", palette[5])
        assertEquals("#fdbf6f", palette[6])
    }
}
