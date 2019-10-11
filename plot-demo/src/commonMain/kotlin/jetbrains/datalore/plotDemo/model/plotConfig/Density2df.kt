package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.Iris
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class Density2df : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            sepanLength()
        )
    }

    private fun sepanLength(): Map<String, Any> {
        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'y': 'sepal width (cm)'," +
                "             'fill': '..level..'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'density2df'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}