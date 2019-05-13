package jetbrains.datalore.visualization.plot.gog

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.plot.Plot

import java.lang.reflect.Type

object DemoAndTest {
    fun parseJson(json: String): MutableMap<String, Any> {
        val type: Type = object : TypeToken<MutableMap<String, Any>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun toJson(o: Any): String {
        return Gson().toJson(o)
    }

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

    fun assertExceptionNotHappened(r: () -> Unit) {
        assertExceptionNotHappened("", r)
    }

    fun assertExceptionNotHappened(message: String, r: () -> Unit) {
        try {
            r()
        } catch (e: RuntimeException) {
            throw AssertionError(message, e)
        }

    }

    fun getMap(opts: Map<String, Any>, key: String): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val map = opts[key] as? Map<String, Any>
        return map ?: emptyMap()
    }
}
