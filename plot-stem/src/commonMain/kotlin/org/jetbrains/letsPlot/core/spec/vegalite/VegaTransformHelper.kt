/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.SummaryStatOptions.AggFunction
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel

class TransformResult internal constructor(
    val stat: StatOptions,
    private val mappingAdjustment: (Mapping) -> Mapping = { it }
) {
    fun adjustMapping(mapping: Mapping): Mapping {
        return mappingAdjustment(mapping)
    }
}

object VegaTransformHelper {
    fun applyTransform(encodings: Map<*, Map<*, *>>, layerSpec: Map<*, *>): TransformResult? {
        run { // y.aggregate(count/sum/mean) -> countStat/summaryStat
            val xAggregate = encodings.getString(Channel.X, Encoding.AGGREGATE)
            val yAggregate = encodings.getString(Channel.Y, Encoding.AGGREGATE)

            if (xAggregate != null && yAggregate != null) {
                error("Both x and y aggregates are not supported")
            }

            val aggregate = xAggregate ?: yAggregate
            if (aggregate != null) {
                return when (aggregate) {
                    Encoding.Aggregate.COUNT -> TransformResult(countStat())
                    Encoding.Aggregate.SUM -> TransformResult(summaryStat { f = AggFunction.SUM })
                    Encoding.Aggregate.MEAN -> TransformResult(summaryStat { f = AggFunction.MEAN })
                    else -> error("Unsupported aggregate function: $aggregate")
                }
            }
        }

        run { // x.bin -> binStat
            val xBinDefinition = encodings.getString(Channel.X, Encoding.BIN)
            val yBinDefinition = encodings.getString(Channel.Y, Encoding.BIN)

            val channel = when {
                xBinDefinition != null -> Channel.X
                yBinDefinition != null -> Channel.Y
                else -> null
            }

            if (channel != null) {
                return TransformResult(binStat())
            }
        }

        run { // transform.density -> densityStat
            val densityTransform = layerSpec
                .getMaps(VegaOption.Mark.TRANSFORM)
                ?.firstOrNull { VegaOption.Transform.DENSITY in it }
                ?: return@run

            val densityVar = densityTransform.getString(VegaOption.Transform.Density.DENSITY) ?: return@run
            val densityGroup = densityTransform.getString(VegaOption.Transform.Density.GROUP_BY)

            val statMapping = mapOf(
                VegaOption.Transform.Density.VAR_DENSITY to Stats.DENSITY.name, // "density" -> "..density.."
                VegaOption.Transform.Density.VAR_VALUE to densityVar // "value" -> "cty_mpg"
            )
            return TransformResult(densityStat()) { mapping ->
                Mapping(
                    groupingVar = densityGroup,
                    mapping.aesthetics.map { (aes, variable) ->
                        when (variable in statMapping) {
                            true -> aes to statMapping[variable]!!
                            false -> aes to variable
                        }
                    }.toMap()
                )
            }
        }
        return null
    }
}