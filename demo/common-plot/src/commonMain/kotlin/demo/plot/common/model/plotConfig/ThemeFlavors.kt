/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class ThemeFlavors {
    fun plotSpecList(): List<MutableMap<String, Any>> {
//        return allThemesWithFlavor(flavorName = "solarized_light")
        return allThemesWithFlavor(flavorName = null, facets = true)
//         return allFlavorsWithTheme(themeName = "grey", facets = false)
//         return allFlavorsWithTheme(themeName = null, facets = false)
    }

    private fun allFlavorsWithTheme(themeName: String?, facets: Boolean = false) = listOf(
        withTheme(themeName, facets = facets),
        withTheme(themeName, flavor = "darcula", facets),
        withTheme(themeName, flavor = "solarized_light", facets),
        withTheme(themeName, flavor = "solarized_dark", facets),
        withTheme(themeName, flavor = "high_contrast_light", facets),
        withTheme(themeName, flavor = "high_contrast_dark", facets),
    )

    private fun allThemesWithFlavor(flavorName: String?, facets: Boolean = false) = listOf(
        withTheme("classic", flavorName, facets),
        withTheme("light", flavorName, facets),
        withTheme("grey", flavorName, facets),
        withTheme("minimal", flavorName, facets),
        withTheme("minimal2", flavorName, facets),
        withTheme("none", flavorName, facets),
        withTheme("bw", flavorName, facets),
    )

    private fun theme(name: String?, flavor: String?): String {
        var result = """
            "theme": {
                ${name?.let { "\"name\": \"$name\"" } ?: ""}
                ${if (name != null && flavor != null) ", " else ""}
                ${flavor?.let { "\"flavor\": \"$flavor\"" } ?: ""}
            }""".trimIndent()
        return result
    }

    private fun withTheme(themeName: String?, flavor: String? = null, facets: Boolean) = plot(
        plotTitle = "Theme: ${themeName ?: "default"}. Flavor: $flavor",
        theme = theme(themeName, flavor),
        facets
    )

    private fun plot(plotTitle: String, theme: String, facets: Boolean): MutableMap<String, Any> {
        val spec = """
            {
                "kind": "plot",
                 ${(theme + ",").takeIf { theme.isNotEmpty() } ?: ""}
                "data": {
                    "x": [0,1,2,3,4],
                    "y": [0,1,2,3,4]
                },
                "mapping": {
                    "x": "x",
                    "y": "y",
                    "size": "y",
                    "fill": "y"
                },
                ${if (facets) "\"facet\":{ \"name\": \"grid\", \"y\": \"y\" }," else ""}
                "ggtitle": {
                    "text": "$plotTitle",
                    "subtitle": "The plot subtitle"
                },
                "caption": { "text": "The plot caption" },
                "scales": [
                    {
                        "name": "New x axis label",
                        "aesthetic": "x"
                    },
                    {
                        "name": "New y axis label",
                        "aesthetic": "y"
                    },
                    {
                        "name": "New legend title",
                        "aesthetic": "size"
                    }
                ],
                "layers": [
                    {
                        "geom": "point",
                        "tooltips": {
                            "lines": [
                                "label|value", "The static text"
                            ],
                            "title": "Title"
                        }
                   }
                ]
            }""".trimIndent()
        return parsePlotSpec(spec)
    }
}