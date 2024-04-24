/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.Annotation
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapProvider
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpec
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.assemble.LegendItem

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

    val legendItem: LegendItem?

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

    fun createAnnotation(): Annotation?
}
