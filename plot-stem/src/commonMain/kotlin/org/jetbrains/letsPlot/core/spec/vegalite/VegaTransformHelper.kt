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
import org.jetbrains.letsPlot.core.spec.read
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Aggregate
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Transform

class TransformResult internal constructor(
    val stat: StatOptions,
    val orientation: String? = null,
    val encodingAdjustment: List<Pair<List<String>, Any>>?
)

object VegaTransformHelper {
    // Adjusts Vega "stat" configs (aggregate, bin, ...), replacing them with the LP stat vars
    fun applyTransform(layerSpec: Map<*, *>, encodings: Map<*, *>): TransformResult? {
        val encodingAdj = mutableListOf<Pair<List<String>, Any>>()

        run { // x.bin -> binStat
            val xBinDefinition = encodings.read(Channel.X, Encoding.BIN)
            val yBinDefinition = encodings.read(Channel.Y, Encoding.BIN)

            val statInputChannel = when {
                xBinDefinition != null -> Channel.X
                yBinDefinition != null -> Channel.Y
                else -> return@run
            }

            encodings.entries.forEach { (channel, encoding) ->
                channel as String
                encoding as Map<*, *>
                if (channel == statInputChannel) {
                    encodingAdj.add(listOf(channel, Encoding.TYPE) to Encoding.Types.QUANTITATIVE)
                }

                if (encoding[Encoding.AGGREGATE] == Aggregate.COUNT) {
                    encodingAdj.add(listOf(channel, Encoding.FIELD) to Stats.COUNT.name)
                    encodingAdj.add(listOf(channel, Encoding.TYPE) to Encoding.Types.QUANTITATIVE)
                }
            }

            val binDefinition = xBinDefinition ?: yBinDefinition!!
            return TransformResult(
                binStat {
                    (binDefinition as? Map<*, *>)?.forEach { (key, value) ->
                        when (key) {
                            Transform.Bin.MAXBINS -> bins = (value as? Number)?.toInt()
                            Transform.Bin.STEP -> binWidth = (value as? Number)?.toDouble()
                            else -> println("Unsupported bin parameter: $key")
                        }
                    }
                },
                "y".takeIf { statInputChannel == Channel.Y },
                encodingAdj
            )
        }


        run { // y.aggregate(count/sum/mean) -> countStat/summaryStat
            val xAggregate = encodings.getString(Channel.X, Encoding.AGGREGATE)
            val yAggregate = encodings.getString(Channel.Y, Encoding.AGGREGATE)

            if (xAggregate != null && yAggregate != null) {
                error("Both x and y aggregates are not supported")
            }

            val aggregate = xAggregate ?: yAggregate ?: return@run
            // layer orientation implicit inference works fine for agg, not need to pass it explicitly
            return when (aggregate) {
                Aggregate.COUNT -> TransformResult(countStat(), encodingAdjustment = null)
                Aggregate.SUM -> TransformResult(summaryStat { f = AggFunction.SUM }, encodingAdjustment = null)
                Aggregate.MEAN -> TransformResult(summaryStat { f = AggFunction.MEAN }, encodingAdjustment = null)
                else -> error("Unsupported aggregate function: $aggregate")
            }
        }


        run { // transform.density -> densityStat
            val densityTransform = layerSpec
                .getMaps(VegaOption.Mark.TRANSFORM)
                ?.firstOrNull { Transform.DENSITY in it }
                ?: return@run

            val origVar = densityTransform.getString(Transform.Density.DENSITY) ?: return@run

            val statInputChannel = encodings
                .entries
                .filter { (channel, _) -> channel == Channel.X || channel == Channel.Y }
                .singleOrNull { (_, encoding) -> (encoding as Map<*, *>)[Encoding.FIELD] == Transform.Density.VAR_VALUE }
                ?.key

            encodings.entries.forEach { (channel, encoding) ->
                channel as String
                encoding as Map<*, *>

                when (encoding[Encoding.FIELD]) {
                    Transform.Density.VAR_DENSITY -> encodingAdj.add(listOf(channel, Encoding.FIELD) to Stats.DENSITY.name)
                    Transform.Density.VAR_VALUE -> encodingAdj.add(listOf(channel, Encoding.FIELD) to origVar)
                }
            }

            return TransformResult(
                densityStat(),
                "y".takeIf { statInputChannel == Channel.Y },
                encodingAdj
            )
        }

        return null
    }
}
