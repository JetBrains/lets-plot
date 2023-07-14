/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

abstract class FigureSvgRoot(
    val bounds: DoubleRectangle
) {
    val svg: SvgSvgElement = SvgSvgElement()

    private var isContentBuilt: Boolean = false

    init {
        svg.addClass(Style.PLOT_CONTAINER)
        setSvgSize(bounds.dimension)
    }

    final fun ensureContentBuilt() {
        if (!isContentBuilt) {
            buildContent()
        }
    }

    private fun buildContent() {
        check(!isContentBuilt)
        isContentBuilt = true
        buildFigureContent()
    }

    fun clearContent() {
        if (isContentBuilt) {
            isContentBuilt = false

            svg.children().clear()
            clearFigureContent()
        }
    }

    protected abstract fun buildFigureContent()
    protected abstract fun clearFigureContent()

    private fun setSvgSize(size: DoubleVector) {
        svg.width().set(size.x)
        svg.height().set(size.y)
    }
}
