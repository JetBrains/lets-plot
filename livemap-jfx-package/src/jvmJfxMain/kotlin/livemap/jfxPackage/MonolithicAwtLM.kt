/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.livemap.jfxPackage

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color.TRANSPARENT
import javafx.scene.paint.Color.WHITE
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.AwtPlotFactory
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.livemap.LiveMapUtil
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

object MonolithicAwtLM {

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

                // Move tooltip when map moved
                plotComponent.addMouseMotionListener(object : MouseAdapter() {
                    override fun mouseDragged(e: MouseEvent) {
                        super.mouseDragged(e)
                        executor {
                            plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                        }
                    }
                })

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
        plotComponent.bounds = Rectangle(0,0, size.x.toInt(), size.y.toInt())

        val panel = JFXPanel().apply {
            scene = Scene(Group(), WHITE)
        }

        (plotComponent as SceneMapperJfxPanel).sceneFillColor = TRANSPARENT

        panel.add(plotComponent)

        liveMapFigures.forEach { canvasFigure ->
            val canvasBounds = canvasFigure.bounds().get()
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
                Platform.runLater{ canvasFigure.mapToCanvas(it) }
            }
        }

        return panel
    }

    private fun injectLiveMapProvider(
        plotAssembler: PlotAssembler,
        processedPlotSpec: MutableMap<String, Any>
    ) {
        LiveMapOptionsParser.parseFromPlotOptions(OptionsAccessor(processedPlotSpec))
            ?.let {
                LiveMapUtil.injectLiveMapProvider(
                    plotAssembler.layersByTile,
                    it
                )
            }
    }
}