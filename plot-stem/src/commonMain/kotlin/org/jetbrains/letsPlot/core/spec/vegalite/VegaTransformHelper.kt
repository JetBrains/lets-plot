/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.SummaryStatOptions.AggFunction
import org.jetbrains.letsPlot.core.spec.vegalite.Util.getDefinedChannelEncodings
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

            getDefinedChannelEncodings(encodings).forEach { (channel, encoding) ->
                if (channel == statInputChannel) {
                    encodingAdj.add(listOf(channel, Encoding.TYPE) to Encoding.Types.QUANTITATIVE)
                }

                if (encoding[Encoding.AGGREGATE] == Aggregate.COUNT) {
                    encodingAdj.add(listOf(channel, Encoding.FIELD) to Stats.COUNT.name)
                    encodingAdj.add(listOf(channel, Encoding.TYPE) to Encoding.Types.QUANTITATIVE)
                }
            }


            return TransformResult(
                binStat {
                    val binDefinition = (xBinDefinition ?: yBinDefinition!!) as? Map<*, *>
                    binDefinition?.let {
                        bins = binDefinition.getInt(Transform.Bin.MAXBINS)
                        binWidth = binDefinition.getDouble(Transform.Bin.STEP)
                    }
                },
                "y".takeIf { statInputChannel == Channel.Y },
                encodingAdj
            )
        }


        run { // y.aggregate(count/sum/mean) -> countStat/summaryStat
            val aggrEncodings = getDefinedChannelEncodings(encodings)
                .filter { (_, enc) -> Encoding.AGGREGATE in enc }

            if (aggrEncodings.isEmpty()) {
                return@run
            }

            val aggrFunctions = aggrEncodings.entries
                .fold(emptySet<String>() ) { acc, (_, enc) -> acc + enc.getString(Encoding.AGGREGATE)!! }

            if (aggrFunctions.size > 1) {
                error("Only one aggregate function is supported in a single layer: $aggrFunctions")
            }

            // layer orientation implicit inference works fine for agg, not need to pass it explicitly
            val stat = when (val aggrFun = aggrFunctions.single()) {
                Aggregate.COUNT -> {
                    // Replace variable name from data to LP stat var name
                    encodingAdj += aggrEncodings.map { (channel, _) ->
                        listOf(channel, Encoding.FIELD) to Stats.COUNT.name
                    }

                    countStat()
                }
                Aggregate.SUM -> summaryStat { f = AggFunction.SUM }
                Aggregate.MEAN -> summaryStat { f = AggFunction.MEAN }
                else -> error("Unsupported aggregate function: $aggrFun")
            }

            return TransformResult(
                stat,
                encodingAdjustment = encodingAdj
            )
        }


        run { // transform.density -> densityStat
            val densityTransform = layerSpec
                .getMaps(VegaOption.Mark.TRANSFORM)
                ?.firstOrNull { Transform.DENSITY in it }
                ?: return@run

            val origVar = densityTransform.getString(Transform.Density.DENSITY) ?: return@run

            val statInputChannel = when {
                encodings.read(Channel.X, Encoding.FIELD) == Transform.Density.VAR_VALUE -> Channel.X
                encodings.read(Channel.Y, Encoding.FIELD) == Transform.Density.VAR_VALUE -> Channel.Y
                else -> null
            }

            getDefinedChannelEncodings(encodings)
                .forEach { (channel, encoding) ->
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
