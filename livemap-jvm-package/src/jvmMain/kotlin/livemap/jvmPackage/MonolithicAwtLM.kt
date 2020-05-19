/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.livemap.jvmPackage

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.AwtPlotFactory
import jetbrains.datalore.plot.DisposableJPanel
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.livemap.LiveMapUtil
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JComponent

object MonolithicAwtLM {

    private val mappers: MutableList<() -> Unit> = ArrayList()

    fun mapsToCanvas() {
        Platform.runLater {
            mappers.forEach { it() }
        }
    }

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

    private fun createPlotFactory(
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): AwtPlotFactory {
        return object : AwtPlotFactory(svgComponentFactory, executor) {
            override fun buildPlotComponent(
                plotBuildInfo: PlotBuildInfo
            ): JComponent {
                val assembler = plotBuildInfo.plotAssembler

                injectLiveMapProvider(assembler, plotBuildInfo.processedPlotSpec)

                val plot = assembler.createPlot()
                val plotContainer = PlotContainer(plot, plotBuildInfo.size)
                val plotComponent = buildPlotComponent(plotContainer)

                return if (plotContainer.liveMapFigures.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    buildPlotLiveMapComponent(
                        plotContainer.liveMapFigures as List<CanvasFigure>,
                        plotComponent,
                        plotBuildInfo.size.get()
                    )
                } else {
                    plotComponent
                }
            }
        }
    }

    private fun buildPlotLiveMapComponent(
        liveMapFigures: List<CanvasFigure>,
        plotComponent: JComponent,
        size: DoubleVector
    ): JComponent {
        val plotBounds = Rectangle(0,0, size.x.toInt(), size.y.toInt())

        plotComponent.bounds = plotBounds

        // Fix background color for jfx
        val panel = DisposableJPanel()
        panel.background = Color.WHITE

        panel.add(plotComponent)

        liveMapFigures.forEach { canvasFigure ->
            val canvasBounds = canvasFigure .bounds().get()
            val rootGroup = Group()

            JFXPanel()
                .apply {
                    scene = Scene(rootGroup)
                    bounds = Rectangle(
                        canvasBounds.origin.x,
                        canvasBounds.origin.y,
                        canvasBounds.dimension.x,
                        canvasBounds.dimension.y
                    )
                    panel.add(this)
                }

            JavafxCanvasControl(
                rootGroup,
                canvasBounds.dimension,
                1.0,
                AwtEventPeer(plotComponent, canvasBounds)
            ).let {
                mappers.add {
                    canvasFigure.mapToCanvas(it)
                }
            }
        }

        // Fixed panel minimum size for scroll pane
        val plotDimensions = Dimension(
            plotBounds.width,
            plotBounds.height
        )

        panel.preferredSize = plotDimensions
        panel.minimumSize = plotDimensions
        panel.maximumSize = plotDimensions

        return panel
    }

    private fun injectLiveMapProvider(
        plotAssembler: PlotAssembler,
        processedPlotSpec: MutableMap<String, Any>
    ) {
        LiveMapOptionsParser.parseFromPlotSpec(processedPlotSpec)
            ?.let {
                LiveMapUtil.injectLiveMapProvider(
                    plotAssembler.layersByTile,
                    it
                )
            }
    }
}