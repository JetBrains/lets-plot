package jetbrains.datalore.visualization.plot.gog.server.config.transform

import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChangeContext

internal class NumericDataVectorSpecChange : SpecChange {
    private fun needChange(l: List<*>): Boolean {
        for (o in l) {
            if (o != null) {
                if (o is Number) {
                    if (o !is Double) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val keys = HashSet(spec.keys)
        for (key in keys) {
            val dat = spec[key]
            if (dat is List<*>) {
                if (needChange(dat)) {
                    spec[key] = dat.map { o: Any? ->
                        if (o is Number) o.toDouble() else o
                    }
                }
            }
        }
    }
}
