/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.DataFrame.Builder
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.data.GroupUtil.indicesByGroup
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.data.SeriesUtil.pickAtIndices

object DataProcessing {

    fun transformOriginals(
        data: DataFrame,
        bindings: List<VarBinding>,
        transformByAes: Map<Aes<*>, Transform>
    ): DataFrame {
        @Suppress("NAME_SHADOWING")
        var data = data
        for (binding in bindings) {
            val variable = binding.variable
            if (variable.isOrigin) {
                data.assertDefined(variable)
                data = DataFrameUtil.applyTransform(
                    data,
                    variable,
                    binding.aes,
                    transformByAes.getValue(binding.aes)
                )
            }
        }

        return data
    }

    /**
     * Backend-side only
     */
    fun buildStatData(
        statInput: StatInput,
        stat: Stat,
        groupingContext: GroupingContext,
        facetVariables: List<Variable>,
        varsWithoutBinding: List<String>,
        orderOptions: List<OrderOptionUtil.OrderOption>,
        aggregateOperation: ((List<Double?>) -> Double?)?,
        messageConsumer: Consumer<String>
    ): DataAndGroupingContext {
        check(stat != Stats.IDENTITY)

        val groups = groupingContext.groupMapper

        val resultSeries: Map<Variable, List<Any?>>
        val groupSizeListAfterStat: List<Int>

        // if only one group no need to modify
        if (groups === GroupUtil.SINGLE_GROUP || statInput.data.rowCount() == 0) {
            val statData = applyStat(
                statInput.data,
                stat,
                statInput.bindings,
                statInput.transformByAes,
                facetVariables,
                statInput.statCtx,
                varsWithoutBinding,
                messageConsumer
            )
            groupSizeListAfterStat = listOf(statData.rowCount())
            resultSeries = statData.variables().associateWith { variable -> statData[variable] }
        } else { // add offset to each group
            val groupMerger = GroupMerger()
            var lastStatGroupEnd = -1
            for (d in splitByGroup(statInput.data, groups)) {
                var statData = applyStat(
                    d,
                    stat,
                    statInput.bindings,
                    statInput.transformByAes,
                    facetVariables,
                    statInput.statCtx,
                    varsWithoutBinding,
                    messageConsumer
                )
                if (statData.isEmpty) {
                    continue
                }
                groupMerger.initOrderSpecs(orderOptions, statData.variables(), statInput.bindings, aggregateOperation)

                val curGroupSizeAfterStat = statData.rowCount()

                // update 'stat group' to avoid collisions as stat is applied independently to each original data group
                if (statData.has(Stats.GROUP)) {
                    val range = statData.range(Stats.GROUP)
                    if (range != null) {
                        val start = lastStatGroupEnd + 1
                        val offset = start - range.lowerEnd.toInt()
                        lastStatGroupEnd = range.upperEnd.toInt() + offset
                        if (offset != 0) {
                            val newG = ArrayList<Double>()
                            for (g in statData.getNumeric(Stats.GROUP)) {
                                newG.add(g!! + offset)
                            }
                            statData = statData.builder().putNumeric(Stats.GROUP, newG).build()
                        }
                    }
                } else {
                    // If stat has ..group.. then groupingVar won't be checked, so no need to update.
                    val groupingVar = groupingContext.optionalGroupingVar
                    if (groupingVar != null) {
                        val size = statData[statData.variables().first()].size
                        val v = d[groupingVar][0]
                        statData = statData.builder().put(groupingVar, List(size) { v }).build()
                    }
                }

                // Add group's data
                groupMerger.addGroup(statData, curGroupSizeAfterStat)
            }
            // Get merged series
            resultSeries = groupMerger.getResultSeries()
            groupSizeListAfterStat = groupMerger.getGroupSizes()
        }

        val dataAfterStat = Builder().run {
            // put results
            for (variable in resultSeries.keys) {
                put(variable, resultSeries[variable]!!)
            }

            // set ordering specifications
            val orderSpecs = orderOptions.map { orderOption ->
                OrderOptionUtil.createOrderSpec(resultSeries.keys, statInput.bindings, orderOption, aggregateOperation)
            }
            addOrderSpecs(orderSpecs)

            // build DataFrame
            build()
        }

        val normalizedData = stat.normalize(dataAfterStat)

        val groupingContextAfterStat = GroupingContext.withOrderedGroups(
            normalizedData,
            groupSizeListAfterStat
        )

        return DataAndGroupingContext(
            normalizedData,
            groupingContextAfterStat
        )
    }

