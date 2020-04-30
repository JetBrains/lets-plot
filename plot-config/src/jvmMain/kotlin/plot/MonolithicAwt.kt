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

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return createPlotFactory(svgComponentFactory, executor)
            .buildPlotFromProcessedSpecs(plotSpec, plotSize, computationMessagesHandler)
    }

    fun buildPlotComponent(
        plotContainer: PlotContainer,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        return createPlotFactory(svgComponentFactory, executor)
            .buildPlotComponent(plotContainer)
    }

    private fun createPlotFactory(
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): AwtPlotFactory {
        return object : AwtPlotFactory(svgComponentFactory, executor) {
            override fun buildPlotComponent(
                plotBuildInfo: PlotBuildInfo
            ): JComponent {
                val assembler = plotBuildInfo.plotAssembler
                val plot = assembler.createPlot()
                val plotContainer = PlotContainer(plot, plotBuildInfo.size)

                require(!plotContainer.isLiveMap) { "geom_livemap is not enabled" }

                return buildPlotComponent(plotContainer)
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
