package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.gogDemo.model.DemoBase
import jetbrains.datalore.visualization.gogDemo.shared.DemoUtil.fill
import jetbrains.datalore.visualization.gogDemo.shared.DemoUtil.gauss
import jetbrains.datalore.visualization.gogDemo.shared.DemoUtil.zip
import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import java.util.*

/**
 * See 'Plotting distributions'
 * http://www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class BoxPlot : DemoBase() {

    override val viewSize: DoubleVector
        get() = viewSize()

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        private val DATA = data()  // make it stable between calls

        fun viewSize(): DoubleVector {
            return toViewSize(DEMO_BOX_SIZE)
        }

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

        fun basic(): Map<String, Any> {
            val spec = "{" +
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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withVarWidth(): Map<String, Any> {
            val spec = "{" +
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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withCondColored(): Map<String, Any> {
            val spec = "{" +
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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withOutlierOverride(): Map<String, Any> {
            val spec = "{" +
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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGrouping(): Map<String, Any> {
            val spec = "{" +
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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGroupingAndVarWidth(): Map<String, Any> {
            val spec = "{" +
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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