    internal fun findOptionalVariable(data: DataFrame, name: String?): Variable? {
        return if (name.isNullOrEmpty())
            null
        else
            DataFrameUtil.findVariableOrFail(data, name)
    }

    private fun splitByGroup(data: DataFrame, groups: (Int) -> Int): List<DataFrame> {
        return indicesByGroup(data.rowCount(), groups).values.map { indices ->
            data.variables().fold(Builder()) { b, variable ->
                when (data.isNumeric(variable)) {
                    true -> b.putNumeric(variable, pickAtIndices(data.getNumeric(variable), indices))
                    false -> b.putDiscrete(variable, pickAtIndices(data[variable], indices))
                }
            }
        }.map(Builder::build)
    }

    /**
     * Backend-side only
     */
    private fun applyStat(
        data: DataFrame,
        stat: Stat,
        bindings: List<VarBinding>,
        transformByAes: Map<Aes<*>, Transform>,
        facetVariables: List<Variable>,
        statCtx: StatContext,
        varsWithoutBinding: List<String>,
        compMessageConsumer: Consumer<String>
    ): DataFrame {

        var statData = stat.apply(data, statCtx, compMessageConsumer)

        val statVariables = statData.variables()
        if (statVariables.isEmpty()) {
            return statData
        }

        statData = inverseTransformStatData(
            statData,
            stat,
            bindings,
            transformByAes
        )

        val statDataSize = statData.rowCount()

        // generate new series for facet variables
        val inputSeriesForFacetVars: Map<Variable, List<Any?>> = run {
            val facetLevelByFacetVar = facetVariables.associateWith { data[it][0] }
            facetLevelByFacetVar.mapValues { (_, facetLevel) -> List(statDataSize) { facetLevel } }
        }

        // generate new series for input variables
        fun newSerieForVariable(variable: Variable): List<Any?> {
            val value = when (data.isNumeric(variable)) {
                true -> SeriesUtil.mean(data.getNumeric(variable), defaultValue = null)
                false -> SeriesUtil.firstNotNull(data[variable], defaultValue = null)
            }
            return List(statDataSize) { value }
        }

        val newInputSeries = HashMap<Variable, List<Any?>>()
        for (binding in bindings) {
            val variable = binding.variable
            if (variable.isStat || facetVariables.contains(variable)) {
                continue
            }

            val aes = binding.aes
            if (stat.hasDefaultMapping(aes)) {
                val defaultStatVar = stat.getDefaultMapping(aes)
                newInputSeries[variable] = statData.get(defaultStatVar)
            } else {
                // Do not override series obtained via 'default stat var'
                if (!newInputSeries.containsKey(variable)) {
                    newInputSeries[variable] = newSerieForVariable(variable)
                }
            }
        }

        // series for variables without bindings
        for (varName in varsWithoutBinding.filterNot(Stats::isStatVar)) {
            val variable = DataFrameUtil.findVariableOrFail(data, varName)
            if (!newInputSeries.containsKey(variable)) {
                newInputSeries[variable] = newSerieForVariable(variable)
            }
        }

        val b = statData.builder()
        (newInputSeries + inputSeriesForFacetVars).forEach { (variable, serie) ->
            b.put(variable, serie)
        }
        return b.build()
    }

