/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil.createVariable
import jetbrains.datalore.plot.base.data.DataFrameUtil.findVariableOrFail
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation
import jetbrains.datalore.plot.config.Option.Meta.MappingAnnotation.DISCRETE

object DataMetaUtil {
    private const val prefix = "@as_discrete@"

    public fun toDiscrete(variable: String) = "$prefix$variable"
    private fun fromDiscrete(variable: String): String? =
        if (variable.startsWith(prefix)) {
            variable.removePrefix(prefix)
        } else {
            // ToDo: why return null? Lets throw IllegalArgumentException
            null
        }

    /**
    @returns Map<aes, annotation>
     */
    // ToDo: rename to getAesMappingAnnotations
    private fun getMappingAnnotation(options: Map<*, *>): Map<String, String> {
        return options
            .getMaps(MappingAnnotation.TAG)
            ?.associate { it.read(MappingAnnotation.AES) as String to it.read(MappingAnnotation.ANNOTATION) as String }
            ?: emptyMap()
    }

    /**
    @returns discrete aes
     */
    // ToDo: rename to getAsDiscreteAesSet
    fun getDiscreteAes(options: Map<*, *>): Set<String> {
        return getMappingAnnotation(options).filterValues(DISCRETE::equals).keys
    }

    // ToDo: rename to createScaleSpecs
    fun processDiscreteScales(plotOptions: Map<String, Any>): List<MutableMap<String, Any>> {
        val plotDiscreteAes = plotOptions
            .getMap(Option.Meta.DATA_META)
            ?.let(DataMetaUtil::getDiscreteAes)
            ?: emptySet<String>()

        val layersDiscreteAes = plotOptions
            .getMaps(Option.Plot.LAYERS)?.asSequence()
            ?.mapNotNull { it.getMap(Option.Meta.DATA_META) }
            ?.map(DataMetaUtil::getDiscreteAes) // diascrete aes from all layers
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
    // ToDo: rename to createLayerDataFrame
    fun processDiscreteData(
        options: OptionsAccessor,
        commonData: DataFrame,
        commonDiscreteAes: Set<String>,
        commonMapping: Map<*, *>,
        isClientSide: Boolean
    ): Pair<Map<*, *>, DataFrame> {
        val data = ConfigUtil.createDataFrame(options.get(Option.PlotBase.DATA))

        // On server side:
        // - create new mappings with new encoded discrete variables.
        // - add new discrete varaibles to DataFrame
        //
        // On client side:
        // - keep names (already encoded mappings and variables)
        // - re-add variables to a DataFrame to mark variables as discrete
        val encode: (varName: String) -> String = if (isClientSide) { { it } } else { { toDiscrete(it) } }
        val decode: (varName: String) -> String? = if (isClientSide) { { it } } else { { fromDiscrete(it) } }
        val insert: (b: DataFrame.Builder, name: String, values: List<*>) -> DataFrame.Builder =
            if (isClientSide) {
                { b, name, values ->
                    // ToDo: Why 'name' have to be present in data?
                    val variable = findVariableOrFail(data, name)
                    // re-insert as discrete
                    b.remove(variable)
                    b.putDiscrete(variable, values)
                }
            } else {
                { b, name, values -> b.putDiscrete(createVariable(name), values) }
            }

        val ownDiscreteMappings = run {
            val ownDiscreteAes = getDiscreteAes(options.getMap(Option.Meta.DATA_META))
            val combinedDiscreteAes = commonDiscreteAes + ownDiscreteAes
            options.getMap(Option.PlotBase.MAPPING).filter { (aes, _) -> aes in combinedDiscreteAes }
        }

        val combinedDiscreteDfVars = run {
            // Special case: ggplot(aes(color=as_discrete('cyl'))) + geom_point(data=mpg)
            // Server side:
            //  - plot: ('color' to 'cyl') -> ('color' to '@as_discrete@cyl')
            //  - layer: ('color' to '@as_discrete@cyl') -> ('color' to 'cyl')   // ToDo: ???
            // Client side:
            //  - plot : ('color' to '@as_discrete@cyl') -> ('color' to '@as_discrete@cyl')
            //  - layer: ('color' to '@as_discrete@cyl') -> ('color' to '@as_discrete@cyl')
            val commonDiscreteMappings = commonMapping
                .map { (aes, varName) -> aes as String to varName as String }.toMap()
                .filterKeys { it in commonDiscreteAes } // common discrete mapping
                .mapValues { (_, varName) -> decode(varName) }    // ToDo: is this server-specific?

            val combinedDiscreteMappingVars = (commonDiscreteMappings + ownDiscreteMappings).values

            val ownDfVars = variables(data).mapValues { (_, dfVar) -> data[dfVar] }
            val commonDfVars = variables(commonData).mapValues { (_, dfVar) -> commonData[dfVar] }
            val combinedDiscreteDfVars = commonDfVars + ownDfVars
            combinedDiscreteDfVars.filter { (dfVarName, _) -> dfVarName in combinedDiscreteMappingVars }
        }

        return Pair(
            ownDiscreteMappings.mapValues { (_, varName) -> encode(varName as String) },
            combinedDiscreteDfVars.mapKeys { (dfVarName, _) -> encode(dfVarName) }
                .entries.fold(DataFrame.Builder(data)) { acc, (varName, values) -> insert(acc, varName, values) }
                .build()
        )
    }
}
