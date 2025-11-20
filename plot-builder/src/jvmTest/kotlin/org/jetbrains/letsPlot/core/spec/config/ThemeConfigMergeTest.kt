/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Name
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Flavor
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import kotlin.test.*


class ThemeConfigMergeTest {

    // Helper functions

    private fun buildTheme(
        themeName: String? = null,
        flavorName: String? = null,
        userOptions: Map<String, Any> = emptyMap()
    ): Theme {
        return ThemeUtil.buildTheme(
            themeName ?: Name.LP_MINIMAL,
            userOptions + if (flavorName != null) {
                mapOf(ThemeOption.FLAVOR to flavorName)
            } else {
                emptyMap()
            },
            DefaultFontFamilyRegistry()
        )
    }

    private fun buildThemeViaConfig(
        themeName: String? = null,
        flavorName: String? = null,
        userOptions: Map<String, Any> = emptyMap(),
        container: Theme? = null
    ): Theme {
        val options = mutableMapOf<String, Any>()
        if (themeName != null) {
            options[Option.Meta.NAME] = themeName
        }
        if (flavorName != null) {
            options[ThemeOption.FLAVOR] = flavorName
        }

        return ThemeConfig(
            themeOptions = options + userOptions,
            containerTheme = container,
            fontFamilyRegistry = DefaultFontFamilyRegistry()
        ).theme
    }

    private fun gatherThemeColors(theme: Theme): List<Color> {
        return listOf(
            theme.verticalAxis(false).lineColor(),
            theme.horizontalAxis(false).lineColor(),
            theme.panel().verticalGrid(false).majorLineColor(),
            theme.panel().horizontalGrid(false).majorLineColor(),
            theme.panel().rectColor(),
            theme.panel().borderColor()
        )
    }

    private fun compareThemes(theme1: Theme, theme2: Theme): Boolean {
        val colors1 = gatherThemeColors(theme1)
        val colors2 = gatherThemeColors(theme2)
        return colors1.zip(colors2).all { (c1, c2) -> c1 == c2 }
    }
    
    private fun assertSameVisualThemes(theme1: Theme, theme2: Theme) {
        assertTrue(compareThemes(theme1, theme2), "Themes are expected to be visually the same.")
    }

    private fun assertDifferentVisualThemes(theme1: Theme, theme2: Theme) {
        assertFalse(compareThemes(theme1, theme2), "Themes are expected to be visually different.")
    }


    @Test
    fun `container theme overrides default subplot theme`() {
        val container = buildTheme(Name.R_BW)
        val subplot = buildThemeViaConfig(
            container = container
        )
        assertSameVisualThemes(subplot, container)
    }


    @Test
    fun `no own options - all container options are inherited`() {
        val container = buildTheme(Name.R_GRAY, Flavor.DARCULA,
            userOptions = mapOf(
                ThemeOption.LEGEND_POSITION to "bottom"
            )
        )

        val subplot = buildThemeViaConfig(
            container = container
        ) as DefaultTheme

        assertEquals(
            Flavor.DARCULA,
            subplot.options[ThemeOption.FLAVOR],
            "Flavor should be inherited from container theme"
        )
        assertEquals(
            "bottom",
            subplot.options[ThemeOption.LEGEND_POSITION]
        )
    }

    @Test
    fun `explicit subplot theme overrides container theme`() {
        val container = buildTheme(Name.R_GREY)
        val subplot = buildThemeViaConfig(Name.R_LIGHT,
            container = container
        )

        val expected = buildTheme(Name.R_LIGHT)

        assertSameVisualThemes(subplot, expected)
        assertDifferentVisualThemes(subplot, container)
    }

    @Test
    fun `own flavor overrides container flavor`() {
        val container = buildTheme(Name.LP_MINIMAL, Flavor.DARCULA)

        val subplot = buildThemeViaConfig(
            flavorName = Flavor.SOLARIZED_DARK,
            container = container
        ) as DefaultTheme

        assertEquals(
            Flavor.SOLARIZED_DARK,
            subplot.options[ThemeOption.FLAVOR]
        )
    }

