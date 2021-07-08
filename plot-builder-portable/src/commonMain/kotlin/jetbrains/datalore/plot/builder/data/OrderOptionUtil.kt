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
        val orderDir: Int
    ) {
        companion object {
            fun create(
                variableName: String,
                orderBy: String?,
                order: Any?
            ): OrderOption? {
                if (orderBy == null && order == null) {
                    return null
                }
                val orderDir = when (order) {
                    null -> -1 // descending by default
                    is Number -> order.toInt()
                    else -> throw IllegalArgumentException(
                        "Unsupported `order` value: $order. Use 1 (ascending) or -1 (descending)."
                    )
                }
                return OrderOption(variableName, orderBy, orderDir)
            }
        }
    }

    fun createOrderingSpec(
        variables: Set<DataFrame.Variable>,
        varBindings: List<VarBinding>,
        orderOption: OrderOption
    ): DataFrame.OrderingSpec {
        fun getVariableByName(varName: String): DataFrame.Variable {
            return  variables.find { it.name == varName }
                ?: error("Undefined variable '$varName' in order options. Full variable list: ${ variables.map { "'${it.name}'" } }")
        }

        var variable = getVariableByName(orderOption.variableName)
        var byVariable = orderOption.byVariable?.let { getVariableByName(it) }

        val bindingsForVariable = varBindings.filter { it.variable == variable }
        require(bindingsForVariable.size <= 1) { "Multiple bindings to one variable which used in ordering options: ${orderOption.variableName}" }
        val varBinding = bindingsForVariable.firstOrNull()
        if (varBinding?.aes == Aes.X) {
            val xVar = SamplingUtil.xVar(variables)
            if (xVar != null) {
                variable = xVar
                byVariable = byVariable ?: varBinding.variable
            }
        }
        return DataFrame.OrderingSpec(variable, byVariable, orderOption.orderDir)
    }
}