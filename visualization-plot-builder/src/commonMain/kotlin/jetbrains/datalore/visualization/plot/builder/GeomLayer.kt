package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpec
import jetbrains.datalore.visualization.plot.base.geom.LivemapProvider
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory

interface GeomLayer {
    val dataFrame: DataFrame

    val group: (Int) -> Int

    val geomKind: GeomKind

    val geom: Geom

    val dataAccess: MappedDataAccess

    val legendKeyElementFactory: LegendKeyElementFactory

    val aestheticsDefaults: AestheticsDefaults

    val isLivemap: Boolean

    val isLegendDisabled: Boolean

    val locatorLookupSpec: LookupSpec

    val contextualMapping: ContextualMapping

    fun handledAes(): List<Aes<*>>

    fun renderedAes(): List<Aes<*>>

    fun createPos(ctx: PosProviderContext): PositionAdjustment

    fun hasBinding(aes: Aes<*>): Boolean

    fun <T> getBinding(aes: Aes<T>): VarBinding

    fun hasConstant(aes: Aes<*>): Boolean

    fun <T> getConstant(aes: Aes<T>): T

    fun <T> getDefault(aes: Aes<T>): T

    fun rangeIncludesZero(aes: Aes<*>): Boolean

    fun setLivemapProvider(livemapProvider: LivemapProvider)
}
