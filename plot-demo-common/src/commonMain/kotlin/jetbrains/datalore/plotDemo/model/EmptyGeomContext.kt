/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.values.Font
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.builder.presentation.DefaultFontFamilyRegistry
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec

/**
 * Used in demos only.
 */
class EmptyGeomContext : GeomContext {
    override val flipped: Boolean = false
    override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()
    override val annotations: Annotations? = null

    override fun getResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getAesBounds(): DoubleRectangle {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun isMappedAes(aes: Aes<*>): Boolean = false
    override fun estimateTextSize(
        text: String,
        family: String,
        size: Double,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector {
        @Suppress("NAME_SHADOWING")
        val family = DefaultFontFamilyRegistry().get(family)
        return PlotLabelSpec(
            Font(
                family = family,
                size = size.toInt(),
                isBold = isBold,
                isItalic = isItalic
            ),
        ).dimensions(text)
    }
}