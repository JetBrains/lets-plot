package jetbrains.datalore.visualization.gogDemo.model.cookbook

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.visualization.gogDemo.model.DemoBase
import jetbrains.datalore.visualization.gogDemo.shared.SharedPieces


open class ABLine : DemoBase() {

    override val viewSize: DoubleVector
        get() = viewSize()

    protected fun lineDefaultAlone(): Map<String, Any> {
        val spec = "    {" +
                "   'layers': [" +
                "           {" +
                "             'geom': 'abline'," +
                "             'size': 3" +
                "           }" +
                "         ]" +
                "}"

        return HashMap(JsonSupport.parseJson(spec))
    }

    protected fun lineDefault(): Map<String, Any> {
        val abLine = "               {" +
                "             'geom': 'abline'," +
                "             'size': 3" +
                "           }"

        return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
    }

    protected fun negativeSlope(): Map<String, Any> {
        val abLine = "               {" +
                "             'geom': 'abline'," +
                "             'slope': '-2'," +
                "             'size': 3" +
                "           }"

        return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
    }

    protected fun zeroSlope(): Map<String, Any> {
        val abLine = "               {" +
                "             'geom': 'abline'," +
                "             'intercept': '1'," +
                "             'slope': '0'," +
                "             'size': 3" +
                "           }"

        return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
    }

    protected fun variableInterceptAndSlope(): Map<String, Any> {
        val intercept = ArrayList<Double>()
        val slope = ArrayList<Double>()
        for (i in 0..9) {
            intercept.add(i * 0.1)
            slope.add(i * 0.2)
        }

        val abLine = "               {" +
                "             'geom': 'abline'," +
                "             'size': 2," +
                "             'mapping': {" +
                "                          'intercept': 'intercept'," +
                "                          'slope': 'slope'," +
                "                          'color': 'intercept'" +
                "                        }" +
                "           }"

        return SharedPieces.samplePolyAndPointsPlotWith(abLine, mapOf(
                "intercept" to intercept,
                "slope" to slope
        ))
    }

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        fun viewSize(): DoubleVector {
            return toViewSize(DEMO_BOX_SIZE)
        }
    }
}
