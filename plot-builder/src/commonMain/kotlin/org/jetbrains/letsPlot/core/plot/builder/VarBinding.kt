/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame

class VarBinding(
    val variable: DataFrame.Variable,
    val aes: Aes<*>
) {
    override fun toString() = "VarBinding{variable=${variable}, aes=${aes}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as VarBinding

        // ToDo: Issue: "source" variables are different in different dataframes.
        //  STAT variables are all the same.
        //  Generally, we can't use VarBinding as a key in a hashmap.
        //  As result, PlotConfigUtil.associateVarBindingsWithData() fails when building Map<VarBinding, DataFrame>
        //  because several (..count.. -> color) bindings become 1 entry in the map.
        //
        //  See commit: https://github.com/JetBrains/lets-plot/commit/e492dac808ec4fc12be13337f782f358044f19ec#diff-c6bd11010baad2b31ea05c0a3482f76ebb8f6bac1e3d8303cb6ed3ad28cb04adR16
        //
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
