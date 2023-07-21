/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.Option

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
                    defaultCoordProvider.with(xLim, yLim, flipped = true)
                }
                else -> {
                    CoordProto.createCoordProvider(coordName, xLim, yLim, accessor)
                }
            }
        }

        private fun validateRange(r: DoubleSpan, t: Transform): DoubleSpan {
            return when (t) {
                is ContinuousTransform -> {
                    val ar = t.toApplicableDomain(r)
                    DoubleSpan(
                        t.apply(ar.lowerEnd)!!,
                        t.apply(ar.upperEnd)!!
                    )
                }
                else -> r
            }
        }
    }
}
