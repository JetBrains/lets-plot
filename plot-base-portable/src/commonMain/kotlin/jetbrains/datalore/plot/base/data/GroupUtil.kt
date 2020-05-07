/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.data

object GroupUtil {
    internal val SINGLE_GROUP = { _: Int -> 0 }

    fun wrap(l: List<Number?>): (Int) -> Int {
        return { index ->
            if (index > 0 && index < l.size)
                l[index]!!.toInt()
            else
                0
        }
    }

    fun wrap(groupByPointIndex: Map<Int, Int>): (Int) -> Int {
        return { groupByPointIndex[it]!! }
    }

    fun indicesByGroup(dataLength: Int, groups: (Int) -> Int): Map<Int, List<Int>> {
        val indicesByGroup = LinkedHashMap<Int, MutableList<Int>>()
        for (i in 0 until dataLength) {
            val group = groups(i)
            if (!indicesByGroup.containsKey(group)) {
                indicesByGroup[group] = ArrayList()
            }
            indicesByGroup[group]!!.add(i)
        }

        return indicesByGroup
    }
}
