/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.config.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout

class CompositeFigureGridLayout(
    private val ncol: Int,
    private val nrow: Int,
) : CompositeFigureLayout {
    override fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo> {

        val cellWidth = size.x / ncol
        val cellHeight = size.y / nrow

        val layouted = ArrayList<FigureBuildInfo>()
        for (row in 0 until nrow) {
            for (col in 0 until ncol) {
                val element: FigureBuildInfo? = elements[row * nrow + col]
                element?.let {
                    layouted.add(
                        it.withBounds(
                            DoubleRectangle(
                                x = col * cellWidth,
                                y = row * cellHeight,
                                cellWidth,
                                cellHeight
                            )
                        )
                    )
                }
            }
        }
        return layouted
    }
}