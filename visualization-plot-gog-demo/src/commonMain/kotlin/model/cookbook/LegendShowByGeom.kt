package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.visualization.gogDemo.model.DemoBase

open class LegendShowByGeom : DemoBase() {

    override val viewSize: DoubleVector
        get() = viewSize()

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        fun viewSize(): DoubleVector {
            return toViewSize(DEMO_BOX_SIZE)
        }

        private val LINES_DATA = "   {" +
                "      'c': ['Line A', 'Line A', 'Line A', 'Line B', 'Line B', 'Line B']," +
                "      'x': [1, 2, 3, 1, 2, 3]," +
                "      'y': [1, 2, 1.5, 1.5, 0.5, 2.5]" +
                "   }"

        private val POINTS_DATA = "   {" +
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

        fun defaultLegend(): Map<String, Any> {
            val spec = "{" +
                    "'layers': [" +
                    linesLayer(false) +
                    "," +
                    pointsLayer(false) +
                    "  ]" +
                    "}"

            return JsonSupport.parseJson(spec)
        }

        fun noLinesLegend(): Map<String, Any> {
            val spec = "{" +
                    "'layers': [" +
                    linesLayer(true) +
                    "," +
                    pointsLayer(false) +
                    "  ]" +
                    "}"

            return JsonSupport.parseJson(spec)
        }

        fun noBothLegends(): Map<String, Any> {
            val spec = "{" +
                    "'layers': [" +
                    linesLayer(true) +
                    "," +
                    pointsLayer(true) +
                    "  ]" +
                    "}"

            return JsonSupport.parseJson(spec)
        }
    }
}
