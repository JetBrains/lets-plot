/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.findVariableOrFail
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.variables
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Meta

object ConfigUtil {
    fun featureName(options: Map<*, *>): String {
        return options[Meta.NAME].toString()
    }

    internal fun isFeatureList(options: Map<*, *>): Boolean {
        return options.containsKey("feature-list")
    }

    internal fun featuresInFeatureList(options: MutableMap<String, Any>): List<Map<*, *>> {
        val list = OptionsAccessor.over(options).getList("feature-list")

        return list
            .map { o: Any? ->
                val featureOptionsByKind = o as Map<*, *>
                featureOptionsByKind.values.iterator().next() as Map<*, *>
            }
    }


    internal fun createDataFrame(rawData: Any?): DataFrame {
        val varNameMap = asVarNameMap(rawData)
        return updateDataFrame(DataFrame.Builder.emptyFrame(), varNameMap)
    }


    fun join(left: DataFrame, leftKeyVariableNames: List<*>, right: DataFrame, rightKeyVariableNames    : List<*>): DataFrame {
        require(rightKeyVariableNames.size == leftKeyVariableNames.size) {
            "Keys count for merging should be equal, but was ${leftKeyVariableNames.size} and ${rightKeyVariableNames.size}"
        }

        fun computeMultiKeys(dataFrame: DataFrame, keyVarNames: List<*>): List<List<Any?>> {
            val keyVars = keyVarNames.map { keyVarName -> findVariableOrFail(dataFrame, keyVarName as String)}
            return (0 until dataFrame.rowCount()).map { rowIndex -> keyVars.map { dataFrame.get(it)[rowIndex] } }
        }

        val leftMultiKeys = computeMultiKeys(left, leftKeyVariableNames)
        val rightMultiKeys = computeMultiKeys(right, rightKeyVariableNames)

        fun List<*>.containsDuplicates(): Boolean = toSet().size < size
        val restrictRightDuplicates = leftMultiKeys.containsDuplicates() && rightMultiKeys.containsDuplicates()


        val jointMap = HashMap<DataFrame.Variable, MutableList<Any?>>()
        right.variables().forEach { variable -> jointMap[variable] = mutableListOf<Any?>() }
        left.variables().forEach { variable -> jointMap[variable] = mutableListOf<Any?>() }

        // return only first match if left and right contains duplicates to not generate m*n rows
        fun List<*>.indicesOf(obj: Any?): List<Int> = when {
            restrictRightDuplicates -> listOf(indexOf(obj))
            else -> mapIndexed { i, v -> i.takeIf { v == obj } }.filterNotNull()
        }

        val notMatchedRightMultiKeys = rightMultiKeys.toMutableSet()
        leftMultiKeys.forEachIndexed { leftRowIndex, leftMultiKey ->
            rightMultiKeys.indicesOf(leftMultiKey).forEach { rightRowIndex ->
                if (rightRowIndex >= 0) {
                    notMatchedRightMultiKeys.remove(leftMultiKey)
                    right.variables().forEach { jointMap[it]!!.add(right.get(it)[rightRowIndex]) }
                    left.variables().forEach { jointMap[it]!!.add(left.get(it)[leftRowIndex]) }
                }
            }
        }

        notMatchedRightMultiKeys.forEach { notMatchedRightKey ->
            val rightRowIndices = rightMultiKeys.indicesOf(notMatchedRightKey)
            rightRowIndices.forEach { rightRowIndex ->
                right.variables().forEach { jointMap[it]!!.add(right.get(it)[rightRowIndex]) }
                left.variables().forEach { jointMap[it]!!.add(null) }
            }
        }

        return jointMap.entries.fold(DataFrame.Builder()) { b, (variable, values) -> b.put(variable, values)}.build()
    }

    private fun asVarNameMap(data: Any?): Map<String, List<*>> {
        if (data == null) {
            return emptyMap()
        }

        val varNameMap = HashMap<String, List<*>>()
        if (data is Map<*, *>) {
            for (k in data.keys) {
                val v = data[k]
                if (v is List<*>) {
                    varNameMap[k.toString()] = v
                }
            }

        /* Deprecated. Handle such cases on front so series annotation could work.
        } else if (data is List<*>) {
            // check if this is a matrix - all elements are lists of the same size
            var matrix = true
            var rowSize = -1
            for (row in data) {
                if (row is List<*>) {
                    if (rowSize < 0 || row.size == rowSize) {
                        rowSize = row.size
                        continue
                    }
                }
                matrix = false
                break
            }

            if (matrix) {
                val dummyNames = Dummies.dummyNames(data.size)
                for (i in data.indices) {
                    varNameMap[dummyNames[i]] = data[i] as List<*>
                }
            } else {
                // simple data vector
                varNameMap[Dummies.dummyNames(1)[0]] = data
            }
        */
        } else {
            throw IllegalArgumentException("Unsupported data structure: " + data::class.simpleName)
        }

        return varNameMap
    }

    private fun updateDataFrame(df: DataFrame, data: Map<String, List<*>>): DataFrame {
        val dfVars = variables(df)
        val b = df.builder()
        for ((varName, values) in data) {
            val variable = dfVars[varName] ?: DataFrameUtil.createVariable(varName)
            b.put(variable, values)
        }
        return b.build()
    }

    private fun toList(o: Any): List<*> {
        return when (o) {
            is List<*> -> o
            is Number -> listOf(o.toDouble())
            is Iterable<*> -> throw IllegalArgumentException("Can't cast/transform to list: " + o::class.simpleName)
            else -> listOf(o.toString())
        }
    }

    internal fun createAesMapping(
        data: DataFrame,
        mapping: Map<*, *>?,
    ): Map<Aes<*>, DataFrame.Variable> {
        if (mapping == null) {
            return emptyMap()
        }

        val dfVariables = variables(data)

        val result = HashMap<Aes<*>, DataFrame.Variable>()
        val options = Option.Mapping.REAL_AES_OPTION_NAMES
        for (option in options) {
            val value = mapping[option]
            if (value is String) {
                val asDiscreteVarName = DataMetaUtil.asDiscreteName(aes = option, variable = value)
                val variable = when {
                    asDiscreteVarName in dfVariables -> dfVariables.getValue(asDiscreteVarName)
                    value in dfVariables -> dfVariables.getValue(value)
                    else -> DataFrameUtil.createVariable(value)
                }
                val aes = Option.Mapping.toAes(option)
                result[aes] = variable
            }
        }
        return result
    }

    fun toNumericPair(twoValueList: List<*>): DoubleVector {
        var x = 0.0
        var y = 0.0
        val it = twoValueList.iterator()
        if (it.hasNext()) {
            try {
                x = ("" + it.next()).toDouble()
            } catch (ignored: NumberFormatException) {
                // ok
            }

        }
        if (it.hasNext()) {
            try {
                y = ("" + it.next()).toDouble()
            } catch (ignored: NumberFormatException) {
                // ok
            }

        }

        return DoubleVector(x, y)
    }
}
