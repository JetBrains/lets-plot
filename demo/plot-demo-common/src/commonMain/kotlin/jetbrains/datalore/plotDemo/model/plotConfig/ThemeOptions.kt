/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class ThemeOptions {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            // use predefined themes
            withTheme("classic"),
            withTheme("light"),
            withTheme("grey"),
            withTheme("minimal"),
            withTheme("minimal2"),
            withTheme("none"),
            withTheme("bw"),

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
        fun option(name: String, value: Any?) : String {
            if (value == null) return ""
            val vStr = if (value is String) "\"$value\"" else value.toString()
            return "\"${name}\": $vStr,"
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
        val theme = "'theme': { " +
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