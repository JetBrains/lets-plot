/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.builder.coord.CoordProvider

class CoordConfig private constructor(
    private val name: String,
    options: Map<String, Any>,
    private val defaultCoordProvider: CoordProvider
) : OptionsAccessor(options) {

    companion object {
        fun create(
            coordOpts: Any?,
            transformX: Transform,
            transformY: Transform,
            defaultCoordProvider: CoordProvider
        ): CoordProvider {
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

            // Use "transformed" values for limits.
            val accessor = over(options)
            val xLim = accessor.getRangeOrNull(Option.Coord.X_LIM)?.let { validateRange(it, transformX) }
            val yLim = accessor.getRangeOrNull(Option.Coord.Y_LIM)?.let { validateRange(it, transformY) }

            return when (coordName) {
                Option.CoordName.FLIP -> {
                    // Flip the 'default' coord system.
//                    val withFlip = options + mapOf(FLIPPED to true)
//                    CoordProto.createCoordProvider(defaultCoordProvider, over(withFlip))
                    CoordProto.createCoordProvider(defaultCoordProvider, xLim, yLim, flipped = true)
                }
                else -> {
//                    val flipped = accessor.getBoolean(Option.Coord.FLIPPED)
//                    CoordProto.createCoordProvider(coordName, over(options))
                    CoordProto.createCoordProvider(coordName, xLim, yLim, accessor)
                }
            }
        }

        private fun validateRange(r: ClosedRange<Double>, t: Transform): ClosedRange<Double> {
            return when (t) {
                is ContinuousTransform -> {
                    val ar = t.toApplicableDomain(r)
                    ClosedRange<Double>(
                        t.apply(ar.lowerEnd)!!,
                        t.apply(ar.upperEnd)!!
                    )
                }
                else -> r
            }
        }
    }
}
