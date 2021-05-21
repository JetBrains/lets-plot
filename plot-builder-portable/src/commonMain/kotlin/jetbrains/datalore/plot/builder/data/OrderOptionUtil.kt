/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

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
}