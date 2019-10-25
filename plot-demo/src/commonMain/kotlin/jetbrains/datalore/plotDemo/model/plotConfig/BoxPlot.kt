package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil.fill
import jetbrains.datalore.plotDemo.model.util.DemoUtil.gauss
import jetbrains.datalore.plotDemo.model.util.DemoUtil.zip

/**
 * See 'Plotting distributions'
 * http://www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class BoxPlot : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            basic(),
            withVarWidth(),
            withCondColored(),
            withOutlierOverride(),
            withGrouping(),
            withGroupingAndVarWidth()
        )
    }


    companion object {
        private val DATA =
            data()  // make it stable between calls

        private fun data(): Map<String, List<*>> {
            val count1 = 50
            val count2 = 100

            val ratingA = gauss(count1, 12, 0.0, 1.0)
            val ratingB = gauss(count2, 24, 0.0, 1.0)
            val rating = zip(ratingA, ratingB)
            val cond = zip(fill("a", count1), fill("b", count2))
            val group = ArrayList(fill("G1", count1))
            group.addAll(fill("G2", count2))

            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
            map["group"] = group
            return map
        }


        //===========================


        fun basic(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withVarWidth(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'," +
                    "                  'varwidth': true" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withCondColored(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'fill': 'cond'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withOutlierOverride(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'," +
                    "                  'outlier_color': 'red'," +
                    "                  'outlier_shape': 1," +
                    "                  'outlier_size': 3" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGrouping(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'color': 'group'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGroupingAndVarWidth(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'color': 'group'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'," +
                    "                  'varwidth': true" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
