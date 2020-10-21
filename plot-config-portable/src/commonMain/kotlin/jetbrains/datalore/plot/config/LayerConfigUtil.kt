/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.plot.config.Option.Layer.POS
import jetbrains.datalore.plot.config.Option.Layer.SAMPLING
import jetbrains.datalore.plot.config.aes.AesOptionConversion

internal object LayerConfigUtil {

    fun initPositionAdjustments(opts: OptionsAccessor, defaultPos: PosProvider): PosProvider {
        if (opts.has(POS)) {
            val posConfig = PosConfig.create(opts[POS]!!)
            return posConfig.pos
        }

        return defaultPos
    }

    fun initConstants(layerConfig: OptionsAccessor): Map<Aes<*>, Any> {
        val result = HashMap<Aes<*>, Any>()
        for (option in Option.Mapping.REAL_AES_OPTION_NAMES) {
            val optionValue = layerConfig[option]
            if (optionValue != null) {
                val aes = Option.Mapping.toAes(option)
                val variable = AesOptionConversion.apply(aes, optionValue)
                result[aes] = variable!!
            }
        }

        return result
    }

    fun createBindings(
        data: DataFrame, mapping: Map<Aes<*>, Variable>?,
        scaleProviders: TypedScaleProviderMap, consumedAesSet: Set<Aes<*>>
    ): List<VarBinding> {

        val result = ArrayList<VarBinding>()
        if (mapping != null) {
            val aesSet = HashSet(consumedAesSet)
            aesSet.retainAll(mapping.keys)
            for (aes in aesSet) {
                val variable = mapping[aes]!!
                val scaleProvider = ScaleProviderHelper.getOrCreateDefault(aes, scaleProviders)
                val binding: VarBinding = when {
                    data.has(variable) -> VarBinding(
                        variable,
                        aes,
                        scaleProvider.createScale(data, variable)
                    )
                    variable.isStat -> VarBinding.deferred(variable, aes, scaleProvider)
                    else -> throw IllegalArgumentException("Undefined variable: '" + variable.name + "'. Variables in data frame: " + data.variables())
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
