/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.base.Strings.isNullOrEmpty
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Ordering.Companion.natural
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.DataFrame.Builder
import jetbrains.datalore.plot.base.DataFrame.Builder.Companion.emptyFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.data.GroupUtil.indicesByGroup
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.data.SeriesUtil.pickAtIndices

object DataProcessing {

    fun transformOriginals(
        data: DataFrame,
        bindings: List<VarBinding>,
        scaleMap: TypedScaleMap
    ): DataFrame {
        @Suppress("NAME_SHADOWING")
        var data = data
        for (binding in bindings) {
            val variable = binding.variable
            if (variable.isOrigin) {
                checkState(data.has(variable), "Undefined variable $variable")
                data = DataFrameUtil.applyTransform(
                    data,
                    variable,
                    binding.aes,
                    scaleMap[binding.aes]
                )
            }
        }

        return data
    }

    fun buildStatData(
        data: DataFrame,
        stat: Stat,
        bindings: List<VarBinding>,
        scaleMap: TypedScaleMap,
        groupingContext: GroupingContext,
        facets: PlotFacets,
        statCtx: StatContext,
        varsWithoutBinding: List<String>,
        messageConsumer: Consumer<String>
    ): DataAndGroupingContext {
        if (stat === Stats.IDENTITY) {
            return DataAndGroupingContext(emptyFrame(), groupingContext)
        }

        val groups = groupingContext.groupMapper
        val resultSeries = HashMap<Variable, List<Any>>()

        val groupSizeListAfterStat = ArrayList<Int>()

        // if only one group no need to modify
        if (groups === GroupUtil.SINGLE_GROUP) {
            val sd = applyStat(data, stat, bindings, scaleMap, facets, statCtx, varsWithoutBinding, messageConsumer)
            groupSizeListAfterStat.add(sd.rowCount())
            for (variable in sd.variables()) {
                @Suppress("UNCHECKED_CAST")
                val list = sd[variable] as List<Any>
                resultSeries[variable] = list
            }
        } else { // add offset to each group
            var lastStatGroupEnd = -1
            for (d in splitByGroup(data, groups)) {
                var sd = applyStat(d, stat, bindings, scaleMap, facets, statCtx, varsWithoutBinding, messageConsumer)
                if (sd.isEmpty) {
                    continue
                }

                groupSizeListAfterStat.add(sd.rowCount())

                // update 'stat group' to avoid collisions as stat is applied independently to each original data group
                if (sd.has(Stats.GROUP)) {
                    val range = sd.range(Stats.GROUP)
                    if (range != null) {
                        val start = lastStatGroupEnd + 1
                        val offset = start - range.lowerEnd.toInt()
                        lastStatGroupEnd = range.upperEnd.toInt() + offset
                        if (offset != 0) {
                            val newG = ArrayList<Double>()
                            for (g in sd.getNumeric(Stats.GROUP)) {
                                newG.add(g!! + offset)
                            }
                            sd = sd.builder().putNumeric(Stats.GROUP, newG).build()
                        }
                    }
                } else { // if stat has ..group.. then groupingVar won't be checked, so no need to update
                    val groupingVar = groupingContext.optionalGroupingVar
                    if (groupingVar != null) {
                        val size = sd[sd.variables().first()].size
                        val v = d[groupingVar][0]
                        sd = sd.builder().put(groupingVar, List(size) { v }).build()
                    }
                }

                // merge results
                for (variable in sd.variables()) {
                    if (!resultSeries.containsKey(variable)) {
                        resultSeries[variable] = ArrayList()
                    }
                    @Suppress("UNCHECKED_CAST")
                    (resultSeries[variable] as MutableList).addAll(sd[variable] as List<Any>)
                }
            }
        }

        val b = Builder()
        for (variable in resultSeries.keys) {
            b.put(variable, resultSeries[variable]!!)
        }

        val dataAfterStat = b.build()
        val groupingContextAfterStat = GroupingContext.withOrderedGroups(
            dataAfterStat,
            groupSizeListAfterStat
        )

        return DataAndGroupingContext(
            dataAfterStat,
            groupingContextAfterStat
        )
    }

