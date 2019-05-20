package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.visualization.plot.base.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpec
import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.base.render.Geom
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.PositionAdjustment
import jetbrains.datalore.visualization.plot.base.render.geom.LivemapProvider

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
