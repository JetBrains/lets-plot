/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector

interface GeomContext {
    val flipped: Boolean
    val targetCollector: GeomTargetCollector
    val annotations: Annotations?

    // ToDo: Just positional resolution along x or y-axis. Also, its now equal to "data resolution". No need to compute it in 'Aesthetics'.
    fun getResolution(aes: Aes<Double>): Double

    /**
     * @return  A rectangle which origin and size are computed basing on
     *          the overall ranges of X,Y (and other positional) aesthetics.
     */
    fun getAesBounds(): DoubleRectangle

    fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext

    fun isMappedAes(aes: Aes<*>): Boolean

    fun estimateTextSize(
        text: String,
        family: String,
        size: Double,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector
}