    internal fun findOptionalVariable(data: DataFrame, name: String?): Variable? {
        return if (isNullOrEmpty(name))
            null
        else
            DataFrameUtil.findVariableOrFail(data, name!!)
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
     * Server-side only
     */

    private fun applyStat(
        data: DataFrame,
        stat: Stat,
        bindings: List<VarBinding>,
        scaleMap: TypedScaleMap,
        facets: PlotFacets,
        statCtx: StatContext,
        varsWithoutBinding: List<String>,
        compMessageConsumer: Consumer<String>
    ): DataFrame {

        var statData = stat.apply(data, statCtx, compMessageConsumer)

        val statVariables = statData.variables()
        if (statVariables.isEmpty()) {
            return statData
        }

        // generate new 'input' series to match stat series
        // see: https://ggplot2.tidyverse.org/current/aes_group_order.html
        // "... the group is set to the interaction of all discrete variables in the plot."

        val inverseTransformedStatSeries =
            inverseTransformContinuousStatData(
                statData,
                stat,
                bindings,
                scaleMap
            )

        // generate new series for facet variables

        val statDataSize = statData[statVariables.iterator().next()].size
        val facetVars = HashSet<Variable>()
        for (facetVarName in facets.variables) {
            val facetVar = DataFrameUtil.findVariableOrFail(data, facetVarName)
            facetVars.add(facetVar)
            if (data[facetVar].isNotEmpty()) {
                val facetLevel = data[facetVar][0]
                // generate series for 'facet' variable
                statData = statData
                    .builder()
                    .put(facetVar, List(statDataSize) { facetLevel })
                    .build()
            }
        }

        // generate new series for input variables

        if (bindings.isEmpty()) {
            return statData
        }

        val newInputSeries = HashMap<Variable, List<*>>()

        fun addSeriesForVariable(variable: Variable) {
            val value = when (data.isNumeric(variable)) {
                true -> SeriesUtil.mean(data.getNumeric(variable), defaultValue = null)
                false -> SeriesUtil.firstNotNull(data[variable], defaultValue = null)
            }
            val newInputSerie = List(statDataSize) { value }
            newInputSeries[variable] = newInputSerie
        }

        for (binding in bindings) {
            val variable = binding.variable
            if (variable.isStat || facetVars.contains(variable)) {
                continue
            }

            val aes = binding.aes
            if (stat.hasDefaultMapping(aes)) {
                val defaultStatVar = stat.getDefaultMapping(aes)
                val newInputSerie: List<*> =
                    if (inverseTransformedStatSeries.containsKey(defaultStatVar)) {
                        inverseTransformedStatSeries.getValue(defaultStatVar)
                    } else {
                        val statSerie = statData.getNumeric(defaultStatVar)
                        ScaleUtil.inverseTransform(statSerie, scaleMap[aes])
                    }
                newInputSeries[variable] = newInputSerie
            } else {
                // Do not override series obtained via 'default stat var'
                if (!newInputSeries.containsKey(variable)) {
                    addSeriesForVariable(variable)
                }
            }
        }
        // series for variables without bindings
        for (varName in varsWithoutBinding.filterNot(Stats::isStatVar)) {
            val variable = DataFrameUtil.findVariableOrFail(data, varName)
            if (!newInputSeries.containsKey(variable)) {
                addSeriesForVariable(variable)
            }
        }

        val b = statData.builder()
        for (variable in newInputSeries.keys) {
            b.put(variable, newInputSeries[variable]!!)
        }
        // also update stat series
        for (variable in inverseTransformedStatSeries.keys) {
            b.putNumeric(variable, inverseTransformedStatSeries.getValue(variable))
        }

        return b.build()
    }

    private fun inverseTransformContinuousStatData(
        statData: DataFrame,
        stat: Stat,
        bindings: List<VarBinding>,
        scaleMap: TypedScaleMap
    ): Map<Variable, List<Double?>> {
        // inverse transform stat data with continuous domain.
        val continuousScaleByAes = HashMap<Aes<*>, Scale<*>>()
        val aesByMappedStatVar = HashMap<Variable, Aes<*>>()
        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                val defaultStatVar = stat.getDefaultMapping(aes)
                aesByMappedStatVar[defaultStatVar] = aes
            }
        }

