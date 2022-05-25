/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class ThemeOptions {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            // use predefined themes
            withTheme(null),
            withTheme("classic"),
            withTheme("light"),
            withTheme("grey"),
            withTheme("minimal"),
            withTheme("minimal2"),
            withTheme("none"),

            setThemeOptions()
        )
    }

    fun theme(name: String) = "'theme': { 'name': '$name' }"

    private fun withTheme(themeName: String?): MutableMap<String, Any> {
        val theme = themeName?.let(this::theme)
        return plot(
            plotTitle = "With theme = ${themeName ?: "default"}",
            theme = theme ?: ""
        )
    }

    private fun setThemeOptions(): MutableMap<String, Any> {
        fun option(className: String, color: String? = null, face: String? = null): String {
            return """
            "$className": {
                 ${("\"color\": \"$color\",").takeIf { color != null } ?: ""}
                 ${("\"face\": \"$face\",").takeIf { face != null } ?: ""}
                "blank": false
            }"""
        }

        val theme = "'theme': { " +
                option("tooltip_text", color = "red", face = "italic") +
                "," + option("tooltip_title_text", color = "blue") +

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
                            "tooltip_lines": [
                                "label|value", "The static text"
                            ],
                            "tooltip_title": "Title"
                        }
                   }
                ]
            }""".trimIndent()
        return parsePlotSpec(spec)
    }
}