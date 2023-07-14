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
        val contentExpand = theme.margin() + theme.padding()
        contentOrigin = DoubleVector(contentExpand, contentExpand)
        fullContentExtend = DoubleVector(contentExpand * 2, contentExpand * 2)

        innerOrigin = DoubleVector(theme.margin(), theme.margin())
        innerContentExtend = DoubleVector(theme.padding() * 2, theme.padding() * 2)
    }

    fun hasTitle(): Boolean {
        return title.isNotBlank() && theme.showTitle()
    }
}
