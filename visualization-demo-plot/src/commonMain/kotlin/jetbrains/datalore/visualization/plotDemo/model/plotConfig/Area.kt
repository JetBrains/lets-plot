package jetbrains.datalore.visualization.plotDemo.model.plotConfig

import jetbrains.datalore.visualization.plot.parsePlotSpec
import jetbrains.datalore.visualization.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.AreaPlotDemo

class Area : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            sepanLength()
        )
    }

    private fun sepanLength(): Map<String, Any> {
        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'group': 'target'," +
                "             'color': 'sepal width (cm)'," +
                "             'fill': 'target'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'area'," +
                "                   'stat': 'density'," +
                "                   'position' : 'identity'," +
                "                   'alpha': 0.7" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AreaPlotDemo.Iris.df
        return plotSpec

    }
}