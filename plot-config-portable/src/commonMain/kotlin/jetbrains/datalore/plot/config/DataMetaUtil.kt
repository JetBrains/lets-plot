/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.AES
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.ANNOTATION
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.AS_DISCRETE
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.LABEL
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.ORDER
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.ORDER_BY
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.PARAMETERS
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation
import jetbrains.datalore.plot.config.Option.Scale

object DataMetaUtil {
    private const val prefix = "@as_discrete@"

    internal fun isDiscrete(variable: String) = variable.startsWith(prefix)

    public fun toDiscrete(variable: String): String {
        require(!isDiscrete(variable)) { "toDiscrete() - variable already encoded: $variable" }
        return "$prefix$variable"
    }

    internal fun fromDiscrete(variable: String): String {
        require(isDiscrete(variable)) { "fromDiscrete() - variable is not encoded: $variable" }
        return variable.removePrefix(prefix)
    }

    private fun getMappingAnnotationsSpec(options: Map<*, *>, annotation: String): List<Map<*, *>> {
        return options
            .getMap(Option.Meta.DATA_META)
            ?.getMaps(MappingAnnotation.TAG)
            ?.filter { it.read(ANNOTATION) == annotation }
            ?: emptyList()
    }

    /**
    @returns Set<aes> of discrete aes
     */
    fun getAsDiscreteAesSet(options: Map<*, *>): Set<String> {
        return options
            .getMaps(MappingAnnotation.TAG)
            ?.associate { it.read(AES) as String to it.read(ANNOTATION)!! }
            ?.filterValues(AS_DISCRETE::equals)
            ?.keys
            ?: emptySet()
    }

    fun createScaleSpecs(plotOptions: Map<String, Any>): List<MutableMap<String, Any?>> {
        val plotDiscreteAnnotations = getMappingAnnotationsSpec(plotOptions, AS_DISCRETE)

        val layersDiscreteAnnotations = plotOptions
            .getMaps(Option.Plot.LAYERS)
            ?.map { layerOptions -> getMappingAnnotationsSpec(layerOptions, AS_DISCRETE) }
            ?.flatten()
            ?: emptyList()

        return (plotDiscreteAnnotations + layersDiscreteAnnotations)
            .groupBy({ it.read(AES)!! }) { it.read(PARAMETERS, LABEL) } // {aes: [labels]}
            .mapValues { (_, labels) -> labels.findLast { it != null } } // {aes: last_not_null_label}
            .map { (aes, label) ->
                mutableMapOf(
                    Scale.AES to aes,
                    Scale.DISCRETE_DOMAIN to true,
                    Scale.NAME to label
                )
            }
    }

    fun getOrderOptions(options: Map<*, *>, commonMappings: Map<*, *>): List<OrderOptionUtil.OrderOption> {
        return getMappingAnnotationsSpec(options, AS_DISCRETE)
            .associate { it.getString(AES)!! to it.getMap(PARAMETERS) }
            .mapNotNull { (aesName, parameters) ->
                check(aesName in commonMappings)
                val variableName = commonMappings[aesName] as String
                OrderOptionUtil.OrderOption.create(
                    variableName,
                    parameters?.getString(ORDER_BY),
                    parameters?.read(ORDER)
                )
            }
    }

    fun List<OrderOptionUtil.OrderOption>.inheritToNonDiscrete(mappings: Map<String, String>): List<OrderOptionUtil.OrderOption> {
        // non-discrete mappings should inherit settings from the as_discrete
        return this + mappings.values.toSet()
            .filterNot(::isDiscrete)
            .mapNotNull { varName ->
                val orderOptionForVar = this
                    .filter { isDiscrete(it.variableName) }
                    .find { fromDiscrete(it.variableName) == varName }
                    ?: return@mapNotNull null

                OrderOptionUtil.OrderOption.create(
                    varName,
                    orderBy = orderOptionForVar.byVariable.takeIf { it != orderOptionForVar.variableName },
                    orderOptionForVar.getOrderDir()
                )
            }
    }

    // Series Annotations

    fun getDateTimeColumns(options: Map<*, *>): Set<String> {
        return options
            .getMaps(SeriesAnnotation.TAG)
            ?.associate { it.getString(SeriesAnnotation.COLUMN)!! to it.read(SeriesAnnotation.TYPE) }
            ?.filterValues(SeriesAnnotation.DateTime.DATE_TIME::equals)
            ?.keys
            ?: emptySet()
    }

    fun getFactorLevelsByDateTimeColumns(dataMeta: Map<*, *>): Map<String, List<Any>> {
        return (dataMeta
            .getMaps(SeriesAnnotation.TAG)
            ?.associate { it.getString(SeriesAnnotation.COLUMN)!! to it.getList(SeriesAnnotation.FACTOR_LEVELS) }
            ?.filterValues { list -> list?.isNotEmpty() ?: false }
            ?.mapValues { (_, list) -> list!!.map { v -> v as Any } }
            ?: emptyMap())
    }
}
