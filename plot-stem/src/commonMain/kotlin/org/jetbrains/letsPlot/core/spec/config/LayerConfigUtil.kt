/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.spec.Option.Layer.POS
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import org.jetbrains.letsPlot.core.spec.GeomProto
import org.jetbrains.letsPlot.core.spec.Option

internal object LayerConfigUtil {

    fun positionAdjustmentOptions(layerOptions: OptionsAccessor, geomProto: GeomProto): Map<String, Any> {
        val preferredPosOptions: Map<String, Any> = geomProto.preferredPositionAdjustmentOptions(layerOptions)
        val hasOwnPositionOptions = geomProto.hasOwnPositionAdjustmentOptions(layerOptions)
        val specifiedPosOptions: Map<String, Any> = when (val v = layerOptions[POS]) {
            null -> preferredPosOptions
            is Map<*, *> ->
                @Suppress("UNCHECKED_CAST")
                v as Map<String, Any>

            else ->
                mapOf(Option.Meta.NAME to v.toString())
        }

        // Geom's parameters have priority over function parameters
        return when {
            specifiedPosOptions[Option.Meta.NAME] == preferredPosOptions[Option.Meta.NAME] -> {
                // Merge
                if (hasOwnPositionOptions) {
                    specifiedPosOptions + preferredPosOptions
                } else {
                    preferredPosOptions + specifiedPosOptions
                }
            }

            hasOwnPositionOptions -> preferredPosOptions
            else -> specifiedPosOptions
        }
    }

    fun initConstants(layerOptions: OptionsAccessor, consumedAesSet: Set<Aes<*>>): Map<Aes<*>, Any> {
        val result = HashMap<Aes<*>, Any>()
        Option.Mapping.REAL_AES_OPTION_NAMES
            .filter(layerOptions::has)
            .associateWith(Option.Mapping::toAes)
            .filterValues { aes -> aes in consumedAesSet }
            .forEach { (option, aes) ->
                val optionValue = layerOptions.getSafe(option)
                val constantValue = AesOptionConversion.apply(aes, optionValue)
                    ?: throw IllegalArgumentException("Can't convert to '$option' value: $optionValue")
                result[aes] = constantValue
            }
        return result
    }

    fun createBindings(
        data: DataFrame,
        mapping: Map<Aes<*>, Variable>?,
        consumedAesSet: Set<Aes<*>>,
        clientSide: Boolean
    ): List<VarBinding> {

        val result = ArrayList<VarBinding>()
        if (mapping != null && data.rowCount() > 0) {
            val aesSet = HashSet(consumedAesSet)
            aesSet.retainAll(mapping.keys)
            for (aes in aesSet) {
                val variable = mapping.getValue(aes)
                val binding: VarBinding = when {
                    data.has(variable) -> VarBinding(variable, aes)
                    variable.isStat && !clientSide -> VarBinding(variable, aes) // 'stat' is not yet built.
                    else -> throw IllegalArgumentException(
                        data.undefinedVariableErrorMessage(variable.name)
                    )
                }
                result.add(binding)
            }
        }
        return result
    }

}
