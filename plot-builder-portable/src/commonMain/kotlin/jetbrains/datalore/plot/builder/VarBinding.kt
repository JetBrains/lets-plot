/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.scale.ScaleProvider

open class VarBinding(
    val variable: DataFrame.Variable,
    val aes: Aes<*>,
    open val scale: Scale<*>?
) {

    open val isDeferred: Boolean
        get() = false

    // ToDo: remove this method because the scale is only created in the client config and
    // 'stat' var must be present (stat vars are added on server side)
    open fun bindDeferred(data: DataFrame): VarBinding {
        throw IllegalStateException("Not a deferred var binding")
    }

    override fun toString() = "VarBinding{variable=${variable}, aes=${aes}, scale=$scale, deferred=$isDeferred}"

    companion object {
        fun deferred(variable: DataFrame.Variable, aes: Aes<*>, scaleProvider: ScaleProvider<*>): VarBinding {
            return object : VarBinding(variable, aes, null) {
                override val scale: Scale<*>
                    get() = throw IllegalStateException("Scale not defined for deferred var binding")

                override val isDeferred: Boolean
                    get() = true

                override fun bindDeferred(data: DataFrame): VarBinding {
                    // ToDo: remove this method because the scale is only created in the client config and
                    // 'stat' var must be present (stat vars are added on server side)
                    val scale = scaleProvider.createScale(data, variable)
                    return VarBinding(variable, aes, scale)
                }
            }
        }
    }
}
