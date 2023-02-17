/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.geom.GeomBase
import jetbrains.datalore.plot.base.geom.LiveMapGeom
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpec
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.stat.SimpleStatContext
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.util.afterOrientation
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.MarginSide
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.annotation.AnnotationLine
import jetbrains.datalore.plot.builder.annotation.AnnotationSpecification
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.assemble.geom.PointDataAccess
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.GroupingContext
import jetbrains.datalore.plot.builder.data.StatInput
import jetbrains.datalore.plot.builder.interact.ContextualMappingProvider
import jetbrains.datalore.plot.builder.presentation.DefaultFontFamilyRegistry
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.builder.theme.ThemeTextStyle

class GeomLayerBuilder constructor(
    private val geomProvider: GeomProvider,
    private val stat: Stat,
    private val posProvider: PosProvider,
    private val fontFamilyRegistry: FontFamilyRegistry,
) {

    private val myBindings = ArrayList<VarBinding>()
    private val myConstantByAes = TypedKeyHashMap()
    private var myGroupingVarName: String? = null
    private var myPathIdVarName: String? = null
    private val myScaleProviderByAes = HashMap<Aes<*>, ScaleProvider>()

    private var myDataPreprocessor: ((DataFrame, Map<Aes<*>, Transform>) -> DataFrame)? = null
    private var myLocatorLookupSpec: LookupSpec = LookupSpec.NONE
    private var myContextualMappingProvider: ContextualMappingProvider = ContextualMappingProvider.NONE

    private var myIsLegendDisabled: Boolean = false
    private var isYOrientation: Boolean = false

    private var isMarginal: Boolean = false
    private var marginalSide: MarginSide = MarginSide.LEFT
    private var marginalSize: Double = Double.NaN

    private var colorByAes: Aes<Color> = Aes.COLOR
    private var fillByAes: Aes<Color> = Aes.FILL

    private var myAnnotationsProvider: ((MappedDataAccess, DataFrame) -> Annotations?)? = null

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

    fun <T> addScaleProvider(aes: Aes<T>, scaleProvider: ScaleProvider): GeomLayerBuilder {
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


    fun yOrientation(v: Boolean): GeomLayerBuilder {
        isYOrientation = v
        return this
    }

    fun marginal(
        isMarginal: Boolean,
        marginalSide: MarginSide,
        marginalSize: Double
    ): GeomLayerBuilder {
        this.isMarginal = isMarginal
        this.marginalSide = marginalSide
        this.marginalSize = marginalSize
        return this
    }

    fun annotationSpecification(
        annotationSpec: AnnotationSpecification,
        themeTextStyle: ThemeTextStyle
    ): GeomLayerBuilder {
        myAnnotationsProvider = { dataAccess, dataFrame ->
            AnnotationLine.createAnnotations(annotationSpec, dataAccess, dataFrame, themeTextStyle)
        }
        return this
    }

    fun colorByAes(aes: Aes<Color>): GeomLayerBuilder {
        colorByAes = aes
        return this
    }

    fun fillByAes(aes: Aes<Color>): GeomLayerBuilder {
        fillByAes = aes
        return this
    }

    fun build(
        data: DataFrame,
        scaleMap: Map<Aes<*>, Scale>,
        scaleMapppersNP: Map<Aes<*>, ScaleMapper<*>>,
    ): GeomLayer {
        val transformByAes: Map<Aes<*>, Transform> = scaleMap.keys.associateWith {
            scaleMap.getValue(it).transform
        }

        @Suppress("NAME_SHADOWING")
        var data = data
        if (myDataPreprocessor != null) {
            // Test and Demo
            data = myDataPreprocessor!!(data, transformByAes)
        }

        // make sure 'original' series are transformed
        data = DataProcessing.transformOriginals(data, myBindings, transformByAes)

        val replacementBindings = HashMap(
            // No 'origin' variables beyond this point.
            // Replace all 'origin' variables in bindings with 'transform' variables
            myBindings.associate {
                it.aes to if (it.variable.isOrigin) {
                    val transformVar = DataFrameUtil.transformVarFor(it.aes)
                    VarBinding(transformVar, it.aes)
                } else {
                    it
                }
            }
        )

        // add 'transform' variable for each 'stat' variable
        val bindingsToPut = ArrayList<VarBinding>()
        for (binding in replacementBindings.values) {
            val variable = binding.variable
            if (variable.isStat) {
                val aes = binding.aes
                val transform = transformByAes.getValue(aes)
                val transformVar = TransformVar.forAes(aes)
                data = DataFrameUtil.applyTransform(data, variable, transformVar, transform)
                bindingsToPut.add(VarBinding(transformVar, aes))
            }
        }

        // replace 'stat' vars with 'transform' vars in bindings
        for (binding in bindingsToPut) {
            replacementBindings[binding.aes] = binding
        }

        // (!) Positional aes scales have undefined `mapper` at this time because
        // dimensions of plot are not yet known.
        // Data Access shouldn't use aes mapper (!)
//        val dataAccess = PointDataAccess(data, replacementBindings, scaleMap)

        val groupingVariables = DataProcessing.defaultGroupingVariables(
            data,
            myBindings,
            myPathIdVarName
        )

        val groupingContext = GroupingContext(data, groupingVariables, myGroupingVarName, handlesGroups())
        return MyGeomLayer(
            data,
            geomProvider,
            posProvider,
            groupingContext.groupMapper,
            replacementBindings,
            myConstantByAes,
            scaleMap,
            scaleMapppersNP,
            myLocatorLookupSpec,
            myContextualMappingProvider,
            myIsLegendDisabled,
            isYOrientation = isYOrientation,
            isMarginal = isMarginal,
            marginalSide = marginalSide,
            marginalSize = marginalSize,
            fontFamilyRegistry = fontFamilyRegistry,
            colorByAes = colorByAes,
            fillByAes = fillByAes,
            annotationsProvider = myAnnotationsProvider
        )
    }

    private fun handlesGroups(): Boolean {
        return geomProvider.handlesGroups() || posProvider.handlesGroups()
    }


    private class MyGeomLayer(
        override val dataFrame: DataFrame,
        geomProvider: GeomProvider,
        override val posProvider: PosProvider,
        override val group: (Int) -> Int,
        private val varBindings: Map<Aes<*>, VarBinding>,
        constantByAes: TypedKeyHashMap,
        override val scaleMap: Map<Aes<*>, Scale>,
        override val scaleMapppersNP: Map<Aes<*>, ScaleMapper<*>>,
        override val locatorLookupSpec: LookupSpec,
        private val contextualMappingProvider: ContextualMappingProvider,
        override val isLegendDisabled: Boolean,
        override val isYOrientation: Boolean,
        override val isMarginal: Boolean,
        override val marginalSide: MarginSide,
        override val marginalSize: Double,
        override val fontFamilyRegistry: FontFamilyRegistry,
        override val colorByAes: Aes<Color>,
        override val fillByAes: Aes<Color>,
        private val annotationsProvider: ((MappedDataAccess, DataFrame) -> Annotations?)?,
    ) : GeomLayer {

        override val geom: Geom = geomProvider.createGeom()
        override val geomKind: GeomKind = geomProvider.geomKind
        override val aestheticsDefaults: AestheticsDefaults = geomProvider.aestheticsDefaults()

        private val myRenderedAes: List<Aes<*>>
        private val myConstantByAes: TypedKeyHashMap

        override val legendKeyElementFactory: LegendKeyElementFactory
            get() = geom.legendKeyElementFactory

        override val isLiveMap: Boolean
            get() = geom is LiveMapGeom

        init {
            myRenderedAes = GeomMeta.renders(geomProvider.geomKind, colorByAes, fillByAes)

            // constant value by aes (default + specified)
            myConstantByAes = TypedKeyHashMap()
            for (key in constantByAes.keys<Any>()) {
                myConstantByAes.put(key, constantByAes[key])
            }
        }

        override fun renderedAes(): List<Aes<*>> {
            return myRenderedAes
        }

        override fun hasBinding(aes: Aes<*>): Boolean {
            return varBindings.containsKey(aes)
        }

        override fun <T> getBinding(aes: Aes<T>): VarBinding {
            return varBindings[aes]!!
        }

        override fun hasConstant(aes: Aes<*>): Boolean {
            return myConstantByAes.containsKey(aes)
        }

        override fun <T> getConstant(aes: Aes<T>): T {
            require(hasConstant(aes)) { "Constant value is not defined for aes $aes" }
            return myConstantByAes[aes]
        }

        override fun <T> getDefault(aes: Aes<T>): T {
            return aestheticsDefaults.defaultValue(aes)
        }

        override fun preferableNullDomain(aes: Aes<*>): DoubleSpan {
            @Suppress("NAME_SHADOWING")
            val aes = aes.afterOrientation(isYOrientation)
            return (geom as GeomBase).preferableNullDomain(aes)
        }

        override fun rangeIncludesZero(aes: Aes<*>): Boolean {
            @Suppress("NAME_SHADOWING")
            val aes = aes.afterOrientation(isYOrientation)
            return aestheticsDefaults.rangeIncludesZero(aes)
        }

        override fun setLiveMapProvider(liveMapProvider: LiveMapProvider) {
            if (geom is LiveMapGeom) {
                geom.setLiveMapProvider(liveMapProvider)
            } else {
                throw IllegalStateException("Not Livemap: " + geom::class.simpleName)
            }
        }

        override fun createContextualMapping(): ContextualMapping {
            val dataAccess = PointDataAccess(dataFrame, varBindings, scaleMap, isYOrientation)
            return contextualMappingProvider.createContextualMapping(dataAccess, dataFrame)
        }

        override fun createAnnotations(): Annotations? {
            return annotationsProvider?.let { provider ->
                val dataAccess = PointDataAccess(dataFrame, varBindings, scaleMap, isYOrientation)
                provider(dataAccess, dataFrame)
            }
        }
    }

    companion object {

        fun demoAndTest(
            geomProvider: GeomProvider,
            stat: Stat,
            posProvider: PosProvider = PosProvider.wrap(PositionAdjustments.identity()),
        ): GeomLayerBuilder {
            val builder = GeomLayerBuilder(geomProvider, stat, posProvider, DefaultFontFamilyRegistry())
            builder.myDataPreprocessor = { data, transformByAes ->
                val transformedData = DataProcessing.transformOriginals(data, builder.myBindings, transformByAes)
                when (builder.stat) {
                    Stats.IDENTITY -> transformedData
                    else -> {
                        val statCtx = SimpleStatContext(transformedData)
                        val groupingVariables = DataProcessing.defaultGroupingVariables(
                            data,
                            builder.myBindings,
                            builder.myPathIdVarName
                        )
                        val groupingCtx = GroupingContext(
                            transformedData,
                            groupingVariables,
                            builder.myGroupingVarName,
                            expectMultiple = true  // ?
                        )
                        val statInput = StatInput(
                            transformedData,
                            builder.myBindings,
                            transformByAes,
                            statCtx,
                            flipXY = false
                        )
                        val dataAndGroupingContext = DataProcessing.buildStatData(
                            statInput,
                            builder.stat,
                            groupingCtx,
                            facetVariables = emptyList(),
                            varsWithoutBinding = emptyList(),
                            orderOptions = emptyList(),
                            aggregateOperation = null,
                            variableValuesOrder = null,
                            ::println
                        )

                        dataAndGroupingContext.data
                    }
                }
            }

            return builder
        }
    }
}
