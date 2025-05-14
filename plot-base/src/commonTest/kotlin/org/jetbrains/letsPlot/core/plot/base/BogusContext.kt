/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.Annotation
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector

object BogusContext : GeomContext {
    override val flipped: Boolean
        get() = error("Not available in a bogus geom context")
    override val targetCollector: GeomTargetCollector
        get() = error("Not available in a bogus geom context")
    override val annotation: Annotation
        get() = error("Not available in a bogus geom context")
    override val backgroundColor: Color
        get() = error("Not available in a bogus geom context")
    override val plotContext: PlotContext
        get() = error("Not available in a bogus geom context")

    override fun getResolution(aes: Aes<Double>): Double {
        error("Not available in a bogus geom context")
    }

    override fun getAesBounds(): DoubleRectangle {
        error("Not available in a bogus geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        error("Not available in a bogus geom context")
    }

    override fun isMappedAes(aes: Aes<*>): Boolean {
        error("Not available in a bogus geom context")
    }

    override fun getDefaultFormatter(aes: Aes<*>): (Any) -> String {
        error("Not available in a bogus geom context")
    }

    override fun getDefaultFormatter(varName: String): (Any) -> String {
        error("Not available in a bogus geom context")
    }

    override fun getCoordinateSystem(): CoordinateSystem? {
        error("Not available in a bogus geom context")
    }

    override fun getContentBounds(): DoubleRectangle {
        error("Not available in a bogus geom context")
    }

    override fun estimateTextSize(
        text: String,
        family: String,
        size: Double,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector {
        error("Not available in a bogus geom context")
    }
}