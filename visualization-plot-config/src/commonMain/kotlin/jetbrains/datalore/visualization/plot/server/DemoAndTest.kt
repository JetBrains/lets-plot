package jetbrains.datalore.visualization.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.Plot

import kotlin.jvm.JvmOverloads

object DemoAndTest {
    @JvmOverloads
    fun createPlot(plotSpec: MutableMap<String, Any>, andBuildComponent: Boolean = true): Plot {
        val plot = Monolithic.createPlot(plotSpec, null)
        if (andBuildComponent) {
            val rootGroup = plot.rootGroup
        }
        return plot
    }

    fun contourDemoData(): Map<String, List<*>> {
        val countX = 20
        val countY = 20

        val mean = DoubleVector(5.0, 5.0)
        val height = 1.0
        val radius = 10.0
        val slop = height / radius
        val x = ArrayList<Double>()
        val y = ArrayList<Double>()
        val z = ArrayList<Double>()
        for (row in 0 until countY) {
            for (col in 0 until countX) {
                val dist = DoubleVector(col.toDouble(), row.toDouble()).subtract(mean).length()
                val v = if (dist >= radius)
                    0.0
                else
                    height - dist * slop

                x.add(col.toDouble())
                y.add(row.toDouble())
                z.add(v)
            }
        }

        val map = HashMap<String, List<*>>()
        map["x"] = x
        map["y"] = y
        map["z"] = z
        return map
    }

    fun getMap(opts: Map<String, Any>, key: String): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val map = opts[key] as? Map<String, Any>
        return map ?: emptyMap()
    }
}
