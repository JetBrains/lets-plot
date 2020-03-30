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
import javax.swing.JComponent

object MonolithicAwt {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return createPlotFactory(svgComponentFactory, executor)
            .buildPlotFromRawSpecs(plotSpec, plotSize, computationMessagesHandler)
    }

    fun buildPlotSvgComponent(
        plotContainer: PlotContainer,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        return createPlotFactory(svgComponentFactory, executor)
            .buildPlotSvgComponent(plotContainer)
    }

    private fun createPlotFactory(
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): AwtPlotFactory {
        return object : AwtPlotFactory(svgComponentFactory, executor) {
            override fun buildPlotComponent(
                plotBuildInfo: PlotBuildInfo,
                plotComponentFactory: (plotContainer: PlotContainer) -> JComponent
            ): JComponent {
                val assembler = plotBuildInfo.plotAssembler
                val plot = assembler.createPlot()
                val plotContainer = PlotContainer(plot, plotBuildInfo.size)
                val plotComponent = plotComponentFactory(plotContainer)

                require(plotContainer.liveMapFigures.isEmpty()) { "geom_livemap is not enabled" }

                return plotComponent
            }
        }
    }

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
    )

    fun buildSvgImagesFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): List<String> {
        UNSUPPORTED("was replaced with PlotSvgExport.buildSvgImageFromRawSpecs")
    }
}
