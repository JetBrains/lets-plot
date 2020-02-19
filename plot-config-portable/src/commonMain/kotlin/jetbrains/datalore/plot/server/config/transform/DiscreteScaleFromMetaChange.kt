/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.Mapping.toAes
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.CATEGORY
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.NAME
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.Scale
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class DiscreteScaleFromMetaChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val factorScales = spec
            .sections(Plot.LAYERS)!!
            .filter { it.has(DATA_META, SeriesAnnotation.TAG) && it.has(Layer.MAPPING) }
            .flatMap(::factorScalesForLayer)

        if (factorScales.isNotEmpty()) {
            spec.provideSections(Plot.SCALES).asMutable().addAll(factorScales)
        }
    }

    companion object {
        private fun factorScalesForLayer(layer: Map<*, *>): List<Map<*, *>> {
            val mapping = layer.section(Layer.MAPPING)!!.entries.associateBy({ it.value }, { it.key as String })

            return layer.sections(DATA_META, SeriesAnnotation.TAG)!!
                .map { varMeta -> Pair(varMeta.read(NAME), varMeta.read(CATEGORY))}
                .filter { (varName, _) -> varName in mapping }
                .mapNotNull { (varName, category) ->
                    val aesOption = mapping.getValue(varName)
                    when (toAes(aesOption)) {
                        Aes.X, Aes.Y -> mutableMapOf<String, Any>()
                        Aes.COLOR, Aes.FILL -> mutableMapOf<String, Any>(Scale.SCALE_MAPPER_KIND to "color_hue")
                        else -> null
                    }?.let { scaleSpec ->
                        scaleSpec[Scale.AES] = aesOption
                        scaleSpec[Scale.DISCRETE_DOMAIN] = category == "discrete"
                        scaleSpec
                    }
                }
        }

        internal fun specSelector(): SpecSelector {
            return SpecSelector.of()
        }
    }
}
