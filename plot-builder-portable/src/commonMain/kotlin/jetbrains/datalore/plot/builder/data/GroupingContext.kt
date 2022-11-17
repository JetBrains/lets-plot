/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.data.DataProcessing.findOptionalVariable

class GroupingContext constructor(
    private val data: DataFrame,
    defaultGroupingVariables: List<Variable>,
    explicitGroupingVarName: String?,
    private val expectMultiple: Boolean,
    private val groupSizeList: List<Int>? = null
) {

    internal val optionalGroupingVar: Variable? = findOptionalVariable(data, explicitGroupingVarName)
    private val groupingVariables: List<Variable> = when (optionalGroupingVar) {
        null -> defaultGroupingVariables
        else -> {
            // The explicit grouping var was 1-st in list before so we just keep this invariant.
            (linkedSetOf(optionalGroupingVar) + defaultGroupingVariables).toList()
        }
    }

    private var _groupMapper: ((Int) -> Int)? = null

    val groupMapper: (Int) -> Int
        get() {
            if (_groupMapper == null) {
                _groupMapper = computeGroups()
            }
            return _groupMapper!!
        }

    private fun computeGroups(): (Int) -> Int {
        if (data.isEmpty || data.rowCount() == 0) return GroupUtil.SINGLE_GROUP
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
                groupingVariables
            )
        }
        return GroupUtil.SINGLE_GROUP
    }

    companion object {
        internal fun withOrderedGroups(data: DataFrame, groupSizeList: List<Int>): GroupingContext {
            val groupingVariables = DataProcessing.defaultGroupingVariables(
                data,
                bindings = emptyList(),
                pathIdVarName = null
            )
            return GroupingContext(
                data,
                groupingVariables,
                explicitGroupingVarName = null,
                expectMultiple = false,
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
                        if (variable == explicitGroupingVar || isDefaultGroupingVariable(
                                data,
                                binding.aes,
                                variable
                            )
                        ) {
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
    }
}
