package jetbrains.datalore.visualization.plotDemo.model.plotConfig

import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.visualization.plotDemo.model.PlotConfigDemoBase

open class BarPlot : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
                basic(),
                fancy()
        )
    }


    companion object {
        private const val OUR_DATA = "   {" +
                "      'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
                "   }"


        fun basic(): Map<String, Any> {
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'bar'" +
                    "               }" +
                    "           ]" +
                    "}"

            return JsonSupport.parseJson(spec)
        }

        fun fancy(): Map<String, Any> {
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
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

            return JsonSupport.parseJson(spec)
        }
    }
}
