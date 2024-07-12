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

interface GeomContext {
    val flipped: Boolean
    val targetCollector: GeomTargetCollector
    val annotation: Annotation?
    val backgroundColor: Color
    val plotContext: PlotContext // ToDo: it's used to apply the same formatting to annotations as for tooltips, need refactoring

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

    // Simple formatter, based on the bound variable type (e.g. int -> "d", float -> "f", datetime -> "%d.%m.%y %H:%M:%S")
    // If type is not known, returns Any::toString
    fun getDefaultFormatter(aes: Aes<*>): (Any) -> String

    // Simple formatter, based on the variable type (e.g. int -> "d", float -> "f", datetime -> "%d.%m.%y %H:%M:%S")
    // If type is not known, returns Any::toString
    fun getDefaultFormatter(varName: String): (Any) -> String
}
