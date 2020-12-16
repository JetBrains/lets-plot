/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.geom.LiveMapGeom
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpec
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.stat.SimpleStatContext
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PosProviderContext
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.assemble.geom.PointDataAccess
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.GroupingContext
import jetbrains.datalore.plot.builder.interact.ContextualMappingProvider
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification

class GeomLayerBuilder {
    private val myBindings = ArrayList<VarBinding>()
    private val myConstantByAes = TypedKeyHashMap()
    private lateinit var myStat: Stat
    private lateinit var myPosProvider: PosProvider
    private lateinit var myGeomProvider: GeomProvider
    private var myGroupingVarName: String? = null
    private var myPathIdVarName: String? = null
    private val myScaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()

    private var myDataPreprocessor: ((DataFrame, TypedScaleMap) -> DataFrame)? = null
    private var myLocatorLookupSpec: LookupSpec = LookupSpec.NONE
    private var myContextualMappingProvider: ContextualMappingProvider = ContextualMappingProvider.NONE

    private var myIsLegendDisabled: Boolean = false
    private var myTooltipProperties: TooltipSpecification.TooltipProperties = TooltipSpecification.TooltipProperties.NONE

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

    fun groupingVar(v: DataFrame.Variable): GeomLayerBuilder {
        myGroupingVarName = v.name
        return this
    }

    fun groupingVarName(v: String): GeomLayerBuilder {
        myGroupingVarName = v
        return this
    }

    fun pathIdVarName(v: String): GeomLayerBuilder {
        myPathIdVarName = v
        return this
    }

    fun <T> addConstantAes(aes: Aes<T>, v: T): GeomLayerBuilder {
        myConstantByAes.put(aes, v)
        return this
    }

    fun <T> addScaleProvider(aes: Aes<T>, scaleProvider: ScaleProvider<T>): GeomLayerBuilder {
        myScaleProviderByAes[aes] = scaleProvider
        return this
    }

    fun locatorLookupSpec(v: LookupSpec): GeomLayerBuilder {
        myLocatorLookupSpec = v
        return this
    }

    fun contextualMappingProvider(v: ContextualMappingProvider): GeomLayerBuilder {
        myContextualMappingProvider = v
        return this
    }

    fun disableLegend(v: Boolean): GeomLayerBuilder {
        myIsLegendDisabled = v
        return this
    }

    fun setTooltipProperties(tooltipProperties: TooltipSpecification.TooltipProperties): GeomLayerBuilder {
        myTooltipProperties = tooltipProperties
        return this
    }

    fun build(data: DataFrame, scaleMap: TypedScaleMap): GeomLayer {
        @Suppress("NAME_SHADOWING")
        var data = data
        if (myDataPreprocessor != null) {
            data = myDataPreprocessor!!(data, scaleMap)
        }

        // make sure 'original' series are transformed
        data = DataProcessing.transformOriginals(data, myBindings, scaleMap)

        val replacementBindings = HashMap(
            // No 'origin' variables beyond this point.
            // Replace all 'origin' variables in bindings with 'transform' variables
            myBindings.map {
                it.aes to if (it.variable.isOrigin) {
                    val transformVar = DataFrameUtil.transformVarFor(it.aes)
                    VarBinding(transformVar, it.aes)
                } else {
                    it
                }
            }.toMap()
        )

        // add 'transform' variable for each 'stat' variable
        val bindingsToPut = ArrayList<VarBinding>()
        for (binding in replacementBindings.values) {
            val variable = binding.variable
            if (variable.isStat) {
                val aes = binding.aes
                val scale = scaleMap[aes]
                data = DataFrameUtil.applyTransform(data, variable, aes, scale)
                bindingsToPut.add(VarBinding(TransformVar.forAes(aes), aes))
            }
        }

        // replace 'stat' vars with 'transform' vars in bindings
        for (binding in bindingsToPut) {
            replacementBindings[binding.aes] = binding
        }

        // (!) Positional aes scales have undefined `mapper` at this time because
        // dimensions of plot are not yet known.
        // Data Access shouldn't use aes mapper (!)
        val dataAccess = PointDataAccess(data, replacementBindings, scaleMap)

        return MyGeomLayer(
            data,
            myGeomProvider,
            myPosProvider,
            myGeomProvider.renders(),
            GroupingContext(data, myBindings, myGroupingVarName, myPathIdVarName, handlesGroups()).groupMapper,
            replacementBindings.values,
            myConstantByAes,
            scaleMap,
            dataAccess,
            myLocatorLookupSpec,
            myContextualMappingProvider.createContextualMapping(dataAccess, data, myTooltipProperties),
            myIsLegendDisabled
        )
    }

    private fun handlesGroups(): Boolean {
        return myGeomProvider.handlesGroups() || myPosProvider.handlesGroups()
    }


    private class MyGeomLayer(
        override val dataFrame: DataFrame,
        geomProvider: GeomProvider,
        private val myPosProvider: PosProvider,
        renderedAes: List<Aes<*>>,
        override val group: (Int) -> Int,
        varBindings: Collection<VarBinding>,
        constantByAes: TypedKeyHashMap,
        override val scaleMap: TypedScaleMap,
        override val dataAccess: MappedDataAccess,
        override val locatorLookupSpec: LookupSpec,
        override val contextualMapping: ContextualMapping,
        override val isLegendDisabled: Boolean
    ) : GeomLayer {

        override val geom: Geom = geomProvider.createGeom()
        override val geomKind: GeomKind = geomProvider.geomKind
        override val aestheticsDefaults: AestheticsDefaults

        private val myRenderedAes: List<Aes<*>>
        private val myConstantByAes: TypedKeyHashMap
        private val myVarBindingsByAes = HashMap<Aes<*>, VarBinding>()

        override val legendKeyElementFactory: LegendKeyElementFactory
            get() = geom.legendKeyElementFactory

        override val isLiveMap: Boolean
            get() = geom is LiveMapGeom

        init {
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

        fun demoAndTest(): GeomLayerBuilder {
            val builder = GeomLayerBuilder()
            builder.myDataPreprocessor = { data, scaleMap ->
                val transformedData = DataProcessing.transformOriginals(data, builder.myBindings, scaleMap)
                when (val stat = builder.myStat) {
                    Stats.IDENTITY -> transformedData
                    else -> {
                        val statCtx = SimpleStatContext(transformedData)
                        val groupingContext =
                            GroupingContext(
                                transformedData,
                                builder.myBindings,
                                builder.myGroupingVarName,
                                builder.myPathIdVarName,
                                true
                            )
                        val dataAndGroupingContext = DataProcessing.buildStatData(
                            transformedData,
                            stat,
                            builder.myBindings,
                            scaleMap,
                            groupingContext, null, null, statCtx, ::println
                        )

                        dataAndGroupingContext.data
                    }
                }
            }

            return builder
        }
    }
}
