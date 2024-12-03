/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.spec.GeomProto
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Layer.POS
import org.jetbrains.letsPlot.core.spec.PosProto
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion

internal object LayerConfigUtil {

    fun positionAdjustmentOptions(layerOptions: OptionsAccessor, geomProto: GeomProto): Map<String, Any> {
        val preferredPosOptions: Map<String, Any> = geomProto.preferredPositionAdjustmentOptions(layerOptions)
        val hasOwnPositionOptions = geomProto.hasOwnPositionAdjustmentOptions(layerOptions)
        val specifiedPosOptions: Map<String, Any>? = layerOptions[POS]?.let { v ->
            when (v) {
                is Map<*, *> ->
                    @Suppress("UNCHECKED_CAST")
                    v as Map<String, Any>
                else ->
                    mapOf(Option.Meta.NAME to v.toString())
            }
        }

        return when {
            specifiedPosOptions == null -> preferredPosOptions
            hasOwnPositionOptions -> mapOf(
                Option.Meta.NAME to PosProto.COMPOSITION,
                Option.Pos.Composition.FIRST to specifiedPosOptions,
                Option.Pos.Composition.SECOND to preferredPosOptions
            )
            specifiedPosOptions[Option.Meta.NAME] == preferredPosOptions[Option.Meta.NAME] -> preferredPosOptions + specifiedPosOptions
            else -> specifiedPosOptions
        }
    }

    fun initConstants(
        layerOptions: OptionsAccessor,
        consumedAesSet: Set<Aes<*>>,
        aopConversion: AesOptionConversion
    ): Map<Aes<*>, Any> {
        val result = HashMap<Aes<*>, Any>()
        Option.Mapping.REAL_AES_OPTION_NAMES
            .filter(layerOptions::has)
            .associateWith(Option.Mapping::toAes)
            .filterValues { aes -> aes in consumedAesSet }
            .forEach { (option, aes) ->
                val optionValue = layerOptions.getSafe(option)
                val constantValue = aopConversion.apply(aes, optionValue)
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
