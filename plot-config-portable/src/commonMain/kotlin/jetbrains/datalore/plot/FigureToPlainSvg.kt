/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

internal class FigureToPlainSvg(
    private val buildInfo: FigureBuildInfo
) {

    fun eval(): SvgSvgElement {

        val buildInfo = buildInfo.layoutedByOuterSize()

        val svgRoot = buildInfo.createSvgRoot()
        svgRoot.ensureContentBuilt()

        val topSvgSvg: SvgSvgElement = svgRoot.svg

        if (svgRoot is CompositeFigureSvgRoot) {
            processCompositeFigure(svgRoot, origin = DoubleVector.ZERO, topSvgSvg)
        }

        return topSvgSvg
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
        origin: DoubleVector,
        topSvgSvg: SvgSvgElement
    ) {

        // Sub-figures

        for (element in svgRoot.elements) {
            val elementOrigin = element.bounds.origin.add(origin)
            element.ensureContentBuilt()

            val elementSvg = element.svg
            elementSvg.x().set(elementOrigin.x)
            elementSvg.y().set(elementOrigin.y)

            if (element is CompositeFigureSvgRoot) {
                processCompositeFigure(element, elementOrigin, topSvgSvg)
            }

            topSvgSvg.children().add(elementSvg)
        }
    }
}