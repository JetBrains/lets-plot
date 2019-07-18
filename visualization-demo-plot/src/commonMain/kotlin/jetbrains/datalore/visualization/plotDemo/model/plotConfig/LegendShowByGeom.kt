package jetbrains.datalore.visualization.plotDemo.model.plotConfig

import jetbrains.datalore.visualization.plot.parsePlotSpec
import jetbrains.datalore.visualization.plotDemo.model.PlotConfigDemoBase

open class LegendShowByGeom : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
                defaultLegend(),
                noLinesLegend(),
                noBothLegends()
        )
    }


    companion object {
        private const val LINES_DATA = "   {" +
                "      'c': ['Line A', 'Line A', 'Line A', 'Line B', 'Line B', 'Line B']," +
                "      'x': [1, 2, 3, 1, 2, 3]," +
                "      'y': [1, 2, 1.5, 1.5, 0.5, 2.5]" +
                "   }"

        private const val POINTS_DATA = "   {" +
                "      'c': ['Point X', 'Point Y', 'Point Z']," +
                "      'x': [1.5, 2, 3]," +
                "      'y': [2, 1.5, 1]" +
                "   }"

        private fun linesLayer(disableLegend: Boolean): String {

            return "{" +
                    (if (disableLegend) "'show_legend': false," else "") +
                    "  'geom':  'line'," +
                    "  'stat': 'identity'," +
                    "  'data': " + LINES_DATA + "," +
                    "  'mapping': {" +
                    "            'x': 'x'," +
                    "            'y': 'y'," +
                    "            'color': 'c'" +
                    "             }" +
                    "}"
        }

        private fun pointsLayer(disableLegend: Boolean): String {

            return "{" +
                    (if (disableLegend) "'show_legend': false," else "") +
                    "  'geom':  'point'," +
                    "  'stat': 'identity'," +
                    "  'data': " + POINTS_DATA + "," +
                    "  'mapping': {" +
                    "            'x': 'x'," +
                    "            'y': 'y'," +
                    "            'color': 'c'" +
                    "          }," +
                    "  'size': 5" +
                    "}"
        }


        //===========================


        fun defaultLegend(): Map<String, Any> {
            val spec = "{" +
                    "'layers': [" +
                    linesLayer(false) +
                    "," +
                    pointsLayer(false) +
                    "  ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun noLinesLegend(): Map<String, Any> {
            val spec = "{" +
                    "'layers': [" +
                    linesLayer(true) +
                    "," +
                    pointsLayer(false) +
                    "  ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun noBothLegends(): Map<String, Any> {
            val spec = "{" +
                    "'layers': [" +
                    linesLayer(true) +
                    "," +
                    pointsLayer(true) +
                    "  ]" +
                    "}"

            return parsePlotSpec(spec)
        }
    }
}
