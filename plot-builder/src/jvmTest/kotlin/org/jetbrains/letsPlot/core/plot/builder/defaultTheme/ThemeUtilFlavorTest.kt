/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("UNCHECKED_CAST")

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Color.Companion.parseHex
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption


class ThemeUtilFlavorTest {

    @Test
    fun `LP_NONE ignores flavor and keeps explicit colors`() {
        val values = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.LP_NONE,
            userOptions = mapOf(
                ThemeOption.FLAVOR to ThemeOption.Flavor.SOLARIZED_DARK // should be ignored
            )
        )
        val geom = values[ThemeOption.GEOM] as Map<String, Any>

        assertEquals(Color.BLUE, geom[ThemeOption.Geom.PEN])
        assertEquals(Color.WHITE, geom[ThemeOption.Geom.PAPER])
        assertEquals(Color.PACIFIC_BLUE, geom[ThemeOption.Geom.BRUSH])
    }

    @Test
    fun `R_Classic resolves symbolic GREY_1 via default palette`() {
        val values = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_CLASSIC,
            userOptions = emptyMap()
        )

        @Suppress("UNCHECKED_CAST")
        val panelGrid = values[ThemeOption.PANEL_GRID] as Map<String, Any>

        val color = panelGrid[ThemeOption.Elem.COLOR] as Color
        assertEquals(parseHex("#E9E9E9"), color)
    }

    @Test
    fun `R_BW uses bwPalette for GREY_4`() {
        val values = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_BW,
            userOptions = emptyMap()
        )

        val panelBkgr = values[ThemeOption.PANEL_BKGR_RECT] as Map<String, Any>

        val actual = panelBkgr[ThemeOption.Elem.COLOR] as Color
        val expected = ThemeFlavor.bwPalette().symbolicColors[SymbolicColor.GREY_4]
        assertEquals(expected, actual)
    }

    @Test
    fun `explicit Color entries are not remapped by flavor`() {
        val values = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.LP_NONE,
            userOptions = mapOf(
                ThemeOption.FLAVOR to ThemeOption.Flavor.DARCULA
            )
        )

        val rect = values[ThemeOption.RECT] as Map<String, Any>

        assertEquals(Color.BLUE, rect[ThemeOption.Elem.COLOR])
        assertEquals(Color.LIGHT_BLUE, rect[ThemeOption.Elem.FILL])
    }

    @Test
    fun `user flavor overrides default`() {
        val greyValues = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_CLASSIC,
            userOptions = mapOf(
                ThemeOption.FLAVOR to ThemeOption.Flavor.DARCULA
            )
        )
        val lightValues = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_CLASSIC,
            userOptions = mapOf(
                ThemeOption.FLAVOR to ThemeOption.Flavor.HIGH_CONTRAST_LIGHT
            )
        )

        val greyGrid = greyValues[ThemeOption.PANEL_GRID] as Map<String, Any>
        val lightGrid = lightValues[ThemeOption.PANEL_GRID] as Map<String, Any>

        val greyColor = greyGrid[ThemeOption.Elem.COLOR] as Color
        val lightColor = lightGrid[ThemeOption.Elem.COLOR] as Color

        assertNotEquals(greyColor, lightColor)
    }

    @Test
    fun `STANDARD restores theme default flavor`() {
        val greyValues = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_CLASSIC,
            userOptions = mapOf(
                ThemeOption.FLAVOR to ThemeOption.Flavor.DARCULA
            )
        )
        val defaultValues = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_CLASSIC,
            userOptions = emptyMap()
        )
        val standardValues = ThemeUtil.getThemeValues(
            themeName = ThemeOption.Name.R_CLASSIC,
            userOptions = mapOf(
                ThemeOption.FLAVOR to ThemeOption.Flavor.STANDARD
            )
        )

        val defaultGrid = defaultValues[ThemeOption.PANEL_GRID] as Map<String, Any>
        val standardGrid = standardValues[ThemeOption.PANEL_GRID] as Map<String, Any>

        assertEquals(
            defaultGrid[ThemeOption.Elem.COLOR],
            standardGrid[ThemeOption.Elem.COLOR],
            "STANDARD must restore theme default"
        )

        val greyGrid = greyValues[ThemeOption.PANEL_GRID] as Map<String, Any>
        assertNotEquals(
            greyGrid[ThemeOption.Elem.COLOR],
            defaultGrid[ThemeOption.Elem.COLOR]
        )
    }
}
