/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil
import jetbrains.datalore.plot.builder.VarBinding

object YOrientationUtil {
    fun flipDataFrame(data: DataFrame): DataFrame {
        val positionalTransformVars = data.variables()
            .filter { it.isTransform }
            .associateBy { TransformVar.toAes(it) }
            .filterKeys { Aes.isPositionalXY(it) }
            .values

        var flippedData: DataFrame = data
        for (positionalTransformVar in positionalTransformVars) {
            val aes = TransformVar.toAes(positionalTransformVar)
            val flippedAes = YOrientationBaseUtil.flipAes(aes)
            val toVar = TransformVar.forAes(flippedAes)
            flippedData = flippedData.replaceVariable(positionalTransformVar, toVar)
        }

        return flippedData
    }

    fun flipVarBinding(bindings: List<VarBinding>): List<VarBinding> {
        return bindings.map {
            if (Aes.isPositionalXY(it.aes)) {
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