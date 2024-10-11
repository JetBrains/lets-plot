/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

abstract class LegendBoxSpec(
    val title: String,
    val theme: LegendTheme,
    val reverse: Boolean
) {

    abstract val layout: LegendBoxLayout
    val contentOrigin: DoubleVector

    private val fullContentExtend: DoubleVector
    private val innerOrigin: DoubleVector
    private val innerContentExtend: DoubleVector

    private val contentSize: DoubleVector
        get() = layout.size

    val size: DoubleVector
        get() = contentSize.add(fullContentExtend)

    val innerBounds: DoubleRectangle
        get() = DoubleRectangle(innerOrigin, contentSize.add(innerContentExtend))

    val contentBounds: DoubleRectangle
        get() = DoubleRectangle(contentOrigin, contentSize)

    init {
        val contentExpand = DoubleVector(theme.backgroundStrokeWidth(), theme.backgroundStrokeWidth())
        contentOrigin = theme.margins().leftTop.add(contentExpand)
        fullContentExtend = theme.margins().size.add(contentExpand.mul(2.0))

        innerOrigin = contentExpand.mul(0.5)
        innerContentExtend = theme.margins().size.add(contentExpand)
    }

    fun hasTitle(): Boolean {
        return title.isNotBlank() && theme.showTitle()
    }
}
