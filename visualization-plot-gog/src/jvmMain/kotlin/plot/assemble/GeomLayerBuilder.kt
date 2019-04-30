package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.visualization.plot.core.AestheticsDefaults
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.config.event3.GeomTargetInteraction.TooltipAesSpec
import jetbrains.datalore.visualization.plot.gog.core.data.*
import jetbrains.datalore.visualization.plot.gog.core.data.stat.Stats
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LookupSpec
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Geom
import jetbrains.datalore.visualization.plot.gog.core.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapGeom
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapProvider
import jetbrains.datalore.visualization.plot.gog.plot.GeomLayer
import jetbrains.datalore.visualization.plot.gog.plot.PosProviderContext
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.gog.plot.data.DataProcessing
import jetbrains.datalore.visualization.plot.gog.plot.data.GroupingContext
import jetbrains.datalore.visualization.plot.gog.plot.event3.TooltipAesSpecProvider
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProvider

class GeomLayerBuilder {
    private val myBindings = ArrayList<VarBinding>()
    private val myConstantByAes = TypedKeyHashMap()
    private var myStat: Stat? = null
    private var myPosProvider: PosProvider? = null
    private var myGeomProvider: GeomProvider? = null
    private var myGroupingVarName: String? = null
    private val myScaleProviderByAes = TypedScaleProviderMap()

    private var myDataPreprocessor: ((DataFrame) -> DataFrame)? = null
    private var myLocatorLookupSpec: LookupSpec? = null
    private var myTooltipAesSpecProvider: TooltipAesSpecProvider? = null

    private var myIsLegendDisabled: Boolean = false

    fun stat(v: Stat): GeomLayerBuilder {
        myStat = v
        return this
    }

    fun pos(v: PosProvider): GeomLayerBuilder {
        myPosProvider = v
        return this
    }

    fun geom(v: GeomProvider): GeomLayerBuilder {
        myGeomProvider = v
        return this
    }

    fun addBinding(v: VarBinding): GeomLayerBuilder {
        myBindings.add(v)
        return this
    }

    fun groupingVar(v: DataFrame.Variable?): GeomLayerBuilder {
        myGroupingVarName = v?.name
        return this
    }

    fun groupingVarName(v: String): GeomLayerBuilder {
        myGroupingVarName = v
        return this
    }

    fun <T> addConstantAes(aes: Aes<T>, v: T): GeomLayerBuilder {
        myConstantByAes.put(aes, v)
        return this
    }

    fun <T> addScaleProvider(aes: Aes<T>, scaleProvider: ScaleProvider<T>): GeomLayerBuilder {
        myScaleProviderByAes.put(aes, scaleProvider)
        return this
    }

    fun locatorLookupSpec(v: LookupSpec): GeomLayerBuilder {
        myLocatorLookupSpec = v
        return this
    }

    fun tooltipAesSpecProvider(v: TooltipAesSpecProvider): GeomLayerBuilder {
        myTooltipAesSpecProvider = v
        return this
    }

    fun disableLegend(v: Boolean): GeomLayerBuilder {
        myIsLegendDisabled = v
        return this
    }

    fun build(data: DataFrame): GeomLayer {
        var data = data
        if (myDataPreprocessor != null) {
            data = myDataPreprocessor!!(data)
        }

        // make sure 'original' series are transformed
        data = DataProcessing.transformOriginals(data, myBindings)

        // create missing bindings for 'stat' variables
        // and other adjustments in bindings.
        val replacementBindings = GeomLayerBuilderUtil.rewireBindingsAfterStat(data, myStat!!, myBindings, myScaleProviderByAes)

        // add 'transform' variable for each 'stat' variable
        val bindingsToPut = ArrayList<VarBinding>()
        for (binding in replacementBindings.values) {
            val `var` = binding.`var`
            if (`var`.isStat) {
                val aes = binding.aes
                val scale = binding.scale
                data = DataFrameUtil.applyTransform(data, `var`, aes, scale!!)

                bindingsToPut.add(VarBinding(TransformVar.forAes(aes), aes, scale))
            }
        }

        // replace 'stat' vars with 'transform' vars in bindings
        for (binding in bindingsToPut) {
            replacementBindings.put(binding.aes, binding)
        }

        val dataAccess = myGeomProvider!!.createDataAccess(data, replacementBindings)
        return MyGeomLayer(data,
                myGeomProvider!!,
                myPosProvider!!,
                handledAes(), // layer handles
                myGeomProvider!!.renders(), // layer renders
                //groups,
                GroupingContext(data, myBindings, myGroupingVarName, handlesGroups()).groupMapper,
                replacementBindings.values,
                myConstantByAes,
                dataAccess,
                myLocatorLookupSpec!!,
                if (myTooltipAesSpecProvider != null) myTooltipAesSpecProvider!!.createTooltipAesSpec(dataAccess) else null,
                myIsLegendDisabled)
    }

    private fun handlesGroups(): Boolean {
        return DataProcessing.groupsHandled(myGeomProvider!!, myPosProvider!!)
    }

    private fun handledAes(): List<Aes<*>> {
        return GeomLayerBuilderUtil.handledAes(myGeomProvider!!, myStat!!)
    }


    private class MyGeomLayer internal constructor(override val dataFrame: DataFrame,
                                                   geomProvider: GeomProvider,
                                                   private val myPosProvider: PosProvider,
                                                   handledAes: List<Aes<*>>,
                                                   renderedAes: List<Aes<*>>,
                                                   override val group: (Int) -> Int,
                                                   varBindings: Collection<VarBinding>,
            // ToDo: use TypedKeyContainer ?
                                                   constantByAes: TypedKeyHashMap,
                                                   override val dataAccess: MappedDataAccess,
                                                   override val locatorLookupSpec: LookupSpec,
                                                   override val tooltipAesSpec: TooltipAesSpec?,
                                                   override val isLegendDisabled: Boolean) : GeomLayer {
        override val geom: Geom
        override val geomKind: GeomKind
        override val aestheticsDefaults: AestheticsDefaults
        private val myHandledAes: List<Aes<*>>
        private val myRenderedAes: List<Aes<*>>
        // ToDo: use TypedKeyContainer ?
        private val myConstantByAes: TypedKeyHashMap
        private val myVarBindingsByAes = HashMap<Aes<*>, VarBinding>()

        override val legendKeyElementFactory: LegendKeyElementFactory
            get() = geom.legendKeyElementFactory

        override val isLivemap: Boolean
            get() = geom is LivemapGeom

        init {
            geom = geomProvider.createGeom()
            geomKind = geomProvider.geomKind
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

        override fun setLivemapProvider(livemapProvider: LivemapProvider) {
            if (geom is LivemapGeom) {
                geom.setLivemapProvider(livemapProvider)
            } else {
                throw IllegalStateException("Not Livemap: " + geom::class.simpleName)
            }
        }
    }

    companion object {


        fun demoAndTest(): GeomLayerBuilder {
            val builder = GeomLayerBuilder()
            builder.myDataPreprocessor = { data ->
                val _data = DataProcessing.transformOriginals(data, builder.myBindings)
                if (builder.myStat === Stats.IDENTITY) {
                    _data
                } else {
                    val statCtx = SimpleStatContext(_data)
                    val groupingContext = GroupingContext(_data, builder.myBindings, builder.myGroupingVarName, true)

                    val dataAndGroupingContext = DataProcessing.buildStatData(_data,
                            builder.myStat!!, builder.myBindings, groupingContext, null, null, statCtx)
                    dataAndGroupingContext.data
                }
            }
            return builder
        }
    }
}
