/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.coord.CoordProvider

class CoordConfig private constructor(name: String, options: Map<String, Any>) : OptionsAccessor(options, emptyMap<Any, Any>()) {

    val coord: CoordProvider = CoordProto.createCoordProvider(name, this)

    companion object {
        fun create(coord: Any): CoordConfig {
            // position - name of position adjustment
            //        or
            //        map with options
            if (coord is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
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
