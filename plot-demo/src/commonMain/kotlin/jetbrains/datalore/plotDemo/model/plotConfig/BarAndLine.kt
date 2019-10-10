package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

/**
 * see: http://www.cookbook-r.com/Graphs/Bar_and_line_graphs_(ggplot2)/
 */
open class BarAndLine : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            defaultBarDiscreteX(),
            barDiscreteXFill(),
            barDiscreteXFillMappedInGeom(),
            barDiscreteXFillAndBlackOutline(),
            barDiscreteXTitleAxisLabelsNarrowWidth()
        )
    }

    companion object {
        private const val OUR_DATA = "   {" +
                "      'time': ['Lunch', 'Dinner']," +
                "      'total_bill': [14.89, 17.23]" +
                "   }"


        //===========================

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

            return parsePlotSpec(spec)
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

            return parsePlotSpec(spec)
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

            return parsePlotSpec(spec)
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

            return parsePlotSpec(spec)
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

            return parsePlotSpec(spec)
        }
    }
}
