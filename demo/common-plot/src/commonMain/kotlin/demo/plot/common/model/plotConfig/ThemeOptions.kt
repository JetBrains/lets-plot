/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class ThemeOptions {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            // use predefined themes
            withTheme("none"),
            withTheme("minimal"),
            withTheme("minimal2"),
            withTheme("classic"),
            withTheme("light"),
            withTheme("grey"),
            withTheme("bw"),

            setThemeOptions(),
            margins(),
        )
    }

    private fun margins(): MutableMap<String, Any> {
        val theme = """
            "theme": {
                "plot_background": {"size": 6, "blank": false},
                "legend_position": "none",
                "axis_title": {"margin": [ null, null, 0, 0 ], "size": 15, "blank": false},
                "axis_text_x": {"margin": [ 20, null, null, null ], "size": 15, "blank": false},
                "plot_margin": [80, 10, null]
            }
        """.trimIndent()

        return plot(
            plotTitle = "Margins for axis_title, axis_text_x and plot_margin",
            theme = theme
        )
    }


    private fun withTheme(themeName: String, flavorName: String? = null): MutableMap<String, Any> {
        val flavor = flavorName?.let { """, "flavor": "$it"""" } ?: ""
        val theme = """"theme": { "name": "$themeName" $flavor }"""
        // , 'strip_background' : {'size': 2, 'blank': false },  'panel_background': {'size': 6, 'blank': false} }"
        return plot(
            plotTitle = "theme = $themeName, flavor = $flavorName",
            theme = theme
        )
    }

    private fun setThemeOptions(): MutableMap<String, Any> {
        fun option(name: String, value: Any?) : String {
            if (value == null) return ""
            val vStr = if (value is String) """"$value"""" else value.toString()
            return """"$name": $vStr,"""
        }
        fun text(key: String, color: String? = null, face: String? = null, size: Double? = null, family: String? = null): String {
            return """
            "$key": {
                ${option("color", color)}
                ${option("face", face)}
                ${option("size", size)}
                ${option("family", family)}
                "blank": false
            }"""
        }
        fun rect(key: String, color: String? = null, fill: String? = null, size: Double? = null): String {
            return """
            "$key": {                 
                ${option("color", color)}
                ${option("fill", fill)}
                ${option("size", size)}
                "blank": false
            }"""
        }
        val theme = "\"theme\": { " +
                text("title", color = "#2a14a8") +
                "," + text("plot_title", face = "bold_italic") +
                "," + text("plot_caption", face = "italic") +
                "," + text("legend_title", face = "bold_italic") +
                "," + text("tooltip_text", color = "#b3deff", face = "italic") +
                "," + rect("tooltip", color = "#2a14a8", fill = "#004d99", size = 2.0) +
                "," + text("axis_title", color = "#9b2d30", face = "bold") +
                "," + text("axis_text", color = "pink", face = "italic") +
                "," + rect("axis_tooltip_x", color = "pink", fill = "#6c4675", size = 2.0) +
                "," + text("axis_tooltip_text_x", color = "pink") +
                "," + "\"geom\" : { \"pen\":\"dark_green\" }" +
                "}"

        return plot(
            plotTitle = "User Theme Options",
            theme = theme
        )
    }


    private fun plot(plotTitle: String, theme: String): MutableMap<String, Any> {
        val spec = """
            {
                "kind": "plot",
                 ${(theme + ",").takeIf { theme.isNotEmpty() } ?: ""}
                "ggsize": { "width": 500, "height": 300 },                 
                "data": {
                    "x": [0],
                    "y": [0]
                },
                "mapping": {
                    "x": "x",
                    "y": "y",
                    "size": "y"
                },
                "facet": {"name": "grid", "x": "x" },
                "ggtitle": {
                    "text": "$plotTitle"
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
                        "geom": "point", "shape": 21,
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