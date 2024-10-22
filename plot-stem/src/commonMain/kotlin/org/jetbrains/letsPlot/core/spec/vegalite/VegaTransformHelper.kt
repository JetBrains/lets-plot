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
    val adjustedEncoding: Map<*, Map<*, *>>, // Vega "stat" configs (aggregate, bin, ...) replaced with LP stat vars
    val stat: StatOptions,
    val orientation: String? = null
)

object VegaTransformHelper {
    fun applyTransform(encodings: Map<*, Map<*, *>>, layerSpec: Map<*, *>): TransformResult? {
        run { // x.bin -> binStat
            val xBinDefinition = encodings.read(Channel.X, Encoding.BIN)
            val yBinDefinition = encodings.read(Channel.Y, Encoding.BIN)

            val statInputChannel = when {
                xBinDefinition != null -> Channel.X
                yBinDefinition != null -> Channel.Y
                else -> return@run
            }

            val adjustedEncoding = encodings.mapValues { (channel, encoding) ->
                when {
                    channel == statInputChannel -> encoding - Encoding.BIN + (Encoding.TYPE to Encoding.Types.QUANTITATIVE)
                    encoding[Encoding.AGGREGATE] == Aggregate.COUNT -> {
                        encoding - Encoding.AGGREGATE +
                                (Encoding.FIELD to Stats.COUNT.name) +
                                (Encoding.TYPE to Encoding.Types.QUANTITATIVE)
                    }
                    else -> encoding
                }
            }

            val binDefinition = xBinDefinition ?: yBinDefinition!!
            return TransformResult(
                adjustedEncoding,
                binStat {
                    (binDefinition as? Map<*, *>)?.forEach { (key, value) ->
                        when (key) {
                            Transform.Bin.MAXBINS -> bins = (value as? Number)?.toInt()
                            Transform.Bin.STEP -> binWidth = (value as? Number)?.toDouble()
                            else -> println("Unsupported bin parameter: $key")
                        }
                    }
                },
                "y".takeIf { statInputChannel == Channel.Y }
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
                Aggregate.COUNT -> TransformResult(encodings, countStat())
                Aggregate.SUM -> TransformResult(encodings, summaryStat { f = AggFunction.SUM })
                Aggregate.MEAN -> TransformResult(encodings, summaryStat { f = AggFunction.MEAN })
                else -> error("Unsupported aggregate function: $aggregate")
            }
        }


        run { // transform.density -> densityStat
            val densityTransform = layerSpec
                .getMaps(VegaOption.Mark.TRANSFORM)
                ?.firstOrNull { Transform.DENSITY in it }
                ?: return@run

            val origVar = densityTransform.getString(Transform.Density.DENSITY) ?: return@run
            val groupingVar = densityTransform.getString(Transform.Density.GROUP_BY)

            val statInputChannel = encodings
                .entries
                .filter { (channel, _) -> channel == Channel.X || channel == Channel.Y }
                .singleOrNull { (_, encoding) -> encoding[Encoding.FIELD] == Transform.Density.VAR_VALUE }
                ?.key

            return TransformResult(
                encodings.mapValues { (_, encoding) ->
                    when {
                        encoding[Encoding.FIELD] == Transform.Density.VAR_DENSITY -> encoding + (Encoding.FIELD to Stats.DENSITY.name)
                        encoding[Encoding.FIELD] == Transform.Density.VAR_VALUE -> encoding + (Encoding.FIELD to origVar)
                        else -> encoding
                    }
                },
                densityStat(),
                "y".takeIf { statInputChannel == Channel.Y }
            )
        }

        return null
    }
}
