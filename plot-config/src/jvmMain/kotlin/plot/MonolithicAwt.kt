/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.awt.RGBEncoderAwt
import jetbrains.datalore.vis.svgToString.SvgToString
import mu.KotlinLogging
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent


class MonolithicAwt(
    componentFactory: (svg: SvgSvgElement) -> JComponent,
    executor: (() -> Unit) -> Unit
) : Monolithic(componentFactory, executor) {

    override fun buildPlotSvgComponent(
        plotBuildInfo: PlotBuildInfo
    ): JComponent {
        val assembler = plotBuildInfo.plotAssembler

        val plot = assembler.createPlot()
        val plotContainer = PlotContainer(plot, plotBuildInfo.size)
        val plotComponent = buildPlotSvgComponent(plotContainer)

        require(plotContainer.liveMapFigures.isEmpty()) { "geom_livemap is not enabled" }

        return plotComponent
    }

    companion object {
        /**
         * Static SVG export
         */
        @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "was replaced with PlotSvgExport.buildSvgImageFromRawSpecs",
        replaceWith = ReplaceWith(
            expression = "PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec, plotSize)",
            imports = ["jetbrains.datalore.plot.PlotSvgExport"]
        )
    )fun buildSvgImagesFromRawSpecs(
            plotSpec: MutableMap<String, Any>,
            plotSize: DoubleVector?,
            computationMessagesHandler: ((List<String>) -> Unit)
        ): List<String> {
            UNSUPPORTED("was replaced with PlotSvgExport.buildSvgImageFromRawSpecs"
            )
        }
    }
}
