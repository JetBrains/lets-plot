package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.plot.builder.PosProviderContext
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.GroupingContext
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.data.TransformVar
import jetbrains.datalore.visualization.plot.base.geom.LiveMapGeom
import jetbrains.datalore.visualization.plot.base.geom.LiveMapProvider
import jetbrains.datalore.visualization.plot.base.interact.ContextualMapping
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupSpec
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.stat.SimpleStatContext
import jetbrains.datalore.visualization.plot.base.stat.Stats

class GeomLayerBuilder {
    private val myBindings = ArrayList<VarBinding>()
    private val myConstantByAes = TypedKeyHashMap()
    private lateinit var myStat: Stat
    private lateinit var myPosProvider: PosProvider
    private lateinit var myGeomProvider: jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
    private var myGroupingVarName: String? = null
    private val myScaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()

    private var myDataPreprocessor: ((DataFrame) -> DataFrame)? = null
    private var myLocatorLookupSpec: LookupSpec = LookupSpec.NONE
    private var myContextualMappingProvider: jetbrains.datalore.plot.builder.interact.ContextualMappingProvider = jetbrains.datalore.plot.builder.interact.ContextualMappingProvider.NONE

    private var myIsLegendDisabled: Boolean = false

    fun stat(v: Stat): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myStat = v
        return this
    }

    fun pos(v: PosProvider): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myPosProvider = v
        return this
    }

    fun geom(v: jetbrains.datalore.plot.builder.assemble.geom.GeomProvider): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myGeomProvider = v
        return this
    }

    fun addBinding(v: VarBinding): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myBindings.add(v)
        return this
    }

    fun groupingVar(v: DataFrame.Variable): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myGroupingVarName = v.name
        return this
    }

    fun groupingVarName(v: String): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myGroupingVarName = v
        return this
    }

    fun <T> addConstantAes(aes: Aes<T>, v: T): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myConstantByAes.put(aes, v)
        return this
    }

    fun <T> addScaleProvider(aes: Aes<T>, scaleProvider: ScaleProvider<T>): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myScaleProviderByAes[aes] = scaleProvider
        return this
    }

    fun locatorLookupSpec(v: LookupSpec): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myLocatorLookupSpec = v
        return this
    }

    fun contextualMappingProvider(v: jetbrains.datalore.plot.builder.interact.ContextualMappingProvider): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myContextualMappingProvider = v
        return this
    }

    fun disableLegend(v: Boolean): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
        myIsLegendDisabled = v
        return this
    }

    fun build(data: DataFrame): jetbrains.datalore.plot.builder.GeomLayer {
        @Suppress("NAME_SHADOWING")
        var data = data
        if (myDataPreprocessor != null) {
            data = myDataPreprocessor!!(data)
        }

        // make sure 'original' series are transformed
        data = DataProcessing.transformOriginals(data, myBindings)

        // create missing bindings for 'stat' variables
        // and other adjustments in bindings.
        val replacementBindings = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilderUtil.rewireBindingsAfterStat(
            data,
            myStat,
            myBindings,
            TypedScaleProviderMap(myScaleProviderByAes)
        )

        // add 'transform' variable for each 'stat' variable
        val bindingsToPut = ArrayList<VarBinding>()
        for (binding in replacementBindings.values) {
            val `var` = binding.variable
            if (`var`.isStat) {
                val aes = binding.aes
                val scale = binding.scale
                data = DataFrameUtil.applyTransform(data, `var`, aes, scale!!)

                bindingsToPut.add(VarBinding(TransformVar.forAes(aes), aes, scale))
            }
        }

        // replace 'stat' vars with 'transform' vars in bindings
        for (binding in bindingsToPut) {
            replacementBindings[binding.aes] = binding
        }

        // (!) Positional aes scales have undefined `mapper` at this time because
        // dimensions of plot are not yet known.
        // Data Access shouldn't use aes mapper (!)
        val dataAccess = jetbrains.datalore.plot.builder.assemble.geom.PointDataAccess(data, replacementBindings)

        return jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.MyGeomLayer(
            data,
            myGeomProvider,
            myPosProvider,
            handledAes(),
            myGeomProvider.renders(),
            GroupingContext(data, myBindings, myGroupingVarName, handlesGroups()).groupMapper,
            replacementBindings.values,
            myConstantByAes,
            dataAccess,
            myLocatorLookupSpec,
            myContextualMappingProvider.createContextualMapping(dataAccess),
            myIsLegendDisabled
        )
    }

    private fun handlesGroups(): Boolean {
//        return DataProcessing.groupsHandled(myGeomProvider, myPosProvider)
        return myGeomProvider.handlesGroups() || myPosProvider.handlesGroups()
    }

    private fun handledAes(): List<Aes<*>> {
        return jetbrains.datalore.plot.builder.assemble.GeomLayerBuilderUtil.handledAes(myGeomProvider, myStat)
    }


    private class MyGeomLayer(
        override val dataFrame: DataFrame,
        geomProvider: jetbrains.datalore.plot.builder.assemble.geom.GeomProvider,
        private val myPosProvider: PosProvider,
        handledAes: List<Aes<*>>,
        renderedAes: List<Aes<*>>,
        override val group: (Int) -> Int,
        varBindings: Collection<VarBinding>,
        constantByAes: TypedKeyHashMap,
        override val dataAccess: MappedDataAccess,
        override val locatorLookupSpec: LookupSpec,
        override val contextualMapping: ContextualMapping,
        override val isLegendDisabled: Boolean
    ) : jetbrains.datalore.plot.builder.GeomLayer {

        override val geom: Geom = geomProvider.createGeom()
        override val geomKind: GeomKind = geomProvider.geomKind
        override val aestheticsDefaults: AestheticsDefaults

        private val myHandledAes: List<Aes<*>>
        private val myRenderedAes: List<Aes<*>>
        private val myConstantByAes: TypedKeyHashMap
        private val myVarBindingsByAes = HashMap<Aes<*>, VarBinding>()

        override val legendKeyElementFactory: LegendKeyElementFactory
            get() = geom.legendKeyElementFactory

        override val isLivemap: Boolean
            get() = geom is LiveMapGeom

        init {
            myHandledAes = ArrayList(handledAes)
            myRenderedAes = ArrayList(renderedAes)

            // constant value by aes (default + specified)
            aestheticsDefaults = geomProvider.aestheticsDefaults()
            myConstantByAes = TypedKeyHashMap()

            for (key in constantByAes.keys<Any>()) {
                myConstantByAes.put(key, constantByAes[key])
            }

            for (varBinding in varBindings) {
                myVarBindingsByAes[varBinding.aes] = varBinding
            }
        }

        override fun handledAes(): List<Aes<*>> {
            return myHandledAes
        }

        override fun renderedAes(): List<Aes<*>> {
            return myRenderedAes
        }

        override fun createPos(ctx: PosProviderContext): PositionAdjustment {
            return myPosProvider.createPos(ctx)
        }

        override fun hasBinding(aes: Aes<*>): Boolean {
            return myVarBindingsByAes.containsKey(aes)
        }

        override fun <T> getBinding(aes: Aes<T>): VarBinding {
            return myVarBindingsByAes[aes]!!
        }

        override fun hasConstant(aes: Aes<*>): Boolean {
            return myConstantByAes.containsKey(aes)
        }

        override fun <T> getConstant(aes: Aes<T>): T {
            checkArgument(hasConstant(aes), "Constant value is not defined for aes $aes")
            return myConstantByAes[aes]
        }

        override fun <T> getDefault(aes: Aes<T>): T {
            return aestheticsDefaults.defaultValue(aes)
        }

        override fun rangeIncludesZero(aes: Aes<*>): Boolean {
            return aestheticsDefaults.rangeIncludesZero(aes)
        }

        override fun setLiveMapProvider(liveMapProvider: LiveMapProvider) {
            if (geom is LiveMapGeom) {
                geom.setLiveMapProvider(liveMapProvider)
            } else {
                throw IllegalStateException("Not Livemap: " + geom::class.simpleName)
            }
        }
    }

    companion object {

        fun demoAndTest(): jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder {
            val builder = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder()
            builder.myDataPreprocessor = { data ->
                val transformedData = DataProcessing.transformOriginals(data, builder.myBindings)
                when (val stat = builder.myStat) {
                    Stats.IDENTITY -> transformedData
                    else -> {
                        val statCtx = SimpleStatContext(transformedData)
                        val groupingContext =
                            GroupingContext(
                                transformedData,
                                builder.myBindings,
                                builder.myGroupingVarName,
                                true
                            )
                        val dataAndGroupingContext = DataProcessing.buildStatData(
                            transformedData,
                            stat,
                            builder.myBindings,
                            groupingContext, null, null, statCtx
                        )

                        dataAndGroupingContext.data
                    }
                }
            }

            return builder
        }
    }
}
