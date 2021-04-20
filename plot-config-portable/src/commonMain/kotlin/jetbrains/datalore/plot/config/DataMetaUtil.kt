/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.createVariable
import jetbrains.datalore.plot.base.data.DataFrameUtil.findVariableOrFail
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.AES
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.ANNOTATION
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.AS_DISCRETE
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.LABEL
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.ORDER
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.ORDER_BY
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.PARAMETERS
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
        return getDataMetaAnnotationsSpec(this.getMap(Option.Meta.DATA_META), annotation)
    }

    private fun getDataMetaAnnotationsSpec(dataMeta: Map<*, *>?, annotation: String): List<Map<*, *>> {
        return dataMeta
            ?.getMaps(MappingAnnotation.TAG)
            ?.filter { it.read(ANNOTATION) == annotation }
            ?: emptyList()
    }

    /**
    @returns Set<aes> of discrete aes
     */
    fun getAsDiscreteAesSet(options: Map<*, *>): Set<Any> {
        return options
            .getMaps(MappingAnnotation.TAG)
            ?.associate { it.read(AES)!! to it.read(ANNOTATION)!! }
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
                mutableMapOf<String, Any?>(
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
        commonDiscreteAes: Set<*>,
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

    fun reorderDataFrame(dataFrame: DataFrame, byVariable: DataFrame.Variable, orderDir: Int): DataFrame {
        val data = dataFrame.get(byVariable)
        val dataWithIndices: List<Pair<Any?, Int>> = data.withIndex().map { it.value to it.index }

        val comparable: (Pair<Any?, Int>) -> Comparable<*>? = if (dataFrame.isNumeric(byVariable)) {
            { (it.first as Number).toDouble() }
        } else {
            { it.first.toString() }
        }
        val comparator: Comparator<Pair<Any?, Int>> = if (orderDir > 0) {
            compareBy(comparable)
        } else {
            compareByDescending(comparable)
        }

        val orderIndices: List<Int> = dataWithIndices.sortedWith(comparator).map { it.second }
        return dataFrame.selectIndices(orderIndices)
    }


    class OrderOption internal constructor(
        val aesName: String,
        val byVariable: String?,
        val orderDir: Int
    ) {
        companion object {
            fun create(
                aesName: String,
                orderBy: String?,
                order: Any?
            ): OrderOption? {
                if (orderBy == null && order == null) {
                    return null
                }
                val orderDir = when (order) {
                    null -> 1
                    is Number -> order.toInt()
                    else -> throw IllegalArgumentException(
                        "Unsupported `order` value: $order. Use 1 (ascending) or -1 (descending)."
                    )
                }
                return OrderOption(aesName, orderBy, orderDir)
            }
        }
    }

    fun createOrderOptions(plotOptions: Map<String, Any>): List<OrderOption> {
        val plotDiscreteAnnotations = plotOptions.getMappingAnnotationsSpec(AS_DISCRETE)
        val layersDiscreteAnnotations = plotOptions
            .getMaps(Option.Plot.LAYERS)
            ?.map { layerOptions -> layerOptions.getMappingAnnotationsSpec(AS_DISCRETE) }
            ?.flatten()
            ?: emptyList()

        return (plotDiscreteAnnotations + layersDiscreteAnnotations).mapNotNull { opts ->
            OrderOption.create(
                opts.getString(AES)!!,
                opts.getString(ORDER_BY),
                opts.read(ORDER)
            )
        }
    }
}

private fun Map<*, *>.variables(): Set<String> {
    return values.map { it as String }.toSet()
}
