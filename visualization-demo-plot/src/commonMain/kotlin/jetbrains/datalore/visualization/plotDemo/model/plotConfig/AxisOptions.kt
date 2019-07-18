package jetbrains.datalore.visualization.plotDemo.model.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.parsePlotSpec
import jetbrains.datalore.visualization.plotDemo.model.PlotConfigDemoBase

open class AxisOptions : PlotConfigDemoBase(DoubleVector(300.0, 200.0)) {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
                defaultAxis(),
                noXTitle(),
                noYTitle(),
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


        fun defaultAxis(): Map<String, Any> {
            val spec = "{" +
                    layerMapping() +
                    "," +
                    title("Default") +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun noXTitle(): Map<String, Any> {
            val spec = "{" +
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

        fun noYTitle(): Map<String, Any> {
            val spec = "{" +
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

        fun noXTickLabels(): Map<String, Any> {
            val spec = "{" +
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

        fun noYTickLabels(): Map<String, Any> {
            val spec = "{" +
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

        fun noTickMarks(): Map<String, Any> {
            val spec = "{" +
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

        fun noTickMarksOrLabels(): Map<String, Any> {
            val spec = "{" +
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

        fun noTitlesOrLabels(): Map<String, Any> {
            val spec = "{" +
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

        fun onlyLines(): Map<String, Any> {
            val spec = "{" +
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

        fun noLinesOrTitles(): Map<String, Any> {
            val spec = "{" +
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
