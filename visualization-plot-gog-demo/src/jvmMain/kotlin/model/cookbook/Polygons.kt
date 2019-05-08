package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.gogDemo.model.DemoBase
import jetbrains.datalore.visualization.gogDemo.shared.SharedPieces
import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import java.util.*

open class Polygons : DemoBase() {

    override val viewSize: DoubleVector
        get() = viewSize()

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        fun viewSize(): DoubleVector {
            return DemoBase.toViewSize(DEMO_BOX_SIZE)
        }

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

            val plotSpec = HashMap(DemoAndTest.parseJson(spec))
            plotSpec["data"] = SharedPieces.samplePolygons()
            return plotSpec
        }
    }
}
