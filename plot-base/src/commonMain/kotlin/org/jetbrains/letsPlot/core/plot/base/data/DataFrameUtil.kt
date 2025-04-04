/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.data

import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Ordering
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import kotlin.jvm.JvmOverloads

object DataFrameUtil {
    fun transformVarFor(aes: Aes<*>): DataFrame.Variable {
        return TransformVar.forAes(aes)
    }

    fun applyTransform(data: DataFrame, variable: DataFrame.Variable, aes: Aes<*>, transform: Transform): DataFrame {
        val transformVar = transformVarFor(aes)
        return applyTransform(data, variable, transformVar, transform)
    }

    fun applyTransform(
        data: DataFrame,
        variable: DataFrame.Variable,
        transformVar: DataFrame.Variable,
        transform: Transform
    ): DataFrame {
        val transformed = try {
            ScaleUtil.applyTransform(data[variable], transform)
        } catch (e: IllegalStateException) {
            throw IllegalStateException(
                "Can't transform '${variable.name}' with ${transform::class.simpleName} : ${e.message}"
            )
        }
        return data.builder()
            .putNumeric(transformVar, transformed)
            .build()
    }

    fun hasVariable(data: DataFrame, varName: String): Boolean {
        for (v in data.variables()) {
            if (varName == v.name) {
                return true
            }
        }
        return false
    }

    fun findVariableOrFail(data: DataFrame, varName: String): DataFrame.Variable {
        for (variable in data.variables()) {
            if (varName == variable.name) {
                return variable
            }
        }
        throw IllegalArgumentException(
            data.undefinedVariableErrorMessage(varName)
        )
    }

    fun findVariableOrNull(data: DataFrame, varName: String): DataFrame.Variable? {
        if (!hasVariable(data, varName)) return null
        return findVariableOrFail(data, varName)
    }

    fun isNumeric(data: DataFrame, varName: String): Boolean {
        return data.isNumeric(findVariableOrFail(data, varName))
    }

    fun sortedCopy(variables: Iterable<DataFrame.Variable>): List<DataFrame.Variable> {
        val ordering = Ordering.from(Comparator<DataFrame.Variable> { o1, o2 -> o1.name.compareTo(o2.name) })
        return ordering.sortedCopy(variables)
    }

    fun variables(df: DataFrame): Map<String, DataFrame.Variable> {
        return df.variables().associateBy(DataFrame.Variable::name)
    }

    fun appendReplace(df0: DataFrame, df1: DataFrame): DataFrame {
        fun DataFrame.Builder.put(destVars: Collection<DataFrame.Variable>, df: DataFrame) = apply {
            destVars.forEach { destVar ->
                val srcVar = findVariableOrFail(df, destVar.name)
                when (df.isNumeric(srcVar)) {
                    true -> putNumeric(destVar, df.getNumeric(srcVar))
                    false -> putDiscrete(destVar, df[srcVar])
                }
            }
        }

        return DataFrame.Builder()
            .put(df0.variables().filter { it.name !in variables(df1) }, df0) // df0 - df1, keep vars from df0
            .put(df0.variables().filter { it.name in variables(df1) }, df1)  // df0 & df1, keep vars from df0
            .put(df1.variables().filter { it.name !in variables(df0) }, df1) // df1 - df0, new vars from df1
            .build()
    }

    fun toMap(df: DataFrame): Map<String, List<*>> {
        val result = HashMap<String, List<*>>()
        val variables = df.variables()
        for (variable in variables) {
            result[variable.name] = df[variable]
        }
        return result
    }

    fun fromMap(map: Map<*, *>): DataFrame {
        val frameBuilder = DataFrame.Builder()
        for ((key, value) in map) {
            require(key is String) {
                "Map to data-frame: key expected a String but was " + key!!::class.simpleName + " : " + key
            }
            require(value is List<*>) {
                "Map to data-frame: value expected a List but was " + value!!::class.simpleName + " : " + value
            }
            frameBuilder.put(createVariable(key), value)
        }
        return frameBuilder.build()
    }

    @JvmOverloads
    fun createVariable(name: String, label: String = name): DataFrame.Variable {
        return when {
            TransformVar.isTransformVar(name) -> TransformVar[name]
            Stats.isStatVar(name) -> Stats.statVar(name)
            Dummies.isDummyVar(name) -> Dummies.newDummy(name)
            else -> DataFrame.Variable(name, DataFrame.Variable.Source.ORIGIN, label)
        }
    }

    fun getSummaryText(df: DataFrame): String {
        val sb = StringBuilder()
        for (variable in df.variables()) {
            sb.append(variable.toSummaryString())
                .append(" numeric: " + df.isNumeric(variable))
                .append(" size: " + df[variable].size)
                .append('\n')
        }
        return sb.toString()
    }

    fun removeAllExcept(df: DataFrame, keepNames: Set<String>): DataFrame {
        val b = df.builder()
        for (variable in df.variables()) {
            if (!keepNames.contains(variable.name)) {
                b.remove(variable)
            }
        }
        return b.build()
    }

    fun concat(dataframes: List<DataFrame>, outer: Boolean = true): DataFrame {
        require(dataframes.isNotEmpty()) { "Dataframes list should not be empty" }
        val variables = dataframes
            .map { it.variables().toSet() }
            .reduce { acc, set ->
                if (outer) {
                    acc.union(set)
                } else {
                    acc.intersect(set)
                }
            }
        val builder = DataFrame.Builder()
        variables.forEach { variable ->
            val values = dataframes.map { df ->
                if (df.has(variable)) {
                    df[variable]
                } else {
                    List(df.rowCount()) { null }
                }
            }.flatten()
            builder.put(variable, values)
        }
        return builder.build()
    }
}
