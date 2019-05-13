package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.visualization.gogDemo.model.DemoBase

open class BarPlot : DemoBase() {

    override val viewSize: DoubleVector
        get() = viewSize()

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        fun viewSize(): DoubleVector {
            return toViewSize(DEMO_BOX_SIZE)
        }

        private val OUR_DATA = "   {" +
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
