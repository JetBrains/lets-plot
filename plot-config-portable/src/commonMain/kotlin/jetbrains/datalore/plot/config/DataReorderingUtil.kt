/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.min

object DataReorderingUtil {
    class OrderOption internal constructor(
        val aesName: String,
        val byVariable: String?,
        val orderDir: Int
    ) {
        companion object {
            fun create(
                aesName: String,
                orderBy: String?,
                order: Any?
            ): OrderOption? {
                if (orderBy == null && order == null) {
                    return null
                }
                val orderDir = when (order) {
                    null -> 1
                    is Number -> order.toInt()
                    else -> throw IllegalArgumentException(
                        "Unsupported `order` value: $order. Use 1 (ascending) or -1 (descending)."
                    )
                }
                return OrderOption(aesName, orderBy, orderDir)
            }
        }
    }

    fun reorderDataFrame(
        dataFrame: DataFrame,
        varBindings: List<VarBinding>,
        orderOptions: List<OrderOption>
    ): DataFrame {
        return reorderDataFrame(
            dataFrame,
            orderOptions.mapNotNull { orderOption -> OrderingSpec.create(dataFrame, varBindings, orderOption) }
        )
    }

    private class OrderingSpec(
        val variable: DataFrame.Variable,
        val orderBy: DataFrame.Variable,
        val direction: Int
    ) {
        companion object {
            fun create(
                dataFrame: DataFrame,
                varBindings: List<VarBinding>,
                orderOption: OrderOption
            ): OrderingSpec? {
                val varBinding = varBindings.find { it.aes.name == orderOption.aesName } ?: return null
                val variable = varBinding.variable
                val byVariable =
                    orderOption.byVariable?.let { DataFrameUtil.findVariableOrFail(dataFrame, it) } ?: variable
                return OrderingSpec(variable, byVariable, orderOption.orderDir)
            }
        }
    }

    private fun <T> getComparator(comparable: (T) -> Comparable<*>?, orderDir: Int): Comparator<T> {
        return if (orderDir > 0) {
            compareBy(comparable)
        } else {
            compareByDescending(comparable)
        }
    }

    private fun reorderDataFrame(
        dataFrame: DataFrame,
        orderingSpecs: List<OrderingSpec>
    ): DataFrame {

        class DataToOrder(val values: List<*>, val orderDir: Int, val isNumeric: Boolean)

        fun prepareDataToOrder(
            variable: DataFrame.Variable,
            byVariable: DataFrame.Variable,
            orderDir: Int
        ): DataToOrder {
            val values = dataFrame[variable]
            val byValues = dataFrame[byVariable].toMutableList()
            if (byVariable == Stats.COUNT) {
                val sumByValues = mutableMapOf<Any?, ArrayList<Double>>()
                values.mapIndexed { index, value ->
                    val byValue = byValues[index] as Double
                    sumByValues.getOrPut(value, ::ArrayList).add(byValue)
                }
                val sumByValue: Map<Any?, Double> =
                    sumByValues.map { (key, values) -> key to SeriesUtil.sum(values) }.toMap()
                values.mapIndexed { index, value ->
                    byValues[index] = sumByValue[value]
                }
            }
            return DataToOrder(byValues, orderDir, dataFrame.isNumeric(byVariable))
        }

        val orderDataList = mutableListOf<DataToOrder>()
        val prevOrderedVars = mutableListOf<DataFrame.Variable>()
        orderingSpecs.forEach { orderingSpec ->
            orderDataList += prepareDataToOrder(
                orderingSpec.variable,
                orderingSpec.orderBy,
                orderingSpec.direction
            )
            prevOrderedVars.add(orderingSpec.variable)
        }

        if (orderDataList.isEmpty()) {
            return dataFrame
        }

        // Build comparator
        var comparator: Comparator<Pair<List<Any?>, Int>>? = null
        orderDataList.forEachIndexed { column, orderData ->
            val comparable: (Pair<List<Any?>, Int>) -> Comparable<*>? = if (orderData.isNumeric) {
                { (it.first[column] as Number).toDouble() }
            } else {
                { it.first[column].toString() }
            }

            val curComparator = getComparator(comparable, orderData.orderDir)
            comparator = if (comparator == null) {
                curComparator
            } else {
                comparator!!.then(curComparator)
            }
        }

        val dataTableToOrder: List<Pair<List<Any?>, Int>> =
            orderDataList.first().values.mapIndexed { column, _ ->
                orderDataList.map { it.values[column] } to column
            }
        val orderIndices = dataTableToOrder.sortedWith(comparator!!).map(Pair<List<Any?>, Int>::second)
        return dataFrame.selectIndices(orderIndices)
    }

    fun distinctOrderedValues(
        dataFrame: DataFrame,
        variable: DataFrame.Variable,
        byVariable: DataFrame.Variable,
        orderDir: Int
    ): Collection<Any> {
        if (variable == byVariable) {
            val comparable: (Any?) -> Comparable<*>? = {
                if (dataFrame.isNumeric(variable)) { (it as Number).toDouble() } else { it.toString() }
            }
            return dataFrame.distinctValues(variable).sortedWith(getComparator(comparable, orderDir))
        }

        val valueToByValues = LinkedHashMap<Any?, Any?>()
        dataFrame.get(variable).forEachIndexed { index, value ->
            val prevByValue = valueToByValues[value]
            val byValue = dataFrame.get(byVariable)[index]
            val newValue = when {
                prevByValue == null -> byValue
                byVariable == Stats.COUNT -> min(prevByValue as Double, byValue as Double)
                else -> prevByValue
            }
            valueToByValues.put(value, newValue)
        }

        val pairComparable: (Pair<Any?, Any?>) -> Comparable<*>? = {
            if (dataFrame.isNumeric(byVariable)) {
                (it.second as Number).toDouble()
            } else {
                it.second.toString()
            }
        }
        return valueToByValues.toList()
            .sortedWith(getComparator(pairComparable, orderDir))
            .mapNotNull(Pair<Any?, Any?>::first)
    }
}