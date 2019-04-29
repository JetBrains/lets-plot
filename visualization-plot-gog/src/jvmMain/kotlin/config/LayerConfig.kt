package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.DATA
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.MAPPING
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.SHOW_LEGEND
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.STAT
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.data.Sampling
import jetbrains.datalore.visualization.plot.gog.core.data.Stat
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PosProvider
import jetbrains.datalore.visualization.plot.gog.plot.assemble.TypedScaleProviderMap
import jetbrains.datalore.visualization.plot.gog.plot.assemble.geom.GeomProvider
import observable.collections.Collections.unmodifiableList
import observable.collections.Collections.unmodifiableMap

class LayerConfig(opts: Map<*, *>,
                  sharedData: DataFrame,
                  plotMapping: Map<*, *>,
                  statProto: StatProto,
                  scaleProviderByAes: TypedScaleProviderMap, private val myClientSide: Boolean) : OptionsAccessor(opts, initDefaultOptions(opts, statProto)) {
    val geomProvider: GeomProvider
    val stat: Stat
    val explicitGroupingVarName: String?
    val posProvider: PosProvider
    private val myCombinedData: DataFrame
    val varBindings: List<VarBinding>
    internal val constantsMap: Map<Aes<*>, *>
    internal val statKind: StatKind
    private val mySamplings: List<Sampling>?

    var ownData: DataFrame? = null
        private set
    private var myOwnDataUpdated = false

    val combinedData: DataFrame
        get() {
            checkState(!myOwnDataUpdated)
            return myCombinedData
        }

    internal val isLegendDisabled: Boolean
        get() = if (hasOwn(SHOW_LEGEND)) {
            !getBoolean(SHOW_LEGEND, true)
        } else false

    val samplings: List<Sampling>?
        get() {
            checkState(!myClientSide)
            return mySamplings
        }

    init {

        val geomProvider = GeomProto.geomProvider(getString(GEOM)!!, this)

        // mapping (inherit from plot)
        val mappingOptions = HashMap<Any, Any>(plotMapping)
        // update with 'layer' mapping
        mappingOptions.putAll(getMap(MAPPING) as Map<Any, Any>)

        val layerData = ConfigUtil.createDataFrame(get(DATA))
        var combinedData: DataFrame
        if (!(sharedData.isEmpty || layerData!!.isEmpty) && sharedData.rowCount() == layerData!!.rowCount()) {
            combinedData = DataFrameUtil.appendReplace(sharedData, layerData!!)
        } else if (!layerData!!.isEmpty) {
            combinedData = layerData
        } else {
            combinedData = sharedData
        }

        var aesMapping: Map<Aes<*>, DataFrame.Variable>?
        if (GeoPositionsDataUtil.hasGeoPositionsData(this) && myClientSide) {
            // join dataset and geo-positions data
            val dataAndMapping = GeoPositionsDataUtil.initDataAndMappingForGeoPositions(
                    geomProvider.geomKind,
                    combinedData,
                    GeoPositionsDataUtil.getGeoPositionsData(this),
                    mappingOptions
            )
            combinedData = dataAndMapping.first!!
            aesMapping = dataAndMapping.second
        } else {
            aesMapping = ConfigUtil.createAesMapping(combinedData, mappingOptions)
        }

        // auto-map variables if necessary
        if (aesMapping!!.isEmpty()) {
            aesMapping = geomProvider.createAesAutoMapper().createMapping(combinedData)
            if (!myClientSide) {
                // store used mapping options to pass to client.
                val autoMappingOptions = HashMap<String, Any>()
                for (aes in aesMapping.keys) {
                    val option = Option.Mapping.toOption(aes)
                    val variable = aesMapping[aes]!!.name
                    autoMappingOptions[option] = variable
                }
                update(MAPPING, autoMappingOptions)
            }
        }

        // exclude constant aes from mapping
        val constants = LayerConfigUtil.initConstants(this)
        if (!constants.isEmpty()) {
            aesMapping = HashMap<Aes<*>, DataFrame.Variable>(aesMapping)
            for (aes in constants.keys) {
                aesMapping.remove(aes)
            }
        }

        // grouping
        explicitGroupingVarName = initGroupingVarName(combinedData, mappingOptions)

        this.geomProvider = geomProvider
        statKind = StatKind.safeValueOf(getString(STAT)!!)
        stat = statProto.createStat(statKind, mergedOptions)
        posProvider = LayerConfigUtil.initPositionAdjustments(this, geomProvider.preferredPos)
        constantsMap = unmodifiableMap(constants)

        val consumedAesSet = HashSet(geomProvider.renders())
        if (!myClientSide) {
            consumedAesSet.addAll(stat.requires())
        }

        val varBindings = LayerConfigUtil.createBindings(combinedData, aesMapping, scaleProviderByAes.unmodifiableCopy(), consumedAesSet)

        this.varBindings = unmodifiableList(varBindings)
        checkState(layerData != null)
        ownData = layerData
        myCombinedData = combinedData

        mySamplings = if (myClientSide)
            null
        else
            LayerConfigUtil.initSampling(this, geomProvider.preferredSampling)
    }

    private fun initGroupingVarName(data: DataFrame, mappingOptions: Map<*, *>): String? {
        val groupBy = mappingOptions[Option.Mapping.GROUP]
        var fieldName: String? = if (groupBy is String)
            groupBy
        else
            null

        if (fieldName == null && GeoPositionsDataUtil.hasGeoPositionsData(this)) {
            // 'default' group is important for 'geom_map'
            val groupVar = DataFrameUtil.variables(data)["group"]
            if (groupVar != null) {
                fieldName = groupVar.name
            }
        }
        return fieldName
    }

    fun hasVarBinding(varName: String): Boolean {
        for (binding in varBindings) {
            if (binding.`var`.name == varName) {
                return true
            }
        }
        return false
    }

    fun replaceOwnData(dataFrame: DataFrame?) {
        checkState(!myClientSide)   // This class is immutable on client-side
        checkArgument(dataFrame != null)
        update(DATA, DataFrameUtil.toMap(dataFrame!!))
        ownData = dataFrame
        myOwnDataUpdated = true
    }

    fun hasExplicitGrouping(): Boolean {
        return explicitGroupingVarName != null
    }

    fun isExplicitGrouping(varName: String): Boolean {
        return explicitGroupingVarName != null && explicitGroupingVarName == varName
    }

    companion object {
        private fun initDefaultOptions(layerOptions: Map<*, *>, statProto: StatProto): Map<*, *> {
            checkArgument(
                    layerOptions.containsKey(GEOM) || layerOptions.containsKey(STAT),
                    "Either 'geom' or 'stat' must be specified")

            val defaults = HashMap<String, Any>()
            if (layerOptions.containsKey(GEOM)) {
                val name = layerOptions[GEOM] as String
                defaults.putAll(GeomProto.defaultOptions(name))
            }

            var statName: String? = layerOptions[STAT] as String
            if (statName == null) {
                statName = defaults[STAT] as String
            }
            defaults.putAll(statProto.defaultOptions(statName))

            return defaults
        }
    }
}
