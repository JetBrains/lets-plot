/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.util.YOrientationBaseUtil
import org.jetbrains.letsPlot.core.plot.builder.VarBinding

object YOrientationUtil {
    fun flipDataFrame(data: DataFrame): DataFrame {
        val positionalTransformVars = data.variables()
            .filter { it.isTransform }
            .associateBy { TransformVar.toAes(it) }
            .filterKeys { org.jetbrains.letsPlot.core.plot.base.Aes.isPositionalXY(it) }
            .values


        // Clean target data builder.
        var toDataBuilder: DataFrame.Builder = data.builder()
        for (transformVar in positionalTransformVars) {
            toDataBuilder.remove(transformVar)
        }

        // Update positional transform vars.
        for (transformVar in positionalTransformVars) {
            val aes = TransformVar.toAes(transformVar)
            val flippedAes = YOrientationBaseUtil.flipAes(aes)
            val toVar = TransformVar.forAes(flippedAes)
            val serie = data.getNumeric(transformVar)
            toDataBuilder.putNumeric(toVar, serie)
        }

        return toDataBuilder.build()
    }

    fun flipVarBinding(bindings: List<VarBinding>): List<VarBinding> {
        return bindings.map {
            if (org.jetbrains.letsPlot.core.plot.base.Aes.isPositionalXY(it.aes)) {
                val flippedAes = YOrientationBaseUtil.flipAes(it.aes)
                VarBinding(
                    it.variable,
                    flippedAes
                )
            } else {
                it
            }
        }
    }
}