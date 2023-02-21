/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.VarBinding

internal class GroupMerger {
    private var myOrderSpecs: List<DataFrame.OrderSpec>? = null
    private val myOrderedGroups = ArrayList<Group>()

    fun initOrderSpecs(
        orderOptions: List<OrderOptionUtil.OrderOption>,
        variables: Set<DataFrame.Variable>,
        bindings: List<VarBinding>,
        aggregateOperation: ((List<Double?>) -> Double?)?
    ) {
        if (myOrderSpecs != null) return
        myOrderSpecs = orderOptions
            .filter { orderOption ->
                // no need to reorder groups by X
                bindings.find { it.variable.name == orderOption.variableName && it.aes == Aes.X } == null
            }
            .map { OrderOptionUtil.createOrderSpec(variables, bindings, it, aggregateOperation) }
    }

    fun getResultSeries(): Map<DataFrame.Variable, List<Any?>> {
        val resultSeries = HashMap<DataFrame.Variable, MutableList<Any?>>()
        myOrderedGroups.forEach { group ->
            group.df.variables().forEach { variable ->
                resultSeries.getOrPut(variable, ::ArrayList).addAll(group.df[variable])
            }
        }
        return resultSeries
    }

    fun getGroupSizes(): List<Int> {
        return myOrderedGroups.map(Group::groupSize)
    }

    inner class Group(
        val df: DataFrame,
        val groupSize: Int
    ) : Comparable<Group> {
        override fun compareTo(other: Group): Int {
            fun compareGroupValue(v1: Any?, v2: Any?, dir: Int): Int {
                // null value is always greater - will be at the end of the result
                if (v1 == null && v2 == null ) return 0
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

            myOrderSpecs?.forEach { spec ->
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
    }

    fun addGroup(d: DataFrame, groupSize: Int) {
        val group = Group(d, groupSize)
        val indexToInsert = findIndexToInsert(group)
        myOrderedGroups.add(indexToInsert, group)
    }

    private fun findIndexToInsert(group: Group): Int {
        if (myOrderSpecs.isNullOrEmpty()) {
            return myOrderedGroups.size
        }
        var index = myOrderedGroups.binarySearch(group)
        if (index < 0) index = index.inv()
        return index
    }
}