    /**
     * Backend-side only
     */
    private fun inverseTransformStatData(
        statData: DataFrame,
        stat: Stat,
        bindings: List<VarBinding>,
        transformByAes: Map<Aes<*>, Transform>
    ): DataFrame {

        // X,Y scale - always.
        check(transformByAes.containsKey(Aes.X))
        check(transformByAes.containsKey(Aes.Y))

        fun transformForAes(aes: Aes<*>): Transform {
            return when {
                Aes.isPositionalX(aes) -> transformByAes.getValue(Aes.X)
                Aes.isPositionalY(aes) -> transformByAes.getValue(Aes.Y)
                else -> throw IllegalStateException("Positional aes expected but was $aes.")
            }
        }

        val needTransformX = stat.consumes().any { Aes.isPositionalX(it) }
        val needTransformY = stat.consumes().any { Aes.isPositionalY(it) }

        fun needInverseTransform(aes: Aes<*>): Boolean {
            if (Aes.isPositionalX(aes)) return needTransformX
            if (Aes.isPositionalY(aes)) return needTransformY
            return false
        }

        val aesByStatVar: Map<Variable, Aes<*>> = run {
            val aesByStatVarDefault = Aes.values()
                .filter { stat.hasDefaultMapping(it) }.associateBy { stat.getDefaultMapping(it) }

            val aesByStatVarMapped = bindings
                .filterNot { it.variable.isStat }.associate { it.variable to it.aes }

            aesByStatVarDefault + aesByStatVarMapped
        }

        val inverseTransformedSeries = statData.variables()
            .filter {
                aesByStatVar.containsKey(it)
            }.filter {
                val aes = aesByStatVar.getValue(it)
                needInverseTransform(aes)
            }.associateWith {
                val aes = aesByStatVar.getValue(it)
                val transform = transformForAes(aes)
                val statSerie = statData.getNumeric(it)
                transform.applyInverse(statSerie)
            }

        // Replace series in the stat data.
        val builder = statData.builder()
        inverseTransformedSeries.forEach { (variable, serie) ->
            builder.put(variable, serie)
        }
        return builder.build()
    }

    internal fun computeGroups(
        data: DataFrame,
        groupingVariables: List<Variable>,
    ): (Int) -> Int {

        var currentGroups: List<Int>? = null
        for (groupingVariable in groupingVariables) {
            val values = data[groupingVariable]
            val groups = computeGroups(values)
            if (currentGroups == null) {
                currentGroups = groups
                continue
            }

            check(currentGroups.size == groups.size) {
                "Data series used to compute groups must be equal in size (encountered sizes: " +
                        "${currentGroups?.size}, ${groups.size} )"
            }
            val dummies = computeDummyValues(currentGroups, groups)
            currentGroups = computeGroups(dummies)
        }

        return if (currentGroups != null) {
            GroupUtil.wrap(currentGroups)
        } else {
            GroupUtil.SINGLE_GROUP
        }
    }

    private fun computeGroups(values: List<*>): List<Int> {
        val groups = ArrayList<Int>()
        val groupByVal = HashMap<Any?, Int>()
        var count = 0
        for (v in values) {
            if (!groupByVal.containsKey(v)) {
                groupByVal[v] = count++
            }
            groups.add(groupByVal.get(v)!!)
        }
        return groups
    }

    private fun computeDummyValues(list1: List<Int>, list2: List<Int>): List<Int> {
        if (list1.isEmpty()) return emptyList()

        val limit = 1000

        val max = (list1 + list2).maxOrNull()!!
        check(max < limit) { "Too many groups: $max" }
        val dummies = ArrayList<Int>()
        val it1 = list1.iterator()
        val it2 = list2.iterator()
        while (it1.hasNext()) {
            val v1 = it1.next()
            val v2 = it2.next()
            val dummy = v1 * limit + v2
            dummies.add(dummy)
        }
        return dummies
    }

    fun defaultGroupingVariables(
        data: DataFrame,
        bindings: List<VarBinding>,
        pathIdVarName: String?,
    ): List<Variable> {
        val pathIdVar: Variable? = findOptionalVariable(data, pathIdVarName)
        return defaultGroupingVariables(data, bindings) + listOfNotNull(pathIdVar)
    }

    private fun defaultGroupingVariables(
        data: DataFrame,
        bindings: List<VarBinding>,
    ): Iterable<Variable> {
        return bindings
            .filter { isDefaultGroupingVariable(data, it.aes, it.variable) }
            .map { it.variable }
            .distinct()
    }

    private fun isDefaultGroupingVariable(
        data: DataFrame,
        aes: Aes<*>,
        variable: Variable
    ): Boolean {
        // 'origin' discrete vars (but not positional)
        return variable.isOrigin && !(Aes.isPositional(aes) || data.isNumeric(variable))
    }


    class DataAndGroupingContext internal constructor(
        val data: DataFrame,
        val groupingContext: GroupingContext
    )
}
