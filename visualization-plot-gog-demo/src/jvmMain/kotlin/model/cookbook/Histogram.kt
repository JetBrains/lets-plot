package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.visualization.gogDemo.model.DemoBase
import jetbrains.datalore.visualization.gogDemo.shared.DemoUtil
import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import java.util.*

/**
 * See 'Plotting distributions'
 * http://www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class Histogram : DemoBase() {

    protected fun basic(): Map<String, Any> {
        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'histogram'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(DemoAndTest.parseJson(spec))
        plotSpec["data"] = DATA
        return plotSpec
    }

    companion object {

        private val DATA = data()  // make it stable between calls

        private fun data(): Map<String, List<*>> {
            val count = 100

            val xs = DemoUtil.gauss(count, 12, 0.0, 5.0)
            val weights = ArrayList<Double>()
            for (x in xs) {
                //weights.add(x < 0 ? 2. : .5);
                //weights.add(2.);
                weights.add(Math.abs(x))
            }
            val map = HashMap<String, List<*>>()
            map["x"] = xs
            map["weight"] = weights
            return map
        }

        fun withConstantWeight(): Map<String, Any> {
            val spec = "{" +
                    "   'mapping': {" +
                    "             'x': 'x'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'," +
                    "                  'fill': 'orange'," +
                    "                  'weight': 10" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec1 = HashMap(DemoAndTest.parseJson(spec))
            plotSpec1["data"] = DATA
            return plotSpec1
        }

        fun withWeights(): Map<String, Any> {
            val spec = "{" +
                    "   'mapping': {" +
                    "             'x': 'x'," +
                    "             'weight': 'weight'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
