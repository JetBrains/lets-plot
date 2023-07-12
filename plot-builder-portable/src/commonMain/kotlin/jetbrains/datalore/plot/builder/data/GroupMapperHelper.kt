/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.stat.Stats

internal object GroupMapperHelper {
    val SINGLE_GROUP = { _: Int -> 0 }

    fun wrap(l: List<Number>): (Int) -> Int {
        return { index ->
            if (index >= 0 && index < l.size) {
                l[index].toInt()
            } else {
                throw IllegalStateException("Can't determin 'group' for data index $index. Expected range: [0, ${l.size}]")
            }
        }
    }

    fun wrap(groupByPointIndex: Map<Int, Int>): (Int) -> Int {
        return { groupByPointIndex.getValue(it) }
    }

    fun firstOptionGroupMapperOrNull(data: DataFrame): ((Int) -> Int)? {
        return if (data.rowCount() == 0) {
            SINGLE_GROUP
        } else if (data.has(Stats.GROUP)) {
            val list = data.getNumeric(Stats.GROUP).map {
                checkNotNull(it) { "Value of ${Stats.GROUP} must be not null." }
            }
            wrap(list)
        } else {
            null
        }
    }

    fun createGroupMapperByGroupSizes(data: DataFrame, groupSizeList: List<Int>): (Int) -> Int {
        return firstOptionGroupMapperOrNull(data)
            ?: if (groupSizeList.size == data.rowCount()) {
                SINGLE_GROUP
            } else {
                val groupByPointIndex = toIndexMap(groupSizeList)
                wrap(groupByPointIndex)
            }
    }

    private fun toIndexMap(groupSizeList: List<Int>): Map<Int, Int> {
        val result = HashMap<Int, Int>()
        var currentGroup = 0
        var currentGroupIndexOffset = 0
        for (groupSize in groupSizeList) {
            for (i in 0 until groupSize) {
                result[currentGroupIndexOffset + i] = currentGroup
            }
            currentGroup++
            currentGroupIndexOffset += groupSize
        }
        return result
    }
}