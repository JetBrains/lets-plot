package jetbrains.datalore.visualization.plotDemo.model.plotConfig

import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.visualization.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.visualization.plotDemo.model.util.DemoUtil
import kotlin.math.abs

/**
 * See 'Plotting distributions'
 * http://www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
@kotlinx.serialization.ImplicitReflectionSerializer
open class Histogram : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
                basic(),
                withWeights(),
                withConstantWeight()
        )
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
                weights.add(abs(x))
            }
            val map = HashMap<String, List<*>>()
            map["x"] = xs
            map["weight"] = weights
            return map
        }


        //===========================


        fun basic(): Map<String, Any> {
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

            val plotSpec = HashMap(JsonSupport.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
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

            val plotSpec1 = HashMap(JsonSupport.parseJson(spec))
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

            val plotSpec = HashMap(JsonSupport.parseJson(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
