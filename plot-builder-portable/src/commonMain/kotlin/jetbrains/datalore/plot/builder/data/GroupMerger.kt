/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.DataFrame

internal class GroupMerger {
    private var myGroups = ArrayList<Group>()

    inner class Group(val df: DataFrame, val groupSize: Int)

    fun addGroup(d: DataFrame, groupSize: Int) {
        myGroups.add(Group(d, groupSize))
    }

    fun getResultSeries(): Map<DataFrame.Variable, List<Any?>> {
        val resultSeries = HashMap<DataFrame.Variable, MutableList<Any?>>()
        myGroups.forEach { group ->
            group.df.variables().forEach { variable ->
                resultSeries.getOrPut(variable, ::ArrayList).addAll(group.df[variable])
            }
        }
        return resultSeries
    }

    fun getGroupSizes(): List<Int> {
        return myGroups.map(Group::groupSize)
    }

    fun regroupWithOrder(valuesOrder: Map<DataFrame.Variable, Collection<Any>>) {
        if (valuesOrder.isEmpty()) return

        val orderedGroups = myGroups
            .sortedWith(object : Comparator<Group> {
                override fun compare(a: Group, b: Group): Int {
                    fun compareGroupValue(v1: Any?, v2: Any?): Int {
                        // null value is always greater - will be at the end of the result
                        if (v1 == null && v2 == null) return 0
                        if (v1 == null) return 1
                        if (v2 == null) return -1
                        return compareValues(v1 as Comparable<*>, v2 as Comparable<*>)
                    }
                    valuesOrder.forEach { (v, orderList) ->
                        val v1 = orderList.indexOf(a.df[v].firstOrNull()).takeIf { it >= 0 }
                        val v2 = orderList.indexOf(b.df[v].firstOrNull()).takeIf { it >= 0 }
                        val cmp = compareGroupValue(v1, v2)
                        if (cmp != 0) {
                            return cmp
                        }
                    }
                    return 0
                }
            })

        myGroups = ArrayList(orderedGroups)
    }
}