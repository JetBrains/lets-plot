package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.POS
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.SAMPLING
import jetbrains.datalore.visualization.plot.gog.config.aes.AesOptionConversion
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import jetbrains.datalore.visualization.plot.gog.core.data.Sampling
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PosProvider
import jetbrains.datalore.visualization.plot.gog.plot.assemble.TypedScaleProviderMap
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProviderHelper
import java.util.*

internal object LayerConfigUtil {

    fun initPositionAdjustments(opts: OptionsAccessor, defaultPos: PosProvider): PosProvider {
        if (opts.has(POS)) {
            val posConfig = PosConfig.create(opts[POS]!!)
            return posConfig.pos
        }

        return defaultPos
    }

    fun initConstants(layerConfig: OptionsAccessor): Map<Aes<*>, *> {
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
            scaleProviders: TypedScaleProviderMap, consumedAesSet: Set<Aes<*>>): List<VarBinding> {

        val result = ArrayList<VarBinding>()
        if (mapping != null) {
            val aesSet = HashSet(consumedAesSet)
            aesSet.retainAll(mapping.keys)
            for (aes in aesSet) {
                val variable = mapping[aes]!!
                val scaleProvider = ScaleProviderHelper.getOrCreateDefault(aes, scaleProviders)
                val binding: VarBinding
                if (data.has(variable)) {
                    binding = VarBinding(variable, aes, scaleProvider.createScale(data, variable))
                } else if (variable.isStat) {
                    binding = VarBinding.deferred(variable, aes, scaleProvider)
                } else {
                    throw IllegalArgumentException("Undefined variable: '" + variable.name + "'. Variables in data frame: " + data.variables())
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
