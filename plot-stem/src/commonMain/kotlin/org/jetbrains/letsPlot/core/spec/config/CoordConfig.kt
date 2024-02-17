/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor.Companion.over
import kotlin.math.max
import kotlin.math.min

object CoordConfig {

    fun allowsDomainExpand(coordOpts: Any?): Boolean {
        if (coordOpts == null) return true
        return getCoordName(coordOpts) != Option.CoordName.POLAR
    }

    fun createCoordProvider(
        coordOpts: Any?,
        transformX: Transform,
        transformY: Transform,
        defaultCoordProvider: CoordProvider
    ): CoordProvider {
        if (coordOpts == null) return defaultCoordProvider

        @Suppress("UNCHECKED_CAST")
        val options: Map<String, Any> = when (coordOpts) {
            is Map<*, *> -> coordOpts as Map<String, Any>
            else -> emptyMap()
        }

        // Use "transformed" values for limits.
        val accessor = over(options)
        val xLim = accessor.getNumQPairDef(
            Option.Coord.X_LIM,
            def = null to null
        ).let {
            transformRange(it, transformX)
        }
        val yLim = accessor.getNumQPairDef(
            Option.Coord.Y_LIM,
            def = null to null
        ).let {
            transformRange(it, transformY)
        }

        return when (val coordName = getCoordName(coordOpts)) {
            // Flip the 'default' coord system.
            Option.CoordName.FLIP -> defaultCoordProvider.with(xLim, yLim, flipped = true)

            else -> CoordProto.createCoordProvider(coordName, xLim, yLim, accessor)
        }
    }

    private fun getCoordName(coordOpts: Any): String {
        val coordName = when (coordOpts) {
            is Map<*, *> -> ConfigUtil.featureName(coordOpts)
            else -> coordOpts.toString()
        }
        return coordName
    }

    private fun transformRange(r: Pair<Number?, Number?>, t: Transform): Pair<Double?, Double?> {
        return when (t) {
            is ContinuousTransform -> {
                val first = r.first?.let { if (t.isInDomain(it)) it.toDouble() else null }
                val second = r.second?.let { if (t.isInDomain(it)) it.toDouble() else null }

                val (lower, upper) = if (first != null && second != null) {
                    Pair(
                        min(first, second),
                        max(first, second)
                    )
                } else {
                    Pair(first, second)
                }

                Pair(
                    t.apply(lower),
                    t.apply(upper)
                )
            }

            else -> Pair(
                r.first?.toDouble(),
                r.second?.toDouble(),
            )
        }
    }
}