        for (binding in bindings) {
            val aes = binding.aes
            val variable = binding.variable
            if (variable.isStat) {
                aesByMappedStatVar[variable] = aes
                // ignore 'stat' var becaue ..?
                continue
            }
//            else if (stat.hasDefaultMapping(aes)) {
//                val defaultStatVar = stat.getDefaultMapping(aes)
//                aesByMappedStatVar[defaultStatVar] = aes
//            }

            val scale = scaleMap[aes]
            if (scale.isContinuousDomain) {
                continuousScaleByAes[aes] = scale
                if (Aes.isPositionalX(aes) && !continuousScaleByAes.containsKey(Aes.X)) {
                    continuousScaleByAes[Aes.X] = scale
                } else if (Aes.isPositionalY(aes) && !continuousScaleByAes.containsKey(Aes.Y)) {
                    continuousScaleByAes[Aes.Y] = scale
                }
            }
        }

        val inverseTransformedStatSeries = HashMap<Variable, List<Double?>>()
        for (statVar in statData.variables()) {
            if (aesByMappedStatVar.containsKey(statVar)) {
                val aes = aesByMappedStatVar.getValue(statVar)
                var scale: Scale<*>? = continuousScaleByAes[aes]
                if (scale == null) {
                    if (Aes.isPositionalX(aes)) {
                        scale = continuousScaleByAes[Aes.X]
                    } else if (Aes.isPositionalY(aes)) {
                        scale = continuousScaleByAes[Aes.Y]
                    }
                }

                if (scale != null) {
                    val statSerie = statData.getNumeric(statVar)
                    val inverseTransformedStatSerie = ScaleUtil.inverseTransformToContinuousDomain(statSerie, scale)
                    inverseTransformedStatSeries[statVar] = inverseTransformedStatSerie
                }
            }
        }
        return inverseTransformedStatSeries
    }

    internal fun computeGroups(
        data: DataFrame,
        bindings: List<VarBinding>,
        groupingVar: Variable?,
        pathIdVar: Variable?
    ): (Int) -> Int {
        val groupingVariables = getGroupingVariables(data, bindings, groupingVar) + listOfNotNull(pathIdVar)

        var currentGroups: List<Int>? = null
        if (groupingVar != null) {
            currentGroups = computeGroups(data[groupingVar])
        }

        for (`var` in groupingVariables) {
            val values = data[`var`]
            val groups = computeGroups(values)
            if (currentGroups == null) {
                currentGroups = groups
                continue
            }

            checkState(
                currentGroups.size == groups.size,
                "Data series used to compute groups must be equal in size (encountered sizes: " + currentGroups.size + ", " + groups.size + ")"
            )
            val dummies = computeDummyValues(currentGroups, groups)
            currentGroups = computeGroups(dummies)
        }

        return if (currentGroups != null) {
            GroupUtil.wrap(currentGroups)
        } else GroupUtil.SINGLE_GROUP
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

        val max = natural<Int>().max(Iterables.concat(list1, list2))
        checkState(max < limit, "Too many groups: " + max)
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

    private fun getGroupingVariables(
        data: DataFrame,
        bindings: List<VarBinding>,
        explicitGroupingVar: Variable?
    ): Iterable<Variable> {

        // all 'origin' discrete vars (but not positional) + explicitGroupingVar
        val result = LinkedHashSet<Variable>()
        for (binding in bindings) {
            val variable = binding.variable
            if (!result.contains(variable)) {
                if (variable.isOrigin) {
                    if (variable == explicitGroupingVar || isDefaultGroupingVariable(data, binding.aes, variable)) {
                        result.add(variable)
                    }
                }
            }
        }
        return result
    }

    private fun isDefaultGroupingVariable(
        data: DataFrame,
        aes: Aes<*>,
        variable: Variable
    ) = !(Aes.isPositional(aes) || data.isNumeric(variable))

    class DataAndGroupingContext internal constructor(val data: DataFrame, val groupingContext: GroupingContext)

}
