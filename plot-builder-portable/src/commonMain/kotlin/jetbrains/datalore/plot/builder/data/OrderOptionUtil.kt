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
                    null -> -1 // descending by default
                    is Number -> order.toInt()
                    else -> throw IllegalArgumentException(
                        "Unsupported `order` value: $order. Use 1 (ascending) or -1 (descending)."
                    )
                }
                return OrderOption(aesName, orderBy, orderDir)
            }
        }
    }

    fun createOrderingSpec(
        variables: Set<DataFrame.Variable>,
        varBindings: List<VarBinding>,
        orderOption: OrderOption
    ): DataFrame.OrderingSpec {
        val varBinding = varBindings.find { it.aes.name == orderOption.aesName }
            ?: error("No variable binding for aes ${orderOption.aesName}")
        var variable = varBinding.variable
        var byVariable = orderOption.byVariable?.let {
            variables.find { it.name == orderOption.byVariable }
                ?: error("No variable with name ${orderOption.byVariable}")
        }
        if (varBinding.aes == Aes.X) {
            val xVar = SamplingUtil.xVar(variables)
            if (xVar != null) {
                variable = xVar
                byVariable = byVariable ?: varBinding.variable
            }
        }
        return DataFrame.OrderingSpec(variable, byVariable, orderOption.orderDir)
    }
}