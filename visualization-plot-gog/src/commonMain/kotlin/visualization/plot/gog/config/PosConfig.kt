package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.plot.assemble.PosProvider

internal class PosConfig private constructor(name: String, options: Map<String, Any>) : OptionsAccessor(options, emptyMap<Any, Any>()) {

    val pos: PosProvider

    init {
        pos = PosProto.createPosProvider(name, mergedOptions)
    }

    companion object {
        fun create(position: Any): PosConfig {
            // position - name of position adjustment
            //        or
            //        map with options
            if (position is Map<*, *>) {
                val positionMap = position as Map<String, Any>
                val options = HashMap(positionMap)
                return createForName(ConfigUtil.featureName(options), options)
            }
            return createForName(position.toString(), HashMap())
        }

        private fun createForName(name: String, options: Map<String, Any>): PosConfig {
            return PosConfig(name, options)
        }
    }
}
