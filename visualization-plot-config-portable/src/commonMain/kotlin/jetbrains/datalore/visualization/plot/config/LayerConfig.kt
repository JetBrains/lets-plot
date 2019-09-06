package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Stat
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.builder.assemble.PosProvider
import jetbrains.datalore.visualization.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.visualization.plot.builder.assemble.geom.DefaultAesAutoMapper
import jetbrains.datalore.visualization.plot.builder.sampling.Sampling
import jetbrains.datalore.visualization.plot.config.Option.Layer.DATA
import jetbrains.datalore.visualization.plot.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.config.Option.Layer.MAPPING
import jetbrains.datalore.visualization.plot.config.Option.Layer.SHOW_LEGEND
import jetbrains.datalore.visualization.plot.config.Option.Layer.STAT

class LayerConfig constructor(
    layerOptions: Map<*, *>,
    sharedData: DataFrame,
    plotMapping: Map<*, *>,
    val geomProto: GeomProto,
    statProto: StatProto,
    scaleProviderByAes: TypedScaleProviderMap,
    private val myClientSide: Boolean
) : OptionsAccessor(layerOptions, initDefaultOptions(layerOptions, geomProto, statProto)) {

    //    val geomProvider: GeomProvider
    val stat: Stat
    val explicitGroupingVarName: String?
    val posProvider: PosProvider
    private val myCombinedData: DataFrame
    val varBindings: List<VarBinding>
    val constantsMap: Map<Aes<*>, *>
    val statKind: StatKind
    private val mySamplings: List<Sampling>?

    var ownData: DataFrame? = null
        private set
    private var myOwnDataUpdated = false

    val combinedData: DataFrame
        get() {
            checkState(!myOwnDataUpdated)
            return myCombinedData
        }

    val isLegendDisabled: Boolean
        get() = if (hasOwn(SHOW_LEGEND)) {
            !getBoolean(SHOW_LEGEND, true)
        } else false

    val samplings: List<Sampling>?
        get() {
            checkState(!myClientSide)
            return mySamplings
        }

    init {

//        val geomName = getString(GEOM)!!
//        val geomKind = Option.GeomName.toGeomKind(geomName)
//        val geomProvider = geomProto.geomProvider(geomName, this)

        // mapping (inherit from plot)
        val mappingOptions = HashMap(plotMapping)
        // update with 'layer' mapping
        mappingOptions.putAll(getMap(MAPPING) as Map<Any, Any>)

        val layerData = ConfigUtil.createDataFrame(get(DATA))
        var combinedData: DataFrame
        if (!(sharedData.isEmpty || layerData.isEmpty) && sharedData.rowCount() == layerData.rowCount()) {
            combinedData = DataFrameUtil.appendReplace(sharedData, layerData)
        } else if (!layerData.isEmpty) {
            combinedData = layerData
        } else {
            combinedData = sharedData
        }

        var aesMapping: Map<Aes<*>, DataFrame.Variable>?
        if (GeoPositionsDataUtil.hasGeoPositionsData(this) && myClientSide) {
            // join dataset and geo-positions data
            val dataAndMapping = GeoPositionsDataUtil.initDataAndMappingForGeoPositions(
//                geomProvider.geomKind,
                geomProto.geomKind,
                combinedData,
                GeoPositionsDataUtil.getGeoPositionsData(this),
                mappingOptions
            )
            combinedData = dataAndMapping.first
            aesMapping = dataAndMapping.second
        } else {
            aesMapping = ConfigUtil.createAesMapping(combinedData, mappingOptions)
        }

        // auto-map variables if necessary
        if (aesMapping.isEmpty()) {
//            aesMapping = geomProvider.createAesAutoMapper().createMapping(combinedData)
            aesMapping = DefaultAesAutoMapper.forGeom(geomProto.geomKind).createMapping(combinedData)
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
        if (constants.isNotEmpty()) {
            aesMapping = HashMap(aesMapping)
            for (aes in constants.keys) {
                aesMapping.remove(aes)
            }
        }

        // grouping
        explicitGroupingVarName = initGroupingVarName(combinedData, mappingOptions)

//        this.geomProvider = geomProvider
        statKind = StatKind.safeValueOf(getString(STAT)!!)
        stat = statProto.createStat(statKind, mergedOptions)
//        posProvider = LayerConfigUtil.initPositionAdjustments(this, geomProvider.preferredPos)
        posProvider = LayerConfigUtil.initPositionAdjustments(this, geomProto.preferredPositionAdjustments(this))
        constantsMap = constants

//        val consumedAesSet = HashSet(geomProvider.renders())
        val consumedAesSet = HashSet(geomProto.renders())
        if (!myClientSide) {
            consumedAesSet.addAll(stat.requires())
        }

        val varBindings = LayerConfigUtil.createBindings(combinedData, aesMapping, scaleProviderByAes, consumedAesSet)

        this.varBindings = varBindings
        ownData = layerData
        myCombinedData = combinedData

        mySamplings = if (myClientSide)
            null
        else
//            LayerConfigUtil.initSampling(this, geomProvider.preferredSampling)
            LayerConfigUtil.initSampling(this, geomProto.preferredSampling())
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
            if (binding.variable.name == varName) {
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

    private companion object {
        private fun initDefaultOptions(layerOptions: Map<*, *>, geomProto: GeomProto, statProto: StatProto): Map<*, *> {
            checkArgument(
                layerOptions.containsKey(GEOM) || layerOptions.containsKey(STAT),
                "Either 'geom' or 'stat' must be specified"
            )

            val defaults = HashMap<String, Any>()
//            if (layerOptions.containsKey(GEOM)) {
//                val name = layerOptions[GEOM] as String
//                defaults.putAll(geomProto.defaultOptions(name))
            defaults.putAll(geomProto.defaultOptions())
//            }

            var statName: String? = layerOptions[STAT] as String?
            if (statName == null) {
                statName = defaults[STAT] as String
            }
            defaults.putAll(statProto.defaultOptions(statName))

            return defaults
        }
    }
}
