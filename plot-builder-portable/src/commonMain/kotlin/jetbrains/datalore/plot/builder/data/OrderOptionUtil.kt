/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.sampling.method.SamplingUtil

object OrderOptionUtil {
    class OrderOption internal constructor(
        val variableName: String,
        val byVariable: String?,
        private val orderDir: Int?
    ) {
        fun getOrderDir(): Int = orderDir ?: -1 // descending by default

        companion object {
            fun create(
                variableName: String,
                orderBy: String?,
                order: Any?
            ): OrderOption? {
                if (orderBy == null && order == null) {
                    return null
                }
                require(order == null || order is Number) {
                    error("Unsupported `order` value: $order. Use 1 (ascending) or -1 (descending).")
                }

                return OrderOption(variableName, orderBy, (order as? Number)?.toInt())
            }

            fun OrderOption.mergeWith(other: OrderOption): OrderOption {
                if (variableName != other.variableName) {
                    error("Can't merge order options for different variables: '$variableName' and '${other.variableName}' ")
                }
                val newByVariable = if (byVariable != null) {
                    require(other.byVariable == null || other.byVariable == byVariable) {
                        "Multiple ordering options for the variable '$variableName' with different non-empty 'order_by' fields: '$byVariable' and '${other.byVariable}'"
                    }
                    byVariable
                } else {
                    other.byVariable
                }
                val newOrderDir = if (orderDir != null) {
                    require(other.orderDir == null || other.orderDir == orderDir) {
                        "Multiple ordering options for the variable '$variableName' with different order direction: '$orderDir' and '${other.orderDir}'"
                    }
                    orderDir
                } else {
                    other.orderDir
                }
                return OrderOption(variableName, newByVariable, newOrderDir)
            }
        }
    }

    fun createOrderingSpec(
        variables: Set<DataFrame.Variable>,
        varBindings: List<VarBinding>,
        orderOption: OrderOption
    ): DataFrame.OrderingSpec {
        fun getVariableByName(varName: String): DataFrame.Variable {
            return variables.find { it.name == varName }
                ?: error("Undefined variable '$varName' in order options. Full variable list: ${variables.map { "'${it.name}'" }}")
        }

        var variable = getVariableByName(orderOption.variableName)
        var byVariable = orderOption.byVariable?.let { getVariableByName(it) }

        val xBinding = varBindings.find { it.variable == variable && it.aes == Aes.X }
        if (xBinding != null) {
            val xVar = SamplingUtil.xVar(variables)
            if (xVar != null) {
                variable = xVar
                byVariable = byVariable ?: xBinding.variable
            }
        }
        return DataFrame.OrderingSpec(variable, byVariable, orderOption.getOrderDir())
    }
}