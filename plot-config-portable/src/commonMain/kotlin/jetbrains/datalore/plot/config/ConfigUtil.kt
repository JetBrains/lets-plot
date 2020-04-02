/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.Dummies
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation

object ConfigUtil {
    fun featureName(options: Map<*, *>): String {
        return options["name"].toString()
    }

    internal fun isFeatureList(options: Map<*, *>): Boolean {
        return options.containsKey("feature-list")
    }

    internal fun featuresInFeatureList(options: MutableMap<*, *>): List<Map<*, *>> {
        val list = OptionsAccessor.over(options).getList("feature-list")

        return list
                .map { o: Any? ->
                    val featureOptionsByKind = o as Map<*, *>
                    featureOptionsByKind.values.iterator().next() as Map<*, *>
                }
    }

    fun getSeriesAnnotation(options: Map<*, *>): Map<String, String> {
        return options
            .getMaps(SeriesAnnotation.TAG)
            ?.associate { it.read(SeriesAnnotation.VARIABLE) as String to it.read(SeriesAnnotation.ANNOTATION) as String }
            ?: emptyMap()
    }

    internal fun createDataFrame(
        rawData: Any?,
        dataMeta: Map<String, String> = emptyMap()
    ): DataFrame {
        val varNameMap = asVarNameMap(rawData)
        return updateDataFrame(DataFrame.Builder.emptyFrame(), varNameMap, dataMeta)
    }

    /**
     * @return All rows from the right table, and the matched rows from the left table
     */
    fun rightJoin(left: DataFrame, leftKey: String, right: DataFrame, rightKey: String): DataFrame {
        val leftMap = DataFrameUtil.toMap(left)
        if (!leftMap.containsKey(leftKey)) {
            throw IllegalArgumentException("Can't join data: left key not found '$leftKey'")
        }
        val rightMap = DataFrameUtil.toMap(right)
        if (!rightMap.containsKey(rightKey)) {
            throw IllegalArgumentException("Can't join data: right key not found '$rightKey'")
        }

        val leftKeyValues = leftMap[leftKey]!!
        val indexByKeyValueLeft = HashMap<Any, Int>()
        var index = 0
        for (keyValue in leftKeyValues) {
            indexByKeyValueLeft[keyValue!!] = index++
        }

        val jointMap = HashMap<String, List<Any?>>()
        for (key in leftMap.keys) {
            jointMap[key] = ArrayList()
        }

        for (key in rightMap.keys) {
            if (leftMap.containsKey(key)) {
                continue
            }

            val values = rightMap[key]!!
            jointMap[key] = values
        }

        for (keyValue in rightMap[rightKey]!!) {
            val leftIndex = indexByKeyValueLeft[keyValue]
            for (key in leftMap.keys) {
                val fillValue = if (leftIndex == null)
                    null
                else
                    leftMap[key]!!.get(leftIndex)

                val list = jointMap[key]
                if (list is ArrayList) {
                    list.add(fillValue)
                } else {
                    throw IllegalStateException("The list should be mutable")
                }
            }
        }

        return createDataFrame(jointMap)
    }

    private fun asVarNameMap(data: Any?): Map<String, List<*>> {
        if (data == null) {
            return emptyMap()
        }

        val varNameMap = HashMap<String, List<*>>()
        if (data is Map<*, *>) {
            val mapData = data as Map<*, *>?
            for (k in mapData!!.keys) {
                val v = mapData[k]
                if (v is List<*>) {
                    varNameMap[k.toString()] = v
                }
            }

        } else if (data is List<*>) {
            val list = data as List<*>?
            // check if this is a matrix - all elements are lists of the same size
            var matrix = true
            var rowSize = -1
            for (row in list!!) {
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
                val dummyNames = Dummies.dummyNames(list.size)
                for (i in list.indices) {
                    varNameMap[dummyNames[i]] = list[i] as List<*>
                }
            } else {
                // simple data vector
                varNameMap[Dummies.dummyNames(1)[0]] = data
            }

        } else {
            throw IllegalArgumentException("Unsupported data structure: " + data::class.simpleName)
        }

        return varNameMap
    }

    private fun updateDataFrame(
        df: DataFrame,
        data: Map<String, List<*>>,
        dataMeta: Map<String, String>
    ): DataFrame {
        val dfVars = DataFrameUtil.variables(df)
        val b = df.builder()
        for ((varName, values) in data) {
            val isDiscrete = dataMeta[varName]?.let(SeriesAnnotation.DISCRETE::equals)
            val variable = dfVars[varName] ?: DataFrameUtil.createVariable(varName)

            @Suppress("UNCHECKED_CAST")
            when (isDiscrete) {
                true -> b.putDiscrete(variable, values as List<Double?>)
                false -> b.putNumeric(variable, values as List<Double?>)
                null -> b.put(variable, values)
            }
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

    internal fun createAesMapping(data: DataFrame, mapping: Map<*, *>?): Map<Aes<*>, DataFrame.Variable> {
        if (mapping == null) {
            return emptyMap()
        }

        val dfVariables = DataFrameUtil.variables(data)

        val result = HashMap<Aes<*>, DataFrame.Variable>()
        val options = Option.Mapping.REAL_AES_OPTION_NAMES
        for (option in options) {
            val value = mapping[option]
            if (value is String) {
                val variable: DataFrame.Variable
                if (dfVariables.containsKey(value)) {
                    variable = dfVariables[value]!!
                } else {
                    variable = DataFrameUtil.createVariable(value)
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
