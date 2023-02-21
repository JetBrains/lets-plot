/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import jetbrains.datalore.vis.svg.SvgSvgElement

internal class FigureToPlainSvg(
    private val buildInfo: FigureBuildInfo
) {

    fun eval(): SvgSvgElement {

        val buildInfo = buildInfo.layoutedByOuterSize()

        val svgRoot = buildInfo.createSvgRoot()
        svgRoot.ensureContentBuilt()

        if (svgRoot is CompositeFigureSvgRoot) {
            processComposite(svgRoot, origin = DoubleVector.ZERO)
        }

        return svgRoot.svg
    }

    private fun processComposite(svgRoot: CompositeFigureSvgRoot, origin: DoubleVector) {
        val rootSvg = svgRoot.svg

        // Sub-figures

        for (element in svgRoot.elements) {
            val elementOrigin = element.bounds.origin.add(origin)
            element.ensureContentBuilt()

            val elementSvg = element.svg
            elementSvg.x().set(elementOrigin.x)
            elementSvg.y().set(elementOrigin.y)

            if (element is CompositeFigureSvgRoot) {
                processComposite(element, elementOrigin)
            }

            rootSvg.children().add(elementSvg)
        }
    }
}