/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.DISCRETE
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.TAG
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

/*
 Copy plot discrete mappings and annotations into layers
 For example:
 - plot mapping: color -> __discrete_cyl
 - plot data: empty
 - plot data meta: cyl -> discrete
 - layer mapping: empty
 - layer data: mpg with column `cyl`
 - layer data meta: empty
 Layer will use plot mapping and will fail trying to find `__discrete_cyl` in layers DataFrame without meta.
 To fix this we will copy color -> __discrete_cyl mapping and data meta into layer.
 [updateDiscreteMapping] function will later copy variable to the layers data.

 After change spec will look like this:
 - plot mapping: color -> __discrete_cyl
 - plot data: empty
 - plot data meta: cyl -> discrete
 - layer mapping: color -> __discrete_cyl
 - layer data: mpg with column `cyl` and `__discrete_cyl`
 - layer data meta: cyl -> discrete
 */
class DiscreteVariableFromAnnotationChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {

        // Copy DATA_META from plot to all other layers
        spec.sections(DATA_META, TAG)?.let { plotDataMeta ->
            spec.sections(Plot.LAYERS)!!.forEach { layer ->
                layer.provideSections(DATA_META, TAG).addAll(plotDataMeta)
                layer.provideSection(MAPPING).putAll(getDiscreteMappings(spec))
            }
        }

        updateDiscreteMapping(
            section = spec,
            sharedData = emptyMap<Any, Any>()
        )

        spec.sections(Plot.LAYERS)!!
            .forEach { layer ->
                updateDiscreteMapping(
                    section = layer,
                    sharedData = spec.section(DATA) ?: emptyMap<Any, Any>()
                )
            }
    }


    /*
     - adds new mapping for discrete variable to a section (plot/layer): color -> __discrete_cyl
     - copies discrete variable to sections data: __discrete_cyl: [...]
     */
    private fun updateDiscreteMapping(
        section: Map<*, *>,
        sharedData: Map<*, *>
    ) {
        val data = section.section(DATA)?.asMutable() ?: mutableMapOf()
        getDiscreteMappings(section)
            .forEach { (aes, discreteVariable) ->
                when {
                    discreteVariable in data -> data
                    discreteVariable in sharedData -> sharedData
                    else -> null
                }?.let { varSource ->
                    val encodedVariable = encodeName(discreteVariable)
                    data[encodedVariable] = varSource[discreteVariable]!!
                    section.write(MAPPING, aes) { encodedVariable }
                }
        }

        // section hasn't any data - put a new one with discrete variables.
        if (!section.has(DATA) && data.isNotEmpty()) {
            section.write(DATA) { data }
        }
    }

    private fun getDiscreteMappings(section: Map<*, *>): Map<String, String> {
        val discreteVars = section
            .section(DATA_META)
            ?.let(ConfigUtil::getSeriesAnnotation)
            ?.filter { (_, annotation) -> annotation == DISCRETE}
            ?.map { (varName, _) -> varName }
            ?.toSet()
            ?: emptySet()

        return section
            .section(MAPPING)
            ?.typed<String, String>()
            ?.filter { (_, variable) -> variable in discreteVars }
            ?: emptyMap()
    }

    companion object {

        internal fun specSelector(): SpecSelector {
            return SpecSelector.of()
        }

        private val discrete = "@as_discrete@"
        fun encodeName(variable: String) = "$discrete$variable"
        fun decodeName(variable: String) = if (variable.startsWith(discrete)) { variable.removePrefix(discrete) } else { null }
    }
}
