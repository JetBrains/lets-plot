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
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.DISCRETE

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

    /**
    @returns Map<aes, annotation>
     */
    private fun getAesMappingAnnotations(options: Map<*, *>): Map<String, String> {
        return options
            .getMaps(MappingAnnotation.TAG)
            ?.associate { it.read(MappingAnnotation.AES) as String to it.read(MappingAnnotation.ANNOTATION) as String }
            ?: emptyMap()
    }

    /**
    @returns Set<aes> of discrete aes
     */
    fun getAsDiscreteAesSet(options: Map<*, *>): Set<String> {
        return getAesMappingAnnotations(options).filterValues(DISCRETE::equals).keys
    }

    fun createScaleSpecs(plotOptions: Map<String, Any>): List<MutableMap<String, Any>> {
        val plotDiscreteAes = plotOptions
            .getMap(Option.Meta.DATA_META)
            ?.let(DataMetaUtil::getAsDiscreteAesSet)
            ?: emptySet<String>()

        val layersDiscreteAes = plotOptions
            .getMaps(Option.Plot.LAYERS)?.asSequence()
            ?.mapNotNull { it.getMap(Option.Meta.DATA_META) }
            ?.map(DataMetaUtil::getAsDiscreteAesSet) // diascrete aes from all layers
            ?.flatten()?.toSet() // List<Set<aes>> -> Set<aes>
            ?: emptySet<String>()


        return (plotDiscreteAes + layersDiscreteAes).map { aes ->
            mutableMapOf<String, Any>(
                Option.Scale.AES to aes,
                Option.Scale.DISCRETE_DOMAIN to true
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

}


private fun Map<*, *>.variables(): Set<String> {
    return values.map { it as String }.toSet()
}
