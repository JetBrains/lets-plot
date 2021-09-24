/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.config.Option.Coord.FLIPPED

class CoordConfig private constructor(
    private val name: String,
    options: Map<String, Any>,
    private val defaultCoordProvider: CoordProvider
) : OptionsAccessor(options) {

    companion object {
        fun create(coordOpts: Any?, defaultCoordProvider: CoordProvider): CoordProvider {
            if (coordOpts == null) return defaultCoordProvider

            val coordName = when (coordOpts) {
                is Map<*, *> -> ConfigUtil.featureName(coordOpts)
                else -> coordOpts.toString()
            }

            @Suppress("UNCHECKED_CAST")
            val options: Map<String, Any> = when (coordOpts) {
                is Map<*, *> -> coordOpts as Map<String, Any>
                else -> emptyMap<String, Any>()
            }
            return when (coordName) {
                Option.CoordName.FLIP -> {
                    // Flip the 'default' coord system.
                    val withFlip = options + mapOf(FLIPPED to true)
                    CoordProto.createCoordProvider(defaultCoordProvider, over(withFlip))
                }
                else -> {
                    CoordProto.createCoordProvider(coordName, over(options))
                }
            }
        }
    }
}
