/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor

class ThemeFlavorTest {

    @Test
    fun `common palette maps all symbolic colors`() {
        val flavor = ThemeFlavor.commonPalette()

        SymbolicColor.entries.forEach { sc ->
            assertTrue(flavor.symbolicColors.containsKey(sc), "Missing color for $sc in basePalette")
        }
    }

    @Test
    fun `grey palette GREY_2 differs from base palette`() {
        val base = ThemeFlavor.commonPalette()
        val grey = ThemeFlavor.greyPalette()

        assertEquals(
            base.symbolicColors[SymbolicColor.GREY_1],
            grey.symbolicColors[SymbolicColor.GREY_1],
            "GREY_1 is shared"
        )

        assertTrue(
            base.symbolicColors[SymbolicColor.GREY_2] != grey.symbolicColors[SymbolicColor.GREY_2],
            "GREY_2 should differ between base and grey palettes"
        )
    }
}

