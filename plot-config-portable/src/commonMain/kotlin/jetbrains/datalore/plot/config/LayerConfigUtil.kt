/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.config.Option.Layer.POS
import jetbrains.datalore.plot.config.Option.Layer.SAMPLING
import jetbrains.datalore.plot.config.aes.AesOptionConversion

internal object LayerConfigUtil {

    fun positionAdjustmentOptions(layerOptions: OptionsAccessor, geomProto: GeomProto): Map<String, Any> {
        val preferredPosOptions: Map<String, Any> = geomProto.preferredPositionAdjustmentOptions(layerOptions)
        val specifiedPosOptions: Map<String, Any> = when (val v = layerOptions[POS]) {
            null -> preferredPosOptions
            is Map<*, *> ->
                @Suppress("UNCHECKED_CAST")
                v as Map<String, Any>
            else ->
                mapOf(Option.Meta.NAME to v.toString())
        }

        return if (specifiedPosOptions[Option.Meta.NAME] == preferredPosOptions[Option.Meta.NAME]) {
            // Merge
            preferredPosOptions + specifiedPosOptions
        } else {
            specifiedPosOptions
        }
    }

    fun initConstants(layerConfig: OptionsAccessor, consumedAesSet: Set<Aes<*>>): Map<Aes<*>, Any> {
        val result = HashMap<Aes<*>, Any>()
        Option.Mapping.REAL_AES_OPTION_NAMES
            .filter(layerConfig::has)
            .associateWith(Option.Mapping::toAes)
            .filterValues { aes -> aes in consumedAesSet }
            .forEach { (option, aes) ->
                val optionValue = layerConfig[option]!!
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
        if (mapping != null) {
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

    fun initSampling(opts: OptionsAccessor, defaultSampling: Sampling): List<Sampling> {
        return if (opts.has(SAMPLING)) {
            SamplingConfig.create(opts[SAMPLING]!!)
        } else listOf(defaultSampling)
    }
}
