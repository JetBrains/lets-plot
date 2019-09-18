package jetbrains.datalore.visualization.plot.pythonExtension

import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide

//import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide

class SampleBarPlot {

    fun getHTML(): String {
//        JsonSupport.parseJson(plotSpec())

        var plotSpec = plotSpecMap()
        // apply server-side transforms (statistics, sampling etc.)
        plotSpec = PlotConfigServerSide.processTransform(plotSpec)

        return PlotHtmlGen.getHtml(plotSpec)
    }

    private companion object {
        private fun plotSpecMap(): MutableMap<String, Any> {
            val map = HashMap<String, Any>()
            map["data"] = mapOf("time" to listOf("Lunch", "Lunch", "Dinner", "Dinner", "Dinner"))
            map["mapping"] = mapOf(
                "x" to "time",
                "y" to "..count..",
                "fill" to "..count.."
            )
            map["layers"] = listOf(mapOf("geom" to "bar"))
            map["scales"] = listOf(
                mapOf(
                    "scale_mapper_kind" to "color_hue",
                    "aesthetic" to "fill",
                    "discrete" to "true"
                )
            )
            return map
        }

        private fun plotSpec(): String {
            return "{" +
                    "   'data': {" +
                    "             'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
                    "           }," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': '..count..'," +
                    "             'fill': '..count..'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'bar'" +
                    "               }" +
                    "           ]" +

                    "   ," +
                    "   'scales': [" +
                    "               {" +
                    "                  'aesthetic': 'fill'," +
                    "                  'discrete': true," +
                    "                  'scale_mapper_kind': 'color_hue'" +
                    "               }" +
                    "           ]" +
                    "}"
        }
    }
}