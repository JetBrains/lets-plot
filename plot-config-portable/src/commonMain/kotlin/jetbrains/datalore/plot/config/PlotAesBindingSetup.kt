/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.VarBinding

internal class PlotAesBindingSetup(
    val mappedAesSet: Set<Aes<*>>,
    val xAesSet: Set<Aes<*>>,
    val yAesSet: Set<Aes<*>>,
    val varBindings: List<VarBinding>,
    val dataByVarBinding: Map<VarBinding, DataFrame>,
    val variablesByMappedAes: Map<Aes<*>, List<DataFrame.Variable>>,

    ) {
    fun isXAxis(aes: Aes<*>): Boolean {
        check(!(aes in xAesSet && aes in yAesSet)) { "'$aes': couldn't determine which scale, X or Y, to apply." }
        return aes in xAesSet
    }

    fun isYAxis(aes: Aes<*>): Boolean {
        check(!(aes in xAesSet && aes in yAesSet)) { "'$aes': couldn't determine which scale, X or Y, to apply." }
        return aes in yAesSet
    }

}