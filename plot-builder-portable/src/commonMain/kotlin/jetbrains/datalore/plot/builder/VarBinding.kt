/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame

class VarBinding(
    val variable: DataFrame.Variable,
    val aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>
) {
    override fun toString() = "VarBinding{variable=${variable}, aes=${aes}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as VarBinding

        if (variable != other.variable) return false
        if (aes != other.aes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variable.hashCode()
        result = 31 * result + aes.hashCode()
        return result
    }
}
