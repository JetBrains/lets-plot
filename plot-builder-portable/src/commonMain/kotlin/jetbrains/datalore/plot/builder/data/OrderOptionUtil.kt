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
                require(order == null || (order is Number && order.toInt() in listOf(-1, 1))) {
                    "Unsupported `order` value: $order. Use 1 (ascending) or -1 (descending)."
                }

                return OrderOption(variableName, orderBy, (order as? Number)?.toInt())
            }

            fun OrderOption.mergeWith(other: OrderOption): OrderOption {
                require(variableName == other.variableName) {
                    "Can't merge order options for different variables: '$variableName' and '${other.variableName}'"
                }
                require(byVariable == null || other.byVariable == null || other.byVariable == byVariable) {
                    "Multiple ordering options for the variable '$variableName' with different non-empty 'order_by' fields: '$byVariable' and '${other.byVariable}'"
                }
                require(orderDir == null || other.orderDir == null || other.orderDir == orderDir) {
                    "Multiple ordering options for the variable '$variableName' with different order direction: '$orderDir' and '${other.orderDir}'"
                }
                return OrderOption(
                    variableName,
                    byVariable ?: other.byVariable,
                    orderDir ?: other.orderDir
                )
            }
        }
    }

    fun createOrderSpec(
        variables: Set<DataFrame.Variable>,
        varBindings: List<VarBinding>,
        orderOption: OrderOption,
        aggregateOperation: ((List<Double?>) -> Double?)?
    ): DataFrame.OrderSpec {
        fun getVariableByName(varName: String): DataFrame.Variable {
            return variables.find { it.name == varName }
                ?: error("Undefined variable '$varName' in order options. Full variable list: ${variables.map { "'${it.name}'" }}")
        }

        val variable =
            if (varBindings.find { it.variable.name == orderOption.variableName && it.aes == Aes.X } != null &&
                SamplingUtil.xVar(variables) != null
            ) {
                // Apply ordering to the X variable which is used for sampling
                SamplingUtil.xVar(variables)!!
            } else {
                getVariableByName(orderOption.variableName)
            }

        return DataFrame.OrderSpec(
            variable,
            orderOption.byVariable?.let(::getVariableByName) ?: getVariableByName(orderOption.variableName),
            orderOption.getOrderDir(),
            aggregateOperation.takeIf {
                // Use the aggregation for ordering by the specified 'order_by' variable
                orderOption.byVariable != null && orderOption.byVariable != orderOption.variableName
            }
        )
    }
}