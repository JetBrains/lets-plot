/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.assemble.PosProvider

internal class PosConfig private constructor(name: String, options: Map<String, Any>) : OptionsAccessor(options) {

    val pos: PosProvider = PosProto.createPosProvider(name, mergedOptions)

    companion object {
        fun create(position: Any): PosConfig {
            // position - name of position adjustment
            //        or
            //        map with options
            if (position is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val options = HashMap(position as Map<String, Any>)
                return createForName(
                    ConfigUtil.featureName(options),
                    options
                )
            }
            return createForName(position.toString(), HashMap())
        }

        private fun createForName(name: String, options: Map<String, Any>): PosConfig {
            return PosConfig(name, options)
        }
    }
}
