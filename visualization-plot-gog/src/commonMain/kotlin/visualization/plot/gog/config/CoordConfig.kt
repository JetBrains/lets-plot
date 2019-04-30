package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProvider

internal class CoordConfig private constructor(name: String, options: Map<String, Any>) : OptionsAccessor(options, emptyMap<Any, Any>()) {

    val coord: CoordProvider

    init {
        coord = CoordProto.createCoordProvider(name, this)
    }

    companion object {
        fun create(coord: Any): CoordConfig {
            // position - name of position adjustment
            //        or
            //        map with options
            if (coord is Map<*, *>) {
                val options = coord as Map<String, Any>
                return createForName(ConfigUtil.featureName(options), options)
            }
            return createForName(coord.toString(), HashMap())
        }

        private fun createForName(name: String, options: Map<String, Any>): CoordConfig {
            return CoordConfig(name, options)
        }
    }
}
