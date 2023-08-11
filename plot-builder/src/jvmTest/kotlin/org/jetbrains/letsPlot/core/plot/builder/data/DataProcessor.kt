/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Stat
import org.jetbrains.letsPlot.core.plot.base.scale.Scales.DemoAndTest.continuousDomain
import org.jetbrains.letsPlot.core.plot.base.scale.Scales.DemoAndTest.pureDiscrete
import org.jetbrains.letsPlot.core.plot.base.stat.SimpleStatContext
import org.jetbrains.letsPlot.core.plot.builder.VarBinding

class DataProcessor {
    private val dataFrame =  DataFrame.Builder()
    private val mappings = mutableMapOf<Aes<*>, DataFrame.Variable>()

    var groupingVarName: String? = null

    fun putVariable(name: String, values: List<Any?>, mappingAes: Aes<*>? = null) : DataFrame.Variable {
        val variable = DataFrame.Variable(name)
        dataFrame.put(variable, values)
        mappingAes?.let { mappings[it] = variable }
        return variable
    }

    fun applyStat(stat: Stat): DataFrame {
        val data = dataFrame.build()
        val bindings = mappings.map { (aes, variable) -> VarBinding(variable, aes) }

        val transformByAes = mappings.mapValues {(aes, variable) ->
            val scale = when (data.isNumeric(variable)) {
                true -> continuousDomain(variable.name, aes)
                false -> pureDiscrete(variable.name, data.distinctValues(variable).toList())
            }

            scale.transform
        }

        val transformedData = DataProcessing.transformOriginals(
            data = data,
            bindings = bindings,
            transformByAes = transformByAes
        )

        val groupingContext = GroupingContext(
            data = transformedData,
            defaultGroupingVariables = emptyList(),
            explicitGroupingVarName = groupingVarName,
            expectMultiple = true
        )
        val statContext = SimpleStatContext(
            myDataFrame = transformedData,
            mappedStatVariables = emptyList()
        )

        val statInput = StatInput(
            data = transformedData,
            bindings = bindings,
            transformByAes = transformByAes,
            statCtx = statContext,
            flipXY = false
        )

        return DataProcessing.buildStatData(
            statInput = statInput,
            stat = stat,
            groupingContext = groupingContext,
            facetVariables = emptyList(),
            varsWithoutBinding = emptyList(),
            orderOptions = emptyList(),
            aggregateOperation = { null },
            messageConsumer = { _ -> }
        ).data
    }
}