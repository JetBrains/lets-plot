/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpec
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry

interface GeomLayer {
    val dataFrame: DataFrame

    val group: (Int) -> Int

    val geomKind: GeomKind

    val geom: Geom

    val posProvider: PosProvider

    val scaleMap: Map<Aes<*>, Scale>

    val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>

    val legendKeyElementFactory: LegendKeyElementFactory

    val aestheticsDefaults: AestheticsDefaults

    val isLiveMap: Boolean

    val isLegendDisabled: Boolean

    val locatorLookupSpec: LookupSpec

    val isYOrientation: Boolean

    val isMarginal: Boolean

    val marginalSide: MarginSide

    val marginalSize: Double

    val fontFamilyRegistry: FontFamilyRegistry

    val colorByAes: Aes<Color>

    val fillByAes: Aes<Color>

    fun renderedAes(considerOrientation: Boolean = false): List<Aes<*>>

    fun hasBinding(aes: Aes<*>): Boolean

    fun <T> getBinding(aes: Aes<T>): VarBinding

    fun hasConstant(aes: Aes<*>): Boolean

    fun <T> getConstant(aes: Aes<T>): T

    fun <T> getDefault(aes: Aes<T>): T

    fun preferableNullDomain(aes: Aes<*>): DoubleSpan

    fun rangeIncludesZero(aes: Aes<*>): Boolean

    fun setLiveMapProvider(liveMapProvider: LiveMapProvider)

    fun createContextualMapping(): ContextualMapping

    fun createAnnotations(): Annotations?
}
