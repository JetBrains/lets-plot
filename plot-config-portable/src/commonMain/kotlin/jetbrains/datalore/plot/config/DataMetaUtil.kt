/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.createVariable
import jetbrains.datalore.plot.base.data.DataFrameUtil.findVariableOrFail
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

    private fun isDiscrete(variable: String) = variable.startsWith(prefix)

    public fun toDiscrete(variable: String): String {
        require(!isDiscrete(variable)) { "toDiscrete() - variable already encoded: $variable" }
        return "$prefix$variable"
    }

    private fun fromDiscrete(variable: String): String {
        require(isDiscrete(variable)) { "fromDiscrete() - variable is not encoded: $variable" }
        return variable.removePrefix(prefix)
    }

    private fun Map<*, *>.getMappingAnnotationsSpec(annotation: String): List<Map<*, *>> {
        return this
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
        val plotDiscreteAnnotations = plotOptions.getMappingAnnotationsSpec(AS_DISCRETE)

        val layersDiscreteAnnotations = plotOptions
            .getMaps(Option.Plot.LAYERS)
            ?.map { layerOptions -> layerOptions.getMappingAnnotationsSpec(AS_DISCRETE) }
            ?.flatten()
            ?: emptyList()

        return (plotDiscreteAnnotations + layersDiscreteAnnotations)
            .groupBy ({ it.read(AES)!! }) { it.read(PARAMETERS, LABEL) } // {aes: [labels]}
            .mapValues { (_, labels) -> labels.findLast { it != null } } // {aes: last_not_null_label}
            .map { (aes, label) ->
                mutableMapOf(
                    Scale.AES to aes,
                    Scale.DISCRETE_DOMAIN to true,
                    Scale.NAME to label
                )
        }
    }

    /**
     * returns mappings and DataFrame extended with auto-generated discrete mappings and variables
     */
    fun createDataFrame(
        options: OptionsAccessor,
        commonData: DataFrame,
        commonDiscreteAes: Set<String>,
        commonMappings: Map<*, *>,
        isClientSide: Boolean
    ): Pair<Map<*, *>, DataFrame> {
        val ownData = ConfigUtil.createDataFrame(options.get(Option.PlotBase.DATA))
        val ownMappings = options.getMap(Option.PlotBase.MAPPING)

        if (isClientSide) {
            return Pair(
                // no new discrete mappings, all job was done on server side
                ownMappings,
                // re-insert existing variables as discrete
                DataFrameUtil.toMap(ownData)
                    .filter { (varName, _) -> isDiscrete(varName) }
                    .entries
                    .fold(DataFrame.Builder(ownData)) { acc, (varName, values) ->
                        val variable = findVariableOrFail(ownData, varName)
                        // re-insert as discrete
                        acc.remove(variable)
                        acc.putDiscrete(variable, values)
                    }
                    .build()
            )

        }

        // server side

        // own names not yet encoded, i.e. 'cyl'
        val ownDiscreteMappings = run {
            val ownDiscreteAes = getAsDiscreteAesSet(options.getMap(Option.Meta.DATA_META))
            return@run ownMappings.filter { (aes, _) -> aes in ownDiscreteAes }
        }

        // Original (not encoded) discrete var names from both common and own mappings.
        val combinedDiscreteVars = run {
            // common names already encoded by PlotConfig, i.e. '@as_discrete@cyl'. Restore original name.
            val commonDiscreteVars = commonMappings.filterKeys { it in commonDiscreteAes }.variables().map(::fromDiscrete)

            val ownSimpleVars = ownMappings.variables() - ownDiscreteMappings.variables()

            // minus own non-discrete mappings (simple layer var overrides discrete plot var)
            return@run ownDiscreteMappings.variables() + commonDiscreteVars - ownSimpleVars
        }

        val combinedDfVars = DataFrameUtil.toMap(commonData) + DataFrameUtil.toMap(ownData)

        return Pair(
            ownMappings + ownDiscreteMappings.mapValues { (_, varName) ->
                require(varName is String)
                toDiscrete(varName)
            },
            combinedDfVars
                .filter { (dfVarName, _) -> dfVarName in combinedDiscreteVars }
                .mapKeys { (dfVarName, _) -> createVariable(toDiscrete(dfVarName)) }
                .entries
                .fold(DataFrame.Builder(ownData)) { acc, (dfVar, values) -> acc.putDiscrete(dfVar, values) }
                .build()
        )
    }

    fun getOrderOptions(options: Map<*, *>?, commonMappings: Map<*, *>): List<OrderOptionUtil.OrderOption> {
        return options
            ?.getMappingAnnotationsSpec(AS_DISCRETE)
            ?.associate { it.getString(AES)!! to it.getMap(PARAMETERS) }
            ?.mapNotNull { (aesName, parameters) ->
                check(aesName in commonMappings)
                val variableName = commonMappings[aesName] as String
                OrderOptionUtil.OrderOption.create(
                    variableName,
                    parameters?.getString(ORDER_BY),
                    parameters?.read(ORDER)
                )
            }
            ?: emptyList()
    }

    fun List<OrderOptionUtil.OrderOption>.inheritToNonDiscrete(mappings: Map<*, *>): List<OrderOptionUtil.OrderOption> {
        // non-discrete mappings should inherit settings from the as_discrete
        return this + mappings.variables()
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
            ?.associate { it.getString(SeriesAnnotation.COLUMN)!! to it.read(SeriesAnnotation.TYPE)!! }
            ?.filterValues(SeriesAnnotation.DateTime.DATE_TIME::equals)
            ?.keys
            ?: emptySet()
    }
}


private fun Map<*, *>.variables(): Set<String> {
    return values.map { it as String }.toSet()
}