    @Test
    fun `own plot background keeps border, container contributes fill if own flavor not set`() {
        val container = buildTheme(Name.R_LIGHT)

        val subplot = buildThemeViaConfig(
            userOptions = mapOf(
                ThemeOption.PLOT_BKGR_RECT to mapOf(
                    ThemeOption.Elem.COLOR to "green",
                    ThemeOption.Elem.FILL to "blue",
                    ThemeOption.Elem.SIZE to 2.0
                )
            ),
            container = container
        )

        assertEquals(Color.GREEN, subplot.plot().backgroundColor())
        assertEquals(Color.BLUE, subplot.plot().backgroundFill())
        assertEquals(2.0, subplot.plot().backgroundStrokeWidth())
    }

    @Test
    fun `subplot explicit flavor overrides container flavor but inherits container theme`() {
        val container = buildTheme(Name.R_CLASSIC, Flavor.DARCULA)

        val subplot = buildThemeViaConfig(
            flavorName = Flavor.SOLARIZED_LIGHT,
            container = container
        )

        val expected = buildTheme(Name.R_CLASSIC, Flavor.SOLARIZED_LIGHT)
        assertSameVisualThemes(subplot, expected)
    }

    @Test
    fun `subplot explicit theme and flavor override container theme and flavor`() {
        val container = buildTheme(Name.R_BW, Flavor.SOLARIZED_LIGHT)
        val subplot = buildThemeViaConfig(Name.LP_MINIMAL, Flavor.DARCULA,
            container = container
        )
        val expected = buildTheme(Name.LP_MINIMAL, Flavor.DARCULA)
        assertSameVisualThemes(subplot, expected)
    }

    @Test
    fun `subplot explicit theme inherits container flavor if own flavor not set`() {
        val container = buildTheme(Name.R_GREY, Flavor.SOLARIZED_DARK)

        val subplot = buildThemeViaConfig(Name.R_CLASSIC,
            container = container
        )

        val expected = buildTheme(Name.R_CLASSIC, Flavor.SOLARIZED_DARK)

        assertSameVisualThemes(subplot, expected)
    }

    @Test
    fun `container legend options are inherited even when subplot has its own theme and flavor`() {
        val container = buildTheme(
            Name.R_GRAY,
            Flavor.SOLARIZED_LIGHT,
            userOptions = mapOf(
                ThemeOption.LEGEND_POSITION to "bottom",
                ThemeOption.LEGEND_DIRECTION to "horizontal",
                ThemeOption.LEGEND_BOX_JUST to "center"
            )
        )

        val subplot = buildThemeViaConfig(Name.LP_MINIMAL, Flavor.HIGH_CONTRAST_DARK,
            container = container
        ) as DefaultTheme

        assertEquals("bottom", subplot.options[ThemeOption.LEGEND_POSITION])
        assertEquals("horizontal", subplot.options[ThemeOption.LEGEND_DIRECTION])
        assertEquals("center", subplot.options[ThemeOption.LEGEND_BOX_JUST])
    }

    @Test
    fun `container flavor affects subplot when subplot has no theme or flavor`() {
        val container = buildTheme(Name.R_CLASSIC, Flavor.HIGH_CONTRAST_LIGHT)

        val subplot = buildThemeViaConfig(
            container = container
        )

        val expected = buildTheme(Name.R_CLASSIC, Flavor.HIGH_CONTRAST_LIGHT)

        assertSameVisualThemes(subplot, expected)
    }

    @Test
    fun `plot background is inherited from container when subplot has no own background or flavor`() {
        val container = buildTheme(Name.R_LIGHT)
        val subplot = buildThemeViaConfig(
            container = container
        )

        assertSameVisualThemes(subplot, container)
    }

    @Test
    fun `container background is ignored when subplot has its own flavor`() {
        val container = buildTheme(Name.R_LIGHT)
        val userOptions = mapOf(
            ThemeOption.PLOT_BKGR_RECT to mapOf(
                ThemeOption.Elem.COLOR to "green"
            )
        )

        val subplot = buildThemeViaConfig(Name.R_LIGHT, Flavor.HIGH_CONTRAST_DARK,
            userOptions = userOptions,
            container = container
        )

        val expected = buildTheme(Name.R_LIGHT, Flavor.HIGH_CONTRAST_DARK,
            userOptions = userOptions
        )

        assertSameVisualThemes(subplot, expected)
    }

