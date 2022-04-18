/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.VarBinding

internal object YOrientationUtil {
    fun flipDataFrame(data: DataFrame): DataFrame {
        // ToDo
        return data
    }

    fun flipVarBinding(bindings: List<VarBinding>): List<VarBinding> {
        // ToDo
        return bindings
    }

    fun <T> flipAesKeys(map: Map<Aes<*>, T>): Map<Aes<*>, T> {
        // ToDo
        return map
    }
}