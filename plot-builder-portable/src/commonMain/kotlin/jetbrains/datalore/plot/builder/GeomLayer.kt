/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapProvider
import org.jetbrains.letsPlot.core.plot.base.interact.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator.LookupSpec
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry

interface GeomLayer {
    val dataFrame: DataFrame

    val group: (Int) -> Int

    val geomKind: GeomKind

    val geom: Geom

    val posProvider: PosProvider

    val scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>

    val scaleMappersNP: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>

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

    val colorByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>

    val fillByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>

    fun renderedAes(considerOrientation: Boolean = false): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>

    fun hasBinding(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean

    fun <T> getBinding(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): VarBinding

    fun hasConstant(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean

    fun <T> getConstant(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T

    fun <T> getDefault(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T

    fun preferableNullDomain(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DoubleSpan

    fun rangeIncludesZero(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean

    fun setLiveMapProvider(liveMapProvider: LiveMapProvider)

    fun createContextualMapping(): ContextualMapping

    fun createAnnotations(): Annotations?
}
