/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.data.DataProcessing.findOptionalVariable

class GroupingContext(
    private val data: DataFrame,
    bindings: List<VarBinding>,
    groupingVarName: String?,
    pathIdVarName: String?,
    private val expectMultiple: Boolean,
    private val groupSizeList: List<Int>? = null
) {

    private val varBindings: List<VarBinding> = ArrayList(bindings)
    internal val optionalGroupingVar: Variable? = findOptionalVariable(data, groupingVarName)
    private val pathIdVar: Variable? = findOptionalVariable(data, pathIdVarName)

    private var _groupMapper: ((Int) -> Int)? = null

    val groupMapper: (Int) -> Int
        get() = { index ->
            if (_groupMapper == null) {
                _groupMapper = computeGroups()
            }
            _groupMapper!!(index)
        }

    private fun computeGroups(): (Int) -> Int {
        if (data.has(Stats.GROUP)) {
            val list = data.getNumeric(Stats.GROUP)
            return GroupUtil.wrap(list)
        } else if (groupSizeList != null) {
            if (groupSizeList.size == data.rowCount()) {
                return GroupUtil.SINGLE_GROUP
            } else {
                val groupByPointIndex =
                    toIndexMap(groupSizeList)
                return GroupUtil.wrap(groupByPointIndex)
            }
        } else if (expectMultiple) {
            return DataProcessing.computeGroups(
                data,
                varBindings,
                optionalGroupingVar,
                pathIdVar
            )
        }
        return GroupUtil.SINGLE_GROUP
    }

    companion object {
        internal fun withOrderedGroups(data: DataFrame, groupSizeList: List<Int>): GroupingContext {
            return GroupingContext(
                data,
                emptyList(), null, null, false,
                groupSizeList = ArrayList(groupSizeList)
            )
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
}
