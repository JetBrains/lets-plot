/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.VarBinding

internal class GroupMerger(
    private val bindings: List<VarBinding>,
    private val orderOptions: List<OrderOptionUtil.OrderOption>,
    private val aggregateOperation: ((List<Double?>) -> Double?)?,
) {

    private val orderedGroups = ArrayList<ComparableGroup>()

    fun getResultSeries(): HashMap<DataFrame.Variable, MutableList<Any?>> {
        val resultSeries = HashMap<DataFrame.Variable, MutableList<Any?>>()
        orderedGroups.forEach { group ->
            group.df.variables().forEach { variable ->
                resultSeries.getOrPut(variable, ::ArrayList).addAll(group.df[variable])
            }
        }
        return resultSeries
    }

    fun getGroupSizes(): List<Int> {
        return orderedGroups.map(ComparableGroup::groupSize)
    }

    fun addGroup(d: DataFrame, groupSize: Int) {
        val orderSpecs = createOrderSpecs(d)
        val group = ComparableGroup(d, groupSize, orderSpecs)
        val indexToInsert = findIndexToInsert(group, orderSpecs)
        orderedGroups.add(indexToInsert, group)
    }

    private fun createOrderSpecs(d: DataFrame): List<DataFrame.OrderSpec> {
        return orderOptions
            .filter { orderOption ->
                // no need to reorder groups by X
                bindings.find { it.variable.name == orderOption.variableName && it.aes == Aes.X } == null
            }
            .map { orderOption ->
                OrderOptionUtil.createOrderSpec(d.variables(), bindings, orderOption, aggregateOperation)
            }
    }

    private fun findIndexToInsert(group: ComparableGroup, orderSpecs: List<DataFrame.OrderSpec>): Int {
        if (orderSpecs.isEmpty()) {
            return orderedGroups.size
        }
        var index = orderedGroups.binarySearch(group)
        if (index < 0) index = index.inv()
        return index
    }

    private class ComparableGroup(
        val df: DataFrame,
        val groupSize: Int,
        val orderSpecs: List<DataFrame.OrderSpec>?,
    ) : Comparable<ComparableGroup> {
        override fun compareTo(other: ComparableGroup): Int {
            orderSpecs?.forEach { spec ->
                var cmp = compareGroupValue(
                    getValue(df, spec.orderBy, spec.aggregateOperation),
                    getValue(other.df, spec.orderBy, spec.aggregateOperation),
                    spec.direction
                )
                if (cmp == 0) {
                    // ensure the order as in the legend
                    cmp = compareGroupValue(
                        getValue(df, spec.variable),
                        getValue(other.df, spec.variable),
                        spec.direction
                    )
                }
                if (cmp != 0) {
                    return cmp
                }
            }
            return 0
        }

        companion object {
            fun compareGroupValue(v1: Any?, v2: Any?, dir: Int): Int {
                // null value is always greater - will be at the end of the result
                if (v1 == null && v2 == null) return 0
                if (v1 == null) return 1
                if (v2 == null) return -1
                return compareValues(v1 as Comparable<*>, v2 as Comparable<*>) * dir
            }

            fun getValue(
                df: DataFrame,
                variable: DataFrame.Variable,
                aggregateOperation: ((List<Double?>) -> Double?)? = null
            ): Any? {
                return if (aggregateOperation != null) {
                    require(df.isNumeric(variable)) { "Can't apply aggregate operation to non-numeric values" }
                    aggregateOperation.invoke(df.getNumeric(variable).requireNoNulls())
                } else {
                    // group has no more than one unique element
                    df[variable].firstOrNull()
                }
            }
        }
    }
}