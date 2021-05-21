/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding

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
    ): DataFrame.Builder.OrderingSpec {
        val varBinding = varBindings.find { it.aes.name == orderOption.aesName }
            ?: error("No variable binding for aes ${orderOption.aesName}")
        val variable = varBinding.variable
        val byVariable = orderOption.byVariable?.let {
            variables.find { it.name == orderOption.byVariable }
                ?: error("No variable with name ${orderOption.byVariable}")
        }
        return DataFrame.Builder.OrderingSpec(variable, byVariable, orderOption.orderDir)
    }

    fun getAdditionalForOrderSpec(
        variables: Set<DataFrame.Variable>,
        varBindings: List<VarBinding>,
        orderSpec: DataFrame.Builder.OrderingSpec
    ): List<DataFrame.Builder.OrderingSpec> {
        val aes = varBindings.find { it.variable == orderSpec.variable }?.aes ?: return emptyList()

        val additionalSpecs = ArrayList<DataFrame.Builder.OrderingSpec>()
        if (aes == Aes.X) {
            if (variables.contains(Stats.X)) {
                additionalSpecs += DataFrame.Builder.OrderingSpec(
                    Stats.X,
                    orderSpec.orderBy ?: orderSpec.variable,
                    orderSpec.direction
                )
            }
            if (variables.contains(TransformVar.X)) {
                additionalSpecs += DataFrame.Builder.OrderingSpec(
                    TransformVar.X,
                    orderSpec.orderBy ?: orderSpec.variable,
                    orderSpec.direction
                )
            }
        }
        return additionalSpecs
    }
}