/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.OrderSpec

internal class GroupMerger(
    private val aggregateOperation: ((List<Double?>) -> Double?)?,
) {

    lateinit var orderSpecs: List<OrderSpec>

    private val groups = ArrayList<ComparableGroup>()
    val isEmpty: Boolean get() = groups.isEmpty()

    private var orderedGroupsIntialized: Boolean = false
    private val orderedGroups: List<ComparableGroup> by lazy {
        orderedGroupsIntialized = true
        val sortedById = groups.sortedBy { it.groupId }
        val sortedTwoWays = ArrayList<ComparableGroup>()
        for (group in sortedById) {
            val indexToInsert = findIndexToInsert(group, sortedTwoWays, orderSpecs)
            sortedTwoWays.add(indexToInsert, group)
        }
        sortedTwoWays
    }

    fun getResultSeries(): Map<DataFrame.Variable, List<Any?>> {
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

    fun addGroup(groupId: Int, d: DataFrame, groupSize: Int) {
        check(!orderedGroupsIntialized) { "Can't any longer add a group" }
        val group = ComparableGroup(groupId, d, groupSize)
        groups.add(group)
    }

    private companion object {
        fun findIndexToInsert(
            group: ComparableGroup,
            orderedGroups: List<ComparableGroup>,
            orderSpecs: List<OrderSpec>
        ): Int {
            if (orderSpecs.isEmpty()) {
                return orderedGroups.size
            }
            var index = orderedGroups.binarySearch(group)
            if (index < 0) index = index.inv()
            return index
        }

        fun compareGroupValue(v1: Any?, v2: Any?, dir: Int): Int {
            // null value is always greater - will be at the end of the result
            if (v1 == null && v2 == null) return 0
            if (v1 == null) return 1
            if (v2 == null) return -1
            return compareValues(v1 as Comparable<*>, v2 as Comparable<*>) * dir
        }

        fun getGroupValue(
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


    inner class ComparableGroup(
        val groupId: Int,
        val df: DataFrame,
        val groupSize: Int,
    ) : Comparable<ComparableGroup> {
        override fun compareTo(other: ComparableGroup): Int {
            orderSpecs.forEach { spec ->
                var cmp = compareGroupValue(
                    getGroupValue(df, spec.orderBy, spec.aggregateOperation),
                    getGroupValue(other.df, spec.orderBy, spec.aggregateOperation),
                    spec.direction
                )
                if (cmp == 0) {
                    // ensure the order as in the legend
                    cmp = compareGroupValue(
                        getGroupValue(df, spec.variable),
                        getGroupValue(other.df, spec.variable),
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
}