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
     * returns new mappings to discrete variables and DataFrame with these discrete variables
     */
    fun createDataFrame(
        options: OptionsAccessor,
        commonData: DataFrame,
        commonDiscreteAes: Set<String>,
        commonMapping: Map<*, *>,
        isClientSide: Boolean
    ): Pair<Map<*, *>, DataFrame> {
        val data = ConfigUtil.createDataFrame(options.get(Option.PlotBase.DATA))
        val combinedDfVars = DataFrameUtil.toMap(commonData) + DataFrameUtil.toMap(data)

        if (isClientSide) {
            return Pair(
                // no new discrete mappings, all job was done on server side
                emptyMap<Any?, Any?>(),
                // re-insert existing variables as discrete
                combinedDfVars
                    .filter { (varName, _) -> isDiscrete(varName) }
                    .entries
                    .fold(DataFrame.Builder(data)) { acc, (varName, values) ->
                        val variable = findVariableOrFail(data, varName)
                        // re-insert as discrete
                        acc.remove(variable)
                        acc.putDiscrete(variable, values)
                    }
                    .build()
            )

        } else { // server side
            val ownMappings = options.getMap(Option.PlotBase.MAPPING)

            // own names - not yet encoded, i.e. 'cyl'
            val ownDiscreteMappings = run {
                val ownDiscreteAes = getAsDiscreteAesSet(options.getMap(Option.Meta.DATA_META))
                ownMappings.filter { (aes, _) -> aes in ownDiscreteAes }
            }

            // common names - already encoded by PlotConfig, i.e. '@as_discrete@cyl'. Restore original name.
            val commonDiscreteVars = commonMapping.filterKeys { it in commonDiscreteAes }.variables().map(::fromDiscrete)

            // Original (not encoded) discrete var names from both common and own mappings.
            val combinedDiscreteMappingVars = mutableSetOf<String>()
            combinedDiscreteMappingVars += ownDiscreteMappings.variables()
            combinedDiscreteMappingVars += commonDiscreteVars
            // minus own non-discrete mappings (layer overrides plot)
            combinedDiscreteMappingVars -= ownMappings.variables() - ownDiscreteMappings.variables()

            return Pair(
                ownMappings + ownDiscreteMappings.mapValues { (_, varName) ->
                    require(varName is String)
                    require(!isDiscrete(varName)) { "Already encoded discrete mapping: $varName" }
                    toDiscrete(varName)
                },
                combinedDfVars
                    .filter { (dfVarName, _) -> dfVarName in combinedDiscreteMappingVars }
                    .mapKeys { (dfVarName, _) -> createVariable(toDiscrete(dfVarName)) }
                    .entries
                    .fold(DataFrame.Builder(data)) { acc, (dfVar, values) -> acc.putDiscrete(dfVar, values)}
                    .build()
            )

        }
    }
}

private fun Map<*, *>.variables(): Set<String> {
    return values.map { it as String }.toSet()
}
