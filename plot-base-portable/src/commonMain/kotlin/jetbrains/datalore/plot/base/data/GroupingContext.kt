/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.data

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.VarBinding
import jetbrains.datalore.plot.base.data.DataProcessing.findOptionalVariable

class GroupingContext(
    private val myData: DataFrame,
    bindings: List<VarBinding>,
    groupingVarName: String?,
    private val myExpectMultiple: Boolean
) {

    private val myBindings: List<VarBinding>
    internal val optionalGroupingVar: Variable?

    private var myGroupSizeList: List<Int>? = null
    private var myGroupMapper: ((Int) -> Int)? = null

    //myGroupMapper = DataProcessing.computeGroups(myData, myBindings, myOptionalGroupingVar, myExpectMultiple);
    val groupMapper: (Int) -> Int
        get() = { index ->
            if (myGroupMapper == null) {
                myGroupMapper = computeGroups()
            }
            myGroupMapper!!(index)
        }

    init {
        myBindings = ArrayList(bindings)
        optionalGroupingVar = findOptionalVariable(myData, groupingVarName)
    }

    private fun computeGroups(): (Int) -> Int {
        if (myData.has(Stats.GROUP)) {
            val list = myData.getNumeric(Stats.GROUP)
            return GroupUtil.wrap(list)
        } else if (myGroupSizeList != null) {
            if (myGroupSizeList!!.size == myData.rowCount()) {
                return GroupUtil.SINGLE_GROUP
            } else {
                val groupByPointIndex =
                    toIndexMap(
                        myGroupSizeList!!
                    )
                return GroupUtil.wrap(groupByPointIndex)
            }
        } else if (myExpectMultiple) {
            return DataProcessing.computeGroups(
                myData,
                myBindings,
                optionalGroupingVar
            )
        }
        return GroupUtil.SINGLE_GROUP
    }

    companion object {
        internal fun withOrderedGroups(data: DataFrame, groupSizeList: List<Int>): GroupingContext {
            val groupingContext =
                GroupingContext(data, emptyList(), null, false)
            groupingContext.myGroupSizeList = ArrayList(groupSizeList)
            return groupingContext
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
