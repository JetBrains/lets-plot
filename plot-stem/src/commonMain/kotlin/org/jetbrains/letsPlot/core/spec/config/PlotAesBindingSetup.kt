/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.VarBinding

internal class PlotAesBindingSetup(
    private val varBindings: List<VarBinding>,
//    val dataByVarBinding: Map<VarBinding, DataFrame>,
    val dataByVarBinding: List<Pair<VarBinding, DataFrame>>,
    val variablesByMappedAes: Map<Aes<*>, List<DataFrame.Variable>>,

    ) {
    fun mappedAesWithoutStatPositional(): Set<Aes<*>> {
        return varBindings.filterNot { it.variable.isStat && Aes.isPositionalXY(it.aes) }
            .map { it.aes }.toSet()
    }

    //    fun dataByVarBindingWithoutStatPositional(): Map<VarBinding, DataFrame> {
    fun dataByVarBindingWithoutStatPositional(): List<Pair<VarBinding, DataFrame>> {
        return dataByVarBinding.filterNot { (binding, _) ->
            binding.variable.isStat && Aes.isPositionalXY(binding.aes)
        }
    }
}