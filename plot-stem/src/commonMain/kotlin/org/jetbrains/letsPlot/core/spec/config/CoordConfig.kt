/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.scale.transform.ReverseTransform
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor.Companion.over

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
        val xLim = transformRange(getCoordLimRange(Option.Coord.X_LIM, accessor), transformX)
        val yLim = transformRange(getCoordLimRange(Option.Coord.Y_LIM, accessor), transformY)

        val xReversed = transformX is ReverseTransform
        val yReversed = transformY is ReverseTransform

        return when (val coordName = getCoordName(coordOpts)) {
            // Flip the 'default' coord system.
            Option.CoordName.FLIP -> defaultCoordProvider.with(xLim, yLim, xReversed, yReversed, flipped = true)

            else -> CoordProto.createCoordProvider(coordName, xLim, yLim, xReversed, yReversed, accessor)
        }
    }

    private fun getCoordName(coordOpts: Any): String {
        val coordName = when (coordOpts) {
            is Map<*, *> -> ConfigUtil.featureName(coordOpts)
            else -> coordOpts.toString()
        }
        return coordName
    }

    private fun getCoordLimRange(optionName: String, accessor: OptionsAccessor): Pair<Double?, Double?> {
        val range = accessor.getNumQPairDef(
            optionName,
            def = null to null
        )
        val first = range.first?.toDouble()
        val second = range.second?.toDouble()
        require(
            first == null || second == null ||
                    second > first
        ) { "Invalid coord $optionName: $range " }
        return Pair(first, second)
    }


    private fun transformRange(r: Pair<Number?, Number?>, t: Transform): Pair<Double?, Double?> {
        return when (t) {
            is ContinuousTransform -> {
                val first = r.first?.let { if (t.isInDomain(it)) it.toDouble() else null }
                val second = r.second?.let { if (t.isInDomain(it)) it.toDouble() else null }
                Pair(
                    t.apply(first),
                    t.apply(second)
                )
            }

            else -> Pair(
                r.first?.toDouble(),
                r.second?.toDouble(),
            )
        }
    }
}
