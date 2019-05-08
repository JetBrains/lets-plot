package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.gogDemo.model.DemoBase
import jetbrains.datalore.visualization.plot.gog.DemoAndTest

/**
 * see: http://www.cookbook-r.com/Graphs/Bar_and_line_graphs_(ggplot2)/
 */
open class BarAndLine : DemoBase() {

    override val viewSize: DoubleVector
        get() = viewSize()

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        fun viewSize(): DoubleVector {
            return toViewSize(DEMO_BOX_SIZE)
        }

        private val OUR_DATA = "   {" +
                "      'time': ['Lunch', 'Dinner']," +
                "      'total_bill': [14.89, 17.23]" +
                "   }"

        fun defaultBarDiscreteX(): Map<String, Any> {
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'" +
                    "               }" +
                    "           ]" +
                    "}"

            return DemoAndTest.parseJson(spec)
        }

        fun barDiscreteXFill(): Map<String, Any> {
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'" +
                    "               }" +
                    "           ]" +
                    "}"

            return DemoAndTest.parseJson(spec)
        }

        fun barDiscreteXFillMappedInGeom(): Map<String, Any> {
            // Must be same result as in the method above
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'," +
                    "                 'mapping': {" +
                    "                              'fill': 'time'" +
                    "                            }" +
                    "               }" +
                    "           ]" +
                    "}"

            return DemoAndTest.parseJson(spec)
        }

        fun barDiscreteXFillAndBlackOutline(): Map<String, Any> {
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'," +
                    "                 'colour': 'black'" +
                    "               }" +
                    "           ]" +
                    "}"

            return DemoAndTest.parseJson(spec)
        }

        fun barDiscreteXTitleAxisLabelsNarrowWidth(): Map<String, Any> {
            val spec = "{" +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'," +
                    "                 'colour': 'black'," +
                    "                 'fill': '#DD8888'," +
                    "                 'width': 0.8" +
                    "               }" +
                    "           ]," +
                    "   'scales': [" +
                    "               {" +
                    "                  'aesthetic': 'x'," +
                    "                  'name': 'Time of day'" +
                    "               }," +
                    "               {" +
                    "                  'aesthetic': 'y'," +
                    "                  'name': 'Total bill'" +
                    "               }" +
                    "           ]," +
                    "   'ggtitle': {" +
                    "                 'text': 'Average bill for 2 people'" +
                    "               }" +
                    "}"

            return DemoAndTest.parseJson(spec)
        }
    }
}
