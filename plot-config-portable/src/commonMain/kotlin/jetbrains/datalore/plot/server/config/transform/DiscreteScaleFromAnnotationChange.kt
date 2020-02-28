/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.ANNOTATION
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.VARIABLE
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.Scale
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class DiscreteScaleFromAnnotationChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val annotationScales = spec
            .sections(Plot.LAYERS)!!
            .filter { it.has(DATA_META, SeriesAnnotation.TAG) && it.has(Layer.MAPPING) }
            .flatMap(::scalesFromAnnotation)

        if (annotationScales.isNotEmpty()) {
            spec.provideSections(Plot.SCALES).asMutable().addAll(annotationScales)
        }
    }

    companion object {
        private fun scalesFromAnnotation(layer: Map<*, *>): List<Map<*, *>> {
            val mapping = layer.section(Layer.MAPPING)!!.entries.associateBy({ it.value }, { it.key as String })

            return layer.sections(DATA_META, SeriesAnnotation.TAG)!!
                .filter { it.read(ANNOTATION) == SeriesAnnotation.DISCRETE }
                .mapNotNull { it.read(VARIABLE).run(mapping::get) }
                .map { aes ->
                    mutableMapOf<String, Any>(
                        Scale.AES to aes,
                        Scale.DISCRETE_DOMAIN to true
                    )
                }
        }

        internal fun specSelector(): SpecSelector {
            return SpecSelector.of()
        }
    }
}
