/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.core.plot.builder.data.OrderOptionUtil
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.AES
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.ANNOTATION
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.AS_DISCRETE
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.LABEL
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.ORDER
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.ORDER_BY
import org.jetbrains.letsPlot.core.spec.Option.Meta.MappingAnnotation.PARAMETERS
import org.jetbrains.letsPlot.core.spec.Option.Meta.SeriesAnnotation
import org.jetbrains.letsPlot.core.spec.Option.Meta.SeriesAnnotation.COLUMN
import org.jetbrains.letsPlot.core.spec.Option.Meta.SeriesAnnotation.FACTOR_LEVELS
import org.jetbrains.letsPlot.core.spec.Option.Scale

object DataMetaUtil {

    internal fun asDiscreteName(aes: String, variable: String): String {
        return "$aes.$variable"
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

    fun getOrderOptions(options: Map<*, *>, commonMappings: Map<*, *>, isClientSide: Boolean): List<OrderOptionUtil.OrderOption> {
        return getMappingAnnotationsSpec(options, AS_DISCRETE)
            .associate { it.getString(AES)!! to it.getMap(PARAMETERS) }
            .mapNotNull { (aesName, parameters) ->
                check(aesName in commonMappings) {
                    "Aes '$aesName' not found in mappings: $commonMappings"
                }
                val variableName = (commonMappings[aesName] as String).let {
                    if (isClientSide) asDiscreteName(aesName, it) else it
                }
                OrderOptionUtil.OrderOption.create(
                    variableName,
                    parameters?.getString(ORDER_BY),
                    parameters?.read(ORDER)
                )
            }
    }

    // Series Annotations

    fun getDateTimeColumns(options: Map<*, *>): Set<String> {
        return options
            .getMaps(SeriesAnnotation.TAG)
            ?.associate { it.getString(COLUMN)!! to it.read(SeriesAnnotation.TYPE) }
            ?.filterValues(SeriesAnnotation.DateTime.DATE_TIME::equals)
            ?.keys
            ?: emptySet()
    }

    fun getCategoricalVariables(dataMeta: Map<*, *>): Set<String> {
        return getFactorLevelsByVariable(dataMeta).keys
    }

    fun getFactorLevelsByVariable(dataMeta: Map<*, *>): Map<String, List<Any>> {
        return (dataMeta
            .getMaps(SeriesAnnotation.TAG)
            ?.associate { it.getString(COLUMN)!! to it.getList(FACTOR_LEVELS) }
            ?.filterNotNullValues()
            ?.mapValues { (_, factorLevels) -> factorLevels.map { v -> v as Any } }
            ?: emptyMap())
    }

    fun getFactorLevelsOrderByVariable(dataMeta: Map<*, *>): Map<String, Int> {
        return (dataMeta
            .getMaps(SeriesAnnotation.TAG)
            ?.associate { it.getString(COLUMN)!! to it.getNumber(SeriesAnnotation.ORDER) }
            ?.filterNotNullValues()
            ?.mapValues { (_, order) -> order.toInt() }
            ?: emptyMap())
    }

    fun updateFactorLevelsByVariable(
        dataMeta: Map<String, Any>,
        levelsByVariable: Map<String, List<Any>>
    ): Map<String, Any> {
        val seriesAnnotations = dataMeta.getMaps(SeriesAnnotation.TAG) ?: listOf()

        val varsWithAnnotation = seriesAnnotations.map { it.getString(COLUMN)!! }.toSet()
        val varsToAddAnnotation = levelsByVariable.keys - varsWithAnnotation

        val updatedSeriesAnnotations = ArrayList(
            // Existing annotationa to keep untouched.
            seriesAnnotations.filter { !levelsByVariable.containsKey(it.getString(COLUMN)) }
        )

        // Modify existing annotations
        val seriesAnnotationsToUpdate = seriesAnnotations.filter { levelsByVariable.containsKey(it.getString(COLUMN)) }
        val seriesAnnotationsUpdated = seriesAnnotationsToUpdate.map {
            val variable = it.getString(COLUMN)!!
            val factorLevels = levelsByVariable.getValue(variable)

            // Just add factor levels to annotation
            HashMap(it).apply {
                this[FACTOR_LEVELS] = factorLevels
            }
        }
        updatedSeriesAnnotations.addAll(seriesAnnotationsUpdated)

        // Add new annotations
        updatedSeriesAnnotations.addAll(
            levelsByVariable
                .filter { (variable, _) -> variable in varsToAddAnnotation }
                .map { (variable, levels) ->
                    mapOf(
                        COLUMN to variable,
                        FACTOR_LEVELS to levels
                    )
                }

        )

        return dataMeta + mapOf(
            SeriesAnnotation.TAG to updatedSeriesAnnotations
        )
    }
}
