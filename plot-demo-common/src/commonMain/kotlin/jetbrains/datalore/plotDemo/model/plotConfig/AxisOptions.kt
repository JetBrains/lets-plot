/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class AxisOptions {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            defaultAxis(),
            noXTitle(),
            noYTitle(),
            noXTooltip(),
            noYTooltip(),
            noXTickLabels(),
            noYTickLabels(),
            noTickMarks(),
            noTickMarksOrLabels(),
            noTitlesOrLabels(),
            onlyLines(),
            noLinesOrTitles()
        )
    }

    companion object {
        private fun data(): Map<String, List<*>> {
            val map = HashMap<String, List<*>>()
            map["x"] = listOf(0.0)
            map["y"] = listOf(0.0)
            return map
        }

        private fun title(s: String): String {
            return "   'ggtitle': {" +
                    "                 'text': '" + s + "'" +
                    "              }" +
                    ""
        }

        private fun layerMapping(): String {
            return "   'mapping': {" +
                    "             'x': 'x'," +
                    "             'y': 'y'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'point'," +
                    "                 'size': 5" +
                    "               }" +
                    "           ]" +
                    ""
        }


        fun defaultAxis(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("Default") +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noXTitle(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No X title") +
                    "," +
                    "    'theme': {" +
                    "                'axis_title_x': {'name': 'blank'}" +  // element_blank()

                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noYTitle(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No Y title") +
                    "," +
                    "    'theme': {" +
                    "                'axis_title_y': {'name': 'blank'}" +  // element_blank()

                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noXTooltip(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No X tooltip") +
                    "," +
                    "    'theme': {" +
                    "                'axis_tooltip_x': {'name': 'blank'}" +
                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noYTooltip(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No Y tooltip") +
                    "," +
                    "    'theme': {" +
                    "                'axis_tooltip_y': {'name': 'blank'}" +
                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noXTickLabels(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No X tick labels") +
                    "," +
                    "    'theme': {" +
                    "                'axis_text_x': {'name': 'blank'}" +  // element_blank()

                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noYTickLabels(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No Y tick labels") +
                    "," +
                    "    'theme': {" +
                    "                'axis_text_y': {'name': 'blank'}" +  // element_blank()

                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noTickMarks(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No tick marks") +
                    "," +
                    "    'theme': {" +
                    "                'axis_ticks': {'name': 'blank'}" +  // element_blank()

                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noTickMarksOrLabels(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No tick marks or labels") +
                    "," +
                    "    'theme': {" +
                    "                'axis_ticks': {'name': 'blank'}," +  // element_blank()

                    "                'axis_text': {'name': 'blank'}" +
                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noTitlesOrLabels(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No titles or labels") +
                    "," +
                    "    'theme': {" +
                    "                'axis_title': {'name': 'blank'}," +  // element_blank()

                    "                'axis_text': {'name': 'blank'}" +
                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun onlyLines(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No titles, labels or tick marks") +
                    "," +
                    "    'theme': {" +
                    "                'axis_title': {'name': 'blank'}," +  // element_blank()

                    "                'axis_text': {'name': 'blank'}," +
                    "                'axis_ticks': {'name': 'blank'}" +
                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noLinesOrTitles(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    layerMapping() +
                    "," +
                    title("No titles, no lines") +
                    "," +
                    "    'theme': {" +
                    "                'axis_title': {'name': 'blank'}," +  // element_blank()

                    "                'axis_line': {'name': 'blank'}" +
                    "             }" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }
    }
}