    @Test
    fun `no container theme - own options are applied as-is`() {
        val userOptions = mapOf(
            ThemeOption.PLOT_BKGR_RECT to mapOf(
                ThemeOption.Elem.COLOR to "green",
                ThemeOption.Elem.SIZE to 2.0
            )
        )

        val subplot = buildThemeViaConfig(Name.R_CLASSIC,
            userOptions = userOptions
        )

        val expected = buildTheme(Name.R_CLASSIC,
            userOptions = userOptions
        )

        assertSameVisualThemes(subplot, expected)
        assertEquals(
            expected.plot().backgroundStrokeWidth(),
            subplot.plot().backgroundStrokeWidth()
        )
    }

    @Test
    fun `STANDARD flavor equals the theme default`() {
        val defaultTheme = buildTheme(Name.R_CLASSIC)
        val standardTheme = buildTheme(Name.R_CLASSIC, Flavor.STANDARD)

        assertSameVisualThemes(defaultTheme, standardTheme)
    }


    @Test
    fun `STANDARD cancels previous explicit flavor`() {
        val greyTheme = buildTheme(Name.R_CLASSIC, Flavor.SOLARIZED_DARK)
        val standardTheme = buildTheme(Name.R_CLASSIC, Flavor.STANDARD)
        val defaultTheme = buildTheme(Name.R_CLASSIC)

        assertSameVisualThemes(defaultTheme, standardTheme)
        assertDifferentVisualThemes(greyTheme, standardTheme)
    }


    @Test
    fun `subplot with STANDARD flavor inherits theme default not container flavor`() {
        val container = buildTheme(Name.R_CLASSIC, Flavor.HIGH_CONTRAST_DARK)
        val subplot = buildThemeViaConfig(Name.R_CLASSIC, Flavor.STANDARD,
            container = container
        )

        val expected = buildTheme(Name.R_CLASSIC)

        assertSameVisualThemes(expected, subplot)
        assertDifferentVisualThemes(container, subplot)
    }


    @Test
    fun `subplot inherits container flavor only when subplot flavor is absent, not when STANDARD is used`() {
        val container = buildTheme(Name.R_CLASSIC, Flavor.DARCULA)
        val subplotInherited = buildThemeViaConfig(container = container)

        val subplotStandard = buildThemeViaConfig(Name.R_CLASSIC, Flavor.STANDARD,
            container = container
        )

        val defaultClassic = buildTheme(Name.R_CLASSIC)

        assertSameVisualThemes(defaultClassic, subplotStandard)
        assertSameVisualThemes(container, subplotInherited)

        assertDifferentVisualThemes(subplotInherited, subplotStandard)
    }


    @Test
    fun `subplot STANDARD resets flavor but preserves explicit theme`() {
        val container = buildTheme(Name.R_LIGHT, Flavor.SOLARIZED_DARK)

        val subplot = buildThemeViaConfig(Name.R_CLASSIC, Flavor.STANDARD,
            container = container
        )

        val expected = buildTheme(Name.R_CLASSIC)

        assertSameVisualThemes(expected, subplot)
    }

    @Test
    fun `container STANDARD does not affect subplot`() {
        val container = buildTheme(Name.R_LIGHT, Flavor.STANDARD)

        val subplot = buildThemeViaConfig(Name.R_CLASSIC, Flavor.DARCULA,
            container = container
        )

        val expected = buildTheme(Name.R_CLASSIC, Flavor.DARCULA)

        assertSameVisualThemes(expected, subplot)
    }

    @Test
    fun `LP_NONE container does not influence subplot theme`() {
        val container = buildTheme(Name.LP_NONE, Flavor.DARCULA)
        val subplot = buildThemeViaConfig(Name.R_CLASSIC, container = container)

        assertSameVisualThemes(subplot, buildTheme(Name.R_CLASSIC, Flavor.DARCULA))
        // Background fill is from the container
        assertEquals(container.plot().backgroundFill(), subplot.plot().backgroundFill())
    }
}

