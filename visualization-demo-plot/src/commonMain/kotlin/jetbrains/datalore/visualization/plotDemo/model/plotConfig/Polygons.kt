package jetbrains.datalore.visualization.plotDemo.model.plotConfig

import jetbrains.datalore.visualization.plot.parsePlotSpec
import jetbrains.datalore.visualization.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.visualization.plotDemo.model.SharedPieces

open class Polygons : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
                basic()
        )
    }

    companion object {
        fun basic(): Map<String, Any> {
            val spec = "{" +
                    //        "   'data': " + ourData +
                    //        "           ," +
                    "   'mapping': {" +
                    "             'x': 'x'," +
                    "             'y': 'y'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'polygon'," +
                    "                  'mapping': {" +
                    "                               'fill': 'value'," +
                    "                               'group': 'id'" +
                    "                              }" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = SharedPieces.samplePolygons()
            return plotSpec
        }
    }
}
